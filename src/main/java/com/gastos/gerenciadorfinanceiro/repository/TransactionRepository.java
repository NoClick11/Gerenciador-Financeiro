package com.gastos.gerenciadorfinanceiro.repository;

import com.gastos.gerenciadorfinanceiro.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
