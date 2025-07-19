package com.imeth.imexbank.web.filters;

import com.imeth.imexbank.common.constants.SecurityConstants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final List<String> PUBLIC_PATHS = Arrays.asList("/login", "/css/", "/js/", "/images/");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow access to public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        boolean isAuthenticated = (session != null && session.getAttribute(SecurityConstants.USER_SESSION_KEY) != null);

        if (isAuthenticated) {
            // User is logged in, continue to the requested page
            chain.doFilter(request, response);
        } else {
            // User is not logged in, redirect to the login page
            logger.warn("Unauthenticated access attempt to protected path: {}", path);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized.");
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed.");
    }
}