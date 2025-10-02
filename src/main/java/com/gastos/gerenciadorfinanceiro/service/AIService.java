package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";

    public AIService(RestTemplate restTemplate, @Value("${google.gemini.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String getSpendingSuggestions(List<Transaction> transactions) {
        return "Implementação pendente";
    }
}
