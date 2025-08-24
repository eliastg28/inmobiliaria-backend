package com.inmobiliaria.inmobiliariabackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "clientes", schema = "crm")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "clienteId", updatable = false, nullable = false)
    private UUID clienteId;

    @NotBlank
    @Size(max = 50)
    private String primerNombre;

    @Size(max = 50)
    private String segundoNombre;

    @NotBlank
    @Size(max = 50)
    private String apellidoPaterno;

    @NotBlank
    @Size(max = 50)
    private String apellidoMaterno;

    @ManyToOne
    @JoinColumn(name = "tipoDocumentoId", nullable = false)
    private TipoDocumento tipoDocumento;

    @NotBlank
    @Size(max = 20)
    private String numeroDocumento;

    @Email
    @Size(max = 100)
    private String correo;

    @Size(max = 20)
    private String telefono;

    private Integer visitasRealizadas;
    private Integer llamadasNoAtendidas;
    private Integer diasDesdeUltimaVisita;
    private Double ingresosMensuales;

    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private Boolean activo = true;
}
