package com.imeth.imexbank.security.realm;

/**
 * Represents the security realm for the ImexBank application.
 * In a real Jakarta EE application server, this would be configured in the server's
 * management console (e.g., as a security domain in WildFly) and would
 * point to the CustomLoginModule.
 */
public class BankingRealm {

    public static final String NAME = "ImexBankRealm";

    private BankingRealm() {
        // Private constructor for utility class
    }

    public String getName() {
        return NAME;
    }
}