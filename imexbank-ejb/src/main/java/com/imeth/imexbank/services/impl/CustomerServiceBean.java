package com.imeth.imexbank.services.impl;

import com.imeth.imexbank.common.constants.BankingConstants;
import com.imeth.imexbank.common.constants.SecurityConstants;
import com.imeth.imexbank.common.dto.CustomerDto;
import com.imeth.imexbank.common.exceptions.BankingException;
import com.imeth.imexbank.dao.CustomerDao;
import com.imeth.imexbank.entities.Customer;
import com.imeth.imexbank.services.interfaces.CustomerService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import com.imeth.imexbank.interceptors.LoggingInterceptor;
import com.imeth.imexbank.interceptors.AuditInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Interceptors({LoggingInterceptor.class, AuditInterceptor.class})
public class CustomerServiceBean implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceBean.class);

    @EJB
    private CustomerDao customerDao;

    @Override
    @RolesAllowed({SecurityConstants.ROLE_TELLER, SecurityConstants.ROLE_MANAGER})
    public CustomerDto createCustomer(CustomerDto customerDto) throws BankingException {
        logger.info("Creating new customer: {} {}",
                customerDto.getFirstName(), customerDto.getLastName());

        // Validate unique constraints
        if (customerDao.existsByNic(customerDto.getNicNumber())) {
            throw new BankingException("NIC number already exists");
        }

        if (customerDao.existsByEmail(customerDto.getEmail())) {
            throw new BankingException("Email already exists");
        }

        // Validate data
        validateCustomerData(customerDto);

        // Generate customer number
        String customerNumber = generateCustomerNumber();

        // Create customer entity
        Customer customer = new Customer(
                customerNumber,
                customerDto.getFirstName(),
                customerDto.getLastName(),
                customerDto.getNicNumber(),
                customerDto.getDateOfBirth(),
                customerDto.getEmail(),
                customerDto.getPhoneNumber(),
                customerDto.getAddress(),
                customerDto.getCity()
        );

        if (customerDto.getPostalCode() != null) {
            customer.setPostalCode(customerDto.getPostalCode());
        }

        // Save customer
        customer = customerDao.create(customer);

        logger.info("Customer created successfully: {}", customerNumber);

        return convertToDto(customer);
    }

    @Override
    @RolesAllowed({SecurityConstants.ROLE_TELLER, SecurityConstants.ROLE_MANAGER})
    public CustomerDto updateCustomer(CustomerDto customerDto) throws BankingException {
        logger.info("Updating customer: {}", customerDto.getId());

        Customer customer = customerDao.findById(customerDto.getId())
                .orElseThrow(() -> new BankingException("Customer not found"));

        // Check if email is being changed
        if (!customer.getEmail().equals(customerDto.getEmail()) &&
                customerDao.existsByEmail(customerDto.getEmail())) {
            throw new BankingException("Email already exists");
        }

        // Update fields
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setAddress(customerDto.getAddress());
        customer.setCity(customerDto.getCity());
        customer.setPostalCode(customerDto.getPostalCode());

        customer = customerDao.update(customer);

        return convertToDto(customer);
    }

    @Override
    public CustomerDto getCustomer(Long customerId) throws BankingException {
        logger.debug("Getting customer: {}", customerId);

        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new BankingException("Customer not found"));

        return convertToDto(customer);
    }

    @Override
    public CustomerDto getCustomerByNic(String nicNumber) throws BankingException {
        logger.debug("Getting customer by NIC: {}", nicNumber);

        Customer customer = customerDao.findByNic(nicNumber)
                .orElseThrow(() -> new BankingException("Customer not found"));

        return convertToDto(customer);
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) throws BankingException {
        logger.debug("Getting customer by email: {}", email);

        Customer customer = customerDao.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        return convertToDto(customer);
    }

    @Override
    public List<CustomerDto> getAllActiveCustomers() {
        logger.debug("Getting all active customers");

        return customerDao.findActiveCustomers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDto> searchCustomers(String searchTerm) {
        logger.debug("Searching customers with term: {}", searchTerm);

        return customerDao.searchCustomers(searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @RolesAllowed(SecurityConstants.ROLE_MANAGER)
    public void activateCustomer(Long customerId) throws BankingException {
        logger.info("Activating customer: {}", customerId);

        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new BankingException("Customer not found"));

        customer.setIsActive(true);
        customerDao.update(customer);
    }

    @Override
    @RolesAllowed(SecurityConstants.ROLE_MANAGER)
    public void deactivateCustomer(Long customerId) throws BankingException {
        logger.info("Deactivating customer: {}", customerId);

        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new BankingException("Customer not found"));

        customer.setIsActive(false);
        customerDao.update(customer);
    }

    @Override
    public String generateCustomerNumber() {
        long timestamp = System.currentTimeMillis() % 100000000L;
        int random = ThreadLocalRandom.current().nextInt(100, 999);
        return String.format(BankingConstants.CUSTOMER_NUMBER_FORMAT,
                timestamp * 1000 + random);
    }

    @Override
    public boolean isNicAvailable(String nicNumber) {
        return !customerDao.existsByNic(nicNumber);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !customerDao.existsByEmail(email);
    }

    private void validateCustomerData(CustomerDto customerDto) throws BankingException {
        if (!customerDto.getEmail().matches(BankingConstants.EMAIL_PATTERN)) {
            throw new BankingException("Invalid email format");
        }

        if (!customerDto.getPhoneNumber().matches(BankingConstants.PHONE_PATTERN)) {
            throw new BankingException("Invalid phone number format");
        }

        if (!customerDto.getNicNumber().matches(BankingConstants.NIC_PATTERN)) {
            throw new BankingException("Invalid NIC number format");
        }
    }

    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setNicNumber(customer.getNicNumber());
        dto.setDateOfBirth(customer.getDateOfBirth());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        dto.setCity(customer.getCity());
        dto.setPostalCode(customer.getPostalCode());
        dto.setIsActive(customer.getIsActive());
        dto.setCreatedDate(customer.getCreatedDate());
        return dto;
    }
}