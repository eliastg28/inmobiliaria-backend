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

    public List<Distrito> listarActivos() {
        return distritoRepository.findAll()
                .stream()
                .filter(Distrito::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Distrito> obtenerPorId(UUID id) {
        return distritoRepository.findById(id)
                .filter(Distrito::getActivo);
    }

    /**
     * Busca distritos activos por una parte de su nombre.
     *
     * @param nombre El texto a buscar en el nombre del distrito.
     * @return Una lista de distritos que coinciden con el criterio de búsqueda.
     */
    public List<Distrito> buscarPorNombre(String nombre) {
        return distritoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public List<Distrito> buscarPorProvinciaId(UUID provinciaId) {
        // Asumiendo que Provincia es una relación dentro de Distrito
        // y que queremos solo los activos.
        return distritoRepository.findByProvincia_ProvinciaIdAndActivoTrue(provinciaId);
    }
}