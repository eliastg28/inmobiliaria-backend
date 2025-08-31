package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.TipoDocumentoDTO;
import com.inmobiliaria.inmobiliariabackend.model.TipoDocumento;
import com.inmobiliaria.inmobiliariabackend.repository.TipoDocumentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        // ✨ Filtrar por fechaEliminacion
        return tipoDocumentoRepository.findAll()
                .stream()
                .filter(tipo -> tipo.getFechaEliminacion() == null)
                .collect(Collectors.toList());
    }

    public Optional<TipoDocumento> obtenerPorId(UUID id) {
        // ✨ Filtrar por fechaEliminacion
        return tipoDocumentoRepository.findById(id)
                .filter(tipo -> tipo.getFechaEliminacion() == null);
    }

    public TipoDocumento crear(TipoDocumentoDTO dto) {
        // ✨ Nuevo: Validar que no exista un tipo de documento con el mismo nombre
        Optional<TipoDocumento> tipoExistente = tipoDocumentoRepository.findByNombre(dto.getNombre());
        if (tipoExistente.isPresent() && tipoExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un tipo de documento con este nombre.");
        }

        TipoDocumento nuevoTipo = new TipoDocumento();
        nuevoTipo.setNombre(dto.getNombre());
        nuevoTipo.setDescripcion(dto.getDescripcion());

        return tipoDocumentoRepository.save(nuevoTipo);
    }

    public TipoDocumento actualizar(UUID id, TipoDocumentoDTO dto) {
        return tipoDocumentoRepository.findById(id)
                .map(existente -> {
                    // ✨ Validar que el nuevo nombre no exista en otro tipo de documento
                    if (!dto.getNombre().equalsIgnoreCase(existente.getNombre())) {
                        Optional<TipoDocumento> tipoExistente = tipoDocumentoRepository.findByNombre(dto.getNombre());
                        if (tipoExistente.isPresent() && tipoExistente.get().getFechaEliminacion() == null) {
                            throw new IllegalArgumentException("Ya existe un tipo de documento con este nombre.");
                        }
                    }

                    existente.setNombre(dto.getNombre());
                    existente.setDescripcion(dto.getDescripcion());
                    return tipoDocumentoRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        tipoDocumentoRepository.findById(id).ifPresent(tipo -> {
            // ✨ Borrado lógico
            tipo.setFechaEliminacion(LocalDateTime.now());
            tipoDocumentoRepository.save(tipo);
        });
    }
}