package com.gastos.gerenciadorfinanceiro.repository;

import com.gastos.gerenciadorfinanceiro.model.RecurrenceType;
import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);
    List<Transaction> findAllByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    boolean existsByUserAndDescriptionAndDateBetween(User user, String description, LocalDate startDate, LocalDate endDate);
    List<Transaction> findAllByUserAndRecurrenceTypeAndDateBetween(User user, RecurrenceType recurrenceType, LocalDate previousMonthStart, LocalDate previousMonthEnd);
}
