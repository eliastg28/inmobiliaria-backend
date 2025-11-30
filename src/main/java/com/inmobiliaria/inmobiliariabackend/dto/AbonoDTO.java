package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AbonoDTO {

    @NotNull(message = "El ID de la venta es obligatorio")
    private UUID ventaId;

    @NotNull(message = "El monto abonado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser positivo")
    private Double montoAbonado;

    @NotNull(message = "La fecha del abono es obligatoria")
    private LocalDateTime fechaAbono;
}