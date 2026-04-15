package com.soft.reclutamiento.infrastructure.audit;

/**
 * DTO para detalle de auditoría
 * Equivalente a AuditDetailDto.cs del proyecto .NET
 */
public class AuditDetailDto {
    private String field;
    private String value;

    public AuditDetailDto() {
    }

    public AuditDetailDto(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
