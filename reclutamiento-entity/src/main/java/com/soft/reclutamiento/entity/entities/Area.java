package com.soft.reclutamiento.entity.entities;

import com.soft.reclutamiento.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Area")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Area extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idArea")
    private Integer idArea;

    @Column(name = "nombre", length = 255)
    private String nombre;

    // Los campos de auditoría vienen heredados de AuditableEntity:
    // - userNameCreate (llenado automáticamente con @CreatedBy)
    // - createDate (llenado automáticamente con @CreatedDate)
    // - userNameUpdate (llenado automáticamente con @LastModifiedBy)
    // - updateDate (llenado automáticamente con @LastModifiedDate)
    // - activo (default = true)
    // - rowVersion (para concurrencia optimista con @Version)
}
