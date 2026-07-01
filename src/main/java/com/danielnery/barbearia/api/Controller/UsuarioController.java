package com.danielnery.barbearia.api.Controller;

import com.danielnery.barbearia.api.DTO.response.UsuarioResponse;
import com.danielnery.barbearia.api.Model.Usuario;
import com.danielnery.barbearia.api.Service.UsuarioService;
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
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoint para administração de usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody Usuario usuario) {
        UsuarioResponse novoUsuario = usuarioService.cadastrar(usuario);

        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @GetMapping()
    @Operation(summary = "Listar todos Usuários")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }


}
