package com.gastos.gerenciadorfinanceiro.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String description;
    BigDecimal amount;
    LocalDate date;

    @Enumerated(EnumType.STRING)
    TransactionType type;
}
