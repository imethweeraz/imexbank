package com.imeth.imexbank.services.interfaces;

import com.imeth.imexbank.common.dto.CustomerDto;
import com.imeth.imexbank.common.exceptions.BankingException;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface CustomerService {

    CustomerDto createCustomer(CustomerDto customerDto) throws BankingException;

    CustomerDto updateCustomer(CustomerDto customerDto) throws BankingException;

    CustomerDto getCustomer(Long customerId) throws BankingException;

    CustomerDto getCustomerByNic(String nicNumber) throws BankingException;

    CustomerDto getCustomerByEmail(String email) throws BankingException;

    List<CustomerDto> getAllActiveCustomers();

    List<CustomerDto> searchCustomers(String searchTerm);

    void activateCustomer(Long customerId) throws BankingException;

    void deactivateCustomer(Long customerId) throws BankingException;

    String generateCustomerNumber();

    boolean isNicAvailable(String nicNumber);

    boolean isEmailAvailable(String email);
}