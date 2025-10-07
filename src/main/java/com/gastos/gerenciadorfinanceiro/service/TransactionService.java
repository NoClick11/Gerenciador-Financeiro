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

    public Transaction createTransaction(CreateTransactionDTO dto, User user) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setRecurrenceType(dto.recurrenceType());
        transaction.setDate(LocalDate.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        if (savedTransaction.getRecurrenceType() == RecurrenceType.RECURRING) {
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
                                System.out.println("Novo modelo recorrente criado para: " + newRecurring.getDescription());
                            }
                    );
        }
        return savedTransaction;
    }

    public List<Transaction> getTransactionsForMonth(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today.withDayOfMonth(1))) {
            return transactionRepository.findAllByUserAndDateBetween(user, startDate, endDate);
        }

        List<RecurringTransaction> recurringModels = recurringTransactionRepository.findAllByUser(user);

        for (RecurringTransaction model : recurringModels) {
            boolean transactionExists = transactionRepository.existsByUserAndDescriptionIgnoreCaseAndDateBetween(
                    user, model.getDescription(), startDate, endDate
            );

            if (!transactionExists) {
                Transaction newTransaction = new Transaction();
                newTransaction.setUser(user);
                newTransaction.setDescription(model.getDescription());
                newTransaction.setAmount(model.getAmount());
                newTransaction.setType(model.getTransactionType());
                newTransaction.setRecurrenceType(RecurrenceType.RECURRING);
                newTransaction.setDate(LocalDate.of(year, month, model.getDayOfMonth()));
                transactionRepository.save(newTransaction);
            }
        }
        return transactionRepository.findAllByUserAndDateBetween(user, startDate, endDate);
    }

    public List<Transaction> findAllByUser(User user) {
        return transactionRepository.findAllByUser(user);
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