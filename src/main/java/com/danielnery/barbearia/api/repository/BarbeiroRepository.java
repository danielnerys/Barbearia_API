package com.danielnery.barbearia.api.repository;

import com.danielnery.barbearia.api.model.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, UUID> {
    List<Barbeiro> findByAtivoTrue();

}
