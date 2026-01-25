package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DistritoRepository extends JpaRepository<Distrito, UUID> {
    Optional<Distrito> findByNombre(String nombre);
    // ✨ Nuevo: Buscar distritos activos por nombre, insensible a mayúsculas
    List<Distrito> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    List<Distrito> findByProvincia_ProvinciaIdAndActivoTrue(UUID provinciaId);
}
