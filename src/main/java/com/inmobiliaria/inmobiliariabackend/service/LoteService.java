package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.LoteRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.LoteResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.model.EstadoLote;
import com.inmobiliaria.inmobiliariabackend.model.Lote;
import com.inmobiliaria.inmobiliariabackend.repository.DistritoRepository;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoLoteRepository;
import com.inmobiliaria.inmobiliariabackend.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final EstadoLoteRepository estadoLoteRepository;
    private final DistritoRepository distritoRepository;

    public LoteService(LoteRepository loteRepository, EstadoLoteRepository estadoLoteRepository, DistritoRepository distritoRepository) {
        this.loteRepository = loteRepository;
        this.estadoLoteRepository = estadoLoteRepository;
        this.distritoRepository = distritoRepository;
    }

    public LoteResponseDTO guardarLote(LoteRequestDTO dto) {
        Distrito distrito = distritoRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new IllegalArgumentException("Distrito no encontrado"));

        // ✨ Nuevo: Validar unicidad del nombre del lote por distrito
        Optional<Lote> loteExistente = loteRepository.findByNombreAndDistrito(dto.getNombre(), distrito);
        if (loteExistente.isPresent() && loteExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un lote con este nombre en el distrito especificado.");
        }

        Lote lote = new Lote();
        mapearDtoALote(dto, lote);
        return mapearLoteADto(loteRepository.save(lote));
    }

    public LoteResponseDTO actualizarLote(UUID id, LoteRequestDTO dto) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));

        Distrito nuevoDistrito = distritoRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new IllegalArgumentException("Distrito no encontrado"));

        // ✨ Nuevo: Validar unicidad para la actualización
        if (!dto.getNombre().equalsIgnoreCase(lote.getNombre()) || !nuevoDistrito.equals(lote.getDistrito())) {
            Optional<Lote> loteExistente = loteRepository.findByNombreAndDistrito(dto.getNombre(), nuevoDistrito);
            if (loteExistente.isPresent() && loteExistente.get().getFechaEliminacion() == null && !loteExistente.get().getLoteId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un lote con este nombre en el distrito especificado.");
            }
        }

        mapearDtoALote(dto, lote);
        return mapearLoteADto(loteRepository.save(lote));
    }

    public LoteResponseDTO obtenerPorId(UUID id) {
        Lote lote = loteRepository.findById(id)
                .filter(l -> l.getFechaEliminacion() == null) // ✨ Filtrar por borrado lógico
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        return mapearLoteADto(lote);
    }

    public List<LoteResponseDTO> listarActivos() {
        // ✨ Usar el nuevo método del repositorio
        return loteRepository.findByFechaEliminacionIsNull().stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> buscarPorDistrito(String nombreDistrito) {
        // ✨ Usar el nuevo método del repositorio
        return loteRepository.findByDistritoNombreContainingIgnoreCaseAndFechaEliminacionIsNull(nombreDistrito).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> buscarPorEstado(String estado) {
        // ✨ Usar el nuevo método del repositorio
        return loteRepository.findByEstadoLoteNombreIgnoreCaseAndFechaEliminacionIsNull(estado).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> buscarPorDistritoId(UUID distritoId) {
        // ✨ Usar el nuevo método del repositorio
        return loteRepository.findByDistritoDistritoIdAndFechaEliminacionIsNull(distritoId).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public Page<LoteResponseDTO> listarLotesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // ✨ Usar el nuevo método del repositorio
        Page<Lote> lotePage = loteRepository.findByFechaEliminacionIsNull(pageable);

        List<LoteResponseDTO> loteDTOs = lotePage.stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());

        return new PageImpl<>(loteDTOs, pageable, lotePage.getTotalElements());
    }

    public void eliminarLote(UUID id) {
        loteRepository.findById(id).ifPresent(lote -> {
            // ✨ Borrado lógico
            lote.setFechaEliminacion(LocalDateTime.now());
            loteRepository.save(lote);
        });
    }

    private void mapearDtoALote(LoteRequestDTO dto, Lote lote) {
        lote.setNombre(dto.getNombre());
        lote.setDescripcion(dto.getDescripcion());
        lote.setPrecio(dto.getPrecio());
        lote.setArea(dto.getArea());
        lote.setDireccion(dto.getDireccion());

        EstadoLote estado = estadoLoteRepository.findById(dto.getEstadoLoteId())
                .orElseThrow(() -> new IllegalArgumentException("EstadoLote no encontrado"));
        lote.setEstadoLote(estado);

        Distrito distrito = distritoRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new IllegalArgumentException("Distrito no encontrado"));
        lote.setDistrito(distrito);
        // ✨ La propiedad activo ya no se mapea desde el DTO
    }

    private LoteResponseDTO mapearLoteADto(Lote lote) {
        return new LoteResponseDTO(
                lote.getLoteId(),
                lote.getNombre(),
                lote.getDescripcion(),
                lote.getPrecio(),
                lote.getArea(),
                lote.getEstadoLote().getNombre(),
                lote.getDistrito().getNombre(),
                lote.getDireccion(),
                lote.getFechaEliminacion() == null // ✨ Se calcula a partir de fechaEliminacion
        );
    }
}