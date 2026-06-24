package com.danielnery.barbearia.api.controller;

import aj.org.objectweb.asm.commons.SerialVersionUIDAdder;
import com.danielnery.barbearia.api.model.Servico;
import com.danielnery.barbearia.api.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoit para gerenciar serviços.")
public class ServicoController {
    @Autowired
    private ServicoService servicoService;

    @GetMapping
    @Operation(summary = "Listar todos os serviços.", description = "Rota para listar todos os serviços cadastrados.")
    public ResponseEntity<List<Servico>> listarTodos() {
        List<Servico> servicos = servicoService.listarTodos();

        if (servicos.isEmpty()) {
            return new ResponseEntity<List<Servico>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Servico>>(servicos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Rota para buscar um serviço por ID")
    public ResponseEntity<Servico> buscarPorId(@PathVariable UUID id) {

        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar serviço", description = "Rota para cadastrar novo serviço")
    public ResponseEntity<Servico> cadastrarServico(@Valid @RequestBody Servico servico){
        Servico novoServico = servicoService.cadastrar(servico);
        return new ResponseEntity<>(novoServico, HttpStatus.CREATED);
    }

    @PatchMapping ("/{id}/ativar")
    @Operation(summary = "Ativar Serviço", description = "Essa rota serve para ativar um serviço")
    public ResponseEntity<Servico> ativar(@PathVariable UUID id){
        return ResponseEntity.ok(servicoService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar serviço", description = "Rota para desativar um serviço")
    public  ResponseEntity<Servico> desativar(@PathVariable UUID id){
        return ResponseEntity.ok(servicoService.desativar(id));
    }


}
