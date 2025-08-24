package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.EstadoVenta;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoVentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstadoVentaService {

    private final EstadoVentaRepository estadoVentaRepository;

    public EstadoVentaService(EstadoVentaRepository estadoVentaRepository) {
        this.estadoVentaRepository = estadoVentaRepository;
    }

    public List<EstadoVenta> listar() {
        return estadoVentaRepository.findAll()
                .stream()
                .filter(EstadoVenta::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<EstadoVenta> obtenerPorId(UUID id) {
        return estadoVentaRepository.findById(id)
                .filter(EstadoVenta::getActivo);
    }

    public EstadoVenta crear(EstadoVenta estadoVenta) {
        return estadoVentaRepository.save(estadoVenta);
    }

    public EstadoVenta actualizar(UUID id, EstadoVenta estadoVentaActualizado) {
        return estadoVentaRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(estadoVentaActualizado.getNombre());
                    existente.setDescripcion(estadoVentaActualizado.getDescripcion());
                    existente.setActivo(estadoVentaActualizado.getActivo());
                    return estadoVentaRepository.save(existente);
                }).orElseThrow(() -> new RuntimeException("EstadoVenta no encontrado con ID: " + id));
    }

    public void eliminar(UUID id) {
        estadoVentaRepository.findById(id).ifPresent(estado -> {
            estado.setActivo(false);
            estadoVentaRepository.save(estado);
        });
    }
}
