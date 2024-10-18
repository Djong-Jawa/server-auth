package com.server.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.ApiErrorResponseDto;
import com.server.auth.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter  {

    private final UserDetailServiceImpl userDetailService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(UserDetailServiceImpl userDetailService, ObjectMapper objectMapper) {
        this.userDetailService = userDetailService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        throw ServletException,IOException {
            try {

                String authHeader = request.getHeader("Authorization");

                String token = null;
                String username = null;

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    username = JwtHelper
                }

            } catch (AccessDeniedException e) {
                ApiErrorResponseDto errorResponse = new ApiErrorResponseDto(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(toJson(errorResponse));
            }
        }
    }

    private String toJson(ApiErrorResponseDto response) {
        try {
            return objectMapper.writeValueAsString(response)
        } catch (Exception e) {
            return "";
        }
    }
}
