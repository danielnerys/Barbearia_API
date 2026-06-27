package com.danielnery.barbearia.api.Controller;

import com.danielnery.barbearia.api.DTO.Request.AgendamentoRequest;
import com.danielnery.barbearia.api.Model.Agendamento;
import com.danielnery.barbearia.api.Service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/agendamentos")

@Tag(name = "Agendamentos", description = "Endpoint para gerenciar agendamentos")
public class AgendamentoController {
    public final AgendamentoService agendamentoService;

    @PostMapping
    @Operation(summary = "Realizar agendamento.")
    public ResponseEntity<Agendamento> cadastrarAgendamento(@Valid @RequestBody AgendamentoRequest agendamento){
        return new ResponseEntity<>(agendamentoService.cadastrar(agendamento), HttpStatus.CREATED);

    }

    @GetMapping
    @Operation(summary = "Listar todos agendamentos")
    public ResponseEntity<List<Agendamento>> listarTodos(){
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        if(agendamentos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(agendamentos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por Id")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable UUID id){
        return new ResponseEntity<>(agendamentoService.buscarPorId(id), HttpStatus.OK);
    }


    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar agendamento")
    public ResponseEntity<Agendamento> cancelarAgendamento(@PathVariable UUID id){
        return ResponseEntity.ok(agendamentoService.cancelarAgendamento(id));
    }
}
