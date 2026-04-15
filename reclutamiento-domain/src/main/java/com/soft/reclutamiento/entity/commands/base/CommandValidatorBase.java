package com.soft.reclutamiento.entity.commands.base;

import com.soft.reclutamiento.entity.resources.Messages;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para validadores de Commands
 * Proporciona métodos helper para validaciones comunes
 */
public abstract class CommandValidatorBase<TCommand> {

    @Autowired
    protected Messages messages;

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    /**
     * Método abstracto que debe implementar cada validador específico
     */
    public abstract List<String> validate(TCommand command);

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
     * Helper: Valida longitud máxima de un string
     */
    protected boolean maxLength(String value, int maxLength, String fieldName, List<String> errors) {
        if (value != null && value.length() > maxLength) {
            errors.add(messages.fieldMaxLength(fieldName, maxLength));
            return false;
        }
        return true;
    }

    /**
     * Helper: Valida longitud mínima de un string
     */
    protected boolean minLength(String value, int minLength, String fieldName, List<String> errors) {
        if (value != null && value.length() < minLength) {
            errors.add(messages.fieldMinLength(fieldName, minLength));
            return false;
        }
        return true;
    }

    /**
     * Helper: Valida email
     */
    protected boolean isValidEmail(String email, String fieldName, List<String> errors) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.add(messages.emailInvalid(fieldName));
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
