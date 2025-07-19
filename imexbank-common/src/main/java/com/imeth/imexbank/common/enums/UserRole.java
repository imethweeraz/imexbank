package com.imeth.imexbank.common.enums;

public enum UserRole {
    CUSTOMER("Customer", "Regular banking customer"),
    TELLER("Teller", "Bank teller for basic operations"),
    MANAGER("Manager", "Branch manager with approval rights"),
    ADMIN("Administrator", "System administrator"),
    AUDITOR("Auditor", "Audit and compliance officer"),
    SYSTEM("System", "System automated operations");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInternal() {
        return this != CUSTOMER;
    }

    public boolean hasApprovalRights() {
        return this == MANAGER || this == ADMIN;
    }
}