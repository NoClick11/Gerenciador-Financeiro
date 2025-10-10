package com.gastos.gerenciadorfinanceiro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.TransactionType;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import com.gastos.gerenciadorfinanceiro.repository.UserRepository;
import com.gastos.gerenciadorfinanceiro.service.JwtService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Deve retornar 200 OK e uma lista vazia para um novo usuário sem transações")
    void getAllTransactions_whenUserHasNoTransactions_shouldReturnEmptyList() throws Exception {
        String username = "username";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]")); //
    }

    @Test
    @DisplayName("Deve retornar 200 OK e uma lista de transações quando o usuário possui dados")
    void getAllTransactions_whenUserHasTransactions_shouldReturnTransactionList() throws Exception {
        String username = "username";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        Transaction transaction = new Transaction();
        Transaction transaction1 = new Transaction();

        transaction.setUser(user);
        transaction1.setUser(user);

        transaction.setDescription("Salário");

        BigDecimal amount = new BigDecimal("1500.00");

        transaction1.setAmount(amount);

        transactionRepository.saveAll(List.of(transaction, transaction1));

        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].description").value("Salário"))
                .andExpect(jsonPath("$[1].amount").value(1500.00));
    }

    @Test
    @DisplayName("Deve criar uma nova transação e retornar 201 Created")
    void createTransaction_whenPayloadIsValid_shouldCreateAndReturnTransaction() throws Exception {
        String username = "username";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        BigDecimal amount = new BigDecimal("1500.00");

        CreateTransactionDTO dto = new CreateTransactionDTO("Carro", amount, TransactionType.INCOME, RecurrenceType.ONE_TIME);

        String jsonPayload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Carro"));
    }

    @Test
    @DisplayName("Deve deletar uma transação existente e retornar 204 No Content")
    void deleteTransaction_whenTransactionExistsAndUserIsOwner_shouldReturnNoContent() throws Exception {
        User user = new User();

        user.setUsername("username");
        user.setPassword("password");

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        Transaction transaction = new Transaction();

        transaction.setUser(user);
        transaction.setDescription("Conta de Luz");
        transaction.setAmount(new BigDecimal("150.00"));

        Transaction savedTransaction = transactionRepository.save(transaction);
        Long savedTransactionId = savedTransaction.getId();

        mockMvc.perform(delete("/api/transactions/{id}", savedTransactionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertFalse(transactionRepository.existsById(savedTransactionId));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar uma transação inexistente")
    void deleteTransaction_whenTransactionDoesNotExist_shouldReturnNotFound() throws Exception {
        User user = new User();

        user.setUsername("username");
        user.setPassword("password");

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        Long nonExistentId = 999L;

        mockMvc.perform(delete("/api/transactions/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar deletar transação de outro usuário")
    void deleteTransaction_whenUserIsNotOwner_shouldReturnForbidden() throws Exception {
        User ownerUser = new User();
        User attackerUser = new User();

        userRepository.saveAll(List.of(ownerUser, attackerUser));

        Transaction transaction = new Transaction();

        transaction.setUser(ownerUser);

        Transaction savedTransaction = transactionRepository.save(transaction);
        Long savedTransactionId = savedTransaction.getId();

        String token = jwtService.generateToken(attackerUser);

        mockMvc.perform(delete("/api/transactions/{id}", savedTransactionId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e as transações do mês especificado")
    void getTransactionsByMonth_whenMonthIsSpecified_shouldReturnFilteredTransactionList() throws Exception {
        User user = new User();

        user.setUsername("username");
        user.setPassword("password");

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        Transaction transaction3 = new Transaction();

        transaction1.setDate(LocalDate.of(2025, 10, 15));
        transaction2.setDate(LocalDate.of(2025, 10, 20));
        transaction3.setDate(LocalDate.of(2025, 9, 5));

        transaction1.setUser(user);
        transaction2.setUser(user);
        transaction3.setUser(user);

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));

        mockMvc.perform(get("/api/transactions/by-month")
                        .param("year", "2025")
                        .param("month", "10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}
