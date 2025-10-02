package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<String> getAllTransactions() {
        transactionRepository.findAll();
        return ResponseEntity.ok("");
    }


}
