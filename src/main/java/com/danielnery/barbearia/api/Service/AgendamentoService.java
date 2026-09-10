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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.danielnery.barbearia.api.Model.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.danielnery.barbearia.api.Model.enums.StatusAgendamento.AGENDADO;
import static com.danielnery.barbearia.api.Model.enums.StatusAgendamento.CANCELADO;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;

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
    public AgendamentoResponse cadastrar(AgendamentoRequest request, Usuario cliente) {

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

        try {
            return toResponse(agendamentoRepository.save(agendamento));
        } catch (DataIntegrityViolationException exception) {
            throw new HorarioIndisponivelException("Horário indisponível");
        }
    }

    public List<AgendamentoResponse> listarTodos() {
        return agendamentoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<AgendamentoResponse> listarMeus(Usuario cliente) {
        return agendamentoRepository.findByClienteOrderByDataHoraVisitaDesc(cliente).stream().map(this::toResponse).toList();
    }

    public List<AgendamentoResponse> listarPorBarbeiroEData(UUID barbeiroId, LocalDate data) {
        Barbeiro barbeiro = buscarBarbeiro(barbeiroId);

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);

        return agendamentoRepository.findByBarbeiroAndDataHoraVisitaBetween(barbeiro, inicio, fim).stream().map(this::toResponse).toList();
    }

    public Agendamento buscarPorId(UUID id) {
        return agendamentoRepository.findById(id).orElseThrow(() -> new AgendamentoNaoEncontrado("Agendamento não encontrado, verifique o ID"));
    }

    public AgendamentoResponse cancelarAgendamento(UUID id, Usuario solicitante) {
        Agendamento agendamento = buscarPorId(id);

        boolean donoDoAgendamento = agendamento.getCliente().getId().equals(solicitante.getId());
        boolean admin = solicitante.getRole() == Role.ADMIN;

        if (!donoDoAgendamento && !admin) {
            throw new OperacaoNaoPermitidaException("Você não pode cancelar o agendamento de outra pessoa.");
        }

        if (agendamento.getStatusAgendamento() == CANCELADO) {
            throw new OperacaoNaoPermitidaException("Agendamento já está cancelado.");
        }

        if (agendamento.getDataHoraVisita().isBefore(LocalDateTime.now())) {
            throw new OperacaoNaoPermitidaException("Não é possível cancelar um agendamento que já passou.");
        }

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
