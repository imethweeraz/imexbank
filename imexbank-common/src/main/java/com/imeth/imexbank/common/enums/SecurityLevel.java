package com.imeth.imexbank.common.enums;

public enum SecurityLevel {
    PUBLIC("Public", 0),
    AUTHENTICATED("Authenticated", 1),
    CONFIDENTIAL("Confidential", 2),
    RESTRICTED("Restricted", 3),
    TOP_SECRET("Top Secret", 4);

    private final String displayName;
    private final int level;

    SecurityLevel(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(SecurityLevel other) {
        return this.level > other.level;
    }
}