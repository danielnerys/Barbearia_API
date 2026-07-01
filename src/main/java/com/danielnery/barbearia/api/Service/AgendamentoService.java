package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.Request.AgendamentoRequest;
import com.danielnery.barbearia.api.DTO.response.AgendamentoResponse;
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

import java.time.LocalDateTime;
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

    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário Não encontrado"));
    }

    private Barbeiro buscarBarbeiro(UUID id) {
        return barbeiroRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário Não encontrado"));
    }

    private Servico buscarServico(UUID id) {
        return servicoRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário Não encontrado"));
    }

    private void validarHorario(AgendamentoRequest request) {
        int minuto = request.dataHoraVisita().getMinute();

        if (minuto != 0 && minuto != 30) {
            throw new HorarioIndisponivelException("Horário deve ser em blocos de 30 minutos.");
        }
    }

    private void validarBarbeiroAtivo(Barbeiro barbeiro) {
        if (!barbeiro.getAtivo()) {
            throw new BarbeiroInativoException("Barbeiro inativo!");
        }
    }

    private void validarServicoAtivo(Servico servico) {
        if (!servico.getAtivo()) {
            throw new ServicoInativoException("Barbeiro inativo!");
        }
    }

    private void validarHorarioDisponivel(Barbeiro barbeiro, LocalDateTime dataHora) {
        if (agendamentoRepository.existsByBarbeiroAndDataHoraVisita(barbeiro, dataHora)) {
            throw new HorarioIndisponivelException("Horário indisponível");
        }
    }

    @Transactional
    public AgendamentoResponse cadastrar(AgendamentoRequest request) {


//        int minuto = request.dataHoraVisita().getMinute();

        Usuario cliente = buscarUsuario(request.clienteId());

        Barbeiro barbeiro = buscarBarbeiro(request.barbeiroId());
        Servico servico = buscarServico(request.servicoId());

        validarBarbeiroAtivo(barbeiro);
        validarServicoAtivo(servico);
        validarHorario(request);
        validarHorarioDisponivel(barbeiro, request.dataHoraVisita());


        Agendamento agendamento = new Agendamento();

        agendamento.setBarbeiro(barbeiro);
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHoraVisita(request.dataHoraVisita());
        agendamento.setStatusAgendamento(AGENDADO);
        agendamento.setValorServicoNoMomento(servico.getPreco());

        return toResponse(agendamentoRepository.save(agendamento));
    }

    public List<AgendamentoResponse> listarTodos() {
        return agendamentoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Agendamento buscarPorId(UUID id) {
        return agendamentoRepository.findById(id).orElseThrow(() -> new AgendamentoNaoEncontrado("Agendamento não encontrado, verifique o ID"));
    }

    public AgendamentoResponse cancelarAgendamento(UUID id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatusAgendamento(CANCELADO);
        return toResponse(agendamentoRepository.save(agendamento));

    }

    private AgendamentoResponse toResponse(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getBarbeiro().getNome(),
                agendamento.getCliente().getNome(),
                agendamento.getServico().getNome(),
                agendamento.getStatusAgendamento(),
                agendamento.getDataHoraVisita(),
                agendamento.getCriadoEm(),
                agendamento.getValorServicoNoMomento()
        );
    }


}
