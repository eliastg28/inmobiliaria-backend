package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.service.DistritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Distritos", description = "Gestión de distritos geográficos (solo lectura)")
@RestController
@RequestMapping("/api/distritos")
@CrossOrigin(origins = "*")
public class DistritoController {

    private final DistritoService distritoService;

    public DistritoController(DistritoService distritoService) {
        this.distritoService = distritoService;
    }

    @GetMapping
    @Operation(summary = "Listar y buscar distritos", description = "Obtiene todos los distritos activos o los filtra por nombre.")
    public ResponseEntity<List<Distrito>> listar(@RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            return ResponseEntity.ok(distritoService.buscarPorNombre(nombre));
        } else {
            return ResponseEntity.ok(distritoService.listarActivos());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener distrito por ID", description = "Obtiene un distrito activo por su ID.")
    public ResponseEntity<Distrito> obtenerPorId(@PathVariable UUID id) {
        return distritoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/provincia/{provinciaId}")
    @Operation(summary = "Obtener distritos por ID de Provincia", description = "Obtiene todos los distritos que pertenecen a una provincia específica.")
    public ResponseEntity<List<Distrito>> obtenerPorProvinciaId(@PathVariable UUID provinciaId) {
        // Asumiendo que tu DistritoService tiene el método buscarPorProvinciaId
        List<Distrito> distritos = distritoService.buscarPorProvinciaId(provinciaId);

        if (distritos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(distritos);
    }
}