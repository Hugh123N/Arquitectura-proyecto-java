package com.soft.reclutamiento.entity.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para resultados de búsqueda paginados
 * Equivalente a SearchResult.cs del proyecto .NET
 *
 * @param <TEntity> Tipo de la entidad
 */
public class SearchResult<TEntity> {
    private int total;
    private List<TEntity> items;

    public SearchResult() {
        this.items = new ArrayList<>();
    }

    public SearchResult(List<TEntity> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public SearchResult(int total, List<TEntity> items) {
        this.total = total;
        this.items = items != null ? items : new ArrayList<>();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TEntity> getItems() {
        return items;
    }

    public void setItems(List<TEntity> items) {
        this.items = items;
    }
}
