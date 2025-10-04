package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.RecurringTransaction;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
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
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public TransactionService(TransactionRepository transactionRepository, RecurringTransactionRepository recurringTransactionRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    public List<Transaction> getTransactionsForMonth(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        List<RecurringTransaction> recurringModels = recurringTransactionRepository.findAllByUser(user);

        for (RecurringTransaction model : recurringModels) {
            boolean transactionExists = transactionRepository.existsByUserAndDescriptionAndDateBetween(
                    user, model.getDescription(), startDate, endDate
            );

            if (!transactionExists) {
                Transaction newTransaction = new Transaction();
                newTransaction.setUser(user);
                newTransaction.setDescription(model.getDescription());
                newTransaction.setAmount(model.getAmount());
                newTransaction.setType(model.getTransactionType());
                newTransaction.setDate(LocalDate.of(year, month, model.getDayOfMonth()));
                transactionRepository.save(newTransaction);
            }
        }
        return transactionRepository.findAllByUserAndDateBetween(user, startDate, endDate);
    }

    public List<Transaction> findAllByUser(User user) {
        return transactionRepository.findAllByUser(user);
    }

}