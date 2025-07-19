package com.imeth.imexbank.common.dto;

import com.imeth.imexbank.common.enums.AccountType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InterestCalculationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long accountId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private LocalDate calculationDate;
    private Integer numberOfDays;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private String calculationMethod;

    // Constructors
    public InterestCalculationDto() {
    }

    // Calculation methods
    public void calculateSimpleInterest() {
        if (principalAmount != null && interestRate != null && numberOfDays != null) {
            // Simple Interest = (P * R * T) / (365 * 100)
            BigDecimal interest = principalAmount
                    .multiply(interestRate)
                    .multiply(BigDecimal.valueOf(numberOfDays))
                    .divide(BigDecimal.valueOf(36500), 2, BigDecimal.ROUND_HALF_UP);

            this.interestAmount = interest;
            this.totalAmount = principalAmount.add(interest);
            this.calculationMethod = "SIMPLE";
        }
    }

    public void calculateCompoundInterest(int compoundingFrequency) {
        if (principalAmount != null && interestRate != null && numberOfDays != null) {
            // Compound Interest = P * (1 + R/n)^(n*t) - P
            double p = principalAmount.doubleValue();
            double r = interestRate.doubleValue() / 100;
            double t = numberOfDays / 365.0;
            double n = compoundingFrequency;

            double amount = p * Math.pow(1 + (r / n), n * t);
            double interest = amount - p;

            this.interestAmount = BigDecimal.valueOf(interest).setScale(2, BigDecimal.ROUND_HALF_UP);
            this.totalAmount = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
            this.calculationMethod = "COMPOUND";
        }
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(LocalDate calculationDate) {
        this.calculationDate = calculationDate;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }
}