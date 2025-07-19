package com.imeth.imexbank.web.utils;

import jakarta.servlet.http.HttpServletRequest;

public class WebUtils {
    // Utility class for common web-related tasks.

    public static String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}