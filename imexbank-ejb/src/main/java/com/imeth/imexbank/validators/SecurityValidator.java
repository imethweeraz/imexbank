package com.imeth.imexbank.validators;

import com.imeth.imexbank.common.constants.SecurityConstants;
import com.imeth.imexbank.entities.User; // Assuming User entity is used for validation
import jakarta.ejb.Stateless;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class SecurityValidator {

    public List<String> validateUserPassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password cannot be empty.");
        } else if (password.length() < SecurityConstants.MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + SecurityConstants.MIN_PASSWORD_LENGTH + " characters long.");
        } else if (password.length() > SecurityConstants.MAX_PASSWORD_LENGTH) {
            errors.add("Password cannot exceed " + SecurityConstants.MAX_PASSWORD_LENGTH + " characters.");
        } else if (!password.matches(SecurityConstants.PASSWORD_PATTERN)) {
            errors.add(SecurityConstants.PASSWORD_POLICY_MESSAGE);
        }
        return errors;
    }

    public List<String> validateUsername(String username) {
        List<String> errors = new ArrayList<>();
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username cannot be empty.");
        }
        // Add more username-specific rules (e.g., character restrictions, length)
        return errors;
    }

    public List<String> validateOTP(String otp) {
        List<String> errors = new ArrayList<>();
        if (otp == null || otp.trim().isEmpty()) {
            errors.add("OTP cannot be empty.");
        } else if (otp.length() != SecurityConstants.OTP_LENGTH) {
            errors.add("OTP must be " + SecurityConstants.OTP_LENGTH + " digits long.");
        } else if (!otp.matches("\\d+")) { // Check if it only contains digits
            errors.add("OTP must contain only digits.");
        }
        return errors;
    }

    public List<String> validatePIN(String pin) {
        List<String> errors = new ArrayList<>();
        if (pin == null || pin.trim().isEmpty()) {
            errors.add("PIN cannot be empty.");
        } else if (pin.length() != SecurityConstants.PIN_LENGTH) {
            errors.add("PIN must be " + SecurityConstants.PIN_LENGTH + " digits long.");
        } else if (!pin.matches("\\d+")) { // Check if it only contains digits
            errors.add("PIN must contain only digits.");
        }
        return errors;
    }

    // Add other security-related validations as needed (e.g., role validation)
}