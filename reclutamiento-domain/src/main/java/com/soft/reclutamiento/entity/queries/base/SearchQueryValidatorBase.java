package com.soft.reclutamiento.entity.queries.base;

import java.util.List;

/**
 * Validador base para SearchQueries
 * Valida automáticamente los parámetros de búsqueda (paginación, ordenamiento)
 */
public class SearchQueryValidatorBase<TQuery extends SearchQueryBase<TFilter, TResponse>, TFilter, TResponse>
        extends QueryValidatorBase<TQuery> {

    private static final String[] SORT_DIRECTIONS = {"asc", "desc"};

    @Override
    public List<String> validate(TQuery query) {
        var errors = createErrorList();

        // Validar que existan searchParams
        if (query.getSearchParams() == null) {
            errors.add(messages.get("common.search.information.required"));
            return errors;
        }

        var searchParams = query.getSearchParams();

        // Validar paginación
        if (searchParams.getPage() == null) {
            errors.add(messages.get("common.search.page.information.required"));
        } else {
            var page = searchParams.getPage();

            // Validar número de página
            if (page.getPage() == 0) {
                errors.add(messages.fieldRequired(messages.get("common.page.field")));
            } else if (page.getPage() <= 0) {
                errors.add(messages.get("common.page.field.min.value"));
            }

            // Validar tamaño de página
            if (page.getPageSize() == 0) {
                errors.add(messages.fieldRequired(messages.get("common.page.size.field")));
            } else {
                if (page.getPageSize() <= 0) {
                    errors.add(messages.get("common.page.size.field.min.value"));
                }
                if (page.getPageSize() > 1000) {
                    errors.add(messages.get("common.page.size.field.max.value"));
                }
            }
        }

        // Validar ordenamiento
        if (searchParams.getSort() != null && !searchParams.getSort().isEmpty()) {
            for (var sort : searchParams.getSort()) {
                if (sort.getDirection() == null) {
                    errors.add(messages.get("common.sort.direction.required"));
                } else {
                    boolean validDirection = false;
                    for (String validDir : SORT_DIRECTIONS) {
                        if (validDir.equalsIgnoreCase(sort.getDirection())) {
                            validDirection = true;
                            break;
                        }
                    }
                    if (!validDirection) {
                        errors.add(messages.get("common.sort.direction.not.valid"));
                    }
                }
            }
        }

        return errors;
    }
}
