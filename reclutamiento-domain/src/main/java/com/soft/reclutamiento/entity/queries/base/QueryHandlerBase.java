package com.soft.reclutamiento.entity.queries.base;

import an.awesome.pipelinr.Command;
import com.soft.reclutamiento.entity.resources.Messages;
import com.soft.reclutamiento.dto.base.ResponseDto;

import java.util.List;

/**
 * Clase base para handlers de Queries
 * Maneja automáticamente la validación antes de ejecutar la query
 */
public abstract class QueryHandlerBase<TQuery extends Query<TResponse>, TResponse>
        implements Command.Handler<TQuery, ResponseDto<TResponse>> {

    protected final Messages messages;
    protected final QueryValidatorBase<TQuery> validator;

    protected QueryHandlerBase(Messages messages) {
        this.messages = messages;
        this.validator = null;
    }

    protected QueryHandlerBase(Messages messages, QueryValidatorBase<TQuery> validator) {
        this.messages = messages;
        this.validator = validator;
    }

    @Override
    public ResponseDto<TResponse> handle(TQuery query) {
        var response = new ResponseDto<TResponse>();

        try {
            // Validación automática si hay validator
            if (validator != null) {
                List<String> validationErrors = validator.validate(query);
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
     * Método abstracto que debe implementar cada handler específico
     * Aquí va la lógica de consulta
     */
    protected abstract ResponseDto<TResponse> handleQuery(TQuery query);

    /**
     * Agrega información de excepción al response
     */
    protected void addExceptionQueryResult(ResponseDto<TResponse> response, Exception exception) {
        var innerException = getInnerException(exception);
        response.addErrorResult("Error al procesar la consulta: " + innerException.getMessage());
    }

    /**
     * Obtiene la excepción más interna
     */
    private Exception getInnerException(Exception ex) {
        int limit = 10;
        int iteration = 0;
        Exception exception = ex;

        while (exception.getCause() != null && iteration < limit) {
            exception = (Exception) exception.getCause();
            iteration++;
        }

        return exception;
    }
}
