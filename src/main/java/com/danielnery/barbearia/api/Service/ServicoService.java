package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.response.ServicoResponse;
import com.danielnery.barbearia.api.Exception.ServicoJaExisteException;
import com.danielnery.barbearia.api.Exception.ServicoNaoEncontradoException;
import com.danielnery.barbearia.api.Model.Servico;
import com.danielnery.barbearia.api.Repository.ServicoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;


    public ServicoResponse cadastrar(Servico servico) {
        servico.setNome(servico.getNome().trim());
        if (servicoRepository.existsByNomeIgnoreCase(servico.getNome())) {
            throw new ServicoJaExisteException("Serviço já existe.");
        }

        return toResponse(servicoRepository.save(servico));
    }

    public List<ServicoResponse> listarTodos() {
        return servicoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ServicoResponse> listarAtivos() {
        return servicoRepository.findByAtivoTrue().stream().map(this::toResponse).toList();
    }

    public ServicoResponse buscarPorId(UUID id) {
        Servico servico = servicoRepository.findById(id).orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado"));

        return toResponse(servico);
    }

    public ServicoResponse desativar(UUID id) {
        Servico servico = servicoRepository.findById(id).orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado"));
        servico.setAtivo(false);
        servicoRepository.save(servico);
        return toResponse(servico);
    }

    public ServicoResponse ativar(UUID id) {
        Servico servico = servicoRepository.findById(id).orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado"));
        servico.setAtivo(true);
        servicoRepository.save(servico);
        return toResponse(servico);
    }

    private ServicoResponse toResponse(Servico servico) {
        return new ServicoResponse(servico.getId(), servico.getNome(), servico.getDescricao(), servico.getPreco(), servico.getDuracaoMinutos(), servico.getAtivo());
    }


}
