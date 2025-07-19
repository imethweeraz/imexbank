package com.imeth.imexbank.common.dto;

import com.imeth.imexbank.common.enums.AccountType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal interestRate;
    private BigDecimal overdraftLimit;
    private Long customerId;
    private String customerName;
    private Boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime lastTransactionDate;

    // Constructors
    public AccountDto() {
    }

    public AccountDto(Long id, String accountNumber, AccountType accountType,
                      BigDecimal balance, Long customerId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.customerId = customerId;
    }

    // Builder pattern for easy construction
    public static class Builder {
        private final AccountDto dto = new AccountDto();

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            dto.accountNumber = accountNumber;
            return this;
        }

        public Builder accountType(AccountType accountType) {
            dto.accountType = accountType;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            dto.balance = balance;
            return this;
        }

        public Builder availableBalance(BigDecimal availableBalance) {
            dto.availableBalance = availableBalance;
            return this;
        }

        public Builder interestRate(BigDecimal interestRate) {
            dto.interestRate = interestRate;
            return this;
        }

        public Builder overdraftLimit(BigDecimal overdraftLimit) {
            dto.overdraftLimit = overdraftLimit;
            return this;
        }

        public Builder customerId(Long customerId) {
            dto.customerId = customerId;
            return this;
        }

        public Builder customerName(String customerName) {
            dto.customerName = customerName;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            dto.isActive = isActive;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            dto.createdDate = createdDate;
            return this;
        }

        public Builder lastTransactionDate(LocalDateTime lastTransactionDate) {
            dto.lastTransactionDate = lastTransactionDate;
            return this;
        }

        public AccountDto build() {
            return dto;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
}