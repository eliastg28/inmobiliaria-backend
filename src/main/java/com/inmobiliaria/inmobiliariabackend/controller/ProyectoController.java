package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.ProyectoRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.ProyectoResponseDTO;
import com.inmobiliaria.inmobiliariabackend.service.ProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Proyectos", description = "Gestión de proyectos inmobiliarios")
@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @Operation(summary = "Crear un nuevo proyecto", description = "Registra un nuevo proyecto con su ubicación.")
    @PostMapping
    public ResponseEntity<?> crear(@Validated @RequestBody ProyectoRequestDTO dto) {
        try {
            ProyectoResponseDTO creado = proyectoService.guardarProyecto(dto);
            return ResponseEntity.created(URI.create("/api/proyectos/" + creado.getProyectoId())).body(creado);
        } catch (IllegalArgumentException e) {
            // Unicidad del nombre
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Distrito no encontrado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un proyecto existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @Validated @RequestBody ProyectoRequestDTO dto) {
        try {
            ProyectoResponseDTO actualizado = proyectoService.actualizarProyecto(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Unicidad del nombre
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener un proyecto activo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> obtener(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(proyectoService.obtenerPorId(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar todos los proyectos activos")
    @GetMapping
    public ResponseEntity<List<ProyectoResponseDTO>> listarActivos(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(proyectoService.listarActivos(search));
    }

    @Operation(summary = "Eliminar un proyecto (lógicamente)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.noContent().build();
    }
}