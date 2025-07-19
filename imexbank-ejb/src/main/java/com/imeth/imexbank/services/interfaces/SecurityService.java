package com.imeth.imexbank.services.interfaces;

import com.imeth.imexbank.common.enums.UserRole;
import jakarta.ejb.Local;

import java.util.Set;

@Local
public interface SecurityService {

    boolean authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void resetPassword(String username, String newPassword);

    void lockUser(String username);

    void unlockUser(String username);

    boolean isUserLocked(String username);

    Set<UserRole> getUserRoles(String username);

    boolean hasRole(String username, UserRole role);

    void assignRole(String username, UserRole role);

    void removeRole(String username, UserRole role);

    String generateOTP(String username);

    boolean validateOTP(String username, String otp);

    void recordLoginAttempt(String username, boolean success, String ipAddress);
}