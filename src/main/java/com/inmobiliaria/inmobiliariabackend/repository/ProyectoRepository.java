package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {

    Optional<Proyecto> findByNombre(String nombre);

    // Obtener proyectos activos (borrado lógico)
    List<Proyecto> findByFechaEliminacionIsNull();

    // Obtener un proyecto por ID que no esté eliminado
    Optional<Proyecto> findByProyectoIdAndFechaEliminacionIsNull(UUID proyectoId);

    // Validación de unicidad: buscar por nombre si no está eliminado
    Optional<Proyecto> findByNombreAndFechaEliminacionIsNull(String nombre);

    // Validación de unicidad: buscar por nombre (ignorando mayúsculas/minúsculas) si no está eliminado
    @Query("SELECT p FROM Proyecto p WHERE LOWER(TRIM(p.nombre)) = LOWER(TRIM(:nombre)) AND p.fechaEliminacion IS NULL")
    Optional<Proyecto> findByNombreNormalizadoAndFechaEliminacionIsNull(@Param("nombre") String nombre);

    // 1. Consulta para listar proyectos activos con conteo de lotes (para listarActivos)
    @Query("SELECT p, COUNT(l) " +
            "FROM Proyecto p LEFT JOIN p.lotes l " +
            "WHERE p.fechaEliminacion IS NULL AND (l.fechaEliminacion IS NULL) " +
            "GROUP BY p.proyectoId")
    List<Object[]> findProyectosActivosWithLoteCount();

    // 2. Consulta simple para obtener el conteo de lotes por ID (para obtenerPorId y actualizarProyecto)
    @Query("SELECT COUNT(l) FROM Lote l WHERE l.proyecto.proyectoId = :proyectoId AND l.fechaEliminacion IS NULL")
    Optional<Long> countLotesByProyectoId(@Param("proyectoId") UUID proyectoId);
}