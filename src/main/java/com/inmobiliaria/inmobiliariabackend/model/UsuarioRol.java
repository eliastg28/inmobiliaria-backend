package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "usuariosRol", schema = "core")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "usuarioRolId", updatable = false, nullable = false)
    private UUID usuarioRolId;

    @Column(nullable = false, length = 50)
    private String nombre;
}