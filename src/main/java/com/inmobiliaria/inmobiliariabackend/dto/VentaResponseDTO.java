package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private UUID ventaId;
    private UUID clienteId;
    private String clienteNombreCompleto;
    private UUID loteId;
    private String loteNombre;
    private UUID proyectoId;
    private String proyectoNombre;
    private UUID estadoVentaId;
    private String estadoVentaNombre;
    private UUID monedaId;
    private String monedaNombre;
    private LocalDate fechaContrato;
    private Integer nroCuotas;
    private Double montoTotal;
    private Double montoAbonado;
    private Double saldoPendiente;
    private Boolean activo;
}