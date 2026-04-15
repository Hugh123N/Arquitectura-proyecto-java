package com.soft.reclutamiento.dto.base;

import java.util.UUID;

/**
 * DTO para respuestas de error HTTP
 * Equivalente a ErrorResponseDto.cs del proyecto .NET
 */
public class ErrorResponseDto {
    private UUID identifier;
    private int statusCode;
    private String title;
    private String message;
    private String stackTrace;

    public ErrorResponseDto() {
        this.identifier = UUID.randomUUID();
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
