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

import java.time.LocalDate;
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
        return ResponseEntity.ok(transactionService.getTransactionsForMonth(user, year, month));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionDTO dto, @AuthenticationPrincipal User user) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setDate(LocalDate.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionRepository.save(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        transactionRepository.delete(transaction);
        return ResponseEntity.noContent().build();
    }

}