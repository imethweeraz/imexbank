package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.common.constants.SecurityConstants;
import com.imeth.imexbank.common.dto.AccountDto;
import com.imeth.imexbank.common.enums.AccountType;
import com.imeth.imexbank.common.exceptions.BankingException;
import com.imeth.imexbank.services.interfaces.AccountService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "AccountServlet", urlPatterns = {"/account/*"})
public class AccountServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AccountServlet.class);

    @EJB
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // List all accounts
            handleListAccounts(request, response);
        } else if (pathInfo.equals("/create")) {
            // Show create account form
            request.setAttribute("accountTypes", AccountType.values());
            request.getRequestDispatcher("/accounts/create-account.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // View account details
            handleViewAccount(request, response);
        } else if (pathInfo.startsWith("/balance/")) {
            // Check balance
            handleCheckBalance(request, response);
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
                handleCreateAccount(request, response);
                break;
            case "/deposit":
                handleDeposit(request, response);
                break;
            case "/withdraw":
                handleWithdraw(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleListAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get customer ID from request or session
            String customerIdParam = request.getParameter("customerId");
            List<AccountDto> accounts;

            if (customerIdParam != null) {
                Long customerId = Long.parseLong(customerIdParam);
                accounts = accountService.getCustomerAccounts(customerId);
            } else {
                // For customer role, get their own accounts
                // Implementation would depend on how customer ID is stored in session
                accounts = List.of(); // Placeholder
            }

            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/accounts/account-list.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error listing accounts", e);
            request.setAttribute("error", "Error loading accounts");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleViewAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pathInfo = request.getPathInfo();
            Long accountId = Long.parseLong(pathInfo.substring("/view/".length()));

            AccountDto account = accountService.getAccount(accountId);
            request.setAttribute("account", account);
            request.getRequestDispatcher("/accounts/account-details.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error viewing account", e);
            request.setAttribute("error", "Account not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleCheckBalance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pathInfo = request.getPathInfo();
            String accountNumber = pathInfo.substring("/balance/".length());

            BigDecimal balance = accountService.getBalance(accountNumber);

            // For AJAX requests
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write("{\"balance\": \"" + balance + "\"}");
            } else {
                request.setAttribute("accountNumber", accountNumber);
                request.setAttribute("balance", balance);
                request.getRequestDispatcher("/accounts/account-balance.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error checking balance", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleCreateAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get form parameters
            Long customerId = Long.parseLong(request.getParameter("customerId"));
            AccountType accountType = AccountType.valueOf(request.getParameter("accountType"));
            String initialDepositStr = request.getParameter("initialDeposit");

            AccountDto accountDto = new AccountDto();
            accountDto.setCustomerId(customerId);
            accountDto.setAccountType(accountType);

            if (initialDepositStr != null && !initialDepositStr.isEmpty()) {
                accountDto.setBalance(new BigDecimal(initialDepositStr));
            }

            AccountDto createdAccount = accountService.createAccount(accountDto);

            request.setAttribute("success", "Account created successfully: " +
                    createdAccount.getAccountNumber());
            request.setAttribute("account", createdAccount);
            request.getRequestDispatcher("/accounts/account-details.jsp").forward(request, response);

        } catch (BankingException e) {
            logger.error("Error creating account", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("accountTypes", AccountType.values());
            request.getRequestDispatcher("/accounts/create-account.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Unexpected error creating account", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDeposit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String accountNumber = request.getParameter("accountNumber");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));

            accountService.deposit(accountNumber, amount);

            response.sendRedirect(request.getContextPath() +
                    "/account/view/" + accountNumber + "?success=deposit");

        } catch (BankingException e) {
            logger.error("Error processing deposit", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/transactions/deposit.jsp").forward(request, response);
        }
    }

    private void handleWithdraw(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String accountNumber = request.getParameter("accountNumber");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));

            accountService.withdraw(accountNumber, amount);

            response.sendRedirect(request.getContextPath() +
                    "/account/view/" + accountNumber + "?success=withdrawal");

        } catch (BankingException e) {
            logger.error("Error processing withdrawal", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/transactions/withdrawal.jsp").forward(request, response);
        }
    }
}