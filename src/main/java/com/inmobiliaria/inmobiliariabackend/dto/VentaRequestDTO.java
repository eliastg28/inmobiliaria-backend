package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class VentaRequestDTO {
    @NotNull(message = "El cliente es obligatorio")
    private UUID clienteId;
    @NotNull(message = "El lote es obligatorio")
    private UUID loteId;
    @NotNull(message = "El estado de la venta es obligatorio")
    private UUID estadoVentaId;
    @NotNull(message = "La moneda es obligatoria")
    private UUID monedaId;
    @NotNull(message = "La fecha de la venta es obligatoria")
    private LocalDate fechaVenta;
    private Double montoTotal;
}