package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double precio;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double area;

    @NotNull
    private UUID estadoLoteId;

    @NotNull
    private UUID distritoId;

    @Size(max = 200)
    private String direccion;

    private Boolean activo = true;
}
