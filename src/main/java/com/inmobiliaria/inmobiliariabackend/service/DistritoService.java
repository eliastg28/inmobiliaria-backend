package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.repository.DistritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DistritoService {

    private final DistritoRepository distritoRepository;

    public DistritoService(DistritoRepository distritoRepository) {
        this.distritoRepository = distritoRepository;
    }

    public List<Distrito> listar() {
        return distritoRepository.findAll()
                .stream()
                .filter(Distrito::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Distrito> obtenerPorId(UUID id) {
        return distritoRepository.findById(id)
                .filter(Distrito::getActivo);
    }

    public Distrito crear(Distrito distrito) {
        return distritoRepository.save(distrito);
    }

    public Distrito actualizar(UUID id, Distrito datos) {
        return distritoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setDescripcion(datos.getDescripcion());
                    return distritoRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        distritoRepository.findById(id).ifPresent(distrito -> {
            distrito.setActivo(false);
            distritoRepository.save(distrito);
        });
    }
}
