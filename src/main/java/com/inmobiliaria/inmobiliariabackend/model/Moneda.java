package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "monedas", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moneda {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "moneda_id", updatable = false, nullable = false)
    private UUID moneda_id;

    private String nombre;

    private String simbolo;

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}
