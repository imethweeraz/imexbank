package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.common.dto.CustomerDto;
import com.imeth.imexbank.common.exceptions.BankingException;
import com.imeth.imexbank.services.interfaces.CustomerService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "CustomerServlet", urlPatterns = {"/customer/*"})
public class CustomerServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServlet.class);

    @EJB
    private CustomerService customerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleListCustomers(request, response);
        } else if (pathInfo.equals("/create")) {
            request.getRequestDispatcher("/customers/create-customer.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            handleViewCustomer(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            handleEditCustomer(request, response);
        } else if (pathInfo.equals("/search")) {
            handleSearchCustomers(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (pathInfo) {
            case "/create":
                handleCreateCustomer(request, response);
                break;
            case "/update":
                handleUpdateCustomer(request, response);
                break;
            case "/checkAvailability":
                handleCheckAvailability(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleListCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<CustomerDto> customers = customerService.getAllActiveCustomers();
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("/customers/customer-list.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error listing customers", e);
            request.setAttribute("error", "Error loading customers
                    request.setAttribute("error", "Error loading customers");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleViewCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pathInfo = request.getPathInfo();
            Long customerId = Long.parseLong(pathInfo.substring("/view/".length()));

            CustomerDto customer = customerService.getCustomer(customerId);
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/customers/customer-details.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error viewing customer", e);
            request.setAttribute("error", "Customer not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleEditCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pathInfo = request.getPathInfo();
            Long customerId = Long.parseLong(pathInfo.substring("/edit/".length()));

            CustomerDto customer = customerService.getCustomer(customerId);
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/customers/update-customer.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading customer for edit", e);
            request.setAttribute("error", "Customer not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setFirstName(request.getParameter("firstName"));
            customerDto.setLastName(request.getParameter("lastName"));
            customerDto.setNicNumber(request.getParameter("nicNumber"));
            customerDto.setEmail(request.getParameter("email"));
            customerDto.setPhoneNumber(request.getParameter("phoneNumber"));
            customerDto.setAddress(request.getParameter("address"));
            customerDto.setCity(request.getParameter("city"));
            customerDto.setPostalCode(request.getParameter("postalCode"));

            // Parse date of birth
            String dobStr = request.getParameter("dateOfBirth");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            customerDto.setDateOfBirth(LocalDate.parse(dobStr, formatter));

            CustomerDto createdCustomer = customerService.createCustomer(customerDto);

            request.setAttribute("success", "Customer created successfully");
            request.setAttribute("customer", createdCustomer);
            request.getRequestDispatcher("/customers/customer-details.jsp").forward(request, response);

        } catch (BankingException e) {
            logger.error("Error creating customer", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/customers/create-customer.jsp").forward(request, response);
        }
    }

    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long customerId = Long.parseLong(request.getParameter("customerId"));

            CustomerDto customerDto = customerService.getCustomer(customerId);
            customerDto.setFirstName(request.getParameter("firstName"));
            customerDto.setLastName(request.getParameter("lastName"));
            customerDto.setEmail(request.getParameter("email"));
            customerDto.setPhoneNumber(request.getParameter("phoneNumber"));
            customerDto.setAddress(request.getParameter("address"));
            customerDto.setCity(request.getParameter("city"));
            customerDto.setPostalCode(request.getParameter("postalCode"));

            CustomerDto updatedCustomer = customerService.updateCustomer(customerDto);

            request.setAttribute("success", "Customer updated successfully");
            request.setAttribute("customer", updatedCustomer);
            request.getRequestDispatcher("/customers/customer-details.jsp").forward(request, response);

        } catch (BankingException e) {
            logger.error("Error updating customer", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("customerId", request.getParameter("customerId"));
            request.getRequestDispatcher("/customers/update-customer.jsp").forward(request, response);
        }
    }

    private void handleSearchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String searchTerm = request.getParameter("searchTerm");
            List<CustomerDto> customers = customerService.searchCustomers(searchTerm);

            request.setAttribute("customers", customers);
            request.setAttribute("searchTerm", searchTerm);
            request.getRequestDispatcher("/customers/customer-search.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error searching customers", e);
            request.setAttribute("error", "Error searching customers");
            request.getRequestDispatcher("/customers/customer-search.jsp").forward(request, response);
        }
    }

    private void handleCheckAvailability(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String value = request.getParameter("value");

        boolean available = false;

        if ("nic".equals(type)) {
            available = customerService.isNicAvailable(value);
        } else if ("email".equals(type)) {
            available = customerService.isEmailAvailable(value);
        }

        response.setContentType("application/json");
        response.getWriter().write("{\"available\": " + available + "}");
    }
}