package com.example.monicio.Config.JWT;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The type Rest auth entry point for exception handling.
 *
 * @author Nikita Zhiznevskiy
 * @see com.example.monicio.Config.SecurityConfig
 */
@Component
public class RestAuthEntryPoint implements AuthenticationEntryPoint {
    /**
     * Commence.
     *
     * @param request       the request
     * @param response      the response
     * @param authException the auth exception
     * @throws IOException      the io exception
     * @throws ServletException the servlet exception
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}