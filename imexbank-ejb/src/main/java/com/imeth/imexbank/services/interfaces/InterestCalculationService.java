package com.imeth.imexbank.services.interfaces;

import com.imeth.imexbank.common.dto.InterestCalculationDto;
import com.imeth.imexbank.common.enums.AccountType;
import jakarta.ejb.Local;

import java.time.LocalDate;
import java.util.List;

@Local
public interface InterestCalculationService {

    void calculateDailyInterest();

    void calculateMonthlyInterest();

    InterestCalculationDto calculateInterestForAccount(Long accountId,
                                                       LocalDate calculationDate);

    List<InterestCalculationDto> calculateInterestForAccountType(
            AccountType accountType, LocalDate calculationDate);

    void creditInterestToAccounts(List<InterestCalculationDto> calculations);

    void updateInterestRates(AccountType accountType,
                             java.math.BigDecimal newRate,
                             LocalDate effectiveDate);
}