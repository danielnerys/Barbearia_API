package com.danielnery.barbearia.api.Service;

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

    public Usuario cadastrar(Usuario usuario) {
        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        if(usuarioRepository.existsByEmail(usuario.getEmail())){
            throw new UsuarioJaExisteException("E-mail já cadastrado!");
        }

        return usuarioRepository.save(usuario);

    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(UUID id){
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não encontrado"));
    }

}
