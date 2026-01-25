package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.EstadoVentaDTO;
import com.inmobiliaria.inmobiliariabackend.model.EstadoVenta;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoVentaRepository;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstadoVentaService {

    private final EstadoVentaRepository estadoVentaRepository;

    public EstadoVentaService(EstadoVentaRepository estadoVentaRepository) {
        this.estadoVentaRepository = estadoVentaRepository;
    }

    public List<EstadoVenta> listar(String busqueda) {
        List<EstadoVenta> todos = estadoVentaRepository.findAll()
                .stream()
                .filter(estado -> estado.getFechaEliminacion() == null)
                .collect(Collectors.toList());

        if (busqueda == null || busqueda.trim().isEmpty()) return todos;

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

    public Optional<EstadoVenta> obtenerPorId(UUID id) {
        // ✨ Filtrar por fechaEliminacion
        return estadoVentaRepository.findById(id)
                .filter(estado -> estado.getFechaEliminacion() == null);
    }

    public EstadoVenta crear(EstadoVentaDTO dto) {
        // ✨ Nuevo: Validar que no exista un estado de venta con el mismo nombre
        Optional<EstadoVenta> estadoExistente = estadoVentaRepository.findByNombre(dto.getNombre());
        if (estadoExistente.isPresent() && estadoExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un estado de venta con este nombre.");
        }

        EstadoVenta nuevoEstado = new EstadoVenta();
        nuevoEstado.setNombre(dto.getNombre());
        nuevoEstado.setDescripcion(dto.getDescripcion());

        return estadoVentaRepository.save(nuevoEstado);
    }

    public EstadoVenta actualizar(UUID id, EstadoVentaDTO dto) {
        return estadoVentaRepository.findById(id)
                .map(existente -> {
                    // ✨ Validar que el nuevo nombre no exista en otro estado de venta
                    if (!dto.getNombre().equalsIgnoreCase(existente.getNombre())) {
                        Optional<EstadoVenta> estadoExistente = estadoVentaRepository.findByNombre(dto.getNombre());
                        if (estadoExistente.isPresent() && estadoExistente.get().getFechaEliminacion() == null) {
                            throw new IllegalArgumentException("Ya existe un estado de venta con este nombre.");
                        }
                    }

                    existente.setNombre(dto.getNombre());
                    existente.setDescripcion(dto.getDescripcion());
                    return estadoVentaRepository.save(existente);
                }).orElseThrow(() -> new IllegalArgumentException("Estado de Venta no encontrado con ID: " + id)); // ✨ Usar IllegalArgumentException para consistencia
    }

    public void eliminar(UUID id) {
        estadoVentaRepository.findById(id).ifPresent(estado -> {
            // ✨ Borrado lógico
            estado.setFechaEliminacion(LocalDateTime.now());
            estadoVentaRepository.save(estado);
        });
    }
}