package com.danielnery.barbearia.api.service;

import com.danielnery.barbearia.api.exception.BarbeiroNaoEncontrado;
import com.danielnery.barbearia.api.model.Barbeiro;
import com.danielnery.barbearia.api.repository.BarbeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarbeiroService {
    private final BarbeiroRepository barbeiroRepository;

    public Barbeiro cadastrar (Barbeiro barbeiro){
        barbeiro.setNome(barbeiro.getNome().trim());
        return barbeiroRepository.save(barbeiro);
    }

    public List<Barbeiro> listarTodos(){
        return barbeiroRepository.findAll();
    }

    public Barbeiro buscarBarbeiroPorId(UUID id){
        return barbeiroRepository.findById(id).orElseThrow(() -> new BarbeiroNaoEncontrado("Barbeiro não encontrado"));
    }

    public List<Barbeiro> listarBarbeirosAtivos(){
        return barbeiroRepository.findByAtivoTrue();
    }

    public Barbeiro desativar(UUID id){
        Barbeiro barbeiro = buscarBarbeiroPorId(id);
        barbeiro.setAtivo(false);
        return barbeiroRepository.save(barbeiro);
    }

    public Barbeiro ativar(UUID id){
        Barbeiro barbeiro = buscarBarbeiroPorId(id);
        barbeiro.setAtivo(true);
        return barbeiroRepository.save(barbeiro);
    }
}
