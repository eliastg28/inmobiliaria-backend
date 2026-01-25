package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.TipoLoteDTO;
import com.inmobiliaria.inmobiliariabackend.model.TipoLote;
import com.inmobiliaria.inmobiliariabackend.repository.TipoLoteRepository;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    public List<TipoLote> listar(String busqueda) {
        List<TipoLote> todos = tipoLoteRepository.findAll()
                .stream()
                .filter(tipo -> tipo.getFechaEliminacion() == null)
                .collect(Collectors.toList());

        if (busqueda == null || busqueda.trim().isEmpty()) return todos;

        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        return todos.stream()
                .filter(t -> {
                    String contenido = TextUtil.limpiarAcentos(
                            (t.getNombre() != null ? t.getNombre() : "") + " " +
                                    (t.getDescripcion() != null ? t.getDescripcion() : "")
                    );
                    return Arrays.stream(palabras).allMatch(p -> contenido.contains(p));
                })
                .collect(Collectors.toList());
    }

    public List<TipoLote> listar() {
        // ✨ Filtrar por fechaEliminacion
        return tipoLoteRepository.findAll()
                .stream()
                .filter(tipo -> tipo.getFechaEliminacion() == null)
                .collect(Collectors.toList());
    }

    public Optional<TipoLote> obtenerPorId(UUID id) {
        // ✨ Filtrar por fechaEliminacion
        return tipoLoteRepository.findById(id)
                .filter(tipo -> tipo.getFechaEliminacion() == null);
    }

    public TipoLote crear(TipoLoteDTO dto) {
        // ✨ Nuevo: Validar que no exista un tipo de lote con el mismo nombre
        Optional<TipoLote> tipoExistente = tipoLoteRepository.findByNombre(dto.getNombre());
        if (tipoExistente.isPresent() && tipoExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un tipo de lote con este nombre.");
        }

        TipoLote nuevoTipo = new TipoLote();
        nuevoTipo.setNombre(dto.getNombre());
        nuevoTipo.setDescripcion(dto.getDescripcion());

        return tipoLoteRepository.save(nuevoTipo);
    }

    public TipoLote actualizar(UUID id, TipoLoteDTO dto) {
        return tipoLoteRepository.findById(id)
                .map(existente -> {
                    // ✨ Validar que el nuevo nombre no exista en otro tipo de lote
                    if (!dto.getNombre().equalsIgnoreCase(existente.getNombre())) {
                        Optional<TipoLote> tipoExistente = tipoLoteRepository.findByNombre(dto.getNombre());
                        if (tipoExistente.isPresent() && tipoExistente.get().getFechaEliminacion() == null) {
                            throw new IllegalArgumentException("Ya existe un tipo de lote con este nombre.");
                        }
                    }

                    existente.setNombre(dto.getNombre());
                    existente.setDescripcion(dto.getDescripcion());
                    return tipoLoteRepository.save(existente);
                }).orElse(null);
    }

    public void eliminar(UUID id) {
        tipoLoteRepository.findById(id).ifPresent(tipo -> {
            // ✨ Borrado lógico
            tipo.setFechaEliminacion(LocalDateTime.now());
            tipoLoteRepository.save(tipo);
        });
    }
}