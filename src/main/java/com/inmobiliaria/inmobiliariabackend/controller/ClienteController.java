package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.ClienteDTO;
import com.inmobiliaria.inmobiliariabackend.model.Cliente;
import com.inmobiliaria.inmobiliariabackend.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid; // ✨ Para validar DTO con anotaciones
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Clientes", description = "Operaciones relacionadas con clientes")
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*") // ✨ Ajusta esto si quieres restringir por dominio
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes activos")
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente por su ID")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable UUID id) {
        return clienteService.obtenerClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente")
    public ResponseEntity<?> crear(@Valid @RequestBody ClienteDTO dto) { // ✨ Usar @Valid para validar DTO
        try {
            Cliente creado = clienteService.crearCliente(dto);
            return ResponseEntity
                    .created(URI.create("/api/clientes/" + creado.getClienteId()))
                    .body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza un cliente existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @Valid @RequestBody ClienteDTO dto) {
        try {
            Cliente actualizado = clienteService.actualizarCliente(id, dto);
            return actualizado != null
                    ? ResponseEntity.ok(actualizado)
                    : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente (borrado lógico)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
