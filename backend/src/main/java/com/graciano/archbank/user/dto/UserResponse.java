package com.graciano.archbank.user.dto;

public record UserResponse(
        String name,
        String cpf,
        String email,
        String phone
) {
}
