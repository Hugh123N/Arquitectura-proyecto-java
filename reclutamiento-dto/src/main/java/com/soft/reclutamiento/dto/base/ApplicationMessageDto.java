package com.soft.reclutamiento.dto.base;

/**
 * DTO para mensajes de la aplicación
 * Equivalente a ApplicationMessageDto.cs del proyecto .NET
 */
public class ApplicationMessageDto {
    private String key;
    private String message;
    private ApplicationMessageType messageType;

    public ApplicationMessageDto() {
    }

    public ApplicationMessageDto(ApplicationMessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApplicationMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(ApplicationMessageType messageType) {
        this.messageType = messageType;
    }
}
