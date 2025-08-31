package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Departamento;
import com.inmobiliaria.inmobiliariabackend.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProvinciaRepository extends JpaRepository<Provincia, UUID> {
    Optional<Provincia> findByNombre(String nombre);
    // ✨ Nuevo: Buscar provincias activas por nombre, insensible a mayúsculas
    List<Provincia> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
