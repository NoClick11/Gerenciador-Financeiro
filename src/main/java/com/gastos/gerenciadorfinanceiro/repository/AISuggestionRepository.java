package com.gastos.gerenciadorfinanceiro.repository;

import com.gastos.gerenciadorfinanceiro.model.AISuggestion;
import com.gastos.gerenciadorfinanceiro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AISuggestionRepository extends JpaRepository<AISuggestion, Long> {
    Optional<AISuggestion> findTopByUserOrderByCreatedAtDesc(User user);
}
