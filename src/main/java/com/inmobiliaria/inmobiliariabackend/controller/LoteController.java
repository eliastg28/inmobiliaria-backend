package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.LoteRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.LoteResponseDTO;
import com.inmobiliaria.inmobiliariabackend.service.LoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Lotes", description = "Operaciones relacionadas con los lotes del sistema inmobiliario")
@RestController
@RequestMapping("/api/lotes")
@CrossOrigin(origins = "*")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @Operation(summary = "Crear un nuevo lote", description = "Registra un nuevo lote en el sistema.")
    @ApiResponse(responseCode = "201", description = "Lote creado exitosamente")
    @PostMapping
    public ResponseEntity<?> crear(@Validated @RequestBody LoteRequestDTO dto) {
        try {
            LoteResponseDTO creado = loteService.guardarLote(dto);
            return ResponseEntity.created(URI.create("/api/lotes/" + creado.getLoteId())).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un lote existente", description = "Actualiza la información de un lote usando su ID.")
    @ApiResponse(responseCode = "200", description = "Lote actualizado correctamente")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @Validated @RequestBody LoteRequestDTO dto) {
        try {
            LoteResponseDTO actualizado = loteService.actualizarLote(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener un lote por ID", description = "Devuelve la información detallada de un lote específico que esté activo.")
    @ApiResponse(responseCode = "200", description = "Lote encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<LoteResponseDTO> obtener(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(loteService.obtenerPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar lotes activos", description = "Devuelve una lista de todos los lotes que están activos.")
    @ApiResponse(responseCode = "200", description = "Listado de lotes activos")
    @GetMapping("/activos")
    public ResponseEntity<List<LoteResponseDTO>> listarActivos() {
        return ResponseEntity.ok(loteService.listarActivos());
    }

    @Operation(summary = "Buscar lotes por distrito", description = "Filtra los lotes activos según el nombre del distrito.")
    @ApiResponse(responseCode = "200", description = "Listado de lotes filtrados por distrito")
    @GetMapping("/buscar/distrito")
    public ResponseEntity<List<LoteResponseDTO>> buscarPorDistrito(@RequestParam String nombre) {
        return ResponseEntity.ok(loteService.buscarPorDistrito(nombre));
    }

    @Operation(summary = "Buscar lotes por estado", description = "Filtra los lotes activos según el estado.")
    @ApiResponse(responseCode = "200", description = "Listado de lotes filtrados por estado")
    @GetMapping("/buscar/estado")
    public ResponseEntity<List<LoteResponseDTO>> buscarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(loteService.buscarPorEstado(estado));
    }

    @Operation(summary = "Buscar lotes por ID de distrito", description = "Filtra los lotes activos por el identificador UUID del distrito.")
    @GetMapping("/buscar/distrito/id")
    public ResponseEntity<List<LoteResponseDTO>> buscarPorDistritoId(@RequestParam UUID distritoId) {
        return ResponseEntity.ok(loteService.buscarPorDistritoId(distritoId));
    }

    @GetMapping
    @Operation(summary = "Listar lotes con paginación", description = "Obtiene los lotes activos paginados.")
    public ResponseEntity<Page<LoteResponseDTO>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(loteService.listarLotesPaginados(page, size));
    }

    @Operation(summary = "Eliminar un lote (lógicamente)", description = "Marca un lote como inactivo.")
    @ApiResponse(responseCode = "204", description = "Lote eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        loteService.eliminarLote(id);
        return ResponseEntity.noContent().build();
    }
}