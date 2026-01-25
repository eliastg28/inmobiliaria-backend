package com.inmobiliaria.inmobiliariabackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 👈 NECESARIO
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ventas", schema = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ventaId", updatable = false, nullable = false)
    private UUID ventaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clienteId", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    // 🟢 Ignora los campos de proxy de Hibernate al serializar Venta
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loteId", nullable = false)
    @NotNull(message = "El lote es obligatorio")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadoVentaId", nullable = false)
    @NotNull(message = "El estado de la venta es obligatorio")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EstadoVenta estadoVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monedaId", nullable = false)
    @NotNull(message = "La moneda es obligatoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Moneda moneda;

    @Column(name = "fechaContrato", nullable = true)
    private LocalDate fechaContrato;

    @Column(name = "nroCuotas", nullable = true)
    private Integer nroCuotas;

    private Double montoTotal;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // 🟢 Rompe el bucle Venta -> Abonos -> Venta
    @JsonIgnoreProperties({"venta"})
    private List<Abono> abonos;
}