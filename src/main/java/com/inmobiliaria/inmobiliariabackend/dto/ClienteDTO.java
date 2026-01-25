package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ClienteDTO {
    private String primerNombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private UUID tipoDocumentoId;
    private String numeroDocumento;
    private String correo;
    private String telefono;
    private Double ingresosMensuales;
}