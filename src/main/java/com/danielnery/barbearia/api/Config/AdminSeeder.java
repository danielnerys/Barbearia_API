package com.danielnery.barbearia.api.Config;

import com.danielnery.barbearia.api.Model.Usuario;
import com.danielnery.barbearia.api.Model.enums.Role;
import com.danielnery.barbearia.api.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminSeeder {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.nome}")
    private String adminNome;

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.senha}")
    private String adminSenha;

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            String email = adminEmail.trim().toLowerCase();

            if (usuarioRepository.existsByEmail(email)) {
                log.info("Usuário admin '{}' já existe, seed ignorado.", email);
                return;
            }

            Usuario admin = new Usuario();
            admin.setNome(adminNome.trim());
            admin.setEmail(email);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setRole(Role.ADMIN);

            usuarioRepository.save(admin);
            log.info("Usuário admin '{}' criado com sucesso.", email);
        };
    }
}
