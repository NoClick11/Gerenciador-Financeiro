package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.CreateRecurringTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.RecurringTransaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.RecurringTransactionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    private final RecurringTransactionRepository repository;

    public RecurringTransactionController(RecurringTransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransaction>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(repository.findAllByUser(user));
    }

    @PostMapping
    public ResponseEntity<RecurringTransaction> create(@Valid @RequestBody CreateRecurringTransactionDTO dto, @AuthenticationPrincipal User user) {
        RecurringTransaction recurring = new RecurringTransaction();
        recurring.setUser(user);
        recurring.setDescription(dto.description());
        recurring.setAmount(dto.amount());
        recurring.setDayOfMonth(dto.dayOfMonth());
        recurring.setTransactionType(dto.transactionType());

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(recurring));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        RecurringTransaction recurring = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!recurring.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        repository.delete(recurring);
        return ResponseEntity.noContent().build();
    }
}