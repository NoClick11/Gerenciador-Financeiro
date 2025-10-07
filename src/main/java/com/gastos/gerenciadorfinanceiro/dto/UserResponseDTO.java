package com.gastos.gerenciadorfinanceiro.dto;
import java.time.LocalDateTime;

public record UserResponseDTO(Long id, String username, LocalDateTime createdAt) {}