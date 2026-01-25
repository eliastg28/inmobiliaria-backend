package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.EstadoVentaDTO;
import com.inmobiliaria.inmobiliariabackend.model.EstadoVenta;
import com.inmobiliaria.inmobiliariabackend.service.EstadoVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Estados de Venta", description = "Gestión de los estados en los que puede estar una venta")
@RestController
@RequestMapping("/api/estados-venta")
@CrossOrigin(origins = "*")
public class EstadoVentaController {

    private final EstadoVentaService estadoVentaService;

    public EstadoVentaController(EstadoVentaService estadoVentaService) {
        this.estadoVentaService = estadoVentaService;
    }

    @GetMapping
    @Operation(summary = "Listar estados de venta", description = "Obtiene todos los estados de venta activos")
    public ResponseEntity<List<EstadoVenta>> listar(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(estadoVentaService.listar(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estado de venta por ID", description = "Obtiene un estado de venta por su ID")
    public ResponseEntity<EstadoVenta> obtenerPorId(@PathVariable UUID id) {
        return estadoVentaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear estado de venta", description = "Crea un nuevo estado de venta")
    public ResponseEntity<?> crear(@RequestBody EstadoVentaDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            EstadoVenta creado = estadoVentaService.crear(dto);
            return ResponseEntity
                    .created(URI.create("/api/estados-venta/" + creado.getEstadoVentaId()))
                    .body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estado de venta", description = "Actualiza un estado de venta existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody EstadoVentaDTO dto) { // ✨ CORREGIDO: Usar DTO
        try {
            EstadoVenta actualizado = estadoVentaService.actualizar(id, dto);
            return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estado de venta", description = "Elimina un estado de venta (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        estadoVentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}