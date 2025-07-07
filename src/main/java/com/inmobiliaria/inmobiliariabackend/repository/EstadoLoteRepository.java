package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.EstadoLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstadoLoteRepository extends JpaRepository<EstadoLote, UUID> {
}
