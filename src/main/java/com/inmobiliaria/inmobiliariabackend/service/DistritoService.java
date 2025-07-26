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
        distrito.setActivo(true);
        return distritoRepository.save(distrito);
    }

    public Distrito actualizar(UUID id, Distrito distrito) {
        return distritoRepository.findById(id)
                .map(d -> {
                    d.setNombre(distrito.getNombre());
                    d.setDescripcion(distrito.getDescripcion());
                    d.setProvincia(distrito.getProvincia());
                    return distritoRepository.save(d);
                })
                .orElse(null);
    }

    public void eliminar(UUID id) {
        distritoRepository.findById(id)
                .ifPresent(d -> {
                    d.setActivo(false);
                    distritoRepository.save(d);
                });
    }
}
