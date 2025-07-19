package com.imeth.imexbank.common.enums;

public enum AccountType {
    SAVINGS("Savings Account", "SAV"),
    CURRENT("Current Account", "CUR"),
    FIXED_DEPOSIT("Fixed Deposit Account", "FD"),
    RECURRING_DEPOSIT("Recurring Deposit Account", "RD"),
    LOAN("Loan Account", "LOAN");

    private final String displayName;
    private final String code;

    AccountType(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public static AccountType fromCode(String code) {
        for (AccountType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid account type code: " + code);
    }
}