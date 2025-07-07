package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "estados_lote", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoLote {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "estado_lote_id", updatable = false, nullable = false)
    private UUID estado_lote_id;

    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}
