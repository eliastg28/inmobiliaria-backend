package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.UUID;

@Data
public class LoteRequestDTO {

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

    @NotNull(message = "El estado del lote es obligatorio")
    private UUID estadoLoteId;

    // ❌ ELIMINADO: @NotNull(message = "El distrito es obligatorio") private UUID distritoId;
    // 🟢 NUEVO: Ahora el lote pertenece a un proyecto
    @NotNull(message = "El proyecto es obligatorio")
    private UUID proyectoId;

    // Dirección es opcional
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
}