package com.danielnery.barbearia.api.DTO.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoResponse(
        UUID id,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer duracaoMinutos,
        boolean ativo


) {
}
