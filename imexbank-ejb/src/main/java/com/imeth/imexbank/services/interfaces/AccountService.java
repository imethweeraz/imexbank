package com.imeth.imexbank.services.interfaces;

import com.imeth.imexbank.common.dto.AccountDto;
import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.common.exceptions.AccountNotFoundException;
import com.imeth.imexbank.common.exceptions.BankingException;
import jakarta.ejb.Local;

import java.math.BigDecimal;
import java.util.List;

@Local
public interface AccountService {

    AccountDto createAccount(AccountDto accountDto) throws BankingException;

    AccountDto updateAccount(AccountDto accountDto) throws BankingException;

    AccountDto getAccount(Long accountId) throws AccountNotFoundException;

    AccountDto getAccountByNumber(String accountNumber) throws AccountNotFoundException;

    List<AccountDto> getCustomerAccounts(Long customerId);

    List<AccountDto> getAccountsByType(AccountType accountType);

    BigDecimal getBalance(String accountNumber) throws AccountNotFoundException;

    void deposit(String accountNumber, BigDecimal amount) throws BankingException;

    void withdraw(String accountNumber, BigDecimal amount) throws BankingException;

    void activateAccount(Long accountId) throws BankingException;

    void deactivateAccount(Long accountId) throws BankingException;

    String generateAccountNumber(AccountType accountType);
}