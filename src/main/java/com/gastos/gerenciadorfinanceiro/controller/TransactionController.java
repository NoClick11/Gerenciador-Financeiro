package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import com.gastos.gerenciadorfinanceiro.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@AuthenticationPrincipal User user) {
        List<Transaction> transactions = transactionService.findAllByUser(user);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<Transaction>> searchByMonth(
            @AuthenticationPrincipal User user,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        List<Transaction> transactions = transactionService.getTransactionsForMonth(user, year, month);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionDTO dto, @AuthenticationPrincipal User user) {
        Transaction savedTransaction = transactionService.createTransaction(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody CreateTransactionDTO dto, @AuthenticationPrincipal User user) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, dto, user);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel-recurrence")
    public ResponseEntity<Transaction> cancelRecurrence(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Transaction updatedTransaction = transactionService.cancelRecurrence(id, user);
        return ResponseEntity.ok(updatedTransaction);
    }

}