package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Ubicacion;
import com.inmobiliaria.inmobiliariabackend.repository.UbicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    public List<Ubicacion> listar() {
        return ubicacionRepository.findAll()
                .stream()
                .filter(Ubicacion::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Ubicacion> obtenerPorId(UUID id) {
        return ubicacionRepository.findById(id)
                .filter(Ubicacion::getActivo);
    }

    public Ubicacion crear(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    public Ubicacion actualizar(UUID id, Ubicacion datos) {
        return ubicacionRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setDescripcion(datos.getDescripcion());
                    return ubicacionRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        ubicacionRepository.findById(id).ifPresent(ubicacion -> {
            ubicacion.setActivo(false);
            ubicacionRepository.save(ubicacion);
        });
    }
}
