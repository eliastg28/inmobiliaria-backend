package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoteResponseDTO {
    private UUID loteId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Double area;
    private String estadoLoteNombre;
    private String distritoNombre;
    private String direccion;
    private Boolean activo;
}