package com.danielnery.barbearia.api.repository;

import com.danielnery.barbearia.api.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
}
