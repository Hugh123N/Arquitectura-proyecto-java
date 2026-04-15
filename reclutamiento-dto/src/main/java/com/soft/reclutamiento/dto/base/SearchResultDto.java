package com.soft.reclutamiento.dto.base;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para resultados de búsqueda paginados
 * Equivalente a SearchResultDto.cs del proyecto .NET
 *
 * @param <TResult> Tipo de los elementos del resultado
 */
public class SearchResultDto<TResult> {
    private List<TResult> items;
    private TResult data;
    private int total;
    private int page;
    private int pageSize;

    public SearchResultDto() {
        this.items = new ArrayList<>();
        this.page = 1;
    }

    public SearchResultDto(List<TResult> items) {
        this.items = items != null ? items : new ArrayList<>();
        this.total = this.items.size();
        this.page = 1;
        this.pageSize = this.items.size();
    }

    public SearchResultDto(TResult data, int total, int pageSize) {
        this.items = new ArrayList<>();
        this.data = data;
        this.total = total;
        this.page = 1;
        this.pageSize = pageSize;
    }

    public SearchResultDto(List<TResult> items, int total) {
        this.items = items != null ? items : new ArrayList<>();
        this.total = Math.max(total, this.items.size());
        this.page = 1;
        this.pageSize = this.items.size();
    }

    public SearchResultDto(List<TResult> items, int total, int page, int pageSize) {
        this.items = items != null ? items : new ArrayList<>();
        this.total = Math.max(total, this.items.size());
        this.page = page;
        this.pageSize = pageSize;
    }

    public SearchResultDto(List<TResult> items, int total, SearchParamsDto<?> searchParams) {
        this.items = items != null ? items : new ArrayList<>();
        this.total = Math.max(total, this.items.size());

        if (searchParams != null && searchParams.getPage() != null) {
            this.page = searchParams.getPage().getPage();
            this.pageSize = searchParams.getPage().getPageSize();
        } else {
            this.page = 1;
            this.pageSize = this.items.size();
        }
    }

    public SearchResultDto(TResult data, int total, SearchParamsDto<?> searchParams) {
        this.items = new ArrayList<>();
        this.data = data;
        this.total = total;

        if (searchParams != null && searchParams.getPage() != null) {
            this.page = searchParams.getPage().getPage();
            this.pageSize = searchParams.getPage().getPageSize();
        } else {
            this.page = 1;
            this.pageSize = total;
        }
    }

    // Getters y Setters
    public List<TResult> getItems() {
        return items;
    }

    public void setItems(List<TResult> items) {
        this.items = items;
    }

    public TResult getData() {
        return data;
    }

    public void setData(TResult data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
