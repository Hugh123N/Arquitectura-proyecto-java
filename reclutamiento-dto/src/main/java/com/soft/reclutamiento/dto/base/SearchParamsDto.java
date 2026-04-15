package com.soft.reclutamiento.dto.base;

import java.util.List;

/**
 * DTO para parámetros de búsqueda
 * Equivalente a SearchParamsDto.cs del proyecto .NET
 *
 * @param <TFilter> Tipo del filtro específico
 */
public class SearchParamsDto<TFilter> {
    private PageParamsDto page;
    private List<SortParamsDto> sort;
    private TFilter filter;

    public SearchParamsDto() {
    }

    public SearchParamsDto(PageParamsDto page, List<SortParamsDto> sort, TFilter filter) {
        this.page = page;
        this.sort = sort;
        this.filter = filter;
    }

    public PageParamsDto getPage() {
        return page;
    }

    public void setPage(PageParamsDto page) {
        this.page = page;
    }

    public List<SortParamsDto> getSort() {
        return sort;
    }

    public void setSort(List<SortParamsDto> sort) {
        this.sort = sort;
    }

    public TFilter getFilter() {
        return filter;
    }

    public void setFilter(TFilter filter) {
        this.filter = filter;
    }
}
