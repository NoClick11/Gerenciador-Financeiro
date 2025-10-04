package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.dto.CreateRecurringTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.RecurringTransaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.RecurringTransactionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recurring-transactions")
public class RecurringTransactionController {

    private final RecurringTransactionRepository recurringTransactionRepository;

    public RecurringTransactionController(RecurringTransactionRepository recurringTransactionRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransaction>> getAll(@AuthenticationPrincipal User user) {
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAllByUser(user);
        return ResponseEntity.ok(recurringTransactions);
    }

    @PostMapping
    public ResponseEntity<RecurringTransaction> createRecurringTransaction(@Valid @RequestBody CreateRecurringTransactionDTO dto, @AuthenticationPrincipal User user) {
        RecurringTransaction recurringTransaction = new RecurringTransaction();
        recurringTransaction.setTransactionType(dto.transactionType());
        recurringTransaction.setAmount(dto.amount());
        recurringTransaction.setDescription(dto.description());
        recurringTransaction.setDayOfMonth(dto.dayOfMonth());
        recurringTransaction.setUser(user);

        RecurringTransaction savedRecurringTransaction = recurringTransactionRepository.save(recurringTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecurringTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Optional<RecurringTransaction> optionalRecurringTransaction = recurringTransactionRepository.findById(id);

        if (optionalRecurringTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RecurringTransaction recurringTransaction = optionalRecurringTransaction.get();

        if (!recurringTransaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        recurringTransactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
