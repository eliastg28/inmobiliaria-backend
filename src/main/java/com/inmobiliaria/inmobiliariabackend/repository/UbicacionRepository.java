package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UbicacionRepository extends JpaRepository<Ubicacion, UUID> {
}
