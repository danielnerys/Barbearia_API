package com.danielnery.barbearia.api.Service;

import com.danielnery.barbearia.api.DTO.response.UsuarioResponse;
import com.danielnery.barbearia.api.Exception.UsuarioJaExisteException;
import com.danielnery.barbearia.api.Exception.UsuarioNaoEncontrado;
import com.danielnery.barbearia.api.Model.Usuario;
import com.danielnery.barbearia.api.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse cadastrar(Usuario usuario) {
        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        if(usuarioRepository.existsByEmail(usuario.getEmail())){
            throw new UsuarioJaExisteException("E-mail já cadastrado!");
        }
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return toResponse(usuarioSalvo);

    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().
                stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(UUID id){
        Usuario usuario =  usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado"));

        return toResponse(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario){
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

}
