package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.TipoLote;
import com.inmobiliaria.inmobiliariabackend.service.TipoLoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<TipoLote> listar() {
        return service.listar();
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
    public ResponseEntity<TipoLote> crear(@RequestBody TipoLote tipo) {
        return ResponseEntity.ok(service.crear(tipo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de lote", description = "Actualiza un tipo de lote existente")
    public ResponseEntity<TipoLote> actualizar(@PathVariable UUID id, @RequestBody TipoLote tipo) {
        TipoLote actualizado = service.actualizar(id, tipo);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de lote", description = "Elimina un tipo de lote (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}