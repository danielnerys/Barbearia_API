package com.danielnery.barbearia.api.Controller;

import com.danielnery.barbearia.api.DTO.Request.BarbeiroRequest;
import com.danielnery.barbearia.api.DTO.response.BarbeiroResponse;
import com.danielnery.barbearia.api.Model.Barbeiro;
import com.danielnery.barbearia.api.Service.BarbeiroService;
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
@CrossOrigin("*")
@RequestMapping("/api/barbeiros")
@RequiredArgsConstructor
@Tag(name = "Barbeiros", description = "Endpoint para gerenciar barbeiros")
public class BarbeiroController {

    private final BarbeiroService barbeiroService;

    @PostMapping
    @Operation(summary = "Cadastrar novo barbeiro")
    public ResponseEntity<BarbeiroResponse> cadastrar(@Valid @RequestBody BarbeiroRequest barbeiro){
        return new ResponseEntity<>(barbeiroService.cadastrar(barbeiro), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos barbeiros")
    public ResponseEntity<List<BarbeiroResponse>> listarTodos(){
        List<BarbeiroResponse> barbeiros = barbeiroService.listarTodos();
        if(barbeiros.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(barbeiros, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar barbeiro por ID")
    public ResponseEntity<BarbeiroResponse> buscarPorId(@PathVariable UUID id){
        return ResponseEntity.ok(barbeiroService.buscarBarbeiroPorId(id));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar todos barbeiros ATIVOS")
    public ResponseEntity<List<BarbeiroResponse>> listarAtivos(){
        List<BarbeiroResponse> barbeirosAtivos = barbeiroService.listarBarbeirosAtivos();
        if(barbeirosAtivos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(barbeirosAtivos, HttpStatus.OK);
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar barbeiro")
    public ResponseEntity<BarbeiroResponse> ativar(@PathVariable UUID id){
        return ResponseEntity.ok(barbeiroService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar barbeiro")
    public ResponseEntity<BarbeiroResponse> desativar(@PathVariable UUID id){
        return ResponseEntity.ok(barbeiroService.desativar(id));
    }
}

