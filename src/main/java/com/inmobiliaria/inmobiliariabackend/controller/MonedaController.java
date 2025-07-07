package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Moneda;
import com.inmobiliaria.inmobiliariabackend.service.MonedaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Monedas", description = "Gestión de monedas disponibles para transacciones")
@RestController
@RequestMapping("/api/monedas")
@CrossOrigin(origins = "*")
public class MonedaController {

    private final MonedaService monedaService;

    public MonedaController(MonedaService monedaService) {
        this.monedaService = monedaService;
    }

    @GetMapping
    @Operation(summary = "Listar monedas", description = "Obtiene todas las monedas activas")
    public List<Moneda> listar() {
        return monedaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener moneda por ID", description = "Obtiene una moneda por su ID")
    public ResponseEntity<Moneda> obtenerPorId(@PathVariable UUID id) {
        return monedaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear moneda", description = "Crea una nueva moneda")
    public ResponseEntity<Moneda> crear(@RequestBody Moneda moneda) {
        return ResponseEntity.ok(monedaService.crear(moneda));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar moneda", description = "Actualiza una moneda existente")
    public ResponseEntity<Moneda> actualizar(@PathVariable UUID id, @RequestBody Moneda moneda) {
        Moneda actualizada = monedaService.actualizar(id, moneda);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar moneda", description = "Elimina una moneda (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        monedaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
