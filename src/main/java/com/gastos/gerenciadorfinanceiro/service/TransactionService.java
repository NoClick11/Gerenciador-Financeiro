package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.model.ExpenseCategory;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.TransactionType;
import com.gastos.gerenciadorfinanceiro.model.User;
import com.gastos.gerenciadorfinanceiro.repository.RecurringExpenseRepository;
import com.gastos.gerenciadorfinanceiro.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.gastos.gerenciadorfinanceiro.model.RecurringExpense;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RecurringExpenseRepository recurringExpenseRepository;

    public TransactionService(TransactionRepository transactionRepository, RecurringExpenseRepository recurringExpenseRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringExpenseRepository = recurringExpenseRepository;
    }

    public List<Transaction> getTransactionsForMonth(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        List<RecurringExpense> recurringExpenses = recurringExpenseRepository.findAllByUser(user);

        for (RecurringExpense recurringExpense : recurringExpenses) {

            boolean transactionExists = transactionRepository.existsByUserAndDescriptionAndDateBetween(
                    user,
                    recurringExpense.getDescription(),
                    startDate,
                    endDate
            );

            if (!transactionExists) {
                Transaction newTransaction = new Transaction();
                newTransaction.setUser(user);
                newTransaction.setDescription(recurringExpense.getDescription());
                newTransaction.setAmount(recurringExpense.getAmount());
                newTransaction.setDate(LocalDate.of(year, month, recurringExpense.getDayOfMonth()));
                newTransaction.setType(TransactionType.EXPENSE);
                newTransaction.setExpenseCategory(ExpenseCategory.MONTHLY);

                transactionRepository.save(newTransaction);
            }
        }

        return transactionRepository.findAllByUserAndDateBetween(user, startDate, endDate);
    }


}
