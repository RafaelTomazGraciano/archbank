package com.graciano.archbank.pix.dto;

import com.graciano.archbank.pix.enums.PixKeyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PixKeyRequest(
        @NotNull(message = "Type of pix key is required")
        PixKeyType keyType,
        @Size(min = 6, message = "Pix key value must have at least 6 characters")
        String keyValue
) {
}
