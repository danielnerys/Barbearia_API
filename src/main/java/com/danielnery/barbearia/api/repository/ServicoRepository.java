package com.danielnery.barbearia.api.repository;

import com.danielnery.barbearia.api.model.Servico;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {

    List<Servico> findByAtivoTrue();


    boolean existsByNomeIgnoreCase(String nome);
}
