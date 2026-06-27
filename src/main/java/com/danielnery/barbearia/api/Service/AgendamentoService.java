package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.Request.AgendamentoRequest;
import com.danielnery.barbearia.api.Exception.*;
import com.danielnery.barbearia.api.Model.Agendamento;
import com.danielnery.barbearia.api.Model.Barbeiro;
import com.danielnery.barbearia.api.Model.Servico;
import com.danielnery.barbearia.api.Model.Usuario;
import com.danielnery.barbearia.api.Repository.AgendamentoRepository;
import com.danielnery.barbearia.api.Repository.BarbeiroRepository;
import com.danielnery.barbearia.api.Repository.ServicoRepository;
import com.danielnery.barbearia.api.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.danielnery.barbearia.api.Model.enums.StatusAgendamento.AGENDADO;
import static com.danielnery.barbearia.api.Model.enums.StatusAgendamento.CANCELADO;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;

    @Transactional
    public Agendamento cadastrar(AgendamentoRequest request) {
        Agendamento agendamento = new Agendamento();

        int minuto = request.dataHoraVisita().getMinute();

        Usuario cliente = usuarioRepository.findById(request.clienteId()).orElseThrow(() -> new UsuarioNaoEncontrado("Usuario Não encontrado"));

        Barbeiro barbeiro = barbeiroRepository.findById(request.barbeiroId()).orElseThrow(() -> new BarbeiroNaoEncontrado("Barbeiro não encontrado"));

        if (!barbeiro.getAtivo()) {
            throw new BarbeiroInativoException("Barbeiro inativo");
        }

        Servico servico = servicoRepository.findById(request.servicoId()).orElseThrow(() -> new ServicoNaoEncontradoException("Serviço Não encontrado"));

        if (!servico.getAtivo()) {
            throw new ServicoInativoException("Serviço não disponivel.");
        }

        if (agendamentoRepository.existsByBarbeiroAndDataHoraVisita(barbeiro, request.dataHoraVisita())) {
            throw new HorarioIndisponivelException("Horário indisponível");
        }

        if(minuto!=0 && minuto !=30){
            throw new RuntimeException("Horário deve ser em bloco de 30 minutos");
        }

        agendamento.setBarbeiro(barbeiro);
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHoraVisita(request.dataHoraVisita());
        agendamento.setStatusAgendamento(AGENDADO);
        agendamento.setValorServicoNoMomento(servico.getPreco());
        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(UUID id) {
        return agendamentoRepository.findById(id).orElseThrow(() -> new AgendamentoNaoEncontrado("Agendamento não encontrado, verifique o ID"));
    }

    public Agendamento cancelarAgendamento(UUID id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatusAgendamento(CANCELADO);
        return agendamentoRepository.save(agendamento);

    }


}
