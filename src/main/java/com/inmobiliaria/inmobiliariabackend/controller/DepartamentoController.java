package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Departamento;
import com.inmobiliaria.inmobiliariabackend.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Departamentos", description = "Gestión de departamentos geográficos (solo lectura)")
@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    @Operation(summary = "Listar y buscar departamentos", description = "Obtiene todos los departamentos activos o los filtra por nombre.")
    public ResponseEntity<List<Departamento>> listar(@RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            return ResponseEntity.ok(departamentoService.buscarPorNombre(nombre));
        } else {
            return ResponseEntity.ok(departamentoService.listarActivos());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener departamento por ID", description = "Obtiene un departamento activo por su ID.")
    public ResponseEntity<Departamento> obtenerPorId(@PathVariable UUID id) {
        return departamentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}