package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.Request.ServicoRequest;
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

    private Servico buscarEntidadePorId(UUID id) {

        return servicoRepository.findById(id).orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado"));


    }


    public ServicoResponse cadastrar(ServicoRequest request) {
        Servico novoServico = new Servico();
        novoServico.setNome(request.nome().trim());
        novoServico.setDescricao(request.descricao().trim());
        novoServico.setDuracaoMinutos(request.duracaoMinutos());
        novoServico.setPreco(request.preco());
        novoServico.setAtivo(request.ativo() != null ? request.ativo() : true);

        if (servicoRepository.existsByNomeIgnoreCase(novoServico.getNome())) {
            throw new ServicoJaExisteException("Serviço já existe.");
        }

        return toResponse(servicoRepository.save(novoServico));
    }

    public List<ServicoResponse> listarTodos() {
        return servicoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ServicoResponse> listarAtivos() {
        return servicoRepository.findByAtivoTrue().stream().map(this::toResponse).toList();
    }

    public ServicoResponse buscarPorId(UUID id) {
        Servico servico = buscarEntidadePorId(id);

        return toResponse(servico);
    }

    public ServicoResponse desativar(UUID id) {
        Servico servico = buscarEntidadePorId(id);
        servico.setAtivo(false);
        servicoRepository.save(servico);
        return toResponse(servico);
    }

    public ServicoResponse ativar(UUID id) {
        Servico servico = buscarEntidadePorId(id);
        servico.setAtivo(true);
        servicoRepository.save(servico);
        return toResponse(servico);
    }

    private ServicoResponse toResponse(Servico servico) {
        return new ServicoResponse(servico.getId(), servico.getNome(), servico.getDescricao(), servico.getPreco(), servico.getDuracaoMinutos(), servico.getAtivo());
    }


}
