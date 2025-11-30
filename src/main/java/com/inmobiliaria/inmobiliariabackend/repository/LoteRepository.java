package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Lote;
import com.inmobiliaria.inmobiliariabackend.model.Proyecto; // 🟢 NUEVO: Necesitamos importar Proyecto
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteRepository extends JpaRepository<Lote, UUID> {

    // CRUD Base y Borrado Lógico
    List<Lote> findByFechaEliminacionIsNull();
    Page<Lote> findByFechaEliminacionIsNull(Pageable pageable);

    // ❌ ELIMINADO: findByNombre (Demasiado general, usamos la unicidad por Proyecto)

    // 🟢 NUEVO: Validación de unicidad por nombre DENTRO de un Proyecto
    Optional<Lote> findByNombreAndProyecto(String nombre, Proyecto proyecto);

    // Búsqueda por estado
    List<Lote> findByEstadoLoteNombreIgnoreCaseAndFechaEliminacionIsNull(String estado);
    List<Lote> findByFechaEliminacionIsNullAndEstadoLote_Nombre(String nombreEstado);

    // 🟢 NUEVO: Búsqueda por ID de Proyecto (Reemplaza la búsqueda por DistritoId)
    List<Lote> findByProyectoProyectoIdAndFechaEliminacionIsNull(UUID proyectoId);

    List<Lote> findByFechaEliminacionIsNullAndEstadoLote_NombreAndProyecto_ProyectoId(String estadoNombre, UUID proyectoId);
}