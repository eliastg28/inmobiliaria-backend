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

    // El nombre del distrito se mantiene, se obtiene a través del Proyecto
    private String distritoNombre;

    private String direccion;
    private Boolean activo;

    // 🟢 NUEVOS CAMPOS: Información del Proyecto
    private UUID proyectoId;
    private String proyectoNombre;

    // CAMPOS GEOGRÁFICOS (Se mantienen para compatibilidad con el frontend)
    private UUID distritoId;
    private UUID provinciaId;
    private UUID departamentoId;
}