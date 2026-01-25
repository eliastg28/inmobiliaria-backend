package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.VentaRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.VentaResponseDTO;
import com.inmobiliaria.inmobiliariabackend.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.net.URI;
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
    public ResponseEntity<List<VentaResponseDTO>> listar(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(ventaService.listar(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Obtiene una venta activa por su ID")
    public ResponseEntity<VentaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ventaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear venta", description = "Registra una nueva venta")
    public ResponseEntity<?> crear(@Valid @RequestBody VentaRequestDTO dto) {
        try {
            VentaResponseDTO creada = ventaService.crear(dto);
            return ResponseEntity
                    .created(URI.create("/api/ventas/" + creada.getVentaId()))
                    .body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar venta", description = "Actualiza una venta existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @Valid @RequestBody VentaRequestDTO dto) {
        try {
            VentaResponseDTO actualizada = ventaService.actualizar(id, dto);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar venta", description = "Elimina una venta (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}