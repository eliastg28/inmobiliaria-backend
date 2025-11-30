package com.inmobiliaria.inmobiliariabackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proyectos", schema = "ventas")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "proyectoId", updatable = false, nullable = false)
    private UUID proyectoId;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "distritoId", nullable = false)
    @NotNull(message = "El distrito es obligatorio")
    private Distrito distrito;

    @OneToMany(mappedBy = "proyecto") // 'proyecto' debe ser el campo en la entidad Lote que apunta a Proyecto
    @JsonManagedReference
    private List<Lote> lotes;
}