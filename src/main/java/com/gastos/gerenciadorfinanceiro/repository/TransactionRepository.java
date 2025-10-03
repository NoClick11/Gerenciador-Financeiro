package com.gastos.gerenciadorfinanceiro.repository;

import com.gastos.gerenciadorfinanceiro.model.Transaction;
import com.gastos.gerenciadorfinanceiro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);
}
