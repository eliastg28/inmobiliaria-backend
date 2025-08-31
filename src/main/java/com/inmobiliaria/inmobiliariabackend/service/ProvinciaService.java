package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Provincia;
import com.inmobiliaria.inmobiliariabackend.repository.ProvinciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        this.provinciaRepository = provinciaRepository;
    }

    public List<Provincia> listarActivas() {
        return provinciaRepository.findAll()
                .stream()
                .filter(Provincia::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Provincia> obtenerPorId(UUID id) {
        return provinciaRepository.findById(id)
                .filter(Provincia::getActivo);
    }

    /**
     * Busca provincias activas por una parte de su nombre.
     *
     * @param nombre El texto a buscar en el nombre de la provincia.
     * @return Una lista de provincias que coinciden con el criterio de búsqueda.
     */
    public List<Provincia> buscarPorNombre(String nombre) {
        return provinciaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }
}