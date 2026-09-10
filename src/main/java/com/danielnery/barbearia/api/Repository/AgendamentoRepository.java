package com.danielnery.barbearia.api.Repository;

import com.danielnery.barbearia.api.Model.Agendamento;
import com.danielnery.barbearia.api.Model.Barbeiro;
import com.danielnery.barbearia.api.Model.Usuario;
import com.danielnery.barbearia.api.Model.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    boolean existsByBarbeiroAndDataHoraVisitaAndStatusAgendamentoNot(Barbeiro barbeiro, LocalDateTime dataHoraVisita, StatusAgendamento statusAgendamento);

    List<Agendamento> findByClienteOrderByDataHoraVisitaDesc(Usuario cliente);

    List<Agendamento> findByBarbeiroAndDataHoraVisitaBetween(Barbeiro barbeiro, LocalDateTime inicio, LocalDateTime fim);
}
