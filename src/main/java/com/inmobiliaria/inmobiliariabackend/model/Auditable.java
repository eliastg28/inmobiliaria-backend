package com.inmobiliaria.inmobiliariabackend.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "fechaCreacion", nullable = false, updatable = false)
    protected LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fechaModificacion")
    protected LocalDateTime fechaModificacion;

    @Column(name = "fechaEliminacion")
    protected LocalDateTime fechaEliminacion;
}