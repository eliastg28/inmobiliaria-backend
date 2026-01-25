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

    public List<Departamento> listarActivos() {
        return departamentoRepository.findAll()
                .stream()
                .filter(Departamento::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Departamento> obtenerPorId(UUID id) {
        return departamentoRepository.findById(id)
                .filter(Departamento::getActivo);
    }

    /**
     * Busca departamentos activos por una parte de su nombre.
     *
     * @param nombre El texto a buscar en el nombre del departamento.
     * @return Una lista de departamentos que coinciden con el criterio de búsqueda.
     */
    public List<Departamento> buscarPorNombre(String nombre) {
        return departamentoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .filter(Departamento::getActivo)
                .collect(Collectors.toList());
    }
}