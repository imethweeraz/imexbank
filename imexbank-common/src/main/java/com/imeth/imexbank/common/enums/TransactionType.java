package com.imeth.imexbank.common.enums;

public enum TransactionType {
    DEPOSIT("Deposit", "DEP"),
    WITHDRAWAL("Withdrawal", "WDL"),
    TRANSFER("Fund Transfer", "TRF"),
    INTEREST_CREDIT("Interest Credit", "INT"),
    FEE_DEBIT("Fee Debit", "FEE"),
    LOAN_DISBURSEMENT("Loan Disbursement", "LND"),
    LOAN_REPAYMENT("Loan Repayment", "LNR"),
    SCHEDULED_TRANSFER("Scheduled Transfer", "SCH");

    private final String displayName;
    private final String code;

    TransactionType(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public boolean isDebit() {
        return this == WITHDRAWAL || this == TRANSFER || this == FEE_DEBIT ||
                this == LOAN_REPAYMENT || this == SCHEDULED_TRANSFER;
    }

    public boolean isCredit() {
        return this == DEPOSIT || this == INTEREST_CREDIT || this == LOAN_DISBURSEMENT;
    }
}