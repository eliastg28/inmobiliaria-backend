package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.dto.LoteResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.model.Lote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteRepository extends JpaRepository<Lote, UUID> {
    Optional<Lote> findByNombre(String nombre);
    // ✨ Nuevo: Para la validación de unicidad
    Optional<Lote> findByNombreAndDistrito(String nombre, Distrito distrito);

    // ✨ Filtrar por fechaEliminacion
    List<Lote> findByFechaEliminacionIsNull();

    // ✨ Filtrar por fechaEliminacion
    List<Lote> findByDistritoNombreContainingIgnoreCaseAndFechaEliminacionIsNull(String nombreDistrito);

    // ✨ Filtrar por fechaEliminacion
    List<Lote> findByEstadoLoteNombreIgnoreCaseAndFechaEliminacionIsNull(String estado);

    // ✨ Filtrar por fechaEliminacion
    List<Lote> findByDistritoDistritoIdAndFechaEliminacionIsNull(UUID distritoId);

    // ✨ Filtrar por fechaEliminacion y paginación
    Page<Lote> findByFechaEliminacionIsNull(Pageable pageable);
}
