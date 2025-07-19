package com.imeth.imexbank.entities;

import com.imeth.imexbank.common.enums.AccountType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "interest_rates")
@NamedQueries({
        @NamedQuery(name = "InterestRate.findAll", query = "SELECT i FROM InterestRate i"),
        @NamedQuery(name = "InterestRate.findByAccountType",
                query = "SELECT i FROM InterestRate i WHERE i.accountType = :accountType AND i.isActive = true"),
        @NamedQuery(name = "InterestRate.findEffective",
                query = "SELECT i FROM InterestRate i WHERE i.effectiveDate <= :date AND (i.expiryDate IS NULL OR i.expiryDate > :date)")
})
public class InterestRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "rate_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal ratePercentage;

    @Column(name = "minimum_balance", precision = 19, scale = 2)
    private BigDecimal minimumBalance;

    @Column(name = "maximum_balance", precision = 19, scale = 2)
    private BigDecimal maximumBalance;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "compound_frequency")
    private String compoundFrequency;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    // Constructors
    public InterestRate() {
    }

    public InterestRate(AccountType accountType, BigDecimal ratePercentage, LocalDate effectiveDate) {
        this.accountType = accountType;
        this.ratePercentage = ratePercentage;
        this.effectiveDate = effectiveDate;
    }

    // Business methods
    public boolean isApplicable(BigDecimal balance) {
        boolean aboveMinimum = minimumBalance == null || balance.compareTo(minimumBalance) >= 0;
        boolean belowMaximum = maximumBalance == null || balance.compareTo(maximumBalance) <= 0;
        return aboveMinimum && belowMaximum;
    }

    public boolean isEffectiveOn(LocalDate date) {
        boolean afterEffective = !date.isBefore(effectiveDate);
        boolean beforeExpiry = expiryDate == null || date.isBefore(expiryDate);
        return afterEffective && beforeExpiry && isActive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getRatePercentage() {
        return ratePercentage;
    }

    public void setRatePercentage(BigDecimal ratePercentage) {
        this.ratePercentage = ratePercentage;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getMaximumBalance() {
        return maximumBalance;
    }

    public void setMaximumBalance(BigDecimal maximumBalance) {
        this.maximumBalance = maximumBalance;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCompoundFrequency() {
        return compoundFrequency;
    }

    public void setCompoundFrequency(String compoundFrequency) {
        this.compoundFrequency = compoundFrequency;
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

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public Long getVersion() {
        return version;
    }
}