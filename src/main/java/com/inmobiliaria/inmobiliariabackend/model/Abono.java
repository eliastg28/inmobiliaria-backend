package com.inmobiliaria.inmobiliariabackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 👈 NECESARIO
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "abonos", schema = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abono extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "abonoId", updatable = false, nullable = false)
    private UUID abonoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ventaId", nullable = false)
    @NotNull(message = "La venta asociada es obligatoria")
    // 🟢 SOLUCIÓN: Rompe el bucle Abono -> Venta.
    // Ignora los proxies de Hibernate y el campo 'abonos' dentro de Venta.
    @JsonIgnoreProperties({"abonos", "hibernateLazyInitializer", "handler"})
    private Venta venta;

    @NotNull(message = "El monto abonado es obligatorio")
    private Double montoAbonado;

    @NotNull(message = "El monto abonado es obligatorio")
    private LocalDateTime fechaAbono;
}