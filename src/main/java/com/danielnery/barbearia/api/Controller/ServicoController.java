package com.danielnery.barbearia.api.Controller;

import com.danielnery.barbearia.api.DTO.response.ServicoResponse;
import com.danielnery.barbearia.api.Model.Servico;
import com.danielnery.barbearia.api.Service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoint para gerenciar serviços.")
public class ServicoController {
    private final ServicoService servicoService;

    @GetMapping
    @Operation(summary = "Listar todos os serviços.", description = "Rota para listar todos os serviços cadastrados.")
    public ResponseEntity<List<ServicoResponse>> listarTodos() {
        List<ServicoResponse> servicos = servicoService.listarTodos();

        if (servicos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/ativos")
    @Operation(summary = "Serviços ativos", description = "Listar todos serviços ativos")
    public ResponseEntity<List<ServicoResponse>> listarAtivos() {
        List<ServicoResponse> servicosAtivos = servicoService.listarAtivos();
        if (servicosAtivos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(servicosAtivos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Rota para buscar um serviço por ID")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable UUID id) {

        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar serviço", description = "Rota para cadastrar novo serviço")
    public ResponseEntity<ServicoResponse> cadastrarServico(@Valid @RequestBody Servico servico) {
        ServicoResponse novoServico = servicoService.cadastrar(servico);
        return new ResponseEntity<>(novoServico, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar Serviço", description = "Essa rota serve para ativar um serviço")
    public ResponseEntity<ServicoResponse> ativar(@PathVariable UUID id) {
        return ResponseEntity.ok(servicoService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar serviço", description = "Rota para desativar um serviço")
    public ResponseEntity<ServicoResponse> desativar(@PathVariable UUID id) {
        return ResponseEntity.ok(servicoService.desativar(id));
    }


}
