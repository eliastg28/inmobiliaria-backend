package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.TipoLoteDTO;
import com.inmobiliaria.inmobiliariabackend.model.TipoLote;
import com.inmobiliaria.inmobiliariabackend.service.TipoLoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Tipos de Lote", description = "Gestión de los tipos de lote disponibles en el sistema")
@RestController
@RequestMapping("/api/tipos-lote")
@CrossOrigin(origins = "*")
public class TipoLoteController {

    private final TipoLoteService service;

    public TipoLoteController(TipoLoteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar tipos de lote", description = "Obtiene todos los tipos de lote activos")
    public ResponseEntity<List<TipoLote>> listar(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.listar(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de lote por ID", description = "Obtiene un tipo de lote por su ID")
    public ResponseEntity<TipoLote> obtenerPorId(@PathVariable UUID id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear tipo de lote", description = "Crea un nuevo tipo de lote")
    public ResponseEntity<?> crear(@RequestBody TipoLoteDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            TipoLote creado = service.crear(dto);
            return ResponseEntity
                    .created(URI.create("/api/tipos-lote/" + creado.getTipoLoteId()))
                    .body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de lote", description = "Actualiza un tipo de lote existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody TipoLoteDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            TipoLote actualizado = service.actualizar(id, dto);
            return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de lote", description = "Elimina un tipo de lote (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}