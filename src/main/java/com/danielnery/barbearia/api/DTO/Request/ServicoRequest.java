package com.danielnery.barbearia.api.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ServicoRequest (
        @NotBlank String nome,
        @NotBlank String descricao,
        @NotNull @Positive Integer duracaoMinutos,
        @NotNull @Positive BigDecimal preco,
        Boolean ativo
){
}
