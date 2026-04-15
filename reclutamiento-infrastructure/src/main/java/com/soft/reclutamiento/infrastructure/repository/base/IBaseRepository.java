package com.soft.reclutamiento.infrastructure.repository.base;

import com.soft.reclutamiento.entity.base.SearchResult;
import com.soft.reclutamiento.entity.base.SortExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * @param <TEntity> Tipo de la entidad
 * @param <ID>      Tipo del identificador
 */
@NoRepositoryBean
public interface IBaseRepository<TEntity, ID> extends JpaRepository<TEntity, ID>, JpaSpecificationExecutor<TEntity> {

    /**
     * Busca una entidad por ID sin tracking (modo read-only)
     */
    Optional<TEntity> findByIdAsNoTracking(ID id);

    /**
     * Busca todas las entidades sin tracking
     */
    List<TEntity> findAllAsNoTracking();

    /**
     * Búsqueda paginada con ordenamiento personalizado
     */
    SearchResult<TEntity> searchBy(
            int page,
            int pageSize,
            List<SortExpression<TEntity>> sortExpressions,
            org.springframework.data.jpa.domain.Specification<TEntity> specification
    );

    /**
     * Búsqueda paginada sin tracking
     */
    SearchResult<TEntity> searchByAsNoTracking(
            int page,
            int pageSize,
            List<SortExpression<TEntity>> sortExpressions,
            org.springframework.data.jpa.domain.Specification<TEntity> specification
    );
}
