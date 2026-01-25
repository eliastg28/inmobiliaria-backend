package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioDTO {
    private String username;
    private String password;
    private Set<String> roles;
    private Boolean activo = true;
}