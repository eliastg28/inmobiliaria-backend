package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.UsuarioRolDTO;
import com.inmobiliaria.inmobiliariabackend.model.UsuarioRol;
import com.inmobiliaria.inmobiliariabackend.service.UsuarioRolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Roles de Usuario", description = "Gestión de los roles de usuario en el sistema")
@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class UsuarioRolController {

    private final UsuarioRolService service;

    public UsuarioRolController(UsuarioRolService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar roles de usuario", description = "Obtiene todos los roles de usuario activos")
    public ResponseEntity<List<UsuarioRol>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol de usuario por ID", description = "Obtiene un rol de usuario por su ID")
    public ResponseEntity<UsuarioRol> obtenerPorId(@PathVariable UUID id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear rol de usuario", description = "Crea un nuevo rol de usuario")
    public ResponseEntity<UsuarioRol> crear(@RequestBody UsuarioRolDTO dto) {
        UsuarioRol nuevoRol = new UsuarioRol();
        nuevoRol.setCodigo(dto.getCodigo());
        nuevoRol.setNombre(dto.getNombre());
        nuevoRol.setActivo(true);

        UsuarioRol creado = service.crear(nuevoRol);
        return ResponseEntity
                .created(URI.create("/api/roles/" + creado.getUsuarioRolId()))
                .body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol de usuario", description = "Actualiza un rol de usuario existente")
    public ResponseEntity<UsuarioRol> actualizar(@PathVariable UUID id, @RequestBody UsuarioRolDTO dto) {
        UsuarioRol rol = new UsuarioRol();
        rol.setCodigo(dto.getCodigo());
        rol.setNombre(dto.getNombre());
        rol.setActivo(dto.getActivo());

        UsuarioRol actualizado = service.actualizar(id, rol);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol de usuario", description = "Elimina un rol de usuario (lógicamente)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
