package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DistritoRepository extends JpaRepository<Distrito, UUID> {
    Optional<Distrito> findByNombre(String nombre);
}
