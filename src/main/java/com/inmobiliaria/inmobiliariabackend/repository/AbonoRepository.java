package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Abono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AbonoRepository extends JpaRepository<Abono, UUID> {

    /**
     * Encuentra todos los abonos asociados a un ID de venta específico.
     * @param ventaId ID de la Venta
     * @return Lista de Abonos
     */
    List<Abono> findByVenta_VentaId(UUID ventaId);
}