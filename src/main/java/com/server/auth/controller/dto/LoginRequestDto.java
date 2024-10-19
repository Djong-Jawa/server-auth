package com.server.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record LoginRequestDto(
        @Schema(description = "email", example = "rizki@gmail.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email")
        String email,

        @Schema(description = "password", example = "123123")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password
) {
}
