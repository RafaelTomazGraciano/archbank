package com.graciano.archbank.transaction.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawalRequest(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 14, fraction = 2, message = "Amount must have at most 14 integer digits and 2 decimal digits")
        BigDecimal amount,
        String description
) {
}
