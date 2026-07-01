package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.response.BarbeiroResponse;
import com.danielnery.barbearia.api.Exception.BarbeiroNaoEncontrado;
import com.danielnery.barbearia.api.Model.Barbeiro;
import com.danielnery.barbearia.api.Repository.BarbeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarbeiroService {
    private final BarbeiroRepository barbeiroRepository;

    private Barbeiro buscarEntidadePorId(UUID id){
        return barbeiroRepository.findById(id).orElseThrow(() -> new BarbeiroNaoEncontrado("Barbeiro não encontrado"));
    }

    public BarbeiroResponse cadastrar (Barbeiro barbeiro){
        barbeiro.setNome(barbeiro.getNome().trim());
        barbeiroRepository.save(barbeiro);
        return toResponse(barbeiro);
    }

    public List<BarbeiroResponse> listarTodos(){
        return barbeiroRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BarbeiroResponse buscarBarbeiroPorId(UUID id){
        Barbeiro barbeiro = buscarEntidadePorId(id);

        return toResponse(barbeiro);
    }

    public List<BarbeiroResponse> listarBarbeirosAtivos(){
        return barbeiroRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BarbeiroResponse desativar(UUID id){
        Barbeiro barbeiro = buscarEntidadePorId(id);
        barbeiro.setAtivo(false);
        barbeiroRepository.save(barbeiro);
        return toResponse(barbeiro);
    }

    public BarbeiroResponse ativar(UUID id){
        Barbeiro barbeiro = buscarEntidadePorId(id);
        barbeiro.setAtivo(true);
        barbeiroRepository.save(barbeiro);
        return toResponse(barbeiro);
    }

    private BarbeiroResponse toResponse(Barbeiro barbeiro){
        return new BarbeiroResponse(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEspecialidade(),
                barbeiro.getAtivo()
        );
    }
}
