package com.soft.reclutamiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application - Reclutamiento API
 * <p>
 * Clean Architecture + CQRS with PipelinR
 * <p>
 * Layers:
 * - API (Controllers)
 * - Application (Services/Facades)
 * - Domain (CQRS Handlers)
 * - Infrastructure (Repositories)
 * - Entity (JPA Entities)
 * - DTO (Data Transfer Objects)
 */
@SpringBootApplication(scanBasePackages = "com.soft.reclutamiento")
public class ReclutamientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReclutamientoApplication.class, args);
	}

}
