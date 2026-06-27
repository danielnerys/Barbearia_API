package com.danielnery.barbearia.api.Repository;

import com.danielnery.barbearia.api.Model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {

    List<Servico> findByAtivoTrue();


    boolean existsByNomeIgnoreCase(String nome);
}
