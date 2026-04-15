package com.soft.reclutamiento.entity.queries.base;

import com.soft.reclutamiento.entity.resources.Messages;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para validadores de Queries
 * Proporciona métodos helper para validaciones comunes
 */
public abstract class QueryValidatorBase<TQuery> {

    @Autowired
    protected Messages messages;

    /**
     * Método abstracto que debe implementar cada validador específico
     */
    public abstract List<String> validate(TQuery query);

    /**
     * Helper: Valida que un campo requerido no sea null ni vacío
     */
    protected boolean isRequired(Object value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(messages.fieldRequired(fieldName));
            return false;
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            errors.add(messages.fieldRequired(fieldName));
            return false;
        }
        return true;
    }

    /**
     * Crea una nueva lista de errores vacía
     */
    protected List<String> createErrorList() {
        return new ArrayList<>();
    }
}
