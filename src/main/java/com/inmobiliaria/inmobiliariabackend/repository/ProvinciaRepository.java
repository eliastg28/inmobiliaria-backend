package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProvinciaRepository extends JpaRepository<Provincia, UUID> {
}
