package com.marketplace.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterRequest(
        @Schema(example = "Nguyen Van A")
        @NotBlank @Size(max = 100) String fullName,
        @Schema(example = "user@example.com")
        @NotBlank @Email @Size(max = 150) String email,
        @Schema(example = "0987654321")
        @NotBlank @Pattern(regexp = "^[0-9]{9,15}$") @Size(max = 15) String phone,
        @Schema(example = "12345678")
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
