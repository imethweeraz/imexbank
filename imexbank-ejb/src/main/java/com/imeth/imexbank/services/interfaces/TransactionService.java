package com.imeth.imexbank.services.interfaces;

import com.imeth.imexbank.common.dto.TransactionDto;
import com.imeth.imexbank.common.dto.TransferRequestDto;
import com.imeth.imexbank.common.enums.TransactionStatus;
import com.imeth.imexbank.common.enums.TransactionType;
import com.imeth.imexbank.common.exceptions.BankingException;
import jakarta.ejb.Local;

import java.time.LocalDateTime;
import java.util.List;

@Local
public interface TransactionService {

    TransactionDto processTransfer(TransferRequestDto transferRequest)
            throws BankingException;

    TransactionDto scheduleTransfer(TransferRequestDto transferRequest)
            throws BankingException;

    TransactionDto getTransaction(Long transactionId) throws BankingException;

    TransactionDto getTransactionByReference(String referenceNumber)
            throws BankingException;

    List<TransactionDto> getAccountTransactions(Long accountId);

    List<TransactionDto> getTransactionsByDateRange(LocalDateTime startDate,
                                                    LocalDateTime endDate);

    List<TransactionDto> getTransactionsByStatus(TransactionStatus status);

    List<TransactionDto> searchTransactions(Long accountId,
                                            TransactionType type,
                                            TransactionStatus status,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate);

    void cancelTransaction(Long transactionId) throws BankingException;

    void reverseTransaction(Long transactionId) throws BankingException;

    String generateTransactionReference();
}