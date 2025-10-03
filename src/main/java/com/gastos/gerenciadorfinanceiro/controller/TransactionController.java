package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@AuthenticationPrincipal User user) {
        List<Transaction> transactions = transactionRepository.findAllByUser(user);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionDTO dto, @AuthenticationPrincipal User user) {
        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody CreateTransactionDTO dto, @AuthenticationPrincipal User user) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction existingTransaction = optionalTransaction.get();

        if (!existingTransaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingTransaction.setDescription(dto.description());
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setType(dto.type());

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        // 1. Busca a transação pelo ID
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        // 2. Verifica se a transação existe
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = optionalTransaction.get();
        if (!transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Retorna 403 Proibido
        }

        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}