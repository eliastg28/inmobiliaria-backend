package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Provincia;
import com.inmobiliaria.inmobiliariabackend.service.ProvinciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Provincias", description = "Gestión de provincias geográficas")
@RestController
@RequestMapping("/api/provincias")
@CrossOrigin(origins = "*")
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    public ProvinciaController(ProvinciaService provinciaService) {
        this.provinciaService = provinciaService;
    }

    @GetMapping
    @Operation(summary = "Listar provincias", description = "Obtiene todas las provincias activas")
    public List<Provincia> listar() {
        return provinciaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener provincia por ID", description = "Obtiene una provincia por su ID")
    public ResponseEntity<Provincia> obtenerPorId(@PathVariable UUID id) {
        return provinciaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear provincia", description = "Crea una nueva provincia")
    public ResponseEntity<Provincia> crear(@RequestBody Provincia provincia) {
        return ResponseEntity.ok(provinciaService.crear(provincia));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar provincia", description = "Actualiza una provincia existente")
    public ResponseEntity<Provincia> actualizar(@PathVariable UUID id, @RequestBody Provincia provincia) {
        Provincia actualizada = provinciaService.actualizar(id, provincia);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar provincia", description = "Elimina una provincia (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        provinciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
