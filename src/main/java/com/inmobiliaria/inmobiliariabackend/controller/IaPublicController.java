package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.LoteResponseDTO;
import com.inmobiliaria.inmobiliariabackend.dto.ProyectoResponseDTO;
import com.inmobiliaria.inmobiliariabackend.dto.VentaResponseDTO;
import com.inmobiliaria.inmobiliariabackend.service.VentaService;
import com.inmobiliaria.inmobiliariabackend.service.LoteService;
import com.inmobiliaria.inmobiliariabackend.service.ProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ia")
public class IaPublicController {

    private final VentaService ventaService;
    private final LoteService loteService;
    private final ProyectoService proyectoService;

    public IaPublicController(
            VentaService ventaService,
            LoteService loteService,
            ProyectoService proyectoService
    ) {
        this.ventaService = ventaService;
        this.loteService = loteService;
        this.proyectoService = proyectoService;
    }

    @GetMapping("/ventas")
    @Operation(summary = "Listar ventas", description = "Obtiene todas las ventas activas")
    public ResponseEntity<List<VentaResponseDTO>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    @GetMapping("/lotes")
    @Operation(summary = "Listar lotes con paginación", description = "Obtiene los lotes activos paginados")
    public ResponseEntity<Page<LoteResponseDTO>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2000") int size
    ) {
        return ResponseEntity.ok(loteService.listarLotesPaginados(page, size));
    }

    @GetMapping("/proyectos")
    @Operation(summary = "Listar todos los proyectos activos")
    public ResponseEntity<List<ProyectoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(proyectoService.listarActivos());
    }
}

