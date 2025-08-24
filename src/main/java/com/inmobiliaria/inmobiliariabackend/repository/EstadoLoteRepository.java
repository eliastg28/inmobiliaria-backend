package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.EstadoLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoLoteRepository extends JpaRepository<EstadoLote, UUID> {
    Optional<EstadoLote> findByNombre(String nombre);
}
