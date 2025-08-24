package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "estadosLote", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoLote {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "estadoLoteId", updatable = false, nullable = false)
    private UUID estadoLoteId;

    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}
