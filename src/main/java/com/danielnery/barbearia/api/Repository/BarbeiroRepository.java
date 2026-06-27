package com.danielnery.barbearia.api.Repository;

import com.danielnery.barbearia.api.Model.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, UUID> {
    List<Barbeiro> findByAtivoTrue();

}
