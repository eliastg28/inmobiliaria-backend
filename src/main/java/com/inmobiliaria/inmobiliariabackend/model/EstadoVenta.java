package com.inmobiliaria.inmobiliariabackend.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "estadosVenta", schema = "ventas") // Se convertirá en "estados_venta"
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoVenta {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "estadoVentaId", updatable = false, nullable = false)
    private UUID estadoVentaId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}
