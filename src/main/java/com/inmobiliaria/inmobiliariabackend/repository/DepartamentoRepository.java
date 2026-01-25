package com.inmobiliaria.inmobiliariabackend.repository;

import com.inmobiliaria.inmobiliariabackend.model.Departamento;
import com.inmobiliaria.inmobiliariabackend.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {
    Optional<Departamento> findByNombre(String nombre);
    // ✨ Nuevo: Buscar por nombre, insensible a mayúsculas
    List<Departamento> findByNombreContainingIgnoreCase(String nombre);
}
