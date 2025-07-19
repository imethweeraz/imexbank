package com.imeth.imexbank.web.servlets;

import com.imeth.imexbank.common.dto.TransactionDto;
import com.imeth.imexbank.common.dto.TransferRequestDto;
import com.imeth.imexbank.common.enums.TransactionStatus;
import com.imeth.imexbank.common.enums.TransactionType;
import com.imeth.imexbank.common.exceptions.BankingException;
import com.imeth.imexbank.services.interfaces.TransactionService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "TransactionServlet", urlPatterns = {"/transaction/*"})
public class TransactionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServlet.class);

    @EJB
    private TransactionService transactionService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleListTransactions(request, response);
        } else if (pathInfo.equals("/transfer")) {
            request.getRequestDispatcher("/transactions/fund-transfer.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            handleViewTransaction(request, response);
        } else if (pathInfo.equals("/history")) {
            handleTransactionHistory(request, response);
        } else if (pathInfo.equals("/scheduled")) {
            handleScheduledTransactions(request, response);
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
            case "/transfer":
                handleTransfer(request, response);
                break;
            case "/schedule":
                handleScheduleTransfer(request, response);
                break;
            case "/cancel":
                handleCancelTransaction(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleListTransactions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String accountIdParam = request.getParameter("accountId");
            String statusParam = request.getParameter("status");
            String typeParam = request.getParameter("type");

            List<TransactionDto> transactions;

            if (accountIdParam != null) {
                Long accountId = Long.parseLong(accountIdParam);
                transactions = transactionService.getAccountTransactions(accountId);
            } else if (statusParam != null) {
                TransactionStatus status = TransactionStatus.valueOf(statusParam);
                transactions = transactionService.getTransactionsByStatus(status);
            } else {
                // Get recent transactions
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime startDate = endDate.minusDays(30);
                transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            }

            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("/transactions/transaction-list.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error listing transactions", e);
            request.setAttribute("error", "Error loading transactions");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleViewTransaction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pathInfo = request.getPathInfo();
            Long transactionId = Long.parseLong(pathInfo.substring("/view/".length()));

            TransactionDto transaction = transactionService.getTransaction(transactionId);
            request.setAttribute("transaction", transaction);
            request.getRequestDispatcher("/transactions/transaction-details.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error viewing transaction", e);
            request.setAttribute("error", "Transaction not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleTransfer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            TransferRequestDto transferRequest = new TransferRequestDto();
            transferRequest.setSourceAccountNumber(request.getParameter("sourceAccount"));
            transferRequest.setTargetAccountNumber(request.getParameter("targetAccount"));
            transferRequest.setAmount(new BigDecimal(request.getParameter("amount")));
            transferRequest.setDescription(request.getParameter("description"));
            transferRequest.setPin(request.getParameter("pin"));

            TransactionDto transaction = transactionService.processTransfer(transferRequest);

            request.setAttribute("success", "Transfer completed successfully");
            request.setAttribute("transaction", transaction);
            request.getRequestDispatcher("/transactions/transfer-success.jsp")
                    .forward(request, response);

        } catch (BankingException e) {
            logger.error("Error processing transfer", e);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/transactions/fund-transfer.jsp")
                    .forward(request, response);
        }
    }

    private void handleScheduleTransfer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            TransferRequestDto transferRequest = new TransferRequestDto();
            transferRequest.setSourceAccountNumber(request.getParameter("sourceAccount"));
            transferRequest.setTargetAccountNumber(request.getParameter("targetAccount"));
            transferRequest.setAmount(new BigDecimal(request.getParameter("amount")));
            transferRequest.setDescription(request.getParameter("description"));

            // Parse scheduled date time
            String scheduledDateTime = request.getParameter("scheduledDateTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            transferRequest.setScheduledExecutionTime(LocalDateTime.parse(scheduledDateTime, formatter));

            TransactionDto transaction = transactionService.scheduleTransfer(transferRequest);

            request.setAttribute("success", "Transfer scheduled successfully");
            request.setAttribute("transaction", transaction);
            request.getRequestDispatcher("/transactions/scheduled-success.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error scheduling transfer", e);
            request.setAttribute("error", "Error scheduling transfer: " + e.getMessage());
            request.getRequestDispatcher("/transactions/fund-transfer.jsp")
                    .forward(request, response);
        }
    }

    private void handleTransactionHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String accountIdParam = request.getParameter("accountId");
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");

            LocalDateTime startDate = startDateParam != null ?
                    LocalDateTime.parse(startDateParam + "T00:00:00") :
                    LocalDateTime.now().minusMonths(1);

            LocalDateTime endDate = endDateParam != null ?
                    LocalDateTime.parse(endDateParam + "T23:59:59") :
                    LocalDateTime.now();

            List<TransactionDto> transactions;

            if (accountIdParam != null) {
                Long accountId = Long.parseLong(accountIdParam);
                transactions = transactionService.searchTransactions(
                        accountId, null, null, startDate, endDate);
            } else {
                transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            }

            request.setAttribute("transactions", transactions);
            request.setAttribute("startDate", startDate.toLocalDate());
            request.setAttribute("endDate", endDate.toLocalDate());
            request.getRequestDispatcher("/transactions/transaction-history.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading transaction history", e);
            request.setAttribute("error", "Error loading transaction history");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleScheduledTransactions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<TransactionDto> scheduledTransactions =
                    transactionService.getTransactionsByStatus(TransactionStatus.SCHEDULED);

            request.setAttribute("transactions", scheduledTransactions);
            request.getRequestDispatcher("/transactions/scheduled-transactions.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading scheduled transactions", e);
            request.setAttribute("error", "Error loading scheduled transactions");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleCancelTransaction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long transactionId = Long.parseLong(request.getParameter("transactionId"));
            transactionService.cancelTransaction(transactionId);

            response.sendRedirect(request.getContextPath() +
                    "/transaction/scheduled?success=cancelled");

        } catch (Exception e) {
            logger.error("Error cancelling transaction", e);
            response.sendRedirect(request.getContextPath() +
                    "/transaction/scheduled?error=" + e.getMessage());
        }
    }
}