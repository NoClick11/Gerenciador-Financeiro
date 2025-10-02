package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import com.gastos.gerenciadorfinanceiro.service.AIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AIService aiService;

    public TransactionController(TransactionRepository transactionRepository, AIService aiService) {
        this.transactionRepository = transactionRepository;
        this.aiService = aiService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> all = transactionRepository.findAll();
        return ResponseEntity.ok(all);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(savedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody CreateTransactionDTO dto) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction existingTransaction = optionalTransaction.get();

        existingTransaction.setDescription(dto.description());
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setType(dto.type());

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);

        return ResponseEntity.ok(updatedTransaction);

    }

    @GetMapping("/suggestions")
    public ResponseEntity<String> getSpendingSuggestions() {
        List<Transaction> all = transactionRepository.findAll();
        String suggestions = aiService.getSpendingSuggestions(all);
        return ResponseEntity.ok(suggestions);
    }
}
