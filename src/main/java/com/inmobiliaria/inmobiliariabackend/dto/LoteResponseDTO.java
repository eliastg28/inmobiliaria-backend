package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteResponseDTO {

    private UUID loteId;

    private String nombre;

    private String descripcion;

    private Double precio;

    private Double area;

    private String estadoLote;

    private String distrito;

    private String direccion;

    private Boolean activo;
}
