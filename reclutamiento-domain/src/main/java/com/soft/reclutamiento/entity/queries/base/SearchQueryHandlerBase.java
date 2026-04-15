package com.soft.reclutamiento.entity.queries.base;

import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.dto.base.ResponseDto;
import com.soft.reclutamiento.dto.base.SearchResultDto;

import java.util.List;

/**
 * Handler base para SearchQueries
 * Extiende QueryHandlerBase y agrega validación automática de parámetros de búsqueda
 */
public abstract class SearchQueryHandlerBase<TQuery extends SearchQueryBase<TFilter, TResponse>, TFilter, TResponse>
        extends QueryHandlerBase<TQuery, SearchResultDto<TResponse>> {

    protected SearchQueryHandlerBase(Messages messages) {
        super(messages);
    }

    protected SearchQueryHandlerBase(Messages messages, QueryValidatorBase<TQuery> validator) {
        super(messages, validator);
    }

    @Override
    public ResponseDto<SearchResultDto<TResponse>> handle(TQuery query) {
        var response = new ResponseDto<SearchResultDto<TResponse>>();

        try {
            // Si no hay validator, usar el validador por defecto de SearchQuery
            QueryValidatorBase<TQuery> validatorToUse = validator;
            if (validatorToUse == null) {
                validatorToUse = createDefaultValidator();
            }

            // Validación automática
            if (validatorToUse != null) {
                List<String> validationErrors = validatorToUse.validate(query);
                if (!validationErrors.isEmpty()) {
                    validationErrors.forEach(response::addErrorResult);
                    return response;
                }
            }

            // Ejecutar la query
            return handleQuery(query);

        } catch (Exception e) {
            addExceptionQueryResult(response, e);
            return response;
        }
    }

    /**
     * Crea un validador por defecto si no se proporciona uno
     */
    @SuppressWarnings("unchecked")
    private QueryValidatorBase<TQuery> createDefaultValidator() {
        return (QueryValidatorBase<TQuery>) new SearchQueryValidatorBase<TQuery, TFilter, TResponse>();
    }
}
