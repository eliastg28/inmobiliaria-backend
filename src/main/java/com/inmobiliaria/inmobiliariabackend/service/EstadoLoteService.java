package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.EstadoLoteDTO;
import com.inmobiliaria.inmobiliariabackend.model.EstadoLote;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoLoteRepository;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstadoLoteService {

    private final EstadoLoteRepository estadoLoteRepository;

    public EstadoLoteService(EstadoLoteRepository estadoLoteRepository) {
        this.estadoLoteRepository = estadoLoteRepository;
    }

    public List<EstadoLote> listar(String busqueda) {
        // Obtenemos todos los activos
        List<EstadoLote> todos = estadoLoteRepository.findAll()
                .stream()
                .filter(estado -> estado.getFechaEliminacion() == null)
                .collect(Collectors.toList());

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return todos;
        }

        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        return todos.stream()
                .filter(e -> {
                    String contenido = TextUtil.limpiarAcentos(
                            (e.getNombre() != null ? e.getNombre() : "") + " " +
                                    (e.getDescripcion() != null ? e.getDescripcion() : "")
                    );
                    return Arrays.stream(palabras).allMatch(p -> contenido.contains(p));
                })
                .collect(Collectors.toList());
    }

    public Optional<EstadoLote> obtenerPorId(UUID id) {
        // ✨ Filtrar por fechaEliminacion
        return estadoLoteRepository.findById(id)
                .filter(estado -> estado.getFechaEliminacion() == null);
    }

    public EstadoLote crear(EstadoLoteDTO dto) {
        // ✨ Nuevo: Validar que no exista un estado de lote con el mismo nombre
        Optional<EstadoLote> estadoExistente = estadoLoteRepository.findByNombre(dto.getNombre());
        if (estadoExistente.isPresent() && estadoExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un estado de lote con este nombre.");
        }

        EstadoLote nuevoEstado = new EstadoLote();
        nuevoEstado.setNombre(dto.getNombre());
        nuevoEstado.setDescripcion(dto.getDescripcion());

        return estadoLoteRepository.save(nuevoEstado);
    }

    public EstadoLote actualizar(UUID id, EstadoLoteDTO dto) {
        return estadoLoteRepository.findById(id)
                .map(existente -> {
                    // ✨ Validar que el nuevo nombre no exista en otro estado de lote
                    if (!dto.getNombre().equalsIgnoreCase(existente.getNombre())) {
                        Optional<EstadoLote> estadoExistente = estadoLoteRepository.findByNombre(dto.getNombre());
                        if (estadoExistente.isPresent() && estadoExistente.get().getFechaEliminacion() == null) {
                            throw new IllegalArgumentException("Ya existe un estado de lote con este nombre.");
                        }
                    }

                    existente.setNombre(dto.getNombre());
                    existente.setDescripcion(dto.getDescripcion());
                    return estadoLoteRepository.save(existente);
                }).orElseThrow(() -> new IllegalArgumentException("Estado de Lote no encontrado con ID: " + id)); // ✨ Usar IllegalArgumentException para consistencia
    }

    public void eliminar(UUID id) {
        estadoLoteRepository.findById(id).ifPresent(estado -> {
            // ✨ Borrado lógico
            estado.setFechaEliminacion(LocalDateTime.now());
            estadoLoteRepository.save(estado);
        });
    }
}