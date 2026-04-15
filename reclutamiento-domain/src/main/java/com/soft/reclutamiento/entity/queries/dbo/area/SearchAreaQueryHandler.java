package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.mappers.dbo.AreaMapper;
import com.soft.reclutamiento.entity.queries.base.SearchQueryHandlerBase;
import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.entity.base.SearchResult;
import com.soft.reclutamiento.entity.base.SortDirection;
import com.soft.reclutamiento.entity.base.SortExpression;
import com.soft.reclutamiento.entity.entities.Area;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;
import com.soft.reclutamiento.dto.dbo.area.SearchAreaDto;
import com.soft.reclutamiento.dto.dbo.area.SearchAreaFilterDto;
import com.soft.reclutamiento.infrastructure.repository.AreaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler para SearchAreaQuery
 * Extiende de SearchQueryHandlerBase para validación automática de búsqueda
 */
@Component
public class SearchAreaQueryHandler extends SearchQueryHandlerBase<SearchAreaQuery, SearchAreaFilterDto, SearchAreaDto> {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;

    public SearchAreaQueryHandler(Messages messages, AreaRepository areaRepository, AreaMapper areaMapper) {
        super(messages);
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    @Override
    protected ResponseDto<SearchResultDto<SearchAreaDto>> handleQuery(SearchAreaQuery query) {
        var response = new ResponseDto<SearchResultDto<SearchAreaDto>>();

        // Construir filtros
        Specification<Area> specification = (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();

            // Filtro por activo = true
            predicates.add(criteriaBuilder.equal(root.get("activo"), true));

            // Filtros adicionales desde SearchAreaFilterDto
            var filters = query.getSearchParams().getFilter();
            if (filters != null) {
                if (filters.getIdArea() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("idArea"), filters.getIdArea()));
                }
                if (filters.getFechaDesde() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), filters.getFechaDesde()));
                }
                if (filters.getFechaHasta() != null) {
                    predicates.add(criteriaBuilder.lessThan(root.get("createDate"), filters.getFechaHasta()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        // Construir ordenamiento
        List<SortExpression<Area>> sortExpressions = new ArrayList<>();
        if (query.getSearchParams().getSort() != null) {
            query.getSearchParams().getSort().forEach(sort -> {
                SortDirection direction = SortDirection.valueOf(sort.getDirection().toUpperCase());
                sortExpressions.add(new SortExpression<>(direction, sort.getProperty()));
            });
        }

        // Ejecutar búsqueda
        int page = query.getSearchParams().getPage() != null ? query.getSearchParams().getPage().getPage() : 1;
        int pageSize = query.getSearchParams().getPage() != null ? query.getSearchParams().getPage().getPageSize() : 10;

        SearchResult<Area> searchResult = areaRepository.searchByAsNoTracking(page, pageSize, sortExpressions, specification);

        // Mapear a DTOs
        List<SearchAreaDto> areaDtos = searchResult.getItems().stream()
                .map(areaMapper::toSearchAreaDto)
                .collect(Collectors.toList());

        SearchResultDto<SearchAreaDto> resultDto = new SearchResultDto<>(
                areaDtos,
                searchResult.getTotal(),
                query.getSearchParams()
        );

        response.updateData(resultDto);

        return response;
    }
}
