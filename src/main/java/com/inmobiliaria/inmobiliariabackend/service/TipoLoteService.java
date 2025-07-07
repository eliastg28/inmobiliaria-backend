package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.TipoLote;
import com.inmobiliaria.inmobiliariabackend.repository.TipoLoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TipoLoteService {

    private final TipoLoteRepository tipoLoteRepository;

    public TipoLoteService(TipoLoteRepository repository) {
        this.tipoLoteRepository = repository;
    }

    public List<TipoLote> listar() {
        return tipoLoteRepository.findAll()
                .stream()
                .filter(TipoLote::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<TipoLote> obtenerPorId(UUID id) {
        return tipoLoteRepository.findById(id)
                .filter(TipoLote::getActivo);
    }

    public TipoLote crear(TipoLote tipo) {
        return tipoLoteRepository.save(tipo);
    }

    public TipoLote actualizar(UUID id, TipoLote datos) {
        return tipoLoteRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setDescripcion(datos.getDescripcion());
                    return tipoLoteRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        tipoLoteRepository.findById(id).ifPresent(tipo -> {
            tipo.setActivo(false); // eliminación lógica
            tipoLoteRepository.save(tipo);
        });
    }
}
