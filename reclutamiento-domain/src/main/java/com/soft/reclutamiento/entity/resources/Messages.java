package com.soft.reclutamiento.entity.resources;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Helper para acceder a mensajes i18n
 */
@Component
public class Messages {

    private final MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Obtiene un mensaje sin parámetros
     */
    public String get(String code) {
        return get(code, null);
    }

    /**
     * Obtiene un mensaje con parámetros
     */
    public String get(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, code, locale);
    }

    /**
     * Obtiene un mensaje formateado con parámetros
     */
    public String format(String code, Object... args) {
        return get(code, args);
    }

    // ========== Common Messages ==========
    public String createSuccess() {
        return get("common.create.success");
    }

    public String updateSuccess() {
        return get("common.update.success");
    }

    public String deleteSuccess() {
        return get("common.delete.success");
    }

    public String recordNotFound() {
        return get("common.record.not.found");
    }

    public String deleteRecordNotFound() {
        return get("common.delete.record.not.found");
    }

    public String deleteReferenceError() {
        return get("common.delete.reference.error");
    }

    public String fieldRequired(String fieldName) {
        return format("common.field.required", fieldName);
    }

    public String fieldMinLength(String fieldName, int minLength) {
        return format("common.field.min.length", fieldName, minLength);
    }

    public String fieldMaxLength(String fieldName, int maxLength) {
        return format("common.field.max.length", fieldName, maxLength);
    }

    public String informationRequired() {
        return get("common.information.required");
    }

    public String emailInvalid(String fieldName) {
        return format("common.email.invalid", fieldName);
    }

    public String identifierRequired() {
        return get("common.identifier.required");
    }
}
