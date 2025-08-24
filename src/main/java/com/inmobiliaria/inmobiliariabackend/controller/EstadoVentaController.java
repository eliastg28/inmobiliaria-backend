package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.EstadoVenta;
import com.inmobiliaria.inmobiliariabackend.service.EstadoVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<EstadoVenta> listar() {
        return estadoVentaService.listar();
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
    public ResponseEntity<EstadoVenta> crear(@RequestBody EstadoVenta estado) {
        return ResponseEntity.ok(estadoVentaService.crear(estado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estado de venta", description = "Actualiza un estado de venta existente")
    public ResponseEntity<EstadoVenta> actualizar(@PathVariable UUID id, @RequestBody EstadoVenta estado) {
        EstadoVenta actualizado = estadoVentaService.actualizar(id, estado);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estado de venta", description = "Elimina un estado de venta (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        estadoVentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
