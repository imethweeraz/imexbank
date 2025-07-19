package com.imeth.imexbank.services.impl;

import com.imeth.imexbank.common.constants.SecurityConstants;
import com.imeth.imexbank.common.enums.UserRole;
import com.imeth.imexbank.dao.AuditLogDao;
import com.imeth.imexbank.entities.User;
import com.imeth.imexbank.services.interfaces.SecurityService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Stateless
public class SecurityServiceBean implements SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityServiceBean.class);

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // In-memory OTP storage (in production, use cache or database)
    private static final Map<String, OtpInfo> otpStore = new ConcurrentHashMap<>();

    @PersistenceContext(unitName = "ImexBankPU")
    private EntityManager entityManager;

    @EJB
    private AuditLogDao auditLogDao;

    @Override
    public boolean authenticate(String username, String password) {
        logger.debug("Authenticating user: {}", username);

        try {
            User user = findUserByUsername(username);

            if (user == null || !user.getIsActive() || user.getIsLocked()) {
                recordLoginAttempt(username, false, null);
                return false;
            }

            boolean authenticated = verifyPassword(password, user.getPasswordHash());

            if (authenticated) {
                user.recordSuccessfulLogin();
                entityManager.merge(user);
                logger.info("User {} authenticated successfully", username);
            } else {
                user.recordFailedLogin();
                entityManager.merge(user);
                logger.warn("Failed authentication attempt for user: {}", username);
            }

            return authenticated;

        } catch (Exception e) {
            logger.error("Error during authentication", e);
            return false;
        }
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        logger.info("Changing password for user: {}", username);

        User user = findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid current password");
        }

        String newPasswordHash = hashPassword(newPassword);
        user.changePassword(newPasswordHash);
        entityManager.merge(user);

        // Audit log
        auditLogDao.createAsync("User", user.getId(),
                SecurityConstants.PASSWORD_CHANGE_ACTION, username, null, null);
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        logger.info("Resetting password for user: {}", username);

        User user = findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String newPasswordHash = hashPassword(newPassword);
        user.changePassword(newPasswordHash);
        entityManager.merge(user);
    }

    @Override
    public void lockUser(String username) {
        logger.info("Locking user: {}", username);

        User user = findUserByUsername(username);
        if (user != null) {
            user.setIsLocked(true);
            entityManager.merge(user);

            auditLogDao.createAsync("User", user.getId(),
                    SecurityConstants.ACCOUNT_LOCKED_ACTION, "SYSTEM", null, null);
        }
    }

    @Override
    public void unlockUser(String username) {
        logger.info("Unlocking user: {}", username);

        User user = findUserByUsername(username);
        if (user != null) {
            user.setIsLocked(false);
            user.setFailedLoginAttempts(0);
            entityManager.merge(user);
        }
    }

    @Override
    public boolean isUserLocked(String username) {
        User user = findUserByUsername(username);
        return user != null && user.getIsLocked();
    }

    @Override
    public Set<UserRole> getUserRoles(String username) {
        User user = findUserByUsername(username);
        return user != null ? new HashSet<>(user.getRoles()) : new HashSet<>();
    }

    @Override
    public boolean hasRole(String username, UserRole role) {
        User user = findUserByUsername(username);
        return user != null && user.hasRole(role);
    }

    @Override
    public void assignRole(String username, UserRole role) {
        logger.info("Assigning role {} to user: {}", role, username);

        User user = findUserByUsername(username);
        if (user != null) {
            user.addRole(role);
            entityManager.merge(user);
        }
    }

    @Override
    public void removeRole(String username, UserRole role) {
        logger.info("Removing role {} from user: {}", role, username);

        User user = findUserByUsername(username);
        if (user != null) {
            user.removeRole(role);
            entityManager.merge(user);
        }
    }

    @Override
    public String generateOTP(String username) {
        logger.debug("Generating OTP for user: {}", username);

        // Generate 6-digit OTP
        int otp = SECURE_RANDOM.nextInt(900000) + 100000;
        String otpString = String.valueOf(otp);

        // Store OTP with expiry
        OtpInfo otpInfo = new OtpInfo(otpString,
                LocalDateTime.now().plusMinutes(SecurityConstants.OTP_VALIDITY_MINUTES));
        otpStore.put(username, otpInfo);

        // In production, send OTP via SMS/Email
        logger.info("OTP generated for user: {}", username);

        return otpString;
    }

    @Override
    public boolean validateOTP(String username, String otp) {
        logger.debug("Validating OTP for user: {}", username);

        OtpInfo otpInfo = otpStore.get(username);

        if (otpInfo == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpInfo.expiryTime)) {
            otpStore.remove(username);
            return false;
        }

        if (otpInfo.attempts >= SecurityConstants.MAX_OTP_ATTEMPTS) {
            otpStore.remove(username);
            return false;
        }

        otpInfo.attempts++;

        if (otpInfo.otp.equals(otp)) {
            otpStore.remove(username);
            return true;
        }

        return false;
    }

    @Override
    public void recordLoginAttempt(String username, boolean success, String ipAddress) {
        String action = success ? SecurityConstants.LOGIN_ACTION :
                SecurityConstants.FAILED_LOGIN_ACTION;

        auditLogDao.createAsync("User", null, action, username, ipAddress, null);
    }

    private User findUserByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createNamedQuery(
                    "User.findByUsername", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[SecurityConstants.SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt,
                    ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Combine salt and hash
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);

            // Extract salt
            byte[] salt = new byte[SecurityConstants.SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, salt.length);

            // Extract hash
            byte[] storedHashBytes = new byte[combined.length - salt.length];
            System.arraycopy(combined, salt.length, storedHashBytes, 0,
                    storedHashBytes.length);

            // Hash the provided password with the same salt
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt,
                    ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            // Compare hashes
            return Arrays.equals(storedHashBytes, testHash);

        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }

    // Inner class for OTP storage
    private static class OtpInfo {
        final String otp;
        final LocalDateTime expiryTime;
        int attempts = 0;

        OtpInfo(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}