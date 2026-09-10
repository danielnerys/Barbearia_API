package com.danielnery.barbearia.api.DTO.Request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequest (
     @NotNull(message = "barbeiroId é obrigatório")
     UUID barbeiroId,
     @NotNull(message = "servicoId é obrigatório")
     UUID servicoId,
     @NotNull(message = "dataHoraVisita é obrigatório")
     @Future(message = "dataHoraVisita deve estar no futuro")
     LocalDateTime dataHoraVisita
){};
