package com.graciano.archbank.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "CPF is required")
        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
        String cpf,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Invalid phone format")
        String phone,
        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]).{8,20}$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 8 characters long"
        )
                @Schema(example = "MyP@ssw0rd123")
        String password,
        @NotBlank(message = "Transaction PIN is required")
        @Pattern(regexp = "\\d{4,6}", message = "PIN must contain only digits, between 4 and 6 numbers")
        String transactionPin
) {
}
