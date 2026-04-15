package com.soft.reclutamiento.infrastructure.config;

import com.soft.reclutamiento.infrastructure.repository.base.BaseRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración de repositorios JPA
 * Configura Spring Data JPA para usar el BaseRepositoryFactoryBean personalizado
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.soft.reclutamiento.infrastructure.repository",
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class
)
public class RepositoryConfig {
    // Esta configuración permite que todos los repositorios usen BaseRepositoryImpl
    // como clase base en lugar de SimpleJpaRepository
}
