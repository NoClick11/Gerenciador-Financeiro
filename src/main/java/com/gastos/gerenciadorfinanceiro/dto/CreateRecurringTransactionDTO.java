package com.gastos.gerenciadorfinanceiro.dto;

import com.gastos.gerenciadorfinanceiro.model.TransactionType;

import java.math.BigDecimal;

public record CreateRecurringTransactionDTO(String description, BigDecimal amount, int dayOfMonth, TransactionType transactionType) {
}
