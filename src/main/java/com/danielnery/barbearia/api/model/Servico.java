package com.danielnery.barbearia.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "servicos")
@NoArgsConstructor
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String nome;

    @Column(nullable = false)
    @NotBlank
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @Positive
    private BigDecimal preco;

    @Column(nullable = false)
    @Positive
    @NotNull
    private int duracaoMinutos;

    @Column(nullable = false)
    private Boolean ativo = true;

}
