package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.service.DistritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Distritos", description = "Gestión de distritos geográficos")
@RestController
@RequestMapping("/api/distritos")
@CrossOrigin(origins = "*")
public class DistritoController {

    private final DistritoService distritoService;

    public DistritoController(DistritoService distritoService) {
        this.distritoService = distritoService;
    }

    @GetMapping
    @Operation(summary = "Listar distritos", description = "Obtiene todos los distritos activos")
    public List<Distrito> listar() {
        return distritoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener distrito por ID", description = "Obtiene un distrito por su ID")
    public ResponseEntity<Distrito> obtenerPorId(@PathVariable UUID id) {
        return distritoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear distrito", description = "Crea un nuevo distrito")
    public ResponseEntity<Distrito> crear(@RequestBody Distrito distrito) {
        return ResponseEntity.ok(distritoService.crear(distrito));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar distrito", description = "Actualiza un distrito existente")
    public ResponseEntity<Distrito> actualizar(@PathVariable UUID id, @RequestBody Distrito distrito) {
        Distrito actualizada = distritoService.actualizar(id, distrito);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar distrito", description = "Elimina un distrito (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        distritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
