package com.danielnery.barbearia.api.DTO.response;

import com.danielnery.barbearia.api.Model.enums.StatusAgendamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse (
        UUID id,
        String barbeiro,
        String cliente,
        String servico,
        StatusAgendamento statusAgendamento,
        LocalDateTime dataHoraVisita,
        LocalDateTime criadoEm,
        BigDecimal valorServicoNoMomento
){
}
