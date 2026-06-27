package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.Request.AgendamentoRequest;
import com.danielnery.barbearia.api.Exception.AgendamentoNaoEncontrado;
import com.danielnery.barbearia.api.Exception.BarbeiroNaoEncontrado;
import com.danielnery.barbearia.api.Exception.ServicoNaoEncontradoException;
import com.danielnery.barbearia.api.Exception.UsuarioNaoEncontrado;
import com.danielnery.barbearia.api.Model.Agendamento;
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

import static com.danielnery.barbearia.api.Model.enums.StatusAgendamento.CANCELADO;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;

    @Transactional
    public Agendamento cadastrar(AgendamentoRequest request){
        Agendamento agendamento = new Agendamento();

        agendamento.setCliente(usuarioRepository.findById(request.clienteId()).orElseThrow(()-> new UsuarioNaoEncontrado("Usuario Não encontrado")));

        agendamento.setBarbeiro(barbeiroRepository.findById(request.barbeiroId()).orElseThrow(() -> new BarbeiroNaoEncontrado("Barbeiro não encontrado")));

        agendamento.setServico(servicoRepository.findById(request.servicoId()).orElseThrow(()-> new ServicoNaoEncontradoException("Serviço Não encontrado")));


        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarTodos(){
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(UUID id){
        return agendamentoRepository.findById(id).orElseThrow(()-> new AgendamentoNaoEncontrado("Agendamento não encontrado, verifique o ID"));
    }

    public void cancelarAgentamento(UUID id){
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatusAgendamento(CANCELADO);
        agendamentoRepository.save(agendamento);

    }



}
