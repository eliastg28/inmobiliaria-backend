package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Moneda;
import com.inmobiliaria.inmobiliariabackend.repository.MonedaRepository;
import org.springframework.stereotype.Service;

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
        return monedaRepository.findAll()
                .stream()
                .filter(Moneda::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Moneda> obtenerPorId(UUID id) {
        return monedaRepository.findById(id)
                .filter(Moneda::getActivo);
    }

    public Moneda crear(Moneda moneda) {
        return monedaRepository.save(moneda);
    }

    public Moneda actualizar(UUID id, Moneda datos) {
        return monedaRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setSimbolo(datos.getSimbolo());
                    existente.setDescripcion(datos.getDescripcion());
                    return monedaRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        monedaRepository.findById(id).ifPresent(moneda -> {
            moneda.setActivo(false); // eliminación lógica
            monedaRepository.save(moneda);
        });
    }
}
