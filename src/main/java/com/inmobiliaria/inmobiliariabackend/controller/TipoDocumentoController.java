package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.TipoDocumentoDTO;
import com.inmobiliaria.inmobiliariabackend.model.TipoDocumento;
import com.inmobiliaria.inmobiliariabackend.service.TipoDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Tipos de Documento", description = "Gestión de los tipos de documento para usuarios y clientes")
@RestController
@RequestMapping("/api/tipos-documento")
@CrossOrigin(origins = "*")
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    public TipoDocumentoController(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping
    @Operation(summary = "Listar tipos de documento", description = "Obtiene todos los tipos de documento activos")
    public ResponseEntity<List<TipoDocumento>> listar(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(tipoDocumentoService.listar(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de documento por ID", description = "Obtiene un tipo de documento por su ID")
    public ResponseEntity<TipoDocumento> obtenerPorId(@PathVariable UUID id) {
        return tipoDocumentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear tipo de documento", description = "Crea un nuevo tipo de documento")
    public ResponseEntity<?> crear(@RequestBody TipoDocumentoDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            TipoDocumento creado = tipoDocumentoService.crear(dto);
            return ResponseEntity
                    .created(URI.create("/api/tipos-documento/" + creado.getTipoDocumentoId()))
                    .body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de documento", description = "Actualiza un tipo de documento existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody TipoDocumentoDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            TipoDocumento actualizado = tipoDocumentoService.actualizar(id, dto);
            return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de documento", description = "Elimina un tipo de documento (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        tipoDocumentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}