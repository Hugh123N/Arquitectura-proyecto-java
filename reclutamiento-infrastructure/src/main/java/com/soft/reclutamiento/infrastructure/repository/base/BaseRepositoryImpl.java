package com.soft.reclutamiento.infrastructure.repository.base;

import com.soft.reclutamiento.entity.base.SearchResult;
import com.soft.reclutamiento.entity.base.SortDirection;
import com.soft.reclutamiento.entity.base.SortExpression;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación base de repositorio personalizado
 * Extiende SimpleJpaRepository y agrega funcionalidades personalizadas
 * Equivalente a Repository.cs del proyecto .NET
 *
 * @param <TEntity> Tipo de la entidad
 * @param <ID>      Tipo del identificador
 */
@Transactional(readOnly = true)
public class BaseRepositoryImpl<TEntity, ID> extends SimpleJpaRepository<TEntity, ID>
        implements IBaseRepository<TEntity, ID> {

    private final EntityManager entityManager;
    private final JpaEntityInformation<TEntity, ID> entityInformation;

    public BaseRepositoryImpl(JpaEntityInformation<TEntity, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    /**
     * Busca una entidad por ID sin tracking (modo read-only)
     */
    @Override
    public Optional<TEntity> findByIdAsNoTracking(ID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TEntity> query = cb.createQuery(getDomainClass());
        Root<TEntity> root = query.from(getDomainClass());

        // Obtener el nombre del campo ID
        String idAttributeName = entityInformation.getIdAttribute().getName();

        query.select(root)
                .where(cb.equal(root.get(idAttributeName), id));

        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);

        // Establecer hint para no tracking
        typedQuery.setHint("org.hibernate.readOnly", true);
        typedQuery.setHint("jakarta.persistence.cache.retrieveMode", "BYPASS");

        List<TEntity> results = typedQuery.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Busca todas las entidades sin tracking
     */
    @Override
    public List<TEntity> findAllAsNoTracking() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TEntity> query = cb.createQuery(getDomainClass());
        Root<TEntity> root = query.from(getDomainClass());

        query.select(root);

        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);

        // Establecer hint para no tracking
        typedQuery.setHint("org.hibernate.readOnly", true);
        typedQuery.setHint("jakarta.persistence.cache.retrieveMode", "BYPASS");

        return typedQuery.getResultList();
    }

    /**
     * Búsqueda paginada con ordenamiento personalizado
     */
    @Override
    public SearchResult<TEntity> searchBy(
            int page,
            int pageSize,
            List<SortExpression<TEntity>> sortExpressions,
            Specification<TEntity> specification
    ) {
        return executeSearch(page, pageSize, sortExpressions, specification, false);
    }

    /**
     * Búsqueda paginada sin tracking
     */
    @Override
    public SearchResult<TEntity> searchByAsNoTracking(
            int page,
            int pageSize,
            List<SortExpression<TEntity>> sortExpressions,
            Specification<TEntity> specification
    ) {
        return executeSearch(page, pageSize, sortExpressions, specification, true);
    }

    /**
     * Ejecuta la búsqueda paginada
     */
    private SearchResult<TEntity> executeSearch(
            int page,
            int pageSize,
            List<SortExpression<TEntity>> sortExpressions,
            Specification<TEntity> specification,
            boolean asNoTracking
    ) {
        Sort sort = buildSort(sortExpressions);

        int pageNumber = page <= 0 ? 0 : page - 1; // Spring usa 0-based index
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<TEntity> resultPage;

        if (asNoTracking) {
            resultPage = findAllWithNoTracking(specification, pageable);
        } else {
            // Usar el método estándar de Spring Data
            if (specification != null) {
                resultPage = findAll(specification, pageable);
            } else {
                resultPage = findAll(pageable);
            }
        }

        // Convertir a SearchResult
        SearchResult<TEntity> searchResult = new SearchResult<>();
        searchResult.setTotal((int) resultPage.getTotalElements());
        searchResult.setItems(resultPage.getContent());

        return searchResult;
    }

    /**
     * Construye el Sort de Spring Data desde SortExpression
     */
    private Sort buildSort(List<SortExpression<TEntity>> sortExpressions) {
        if (sortExpressions == null || sortExpressions.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (SortExpression<TEntity> sortExpression : sortExpressions) {
            if (sortExpression.getProperty() != null && !sortExpression.getProperty().isEmpty()) {
                Sort.Direction direction = sortExpression.getDirection() == SortDirection.ASC
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;
                orders.add(new Sort.Order(direction, sortExpression.getProperty()));
            }
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    /**
     * Ejecuta findAll con Specification y hints de no tracking
     */
    private Page<TEntity> findAllWithNoTracking(Specification<TEntity> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TEntity> query = cb.createQuery(getDomainClass());
        Root<TEntity> root = query.from(getDomainClass());

        // Aplicar especificación si existe
        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }

        // Aplicar ordenamiento
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                if (order.isAscending()) {
                    orders.add(cb.asc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(order.getProperty())));
                }
            }
            query.orderBy(orders);
        }

        // Query para contar total
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<TEntity> countRoot = countQuery.from(getDomainClass());
        countQuery.select(cb.count(countRoot));

        if (spec != null) {
            Predicate countPredicate = spec.toPredicate(countRoot, countQuery, cb);
            if (countPredicate != null) {
                countQuery.where(countPredicate);
            }
        }

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // Query para obtener items
        TypedQuery<TEntity> typedQuery = entityManager.createQuery(query);

        // Hints para no tracking
        typedQuery.setHint("org.hibernate.readOnly", true);
        typedQuery.setHint("jakarta.persistence.cache.retrieveMode", "BYPASS");

        // Paginación
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<TEntity> content = typedQuery.getResultList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }
}
