package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.queries.base.SearchQueryBase;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.dbo.area.SearchAreaDto;
import com.soft.reclutamiento.dto.dbo.area.SearchAreaFilterDto;

/**
 * Query para búsqueda paginada de Areas
 * Extiende de SearchQueryBase para funcionalidad de búsqueda
 */
public class SearchAreaQuery extends SearchQueryBase<SearchAreaFilterDto, SearchAreaDto> {

    public SearchAreaQuery(SearchParamsDto<SearchAreaFilterDto> searchParams) {
        super(searchParams);
    }
}
