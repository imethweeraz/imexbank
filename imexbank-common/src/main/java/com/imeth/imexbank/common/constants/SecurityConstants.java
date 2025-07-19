package com.imeth.imexbank.common.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    // Authentication
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BASIC_PREFIX = "Basic ";

    // Session Attributes
    public static final String USER_SESSION_KEY = "currentUser";
    public static final String USER_ROLES_KEY = "userRoles";
    public static final String LOGIN_TIME_KEY = "loginTime";
    public static final String IP_ADDRESS_KEY = "ipAddress";

    // Security Headers
    public static final String CSRF_TOKEN_HEADER = "X-CSRF-Token";
    public static final String API_KEY_HEADER = "X-API-Key";

    // Password Policy
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String PASSWORD_POLICY_MESSAGE =
            "Password must be at least 8 characters long and contain at least one digit, " +
                    "one lowercase letter, one uppercase letter, and one special character";

    // Account Lockout
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int LOCKOUT_DURATION_MINUTES = 30;

    // OTP Configuration
    public static final int OTP_LENGTH = 6;
    public static final int OTP_VALIDITY_MINUTES = 5;
    public static final int MAX_OTP_ATTEMPTS = 3;

    // PIN Configuration
    public static final int PIN_LENGTH = 4;
    public static final int MAX_PIN_ATTEMPTS = 3;

    // Token Configuration
    public static final int TOKEN_VALIDITY_HOURS = 24;
    public static final int REFRESH_TOKEN_VALIDITY_DAYS = 30;

    // Encryption
    public static final String ENCRYPTION_ALGORITHM = "AES";
    public static final String HASH_ALGORITHM = "SHA-256";
    public static final int SALT_LENGTH = 16;

    // Audit Actions
    public static final String LOGIN_ACTION = "LOGIN";
    public static final String LOGOUT_ACTION = "LOGOUT";
    public static final String FAILED_LOGIN_ACTION = "FAILED_LOGIN";
    public static final String PASSWORD_CHANGE_ACTION = "PASSWORD_CHANGE";
    public static final String ACCOUNT_LOCKED_ACTION = "ACCOUNT_LOCKED";
    public static final String UNAUTHORIZED_ACCESS_ACTION = "UNAUTHORIZED_ACCESS";

    // Security Roles (aligned with UserRole enum)
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_TELLER = "TELLER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_AUDITOR = "AUDITOR";
    public static final String ROLE_SYSTEM = "SYSTEM";
}