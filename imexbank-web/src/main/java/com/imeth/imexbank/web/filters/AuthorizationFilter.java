package com.imeth.imexbank.web.filters;

import com.imeth.imexbank.common.constants.SecurityConstants;
import com.imeth.imexbank.common.enums.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/*"})
public class AuthorizationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    private static final Map<String, UserRole[]> PATH_ROLES = new HashMap<>();

    static {
        // Admin paths
        PATH_ROLES.put("/admin/", new UserRole[]{UserRole.ADMIN});

        // Manager paths
        PATH_ROLES.put("/report/", new UserRole[]{UserRole.MANAGER, UserRole.ADMIN});
        PATH_ROLES.put("/customer/create", new UserRole[]{UserRole.MANAGER, UserRole.TELLER});
        PATH_ROLES.put("/account/create", new UserRole[]{UserRole.MANAGER, UserRole.TELLER});

        // Teller paths
        PATH_ROLES.put("/transaction/", new UserRole[]{UserRole.TELLER, UserRole.MANAGER, UserRole.CUSTOMER});
        PATH_ROLES.put("/customer/", new UserRole[]{UserRole.TELLER, UserRole.MANAGER});

        // Customer paths
        PATH_ROLES.put("/account/balance", new UserRole[]{UserRole.CUSTOMER, UserRole.TELLER, UserRole.MANAGER});
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Authorization filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Get user roles from session
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            chain.doFilter(request, response);
            return;
        }

        Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute(SecurityConstants.USER_ROLES_KEY);
        if (userRoles == null || userRoles.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // Check authorization
        if (!isAuthorized(path, userRoles)) {
            String username = (String) session.getAttribute(SecurityConstants.USER_SESSION_KEY);
            logger.warn("Unauthorized access attempt by user {} to path: {}", username, path);

            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You don't have permission to access this resource");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("Authorization filter destroyed");
    }

    private boolean isAuthorized(String path, Set<UserRole> userRoles) {
        for (Map.Entry<String, UserRole[]> entry : PATH_ROLES.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                UserRole[] requiredRoles = entry.getValue();
                for (UserRole requiredRole : requiredRoles) {
                    if (userRoles.contains(requiredRole)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true; // Allow access if no specific role requirement
    }
}