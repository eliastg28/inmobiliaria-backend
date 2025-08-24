package com.inmobiliaria.inmobiliariabackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegistroUsuarioDTO {
    private String username;
    private String password;
    private List<String> roles;
}
