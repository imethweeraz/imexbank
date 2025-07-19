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

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login", "/logout", "/css/", "/js/", "/images/", "/error"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Authentication filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Check if path is public
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        boolean isAuthenticated = session != null &&
                session.getAttribute(SecurityConstants.USER_SESSION_KEY) != null;

        if (!isAuthenticated) {
            logger.debug("Unauthenticated access attempt to: {}", path);

            // Save requested URL
            if (session != null) {
                session.setAttribute("requestedUrl",
                        httpRequest.getRequestURL().toString());
            }

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Check session timeout
        Long loginTime = (Long) session.getAttribute(SecurityConstants.LOGIN_TIME_KEY);
        if (loginTime != null) {
            long sessionDuration = System.currentTimeMillis() - loginTime;
            long maxDuration = SecurityConstants.SESSION_TIMEOUT_MINUTES * 60 * 1000;

            if (sessionDuration > maxDuration) {
                logger.info("Session timeout for user: {}",
                        session.getAttribute(SecurityConstants.USER_SESSION_KEY));
                session.invalidate();
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?timeout=true");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("Authentication filter destroyed");
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}