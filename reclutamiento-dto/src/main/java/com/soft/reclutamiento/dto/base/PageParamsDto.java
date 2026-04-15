package com.soft.reclutamiento.dto.base;

/**
 * DTO para parámetros de paginación
 * Equivalente a PageParamsDto.cs del proyecto .NET
 */
public class PageParamsDto {
    private int page;
    private int pageSize;

    public PageParamsDto() {
    }

    public PageParamsDto(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
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
