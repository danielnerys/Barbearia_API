package com.danielnery.barbearia.api.Controller;

import com.danielnery.barbearia.api.DTO.Request.AgendamentoRequest;
import com.danielnery.barbearia.api.DTO.response.AgendamentoResponse;
import com.danielnery.barbearia.api.Model.Usuario;
import com.danielnery.barbearia.api.Service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agendamentos")

@Tag(name = "Agendamentos", description = "Endpoint para gerenciar agendamentos")
public class AgendamentoController {
    public final AgendamentoService agendamentoService;

    @PostMapping
    @Operation(summary = "Realizar agendamento.")
    public ResponseEntity<AgendamentoResponse> cadastrarAgendamento(@Valid @RequestBody AgendamentoRequest agendamento, @AuthenticationPrincipal Usuario cliente){
        return new ResponseEntity<>(agendamentoService.cadastrar(agendamento, cliente), HttpStatus.CREATED);

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos agendamentos")
    public ResponseEntity<List<AgendamentoResponse>> listarTodos(){
        List<AgendamentoResponse> agendamentos = agendamentoService.listarTodos();
        if(agendamentos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(agendamentos, HttpStatus.OK);
    }

    @GetMapping("/meus")
    @Operation(summary = "Listar meus agendamentos")
    public ResponseEntity<List<AgendamentoResponse>> listarMeus(@AuthenticationPrincipal Usuario cliente){
        List<AgendamentoResponse> agendamentos = agendamentoService.listarMeus(cliente);
        if(agendamentos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(agendamentos, HttpStatus.OK);
    }

    @GetMapping("/barbeiro/{barbeiroId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agenda de um barbeiro em um dia")
    public ResponseEntity<List<AgendamentoResponse>> listarPorBarbeiroEData(@PathVariable UUID barbeiroId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data){
        List<AgendamentoResponse> agendamentos = agendamentoService.listarPorBarbeiroEData(barbeiroId, data);
        if(agendamentos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(agendamentos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por Id", description = "Restrito ao dono do agendamento ou a um ADMIN.")
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable UUID id, @AuthenticationPrincipal Usuario solicitante){
        return ResponseEntity.ok(agendamentoService.buscarPorIdParaUsuario(id, solicitante));
    }

    @GetMapping("/disponibilidade")
    @Operation(summary = "Horários disponíveis de um barbeiro em um dia",
            description = "Retorna os horários livres (\"HH:mm\") dentro do horário de funcionamento. " +
                    "Se o barbeiro estiver inativo, retorna lista vazia.")
    public ResponseEntity<List<String>> listarDisponibilidade(@RequestParam UUID barbeiroId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data){
        return ResponseEntity.ok(agendamentoService.listarDisponibilidade(barbeiroId, data));
    }


    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar agendamento")
    public ResponseEntity<AgendamentoResponse> cancelarAgendamento(@PathVariable UUID id, @AuthenticationPrincipal Usuario solicitante){
        return ResponseEntity.ok(agendamentoService.cancelarAgendamento(id, solicitante));
    }
}
