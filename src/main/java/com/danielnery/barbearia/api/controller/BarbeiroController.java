package com.danielnery.barbearia.api.controller;

import com.danielnery.barbearia.api.model.Barbeiro;
import com.danielnery.barbearia.api.service.BarbeiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/barbeiros")
@RequiredArgsConstructor
@Tag(name = "Barbeiros", description = "Endpoint para gerenciar barbeiros")
public class BarbeiroController {

    private final BarbeiroService barbeiroService;

    @PostMapping
    @Operation(summary = "Cadastrar novo barbeiro")
    public ResponseEntity<Barbeiro> cadastrar(@Valid @RequestBody Barbeiro barbeiro){
        Barbeiro novoBarbeiro = barbeiroService.cadastrar(barbeiro);
        return new ResponseEntity<>(novoBarbeiro, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos barbeiros")
    public ResponseEntity<List<Barbeiro>> listarTodos(){
        List<Barbeiro> barbeiros = barbeiroService.listarTodos();
        if(barbeiros.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(barbeiros, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar barbeiro por ID")
    public ResponseEntity<Barbeiro> buscarPorId(@PathVariable UUID id){
        return ResponseEntity.ok(barbeiroService.buscarBarbeiroPorId(id));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar todos barbeiros ATIVOS")
    public ResponseEntity<List<Barbeiro>> listarAtivos(){
        List<Barbeiro> barbeiros_ativos = barbeiroService.listarBarbeirosAtivos();
        if(barbeiros_ativos.isEmpty()){
            return new ResponseEntity<List<Barbeiro>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(barbeiros_ativos, HttpStatus.OK);
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar barbeiro")
    public ResponseEntity<Barbeiro> ativar(@PathVariable UUID id){
        return ResponseEntity.ok(barbeiroService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar barbeiro")
    public ResponseEntity<Barbeiro> desativar(@PathVariable UUID id){
        return ResponseEntity.ok(barbeiroService.desativar(id));
    }
}

