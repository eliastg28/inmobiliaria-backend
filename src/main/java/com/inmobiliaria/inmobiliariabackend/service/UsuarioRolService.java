package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.UsuarioRolDTO;
import com.inmobiliaria.inmobiliariabackend.model.Usuario;
import com.inmobiliaria.inmobiliariabackend.model.UsuarioRol;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRepository;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRolRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioRepository usuarioRepository; // ✨ Nuevo: Inyectar el repositorio de usuarios

    public UsuarioRolService(UsuarioRolRepository repository, UsuarioRepository usuarioRepository) {
        this.usuarioRolRepository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioRol> listar() {
        return usuarioRolRepository.findAll()
                .stream()
                .filter(rol -> rol.getFechaEliminacion() == null)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioRol> obtenerPorId(UUID id) {
        return usuarioRolRepository.findById(id)
                .filter(rol -> rol.getFechaEliminacion() == null);
    }

    public UsuarioRol crear(UsuarioRol rol) {
        // ✨ Nuevo: Validar que no exista un rol activo con el mismo nombre
        Optional<UsuarioRol> rolExistente = usuarioRolRepository.findByNombre(rol.getNombre());
        if (rolExistente.isPresent() && rolExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un rol con este nombre.");
        }
        return usuarioRolRepository.save(rol);
    }

    public UsuarioRol actualizar(UUID id, UsuarioRolDTO dto) {
        return usuarioRolRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(dto.getNombre());
                    // ✨ Eliminado: La lógica de 'activo' ya no es necesaria
                    return usuarioRolRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        usuarioRolRepository.findById(id).ifPresent(rol -> {
            long count = usuarioRepository.countByRolesContains(rol);
            if (count > 0) {
                throw new IllegalStateException("No se puede eliminar el rol, porque está en uso.");
            }

            // ✨ Corregido: Usar fechaEliminacion para el borrado lógico
            rol.setFechaEliminacion(LocalDateTime.now());
            usuarioRolRepository.save(rol);
        });
    }
}