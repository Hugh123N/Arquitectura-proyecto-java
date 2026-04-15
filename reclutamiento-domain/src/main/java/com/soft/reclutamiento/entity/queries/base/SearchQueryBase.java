package com.soft.reclutamiento.entity.queries.base;

import com.soft.reclutamiento.dto.base.SearchParamsDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;

/**
 * Query base para búsquedas con paginación, filtros y ordenamiento
 */
public abstract class SearchQueryBase<TFilter, TResponse> extends Query<SearchResultDto<TResponse>> {

    private SearchParamsDto<TFilter> searchParams;

    public SearchQueryBase(SearchParamsDto<TFilter> searchParams) {
        this.searchParams = searchParams;
    }

    public SearchParamsDto<TFilter> getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SearchParamsDto<TFilter> searchParams) {
        this.searchParams = searchParams;
    }
}
