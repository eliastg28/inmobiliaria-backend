package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.EstadoLote;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoLoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstadoLoteService {

    private final EstadoLoteRepository estadoLoteRepository;

    public EstadoLoteService(EstadoLoteRepository estadoLoteRepository) {
        this.estadoLoteRepository = estadoLoteRepository;
    }

    public List<EstadoLote> listar() {
        return estadoLoteRepository.findAll()
                .stream()
                .filter(EstadoLote::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<EstadoLote> obtenerPorId(UUID id) {
        return estadoLoteRepository.findById(id)
                .filter(EstadoLote::getActivo);
    }

    public EstadoLote crear(EstadoLote estado) {
        return estadoLoteRepository.save(estado);
    }

    public EstadoLote actualizar(UUID id, EstadoLote datos) {
        return estadoLoteRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setDescripcion(datos.getDescripcion());
                    return estadoLoteRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        estadoLoteRepository.findById(id).ifPresent(estado -> {
            estado.setActivo(false);
            estadoLoteRepository.save(estado);
        });
    }
}
