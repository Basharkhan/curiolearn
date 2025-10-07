package com.example.curiolearn.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtService.extractToken(request);

        try {
            if (token != null) {
                if (!jwtService.validateToken(token)) {
                    throw new BadCredentialsException("Invalid or expired token");
                }
                Authentication auth = jwtService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // Continue the filter chain
            filterChain.doFilter(request, response);

        } catch (JwtException | AuthenticationException ex) {
            // Clear security context
            SecurityContextHolder.clearContext();

            // Wrap non-AuthenticationException in BadCredentialsException
            AuthenticationException authEx = ex instanceof AuthenticationException
                    ? (AuthenticationException) ex
                    : new BadCredentialsException("Invalid or expired token", ex);

            // Delegate to custom entry point for consistent JSON response
            authenticationEntryPoint.commence(request, response, authEx);
        }
    }
}
