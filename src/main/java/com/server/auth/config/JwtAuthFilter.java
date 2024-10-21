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

        // Skip the filter for public endpoints
        String requestURI = request.getRequestURI();

        // Public endpoints to skip JWT authentication
        if (requestURI.equals("/api/auth/signup") || requestURI.equals("/api/auth/login") || requestURI.equals("/authentication-docs")) {
            System.out.println("requestURI : "+requestURI);
            System.out.println("Public endpoints to skip JWT authentication");
            filterChain.doFilter(request, response); // Just pass the request down the filter chain
            return;
        }

        try {
            System.out.println("here...");
            String authHeader = request.getHeader("Authorization");

            // handling public key
            String publicKeyString = request.getHeader("publicKey");
            SignatureConvertor signatureConvertor = new SignatureConvertor();
            PublicKey publicKey = signatureConvertor.getPublicKeyFromString(publicKeyString);

            String token = null;
            String username = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = JwtHelper.extractUsername(token, publicKey);
            }

            if (token == null) {
                System.out.println("token == null");
                filterChain.doFilter(request, response);
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("username != null");
                UserDetails userDetails = userDetailService.loadUserByUsername(username);

                if (JwtHelper.validateToken(token, userDetails, publicKey)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (IOException e) {
            ApiErrorResponseDto errorResponse = new ApiErrorResponseDto(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(toJson(errorResponse));
        } catch (Exception e) {
            throw new RuntimeException(e);
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
