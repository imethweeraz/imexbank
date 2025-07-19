dto.setCreatedBy(transaction.getCreatedBy());

        if (transaction.getSourceAccount() != null) {
        dto.setSourceAccountId(transaction.getSourceAccount().getId());
        dto.setSourceAccountNumber(transaction.getSourceAccount().getAccountNumber());
        }

        if (transaction.getTargetAccount() != null) {
        dto.setTargetAccountId(transaction.getTargetAccount().getId());
        dto.setTargetAccountNumber(transaction.getTargetAccount().getAccountNumber());
        }

        return dto;
    }

private String getCurrentUsername() {
    // This would be retrieved from security context
    return "SYSTEM";
}
}