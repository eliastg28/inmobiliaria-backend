package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.UsuarioRol;
import com.inmobiliaria.inmobiliariabackend.repository.UsuarioRolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioRolService {

    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioRolService(UsuarioRolRepository repository) {
        this.usuarioRolRepository = repository;
    }

    public List<UsuarioRol> listar() {
        return usuarioRolRepository.findAll()
                .stream()
                .filter(UsuarioRol::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioRol> obtenerPorId(UUID id) {
        return usuarioRolRepository.findById(id)
                .filter(UsuarioRol::getActivo);
    }

    public UsuarioRol crear(UsuarioRol rol) {
        return usuarioRolRepository.save(rol);
    }

    public UsuarioRol actualizar(UUID id, UsuarioRol datos) {
        return usuarioRolRepository.findById(id)
                .map(existente -> {
                    existente.setCodigo(datos.getCodigo());
                    existente.setNombre(datos.getNombre());
                    return usuarioRolRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        usuarioRolRepository.findById(id).ifPresent(rol -> {
            rol.setActivo(false);
            usuarioRolRepository.save(rol);
        });
    }

}
