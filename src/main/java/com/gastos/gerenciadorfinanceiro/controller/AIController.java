package com.gastos.gerenciadorfinanceiro.controller;

import com.gastos.gerenciadorfinanceiro.model.AISuggestion;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.AISuggestionRepository;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import com.gastos.gerenciadorfinanceiro.service.AIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;
    private final AISuggestionRepository aiSuggestionRepository;
    private final TransactionRepository transactionRepository;

    public AIController(AIService aiService, AISuggestionRepository aiSuggestionRepository, TransactionRepository transactionRepository) {
        this.aiService = aiService;
        this.aiSuggestionRepository = aiSuggestionRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/suggestions")
    public ResponseEntity<AISuggestion> getLatestSuggestion(@AuthenticationPrincipal User user) {
        return aiSuggestionRepository.findTopByUserOrderByCreatedAtDesc(user)
                .map(suggestion -> ResponseEntity.ok(suggestion))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/suggestions")
    public ResponseEntity<AISuggestion> generateNewSuggestion(@AuthenticationPrincipal User user) {
        aiSuggestionRepository.findTopByUserOrderByCreatedAtDesc(user)
                .ifPresent(aiSuggestionRepository::delete);
        List<Transaction> transactions = transactionRepository.findAllByUser(user);

        if (transactions.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String suggestionText = aiService.getSpendingSuggestions(transactions);

        AISuggestion newSuggestion = new AISuggestion();
        newSuggestion.setUser(user);
        newSuggestion.setSuggestionText(suggestionText);

        AISuggestion savedSuggestion = aiSuggestionRepository.save(newSuggestion);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSuggestion);
    }
}