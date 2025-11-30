package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "distritos", schema = "catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "provincia"})
public class Distrito {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "distritoId", updatable = false, nullable = false)
    private UUID distritoId;

    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    // La relación @ManyToOne es Lazy, lo que causa el problema de serialización.
    // Aunque la ignoramos arriba, mantenemos la configuración de JPA.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provinciaId", nullable = false)
    private Provincia provincia;
}