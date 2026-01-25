package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MonedaRepository extends JpaRepository<Moneda, UUID> {
    Optional<Moneda> findByNombre(String nombre);
}
