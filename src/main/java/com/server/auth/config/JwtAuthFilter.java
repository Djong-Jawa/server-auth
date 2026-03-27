package com.server.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.ApiErrorResponseDto;
import com.server.auth.helper.JwtHelper;
import com.server.auth.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.PublicKey;

@Component
public class JwtAuthFilter extends OncePerRequestFilter  {

    private final UserDetailServiceImpl userDetailService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(UserDetailServiceImpl userDetailService, ObjectMapper objectMapper) {
        this.userDetailService = userDetailService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Skip the filter for public endpoints
        if (requestURI.equals("/api/auth/signup") || requestURI.equals("/api/auth/login") ||
                requestURI.equals("/authentication-docs") || requestURI.equals("/.well-known/jwks.json")) {
            System.out.println("Public endpoint: " + requestURI);
            filterChain.doFilter(request, response); // Just pass the request down the filter chain
            return;
        }

        try {
            System.out.println("Processing secured endpoint...");
            String authHeader = request.getHeader("Authorization");

            // No Bearer token → let Spring Security decide (returns 401 for protected endpoints)
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Only parse the public key when a Bearer token is actually present
            String publicKeyString = request.getHeader("publicKey");
            SignatureConvertor signatureConvertor = new SignatureConvertor();
            PublicKey publicKey = signatureConvertor.getPublicKeyFromString(publicKeyString);

            String token = authHeader.substring(7);
            String username = JwtHelper.extractUsername(token, publicKey);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(username);

                if (JwtHelper.validateToken(token, userDetails, publicKey)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (IOException e) {
            System.out.println("Message error in doFilterInternal - IOException: " + e.getMessage());
            ApiErrorResponseDto errorResponse = new ApiErrorResponseDto(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(toJson(errorResponse));
        } catch (Exception e) {
            System.out.println("Message error in doFilterInternal: " + e.getMessage());
            ApiErrorResponseDto errorResponse = new ApiErrorResponseDto(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(toJson(errorResponse));
        }
    }


    private String toJson(ApiErrorResponseDto response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "";
        }
    }
}
