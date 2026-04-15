package com.soft.reclutamiento.infrastructure.repository;

import com.soft.reclutamiento.entity.entities.Area;
import com.soft.reclutamiento.infrastructure.repository.base.IBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Area
 * Hereda todos los métodos CRUD de IBaseRepository:
 * - save, saveAll, delete, deleteAll
 * - findById, findAll, findAllById
 * - findByIdAsNoTracking, findAllAsNoTracking
 * - searchBy, searchByAsNoTracking
 * - count, existsById
 */
@Repository
public interface AreaRepository extends IBaseRepository<Area, Integer> {

    // Aquí puedes agregar métodos personalizados de consulta si los necesitas
    // Ejemplos:
    // Optional<Area> findByNombre(String nombre);
    // List<Area> findByActivoTrue();
}
