package com.soft.reclutamiento.entity.queries.dbo.area;

import com.soft.reclutamiento.entity.queries.base.SearchQueryBase;
import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.dbo.area.SelectAreaDto;
import com.soft.reclutamiento.dto.dbo.area.SelectAreaFilterDto;

/**
 * Query para selección paginada de Areas
 * Extiende de SearchQueryBase para funcionalidad de búsqueda
 */
public class SelectAreaQuery extends SearchQueryBase<SelectAreaFilterDto, SelectAreaDto> {

    public SelectAreaQuery(SearchParamsDto<SelectAreaFilterDto> searchParams) {
        super(searchParams);
    }
}
