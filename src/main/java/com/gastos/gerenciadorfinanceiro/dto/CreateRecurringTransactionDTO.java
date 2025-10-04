package com.gastos.gerenciadorfinanceiro.dto;

import com.gastos.gerenciadorfinanceiro.model.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateRecurringTransactionDTO(
        @NotBlank String description,
        @NotNull BigDecimal amount,
        @NotNull @Min(1) @Max(31) int dayOfMonth,
        @NotNull TransactionType transactionType
) {}