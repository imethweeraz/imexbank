package com.imeth.imexbank.validators;

import com.imeth.imexbank.common.constants.BankingConstants;
import com.imeth.imexbank.common.dto.CustomerDto;
import jakarta.ejb.Stateless;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CustomerValidator {

    public List<String> validateCustomerCreation(CustomerDto customerDto) {
        List<String> errors = new ArrayList<>();

        if (customerDto.getFirstName() == null || customerDto.getFirstName().trim().isEmpty()) {
            errors.add("First name is required.");
        }
        if (customerDto.getLastName() == null || customerDto.getLastName().trim().isEmpty()) {
            errors.add("Last name is required.");
        }
        if (customerDto.getEmail() == null || customerDto.getEmail().trim().isEmpty()) {
            errors.add("Email is required.");
        } else if (!customerDto.getEmail().matches(BankingConstants.EMAIL_PATTERN)) {
            errors.add("Invalid email format.");
        }
        if (customerDto.getPhoneNumber() == null || customerDto.getPhoneNumber().trim().isEmpty()) {
            errors.add("Phone number is required.");
        } else if (!customerDto.getPhoneNumber().matches(BankingConstants.PHONE_PATTERN)) {
            errors.add("Invalid phone number format.");
        }
        if (customerDto.getNicNumber() == null || customerDto.getNicNumber().trim().isEmpty()) {
            errors.add("NIC number is required.");
        } else if (!customerDto.getNicNumber().matches(BankingConstants.NIC_PATTERN)) {
            errors.add("Invalid NIC number format.");
        }
        if (customerDto.getDateOfBirth() == null) {
            errors.add("Date of birth is required.");
        } else if (customerDto.getDateOfBirth().isAfter(LocalDate.now().minusYears(18))) {
            errors.add("Customer must be at least 18 years old.");
        }
        // Add more validations as needed (e.g., address, city, postal code)

        return errors;
    }

    public List<String> validateCustomerUpdate(CustomerDto customerDto) {
        List<String> errors = new ArrayList<>();

        if (customerDto.getId() == null) {
            errors.add("Customer ID is required for update.");
        }
        // Reuse creation validations, or add specific update validations
        errors.addAll(validateCustomerCreation(customerDto));

        return errors;
    }
}