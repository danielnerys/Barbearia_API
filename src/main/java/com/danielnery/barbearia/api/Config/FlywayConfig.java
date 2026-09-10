package com.danielnery.barbearia.api.Config;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    /**
     * O Flyway foi adotado depois que já existiam bancos criados por
     * {@code hibernate.ddl-auto=update}. Sem baseline, ele recusaria migrar um banco não vazio;
     * com baseline, esses bancos são marcados na versão 1 e V1__baseline.sql é pulada — o schema
     * que ela cria já está lá. Bancos novos rodam V1 normalmente.
     * <p>
     * Configurado aqui, e não em application.properties, porque aquele arquivo é gitignored:
     * um checkout limpo não o tem, e o Flyway precisa desta configuração em qualquer ambiente.
     */
    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return (FluentConfiguration configuration) -> configuration
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .baselineDescription("baseline");
    }
}
