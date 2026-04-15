package com.soft.reclutamiento.dto.base;

/**
 * DTO para parámetros de ordenamiento
 * Equivalente a SortParamsDto.cs del proyecto .NET
 */
public class SortParamsDto {
    private String property;
    private String direction;

    public SortParamsDto() {
    }

    public SortParamsDto(String property, String direction) {
        this.property = property;
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
