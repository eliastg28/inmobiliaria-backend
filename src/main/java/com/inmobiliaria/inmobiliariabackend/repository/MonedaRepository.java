package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonedaRepository extends JpaRepository<Moneda, UUID> {
}
