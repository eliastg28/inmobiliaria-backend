package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.UUID;

@Data
public class LoteRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull(message = "El precio es obligatorio")
    private Double precio;
    @NotNull(message = "El área es obligatoria")
    private Double area;
    @NotNull(message = "El estado del lote es obligatorio")
    private UUID estadoLoteId;
    @NotNull(message = "El distrito es obligatorio")
    private UUID distritoId;
    private String direccion;
}