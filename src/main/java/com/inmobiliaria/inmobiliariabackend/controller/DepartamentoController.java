package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Departamento;
import com.inmobiliaria.inmobiliariabackend.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Departamentos", description = "Gestión de departamentos geográficos")
@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    @Operation(summary = "Listar departamentos", description = "Obtiene todos los departamentos activos")
    public List<Departamento> listar() {
        return departamentoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener departamento por ID", description = "Obtiene un departamento por su ID")
    public ResponseEntity<Departamento> obtenerPorId(@PathVariable UUID id) {
        return departamentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear departamento", description = "Crea un nuevo departamento")
    public ResponseEntity<Departamento> crear(@RequestBody Departamento departamento) {
        return ResponseEntity.ok(departamentoService.crear(departamento));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar departamento", description = "Actualiza un departamento existente")
    public ResponseEntity<Departamento> actualizar(@PathVariable UUID id, @RequestBody Departamento departamento) {
        Departamento actualizada = departamentoService.actualizar(id, departamento);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar departamento", description = "Elimina un departamento (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        departamentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
