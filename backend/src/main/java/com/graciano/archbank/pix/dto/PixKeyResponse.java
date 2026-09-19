package com.graciano.archbank.pix.dto;

import com.graciano.archbank.pix.enums.PixKeyType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PixKeyResponse(
        UUID id,
        PixKeyType keyType,
        String keyValue,
        LocalDateTime createdAt
) {
}
