package com.danielnery.barbearia.api.DTO.response;

import com.danielnery.barbearia.api.Model.enums.Especialidade;

import java.util.UUID;

public record BarbeiroResponse (
      UUID id,
      String nome,
      Especialidade especialidade,
      boolean ativo
){
}
