package com.hospital.appointment.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN);

        response.setContentType(
                "application/json");

        String jsonResponse =
                String.format(
                        """
                        {
                          "status": 403,
                          "error": "FORBIDDEN",
                          "message": "Access Denied, You are not authorized to accesss this",
                          "path": "%s"
                        }
                        """,
                        request.getRequestURI());

        response.getWriter().write(jsonResponse);
    }
}