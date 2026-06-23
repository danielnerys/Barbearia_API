package com.danielnery.barbearia.api.model;

import com.danielnery.barbearia.api.model.enums.Especialidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "barbeiros")
public class Barbeiro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;

    @Column(nullable = false)
    private Boolean ativo = true;



}
