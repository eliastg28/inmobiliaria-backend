package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tiposDocumento", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumento extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "tipoDocumentoId", updatable = false, nullable = false)
    private UUID tipoDocumentoId;

    private String nombre;

    private String descripcion;

}