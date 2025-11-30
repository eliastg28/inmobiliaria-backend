package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "provincias", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "departamento"})
public class Provincia {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "provinciaId", updatable = false, nullable = false)
    private UUID provinciaId;

    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    // La relación @ManyToOne, por defecto, usa FetchType.EAGER si no se especifica.
    // Sin embargo, Jackson sigue fallando si intenta serializar el objeto completo de la relación.
    @ManyToOne
    @JoinColumn(name = "departamentoId", nullable = false)
    private Departamento departamento;
}