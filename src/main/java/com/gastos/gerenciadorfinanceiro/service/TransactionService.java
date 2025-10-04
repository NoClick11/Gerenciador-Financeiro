package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.model.ExpenseCategory;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.TransactionType;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.RecurringTransactionRepository;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.gastos.gerenciadorfinanceiro.model.RecurringTransaction;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import java.util.List;

@Service
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

        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAllByUser(user);

        for (RecurringTransaction recurringTransaction : recurringTransactions) {

            boolean transactionExists = transactionRepository.existsByUserAndDescriptionAndDateBetween(
                    user,
                    recurringTransaction.getDescription(),
                    startDate,
                    endDate
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
}
