package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Ubicacion;
import com.inmobiliaria.inmobiliariabackend.service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Ubicaciones", description = "Gestión de ubicaciones disponibles en el sistema")
@RestController
@RequestMapping("/api/ubicaciones")
@CrossOrigin(origins = "*")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping
    @Operation(summary = "Listar ubicaciones", description = "Obtiene todas las ubicaciones activas")
    public List<Ubicacion> listar() {
        return ubicacionService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ubicación por ID", description = "Obtiene una ubicación por su ID")
    public ResponseEntity<Ubicacion> obtenerPorId(@PathVariable UUID id) {
        return ubicacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear ubicación", description = "Crea una nueva ubicación")
    public ResponseEntity<Ubicacion> crear(@RequestBody Ubicacion ubicacion) {
        return ResponseEntity.ok(ubicacionService.crear(ubicacion));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ubicación", description = "Actualiza una ubicación existente")
    public ResponseEntity<Ubicacion> actualizar(@PathVariable UUID id, @RequestBody Ubicacion ubicacion) {
        Ubicacion actualizada = ubicacionService.actualizar(id, ubicacion);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ubicación", description = "Elimina una ubicación (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
