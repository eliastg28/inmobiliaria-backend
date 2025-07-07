package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.TipoLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TipoLoteRepository extends JpaRepository<TipoLote, UUID> {
}
