package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Provincia;
import com.inmobiliaria.inmobiliariabackend.service.ProvinciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Provincias", description = "Gestión de provincias geográficas (solo lectura)")
@RestController
@RequestMapping("/api/provincias")
@CrossOrigin(origins = "*")
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    public ProvinciaController(ProvinciaService provinciaService) {
        this.provinciaService = provinciaService;
    }

    @GetMapping
    @Operation(summary = "Listar y buscar provincias", description = "Obtiene todas las provincias activas o las filtra por nombre.")
    public ResponseEntity<List<Provincia>> listar(@RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            return ResponseEntity.ok(provinciaService.buscarPorNombre(nombre));
        } else {
            return ResponseEntity.ok(provinciaService.listarActivas());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener provincia por ID", description = "Obtiene una provincia activa por su ID.")
    public ResponseEntity<Provincia> obtenerPorId(@PathVariable UUID id) {
        return provinciaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}