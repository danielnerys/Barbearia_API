package com.danielnery.barbearia.api.Repository;

import com.danielnery.barbearia.api.Model.Agendamento;
import com.danielnery.barbearia.api.Model.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    boolean existsByBarbeiroAndDataHoraVisita(Barbeiro barbeiro, LocalDateTime dataHoraVisita);
}
