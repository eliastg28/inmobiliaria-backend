package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.UsuarioDTO;
import com.inmobiliaria.inmobiliariabackend.model.Usuario;
import com.inmobiliaria.inmobiliariabackend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Usuarios", description = "Gestión de usuarios en el sistema")
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios activos")
    public ResponseEntity<List<Usuario>> listar(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(usuarioService.listar(search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario por su ID")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable UUID id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario con roles")
    public ResponseEntity<?> crear(@RequestBody UsuarioDTO dto) {
        try {
            Usuario nuevoUsuario = usuarioService.crear(dto);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(nuevoUsuario.getUsuarioId())
                    .toUri();
            return ResponseEntity.created(location).body(nuevoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody UsuarioDTO dto) {
        try {
            Usuario actualizado = usuarioService.actualizar(id, dto);
            return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario (lógicamente)")
    public ResponseEntity<?> eliminar(@PathVariable UUID id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}