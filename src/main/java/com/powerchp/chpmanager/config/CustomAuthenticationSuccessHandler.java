package com.powerchp.chpmanager.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isManager = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"));
        boolean isOperator = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPERATOR"));

        if (isAdmin || isManager) {
            response.sendRedirect(request.getContextPath() + "/manager/dashboard");
        } else if (isOperator) {
            response.sendRedirect(request.getContextPath() + "/operator/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
