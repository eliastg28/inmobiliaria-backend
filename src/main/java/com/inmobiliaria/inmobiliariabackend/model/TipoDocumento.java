package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tipos_documento", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumento {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "tipo_documento_id", updatable = false, nullable = false)
    private UUID tipo_documento_id;

    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}
