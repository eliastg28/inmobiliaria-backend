package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor // ¡Importante: Este constructor debe coincidir con el orden del mapeador!
public class ProyectoResponseDTO {
    private UUID proyectoId;
    private String nombre;
    private String descripcion;

    // Información de ubicación
    private UUID distritoId;
    private String distritoNombre;
    private UUID provinciaId;
    private String provinciaNombre;
    private UUID departamentoId;
    private String departamentoNombre;

    // Campo de conteo de lotes
    private Long totalLotes;

    private Boolean activo;
}