package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.MonedaDTO;
import com.inmobiliaria.inmobiliariabackend.model.Moneda;
import com.inmobiliaria.inmobiliariabackend.repository.MonedaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonedaService {

    private final MonedaRepository monedaRepository;

    public MonedaService(MonedaRepository monedaRepository) {
        this.monedaRepository = monedaRepository;
    }

    public List<Moneda> listar() {
        // ✨ Filtrar por fechaEliminacion
        return monedaRepository.findAll()
                .stream()
                .filter(moneda -> moneda.getFechaEliminacion() == null)
                .collect(Collectors.toList());
    }

    public Optional<Moneda> obtenerPorId(UUID id) {
        // ✨ Filtrar por fechaEliminacion
        return monedaRepository.findById(id)
                .filter(moneda -> moneda.getFechaEliminacion() == null);
    }

    public Moneda crear(MonedaDTO dto) {
        // ✨ Nuevo: Validar que no exista una moneda con el mismo nombre
        Optional<Moneda> monedaExistente = monedaRepository.findByNombre(dto.getNombre());
        if (monedaExistente.isPresent() && monedaExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe una moneda con este nombre.");
        }

        Moneda nuevaMoneda = new Moneda();
        nuevaMoneda.setNombre(dto.getNombre());
        nuevaMoneda.setSimbolo(dto.getSimbolo());
        nuevaMoneda.setDescripcion(dto.getDescripcion());

        return monedaRepository.save(nuevaMoneda);
    }

    public Moneda actualizar(UUID id, MonedaDTO dto) {
        return monedaRepository.findById(id)
                .map(existente -> {
                    // ✨ Validar que el nuevo nombre no exista en otra moneda
                    if (!dto.getNombre().equalsIgnoreCase(existente.getNombre())) {
                        Optional<Moneda> monedaExistente = monedaRepository.findByNombre(dto.getNombre());
                        if (monedaExistente.isPresent() && monedaExistente.get().getFechaEliminacion() == null) {
                            throw new IllegalArgumentException("Ya existe una moneda con este nombre.");
                        }
                    }

                    existente.setNombre(dto.getNombre());
                    existente.setSimbolo(dto.getSimbolo());
                    existente.setDescripcion(dto.getDescripcion());
                    return monedaRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        monedaRepository.findById(id).ifPresent(moneda -> {
            // ✨ Borrado lógico
            moneda.setFechaEliminacion(LocalDateTime.now());
            monedaRepository.save(moneda);
        });
    }
}