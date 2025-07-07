package com.inmobiliaria.inmobiliariabackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "clientes", schema = "crm")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String estado;

    private Integer visitasRealizadas;
    private Integer llamadasNoAtendidas;
    private Integer diasDesdeUltimaVisita;
    private Double ingresosMensuales;
    private String tipoLote;

    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private Boolean activo = true;
}
