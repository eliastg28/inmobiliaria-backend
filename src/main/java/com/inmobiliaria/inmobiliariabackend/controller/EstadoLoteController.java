package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.EstadoLoteDTO;
import com.inmobiliaria.inmobiliariabackend.model.EstadoLote;
import com.inmobiliaria.inmobiliariabackend.service.EstadoLoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Estados de Lote", description = "Gestión de los estados en los que puede estar un lote")
@RestController
@RequestMapping("/api/estados-lote")
@CrossOrigin(origins = "*")
public class EstadoLoteController {

    private final EstadoLoteService estadoLoteService;

    public EstadoLoteController(EstadoLoteService estadoLoteService) {
        this.estadoLoteService = estadoLoteService;
    }

    @GetMapping
    @Operation(summary = "Listar estados de lote", description = "Obtiene todos los estados de lote activos")
    public ResponseEntity<List<EstadoLote>> listar(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(estadoLoteService.listar(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estado de lote por ID", description = "Obtiene un estado de lote por su ID")
    public ResponseEntity<EstadoLote> obtenerPorId(@PathVariable UUID id) {
        return estadoLoteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear estado de lote", description = "Crea un nuevo estado de lote")
    public ResponseEntity<?> crear(@RequestBody EstadoLoteDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            EstadoLote creado = estadoLoteService.crear(dto);
            return ResponseEntity
                    .created(URI.create("/api/estados-lote/" + creado.getEstadoLoteId()))
                    .body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estado de lote", description = "Actualiza un estado de lote existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody EstadoLoteDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            EstadoLote actualizado = estadoLoteService.actualizar(id, dto);
            return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estado de lote", description = "Elimina un estado de lote (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        estadoLoteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}