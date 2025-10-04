package com.gastos.gerenciadorfinanceiro.dto;

import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateTransactionDTO(
        @NotBlank String description,
        @NotNull BigDecimal amount,
        @NotNull TransactionType type,
        @NotNull RecurrenceType recurrenceType
) {}