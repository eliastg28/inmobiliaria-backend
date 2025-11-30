package com.inmobiliaria.inmobiliariabackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lotes", schema = "ventas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lote extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "loteId", updatable = false, nullable = false)
    private UUID loteId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El área es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "El área debe ser mayor a 0")
    private Double area;

    @ManyToOne
    @JoinColumn(name = "estadoLoteId", nullable = false)
    @NotNull(message = "El estado del lote es obligatorio")
    private EstadoLote estadoLote;

    @ManyToOne
    @JoinColumn(name = "proyectoId", nullable = false)
    @NotNull(message = "El proyecto es obligatorio")
    @JsonBackReference
    private Proyecto proyecto;

    // Dirección es opcional
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
}