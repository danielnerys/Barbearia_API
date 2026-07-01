package com.danielnery.barbearia.api.DTO.Request;

import com.danielnery.barbearia.api.Model.enums.Especialidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BarbeiroRequest (
        @NotBlank String nome,
        @NotNull Especialidade especialidade,
        Boolean ativo
) {
}
