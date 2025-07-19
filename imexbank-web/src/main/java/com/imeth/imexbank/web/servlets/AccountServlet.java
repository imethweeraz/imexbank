// ... (existing imports and class definition)

@WebServlet(name = "AccountServlet", urlPatterns = {"/account/*"})
public class AccountServlet extends HttpServlet {

    @EJB
    private AccountService accountService;

    // ... (existing doGet method)

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            handleCreateAccount(request, response);
        } else {
            // Handle other POST actions like update
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
        }
    }

    private void handleCreateAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long customerId = Long.parseLong(request.getParameter("customerId"));
            String accountTypeStr = request.getParameter("accountType");
            String balanceStr = request.getParameter("initialBalance");

            AccountDto accountDto = new AccountDto();
            accountDto.setCustomerId(customerId);
            accountDto.setAccountType(com.imeth.imexbank.common.enums.AccountType.valueOf(accountTypeStr));
            if (balanceStr != null && !balanceStr.isEmpty()) {
                accountDto.setBalance(new java.math.BigDecimal(balanceStr));
            }

            AccountDto createdAccount = accountService.createAccount(accountDto);
            request.setAttribute("success", "Account created successfully: " + createdAccount.getAccountNumber());
            request.getRequestDispatcher("/accounts/account-details.jsp?accountId=" + createdAccount.getId()).forward(request, response);

        } catch (Exception e) {
            logger.error("Error creating account", e);
            request.setAttribute("error", "Account creation failed: " + e.getMessage());
            request.getRequestDispatcher("/accounts/create-account.jsp").forward(request, response);
        }
    }
}