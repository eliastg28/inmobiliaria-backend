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
@Table(name = "monedas", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moneda extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "monedaId", updatable = false, nullable = false)
    private UUID monedaId;

    private String nombre;

    private String simbolo;

    private String descripcion;

}