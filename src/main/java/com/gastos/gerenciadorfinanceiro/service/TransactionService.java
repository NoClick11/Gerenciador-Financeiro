package com.gastos.gerenciadorfinanceiro.service;

import com.gastos.gerenciadorfinanceiro.dto.CreateTransactionDTO;
import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
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

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(CreateTransactionDTO dto, User user) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setRecurrenceType(dto.recurrenceType());
        transaction.setDate(LocalDate.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsForMonth(User user, int year, int month) {
        LocalDate currentMonthStart = LocalDate.of(year, month, 1);
        LocalDate currentMonthEnd = currentMonthStart.with(TemporalAdjusters.lastDayOfMonth());

        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDate previousMonthEnd = previousMonthStart.with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> recurringFromLastMonth = transactionRepository.findAllByUserAndRecurrenceTypeAndDateBetween(
                user, RecurrenceType.RECURRING, previousMonthStart, previousMonthEnd
        );

        for (Transaction lastMonthTransaction : recurringFromLastMonth) {
            boolean alreadyExistsThisMonth = transactionRepository.existsByUserAndDescriptionAndDateBetween(
                    user, lastMonthTransaction.getDescription(), currentMonthStart, currentMonthEnd
            );

            if (!alreadyExistsThisMonth) {
                Transaction newRecurringTransaction = new Transaction();
                newRecurringTransaction.setUser(user);
                newRecurringTransaction.setDescription(lastMonthTransaction.getDescription());
                newRecurringTransaction.setAmount(lastMonthTransaction.getAmount());
                newRecurringTransaction.setType(lastMonthTransaction.getType());
                newRecurringTransaction.setRecurrenceType(RecurrenceType.RECURRING);

                newRecurringTransaction.setDate(lastMonthTransaction.getDate().withYear(year).withMonth(month));

                transactionRepository.save(newRecurringTransaction);
            }
        }

        return transactionRepository.findAllByUserAndDateBetween(user, currentMonthStart, currentMonthEnd);
    }

    public List<Transaction> findAllByUser(User user) {
        return transactionRepository.findAllByUser(user);
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
        transaction.setRecurrenceType(dto.recurrenceType());
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