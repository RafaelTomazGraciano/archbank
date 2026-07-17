package com.graciano.archbank.auth.dto;

public record TokenResponse(
        String token,
        String name,
        String email
) {
}
