package com.gastos.gerenciadorfinanceiro.dto;

import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.TransactionType;

import java.math.BigDecimal;

public record CreateTransactionDTO(String description, BigDecimal amount, TransactionType type, RecurrenceType recurrenceType) {

}
