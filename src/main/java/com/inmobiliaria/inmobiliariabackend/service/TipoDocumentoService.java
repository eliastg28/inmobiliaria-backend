package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.TipoDocumento;
import com.inmobiliaria.inmobiliariabackend.repository.TipoDocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoService(TipoDocumentoRepository repository) {
        this.tipoDocumentoRepository = repository;
    }

    public List<TipoDocumento> listar() {
        return tipoDocumentoRepository.findAll()
                .stream()
                .filter(TipoDocumento::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<TipoDocumento> obtenerPorId(UUID id) {
        return tipoDocumentoRepository.findById(id)
                .filter(TipoDocumento::getActivo);
    }

    public TipoDocumento crear(TipoDocumento tipo) {
        return tipoDocumentoRepository.save(tipo);
    }

    public TipoDocumento actualizar(UUID id, TipoDocumento datos) {
        return tipoDocumentoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setDescripcion(datos.getDescripcion());
                    return tipoDocumentoRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        tipoDocumentoRepository.findById(id).ifPresent(tipo -> {
            tipo.setActivo(false); // eliminación lógica
            tipoDocumentoRepository.save(tipo);
        });
    }
}
