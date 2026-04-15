package com.soft.reclutamiento.infrastructure.audit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para auditoría
 * Equivalente a AuditDto.cs del proyecto .NET
 */
public class AuditDto {
    private Operation operation;
    private String entity;
    private String identifier;
    private String userName;
    private LocalDateTime date;
    private String clientName;
    private String clientIP;
    private String module;
    private List<AuditDetailDto> details;

    public AuditDto() {
        this.details = new ArrayList<>();
        this.date = LocalDateTime.now();
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<AuditDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<AuditDetailDto> details) {
        this.details = details;
    }
}
