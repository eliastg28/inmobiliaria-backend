package com.inmobiliaria.inmobiliariabackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ventas", schema = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta extends Auditable { // ✨ Se extiende de Auditable

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ventaId", updatable = false, nullable = false)
    private UUID ventaId;

    @ManyToOne
    @JoinColumn(name = "clienteId", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "loteId", nullable = false)
    @NotNull(message = "El lote es obligatorio")
    private Lote lote;

    @ManyToOne
    @JoinColumn(name = "estadoVentaId", nullable = false)
    @NotNull(message = "El estado de la venta es obligatorio")
    private EstadoVenta estadoVenta;

    @ManyToOne
    @JoinColumn(name = "monedaId", nullable = false)
    @NotNull(message = "La moneda es obligatoria")
    private Moneda moneda;

    @NotNull(message = "La fecha de la venta es obligatoria")
    private LocalDate fechaVenta;

    private Double montoTotal;
}