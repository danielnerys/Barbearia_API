package com.danielnery.barbearia.api.DTO.Request;
import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequest (
     UUID clienteId,
    UUID barbeiroId,
    UUID servicoId,
     LocalDateTime dataHoraVisita
){};
