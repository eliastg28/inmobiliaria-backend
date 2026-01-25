package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VentaRepository extends JpaRepository<Venta, UUID> {
    List<Venta> findByFechaEliminacionIsNull();

    Optional<Venta> findByLoteLoteIdAndFechaEliminacionIsNull(UUID loteId);
    Optional<Venta> findByLoteLoteIdAndFechaEliminacionIsNullAndEstadoVenta_EstadoVentaIdIsNot(
            UUID loteId,
            UUID estadoVentaId
    );
}
