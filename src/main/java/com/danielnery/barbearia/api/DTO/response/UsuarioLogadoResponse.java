package com.danielnery.barbearia.api.DTO.response;

import com.danielnery.barbearia.api.Model.enums.Role;

import java.util.UUID;

public record UsuarioLogadoResponse(
        UUID id,
        String nome,
        String email,
        Role role
) {
}
