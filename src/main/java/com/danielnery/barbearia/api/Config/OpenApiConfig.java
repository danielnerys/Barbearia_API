package com.danielnery.barbearia.api.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Barbearia API")
                        .version("1.0.0")
                        .description("API REST desenvolvida com Spring Boot para gerenciamento de uma barbearia.\n" +
                                "\n" +
                                "Funcionalidades:\n" +
                                "• Autenticação JWT\n" +
                                "• Gestão de usuários\n" +
                                "• Gestão de barbeiros\n" +
                                "• Gestão de serviços\n" +
                                "• Agendamento de horários\n" +
                                "• Documentação OpenAPI/Swagger\n" +
                                "\n" +
                                "Tecnologias:\n" +
                                "Spring Boot • Spring Security • JWT • PostgreSQL • JPA/Hibernate")
                        .contact(new Contact()
                                .name("Daniel Nery").email("danielnerys@icloud.com").url("https://github.com/danielnerys"))
                        .license(new License()
                                .name("MIT License").url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}