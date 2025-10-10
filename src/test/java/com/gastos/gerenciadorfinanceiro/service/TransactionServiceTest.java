package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.*;
import com.gastos.gerenciadorfinanceiro.repository.RecurringTransactionRepository;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve deletar a transação com sucesso quando ela pertence ao usuário")
    void deleteTransaction_whenTransactionExistsAndBelongsToUser_shouldDeleteSuccessfully() {
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(99L);
        transaction.setUser(user);

        when(transactionRepository.findById(99L)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).delete(any(Transaction.class));


        transactionService.deleteTransaction(99L, user);


        verify(transactionRepository, times(1)).findById(99L);
        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar transação inexistente")
    void deleteTransaction_whenTransactionNotFound_shouldThrowException() {
        User user = new User();
        user.setId(1L);
        Long nonExistentId = 123L;

        when(transactionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionService.deleteTransaction(nonExistentId, user);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar transação de outro usuário")
    void deleteTransaction_whenUserIsNotOwner_shouldThrowForbiddenException() {
        User transactionOwner =  new User();
        User attackerUser = new User();
        transactionOwner.setId(1L);
        attackerUser.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setId(99L);
        transaction.setUser(transactionOwner);

        when(transactionRepository.findById(99L)).thenReturn(Optional.of(transaction));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionService.deleteTransaction(99L, attackerUser);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar apenas transações existentes para um mês passado sem criar novas")
    void getTransactionsForMonth_whenMonthIsInThePast_shouldOnlyFetchExistingTransactions() {
        User user = new User();
        int year = 2023;
        int month = 10;

        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction = new Transaction();
        Transaction transaction1 = new Transaction();

        transactions.add(transaction1);
        transactions.add(transaction);

        when(transactionRepository.findAllByUserAndDateBetween(any(User.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(transactions);

        List<Transaction> transactionMonth = transactionService.getTransactionsForMonth(user, year, month);

        assertNotNull(transactionMonth);
        assertEquals(transactionMonth.size(), transactions.size());

        verify(transactionRepository, never()).save(any());
        verify(transactionRepository, times(1)).findAllByUserAndDateBetween(any(User.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve criar transações recorrentes faltantes para o mês atual")
    void getTransactionsForMonth_whenMonthIsCurrentAndRecurringIsMissing_shouldCreateAndReturnIt() {
        // ARRANGE
        User user = new User();
        user.setId(1L);
        int year = 2025;
        int month = 10;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        RecurringTransaction recurringAluguel = new RecurringTransaction(1L, "Aluguel", new BigDecimal("1500.00"), 5, TransactionType.EXPENSE, user);
        List<RecurringTransaction> recurringList = List.of(recurringAluguel);

        when(recurringTransactionRepository.findAllByUser(user)).thenReturn(recurringList);

        when(transactionRepository.existsByUserAndDescriptionIgnoreCaseAndDateBetween(user, "Aluguel", startDate, endDate))
                .thenReturn(false);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction aluguelCriado = new Transaction();
        aluguelCriado.setDescription("Aluguel");
        aluguelCriado.setUser(user);
        aluguelCriado.setAmount(new BigDecimal("1500.00"));
        when(transactionRepository.findAllByUserAndDateBetween(user, startDate, endDate))
                .thenReturn(List.of(aluguelCriado));

        List<Transaction> result = transactionService.getTransactionsForMonth(user, year, month);

        assertNotNull(result, "A lista de resultados não deveria ser nula.");
        assertEquals(1, result.size(), "A lista de resultados deveria conter 1 transação.");
        assertEquals("Aluguel", result.get(0).getDescription(), "A descrição da transação retornada está incorreta.");

        verify(recurringTransactionRepository, times(1)).findAllByUser(user);
        verify(transactionRepository, times(1)).existsByUserAndDescriptionIgnoreCaseAndDateBetween(user, "Aluguel", startDate, endDate);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(transactionRepository, times(1)).findAllByUserAndDateBetween(user, startDate, endDate);
    }
}