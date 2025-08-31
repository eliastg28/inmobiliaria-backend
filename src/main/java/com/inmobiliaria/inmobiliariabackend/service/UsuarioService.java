package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.UsuarioDTO;
import com.inmobiliaria.inmobiliariabackend.model.Usuario;
import com.inmobiliaria.inmobiliariabackend.model.UsuarioRol;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRepository;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ROL_PROPIETARIO = "PROPIETARIO";

    public List<Usuario> listar() {
        // Obtener el usuario autenticado para aplicar las reglas de visibilidad
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> authenticatedUser = usuarioRepository.findByUsername(authenticatedUsername);

        boolean isPropietario = authenticatedUser.isPresent() && authenticatedUser.get().getRoles().stream()
                .anyMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre()));

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario -> usuario.getFechaEliminacion() == null)
                .filter(usuario -> isPropietario || usuario.getRoles().stream().noneMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre())))
                .collect(Collectors.toList());
    }

    public Optional<Usuario> obtenerPorId(UUID id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> usuario.getFechaEliminacion() == null);
    }

    public Usuario crear(UsuarioDTO dto) {
        // Validar unicidad del rol PROPIETARIO
        if (dto.getRoles().contains(ROL_PROPIETARIO)) {
            long countPropietarios = usuarioRepository.findAll().stream()
                    .filter(u -> u.getRoles().stream().anyMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre())) && u.getFechaEliminacion() == null)
                    .count();
            if (countPropietarios > 0) {
                throw new IllegalStateException("Solo puede existir un usuario con el rol de PROPIETARIO.");
            }
        }

        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(dto.getUsername());
        if (usuarioExistente.isPresent() && usuarioExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(dto.getUsername());
        nuevo.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<UsuarioRol> roles = dto.getRoles().stream()
                .map(nombreRol -> usuarioRolRepository.findByNombre(nombreRol)
                        .orElseThrow(() -> new IllegalArgumentException("Rol con nombre " + nombreRol + " no encontrado")))
                .collect(Collectors.toSet());

        nuevo.setRoles(roles);

        return usuarioRepository.save(nuevo);
    }

    public Usuario actualizar(UUID id, UsuarioDTO dto) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> authenticatedUser = usuarioRepository.findByUsername(authenticatedUsername);
        boolean isPropietario = authenticatedUser.isPresent() && authenticatedUser.get().getRoles().stream()
                .anyMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre()));

        return usuarioRepository.findById(id)
                .map(existente -> {
                    // Validar si el usuario autenticado no es el propietario y si el usuario a actualizar es propietario
                    if (!isPropietario && existente.getRoles().stream().anyMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre()))) {
                        throw new IllegalStateException("Solo el usuario PROPIETARIO puede actualizarse a sí mismo.");
                    }

                    // Si es un ADMIN, no puede cambiarse su propio estado ni roles a sí mismo
                    if (existente.getUsername().equals(authenticatedUsername) && !isPropietario) {
                        if (dto.getActivo() != null && !dto.getActivo().equals(existente.getActivo())) {
                            throw new IllegalStateException("Un administrador no puede cambiar su propio estado de 'activo'.");
                        }
                        if (dto.getRoles() != null) {
                            throw new IllegalStateException("Un administrador no puede cambiar sus propios roles.");
                        }
                    }

                    // El resto de la lógica de actualización
                    Optional<Usuario> otroUsuarioConMismoUsername = usuarioRepository.findByUsername(dto.getUsername());
                    if (otroUsuarioConMismoUsername.isPresent() && !otroUsuarioConMismoUsername.get().getUsuarioId().equals(id)) {
                        throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsername() + "' ya está en uso por otro usuario.");
                    }

                    existente.setUsername(dto.getUsername());

                    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                        existente.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }

                    if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
                        Set<UsuarioRol> nuevosRoles = dto.getRoles().stream()
                                .map(nombreRol -> usuarioRolRepository.findByNombre(nombreRol)
                                        .orElseThrow(() -> new IllegalArgumentException("Rol con nombre " + nombreRol + " no encontrado")))
                                .collect(Collectors.toSet());
                        existente.setRoles(nuevosRoles);
                    }

                    if (dto.getActivo() != null) {
                        existente.setActivo(dto.getActivo());
                    }

                    return usuarioRepository.save(existente);
                }).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    public void eliminar(UUID id) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> authenticatedUser = usuarioRepository.findByUsername(authenticatedUsername);
        boolean isPropietario = authenticatedUser.isPresent() && authenticatedUser.get().getRoles().stream()
                .anyMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre()));

        usuarioRepository.findById(id).ifPresent(usuario -> {
            // Validar si el usuario autenticado no es el propietario y si el usuario a eliminar es propietario
            if (!isPropietario && usuario.getRoles().stream().anyMatch(rol -> ROL_PROPIETARIO.equals(rol.getNombre()))) {
                throw new IllegalStateException("Solo el usuario PROPIETARIO puede eliminarse a sí mismo.");
            }

            // Un usuario no puede eliminarse a sí mismo (a menos que sea propietario, validado arriba)
            if (usuario.getUsername().equals(authenticatedUsername)) {
                throw new IllegalStateException("Un usuario no puede eliminarse a sí mismo.");
            }

            // Los administradores no pueden eliminar a otros administradores
            boolean isUserBeingDeletedAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getNombre().equals("ADMIN"));
            if (isUserBeingDeletedAdmin && !isPropietario) {
                throw new IllegalStateException("No tienes permisos para eliminar a otro administrador.");
            }

            usuario.setActivo(false);
            usuario.setFechaEliminacion(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }
}