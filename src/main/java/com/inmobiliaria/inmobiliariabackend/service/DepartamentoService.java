package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Departamento;
import com.inmobiliaria.inmobiliariabackend.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public List<Departamento> listar() {
        return departamentoRepository.findAll()
                .stream()
                .filter(Departamento::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Departamento> obtenerPorId(UUID id) {
        return departamentoRepository.findById(id)
                .filter(Departamento::getActivo);
    }

    public Departamento crear(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    public Departamento actualizar(UUID id, Departamento datos) {
        return departamentoRepository.findById(id)
                .map(dep -> {
                    dep.setNombre(datos.getNombre());
                    dep.setDescripcion(datos.getDescripcion());
                    return departamentoRepository.save(dep);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        departamentoRepository.findById(id).ifPresent(dep -> {
            dep.setActivo(false);
            departamentoRepository.save(dep);
        });
    }
}
