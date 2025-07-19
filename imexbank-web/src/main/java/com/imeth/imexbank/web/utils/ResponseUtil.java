package com.imeth.imexbank.web.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {

    /**
     * Writes a JSON response to the client.
     * @param response The HttpServletResponse object.
     * @param json The JSON string to be written.
     * @throws IOException If an input or output exception occurs.
     */
    public static void sendJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }
}