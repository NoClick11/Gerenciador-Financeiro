package com.gastos.gerenciadorfinanceiro.repository;

import com.gastos.gerenciadorfinanceiro.model.RecurringTransaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findAllByUser(User user);
}