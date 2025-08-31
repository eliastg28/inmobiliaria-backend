package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;

@Data
public class UsuarioRolDTO {
    private String nombre;
    private Boolean activo = true;
}
