package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, UUID> {
    Optional<EstadoVenta> findByNombre(String nombre);
}
