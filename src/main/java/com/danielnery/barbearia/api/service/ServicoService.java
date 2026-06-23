package com.danielnery.barbearia.api.service;

import com.danielnery.barbearia.api.model.Servico;
import com.danielnery.barbearia.api.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public Servico cadastrar(Servico servico) {
        servico.setNome(servico.getNome().trim());
        if (servicoRepository.existsByNomeIgnoreCase(servico.getNome())) {
            throw new RuntimeException("Serviço já existe.");
        }

        return servicoRepository.save(servico);
    }

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public List<Servico> listarAtivos() {
        return servicoRepository.findByAtivoTrue();
    }

    public Servico buscarPorId(UUID id) {
        return servicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public void desativar(UUID id) {

        Servico servico = buscarPorId(id);
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }


}
