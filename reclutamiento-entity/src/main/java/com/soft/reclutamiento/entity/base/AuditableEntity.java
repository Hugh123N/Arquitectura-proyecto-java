package com.soft.reclutamiento.entity.base;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Clase base abstracta para entidades auditables
 * Contiene campos comunes de auditoría que se llenan automáticamente
 * Equivalente a las propiedades de auditoría en Entity.cs del proyecto .NET
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedBy
    @Column(name = "userNameCreate", length = 250, updatable = false)
    private String userNameCreate;

    @CreatedDate
    @Column(name = "createDate", updatable = false)
    private OffsetDateTime createDate;

    @LastModifiedBy
    @Column(name = "userNameUpdate", length = 250)
    private String userNameUpdate;

    @LastModifiedDate
    @Column(name = "updateDate")
    private OffsetDateTime updateDate;

    @Column(name = "activo")
    private Boolean activo = true;

    @Version
    @Column(name = "rowVersion")
    private byte[] rowVersion;

    // Getters y Setters

    public String getUserNameCreate() {
        return userNameCreate;
    }

    public void setUserNameCreate(String userNameCreate) {
        this.userNameCreate = userNameCreate;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public String getUserNameUpdate() {
        return userNameUpdate;
    }

    public void setUserNameUpdate(String userNameUpdate) {
        this.userNameUpdate = userNameUpdate;
    }

    public OffsetDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(OffsetDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public byte[] getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(byte[] rowVersion) {
        this.rowVersion = rowVersion;
    }

    /**
     * Hook ejecutado antes de persistir la entidad
     */
    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
    }
}
