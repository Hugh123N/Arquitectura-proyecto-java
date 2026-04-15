package com.soft.reclutamiento.infrastructure.config;

import com.soft.reclutamiento.infrastructure.security.IUserIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuración de JPA Auditing
 * Habilita la auditoría automática de entidades (@CreatedBy, @LastModifiedBy, etc.)
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Proveedor de auditor que obtiene el usuario actual desde IUserIdentity
     */
    @Bean
    public AuditorAware<String> auditorProvider(IUserIdentity userIdentity) {
        return () -> {
            try {
                String currentUser = userIdentity.getCurrentUser();
                return Optional.ofNullable(currentUser);
            } catch (Exception e) {
                // Si no hay usuario autenticado, usar "system"
                return Optional.of("system");
            }
        };
    }
}
