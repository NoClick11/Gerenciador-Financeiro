package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.TransactionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Você é um assistente financeiro. Com base na seguinte lista de transações (entradas e saídas), forneça 3 sugestões curtas e práticas para que o usuário possa melhorar seus gastos ou economizar dinheiro. Responda em português do Brasil.\n\n");
        promptBuilder.append("Lista de Transações:\n");
        for (Transaction t : transactions) {
            String type = t.getType().equals(TransactionType.INCOME) ? "ENTRADA" : "SAÍDA";
            promptBuilder.append(String.format("- %s: R$ %.2f - %s\n", type, t.getAmount(), t.getDescription()));
        }
        promptBuilder.append("\nSugestões:");

        String prompt = promptBuilder.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(geminiUrl + apiKey, entity, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> contentResponse = (Map<String, Object>) firstCandidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResponse.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "Não foi possível gerar sugestões no momento.";
        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Gemini: " + e.getMessage());
            return "Erro de comunicação com o serviço de IA.";
        }
    }
}
