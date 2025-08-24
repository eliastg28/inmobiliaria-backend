package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Venta;
import com.inmobiliaria.inmobiliariabackend.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Ventas", description = "Gestión de ventas de lotes a clientes")
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    @Operation(summary = "Listar ventas", description = "Obtiene todas las ventas activas")
    public List<Venta> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Obtiene una venta por su ID")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable UUID id) {
        return ventaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear venta", description = "Registra una nueva venta")
    public ResponseEntity<Venta> crear(@Valid @RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.crear(venta));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar venta", description = "Actualiza una venta existente")
    public ResponseEntity<Venta> actualizar(@PathVariable UUID id, @Valid @RequestBody Venta venta) {
        Venta actualizada = ventaService.actualizar(id, venta);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar venta", description = "Elimina una venta (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
