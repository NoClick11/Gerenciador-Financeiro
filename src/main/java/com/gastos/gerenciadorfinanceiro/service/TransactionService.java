package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.*;
import com.gastos.gerenciadorfinanceiro.repository.RecurringTransactionRepository;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional // Boa prática para garantir a consistência das operações no banco
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public TransactionService(TransactionRepository transactionRepository, RecurringTransactionRepository recurringTransactionRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    public List<Transaction> findAllByUser(User user) {
        return transactionRepository.findAllByUser(user);
    }

    public Transaction createTransaction(CreateTransactionDTO dto, User user) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setExpenseCategory(dto.expenseCategory());
        transaction.setDate(LocalDate.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (savedTransaction.getExpenseCategory() == ExpenseCategory.MONTHLY) {
            recurringTransactionRepository.findByUserAndDescriptionIgnoreCase(user, savedTransaction.getDescription())
                    .ifPresentOrElse(
                            (existingRecurring) -> System.out.println("Modelo recorrente já existe para: " + existingRecurring.getDescription()),
                            () -> {
                                RecurringTransaction newRecurring = new RecurringTransaction();
                                newRecurring.setUser(user);
                                newRecurring.setDescription(savedTransaction.getDescription());
                                newRecurring.setAmount(savedTransaction.getAmount());
                                newRecurring.setTransactionType(savedTransaction.getType());
                                newRecurring.setDayOfMonth(savedTransaction.getDate().getDayOfMonth());
                                recurringTransactionRepository.save(newRecurring);
                            }
                    );
        }

        return savedTransaction;
    }

    public List<Transaction> getTransactionsForMonth(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAllByUser(user);

        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            boolean transactionExists = transactionRepository.existsByUserAndDescriptionAndDateBetween(
                    user, recurringTransaction.getDescription(), startDate, endDate
            );

            if (!transactionExists) {
                Transaction newTransaction = new Transaction();
                newTransaction.setUser(user);
                newTransaction.setDescription(recurringTransaction.getDescription());
                newTransaction.setAmount(recurringTransaction.getAmount());
                newTransaction.setDate(LocalDate.of(year, month, recurringTransaction.getDayOfMonth()));
                newTransaction.setType(recurringTransaction.getTransactionType());
                if (newTransaction.getType() == TransactionType.EXPENSE) {
                    newTransaction.setExpenseCategory(ExpenseCategory.MONTHLY);
                }
                transactionRepository.save(newTransaction);
            }
        }
        return transactionRepository.findAllByUserAndDateBetween(user, startDate, endDate);
    }

    public Transaction updateTransaction(Long id, CreateTransactionDTO dto, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setExpenseCategory(dto.expenseCategory());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        transactionRepository.delete(transaction);
    }
}