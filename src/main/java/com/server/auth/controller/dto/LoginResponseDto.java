package com.server.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDto(
        @Schema(description = "email")
        String email,

        @Schema(description = "Jwt Token")
        String token
) {
}
