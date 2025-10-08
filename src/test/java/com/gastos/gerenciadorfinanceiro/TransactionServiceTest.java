package com.gastos.gerenciadorfinanceiro;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.*;
import com.gastos.gerenciadorfinanceiro.repository.RecurringTransactionRepository;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import com.gastos.gerenciadorfinanceiro.service.TransactionService  ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.gastos.gerenciadorfinanceiro.model.RecurrenceType.ONE_TIME;
import static com.gastos.gerenciadorfinanceiro.model.TransactionType.INCOME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    TransactionService transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    RecurringTransactionRepository recurringTransactionRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Deve criar uma transação de entrada (INCOME) com sucesso")
    void createTransaction_whenIncome_shouldSucceed() {
        User user = new User();
        user.setId(1L);
        BigDecimal valor = new BigDecimal("200.00");
        CreateTransactionDTO dto = new CreateTransactionDTO("Salário", valor, TransactionType.INCOME, RecurrenceType.ONE_TIME);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction transaction = transactionService.createTransaction(dto, user);

        assertNotNull(transaction, "A transação não deveria ser nula.");

        assertEquals(dto.description(), transaction.getDescription(), "A descrição da transação está incorreta.");
        assertEquals(dto.amount(), transaction.getAmount(), "O valor da transação está incorreto.");
        assertEquals(dto.type(), transaction.getType(), "O tipo da transação está incorreto.");
        assertEquals(user, transaction.getUser(), "O usuário associado à transação está incorreto.");

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve criar uma transação de despesa recorrente e um novo modelo de recorrência")
    void createTransaction_whenRecurringExpenseAndNoModelExists_shouldCreateTransactionAndRecurringModel() {
        User user = new User();
        user.setId(1L);
        BigDecimal valor = new BigDecimal("200.00");
        CreateTransactionDTO dto = new CreateTransactionDTO("Salário", valor, TransactionType.EXPENSE, RecurrenceType.RECURRING);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(recurringTransactionRepository.findByUserAndDescriptionIgnoreCase(user, "Salário"))
                .thenReturn(Optional.empty());
        when(recurringTransactionRepository.save(any(RecurringTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction transaction = transactionService.createTransaction(dto, user);

        assertNotNull(transaction, "A transação não deveria ser nula.");

        assertEquals(dto.description(), transaction.getDescription(), "A descrição da transação está incorreta.");
        assertEquals(dto.amount(), transaction.getAmount(), "O valor da transação está incorreto.");
        assertEquals(dto.type(), transaction.getType(), "O tipo da transação está incorreto.");
        assertEquals(user, transaction.getUser(), "O usuário associado à transação está incorreto.");

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(recurringTransactionRepository, times(1)).save(any(RecurringTransaction.class));
    }

}