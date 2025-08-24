package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.dto.LoteResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.Lote;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteRepository extends JpaRepository<Lote, UUID> {
    Optional<Lote> findByNombre(String nombre);

    List<Lote> findByDistritoNombreContainingIgnoreCase(String nombreDistrito);

    List<Lote> findByEstadoLoteNombreIgnoreCase(String estado);

    List<Lote> findByActivoTrue();

    List<Lote> findByDistritoDistritoId(UUID distritoId);
}
