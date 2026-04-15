package com.soft.reclutamiento.dto.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de respuesta estándar de la aplicación
 * Equivalente a ResponseDto.cs del proyecto .NET
 *
 * @param <T> Tipo de dato que se retorna
 */
public class ResponseDto<T> {
    private T data;
    private List<ApplicationMessageDto> messages;

    public ResponseDto() {
        this.messages = new ArrayList<>();
    }

    public ResponseDto(T data) {
        this();
        this.data = data;
    }

    public ResponseDto(List<ApplicationMessageDto> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
    }

    public ResponseDto(T data, List<ApplicationMessageDto> messages) {
        this.data = data;
        this.messages = messages != null ? messages : new ArrayList<>();
    }

    public ResponseDto(T data, String message) {
        this(data);
        addOkResult(message);
    }

    public ResponseDto(Exception exception) {
        this();
        addErrorResult(exception);
    }

    // Getters y Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void updateData(T data) {
        this.data = data;
    }

    public List<ApplicationMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<ApplicationMessageDto> messages) {
        this.messages = messages;
    }

    /**
     * Indica si la respuesta es válida (no tiene errores)
     */
    public boolean isValid() {
        return messages.stream()
                .noneMatch(m -> m.getMessageType() == ApplicationMessageType.ERROR);
    }

    // Métodos para agregar resultados
    public void addOkResult(String message) {
        addResult(ApplicationMessageType.OK, message);
    }

    public void addInfoResult(String message) {
        addResult(ApplicationMessageType.INFO, message);
    }

    public void addWarningResult(String message) {
        addResult(ApplicationMessageType.WARNING, message);
    }

    public void addErrorResult(String message) {
        addResult(ApplicationMessageType.ERROR, message);
    }

    public void addErrorResult(Exception exception) {
        addResult(ApplicationMessageType.ERROR, getExceptionMessage(exception));
    }

    public void addErrorResult(String message, Exception exception) {
        addResult(ApplicationMessageType.ERROR, message + System.lineSeparator() + getExceptionMessage(exception));
    }

    /**
     * Agrega un resultado con tipo y mensaje específicos
     */
    public void addResult(ApplicationMessageType messageType, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo o vacío");
        }

        ApplicationMessageDto messageDto = new ApplicationMessageDto();
        messageDto.setMessageType(messageType);
        messageDto.setMessage(message);

        this.messages.add(messageDto);
    }

    /**
     * Adjunta los resultados de otra respuesta
     */
    public void attachResults(ResponseDto<?> response) {
        if (response == null) {
            return;
        }

        this.messages.addAll(response.getMessages());
    }

    /**
     * Adjunta los resultados de otra respuesta con un tipo específico
     */
    public void attachResultsWithType(ApplicationMessageType messageType, ResponseDto<?> response) {
        if (response == null) {
            return;
        }

        List<ApplicationMessageDto> typedMessages = response.getMessages().stream()
                .map(m -> {
                    ApplicationMessageDto newMessage = new ApplicationMessageDto();
                    newMessage.setMessageType(messageType);
                    newMessage.setMessage(m.getMessage());
                    newMessage.setKey(m.getKey());
                    return newMessage;
                })
                .collect(Collectors.toList());

        this.messages.addAll(typedMessages);
    }

    /**
     * Obtiene el mensaje formateado de una excepción
     */
    private String getExceptionMessage(Exception exception) {
        if (exception == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(exception.getMessage());

        if (exception.getStackTrace() != null && exception.getStackTrace().length > 0) {
            sb.append(System.lineSeparator());
            for (StackTraceElement element : exception.getStackTrace()) {
                sb.append(element.toString()).append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    /**
     * Obtiene la respuesta formateada como string
     */
    public String getFormattedApiResponse() {
        StringBuilder response = new StringBuilder();

        response.append("Is Valid: ").append(isValid());

        if (!messages.isEmpty()) {
            response.append(System.lineSeparator()).append("Messages: ").append(System.lineSeparator());

            for (ApplicationMessageDto message : messages) {
                response.append("- ")
                        .append(message.getMessageType().name())
                        .append(": ")
                        .append(message.getMessage())
                        .append(System.lineSeparator());
            }
        }

        return response.toString();
    }
}
