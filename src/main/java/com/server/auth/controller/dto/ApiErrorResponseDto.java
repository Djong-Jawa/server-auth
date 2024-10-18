package com.server.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiErrorResponseDto (
        @Schema(description = "Error code")
        int errorCode,

        @Schema(description = "Error description")
        String description
) {
}
