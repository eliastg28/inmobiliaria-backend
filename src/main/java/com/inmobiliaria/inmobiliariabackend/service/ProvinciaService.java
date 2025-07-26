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

    public List<Provincia> listar() {
        return provinciaRepository.findAll()
                .stream()
                .filter(Provincia::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Provincia> obtenerPorId(UUID id) {
        return provinciaRepository.findById(id)
                .filter(Provincia::getActivo);
    }

    public Provincia crear(Provincia provincia) {
        return provinciaRepository.save(provincia);
    }

    public Provincia actualizar(UUID id, Provincia datos) {
        return provinciaRepository.findById(id)
                .map(p -> {
                    p.setNombre(datos.getNombre());
                    p.setDescripcion(datos.getDescripcion());
                    p.setDepartamento(datos.getDepartamento());
                    return provinciaRepository.save(p);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        provinciaRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            provinciaRepository.save(p);
        });
    }
}
