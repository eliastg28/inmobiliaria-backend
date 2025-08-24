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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoteService {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private EstadoLoteRepository estadoLoteRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    public LoteResponseDTO guardarLote(LoteRequestDTO dto) {
        Lote lote = new Lote();
        mapearDtoALote(dto, lote);
        return mapearLoteADto(loteRepository.save(lote));
    }

    public LoteResponseDTO actualizarLote(UUID id, LoteRequestDTO dto) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));
        mapearDtoALote(dto, lote);
        return mapearLoteADto(loteRepository.save(lote));
    }

    public LoteResponseDTO obtenerPorId(UUID id) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));
        return mapearLoteADto(lote);
    }

    public List<LoteResponseDTO> listarActivos() {
        return loteRepository.findByActivoTrue().stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> buscarPorDistrito(String nombreDistrito) {
        return loteRepository.findByDistritoNombreContainingIgnoreCase(nombreDistrito).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> buscarPorEstado(String estado) {
        return loteRepository.findByEstadoLoteNombreIgnoreCase(estado).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    private void mapearDtoALote(LoteRequestDTO dto, Lote lote) {
        lote.setNombre(dto.getNombre());
        lote.setDescripcion(dto.getDescripcion());
        lote.setPrecio(dto.getPrecio());
        lote.setArea(dto.getArea());
        lote.setDireccion(dto.getDireccion());
        lote.setActivo(dto.getActivo());

        EstadoLote estado = estadoLoteRepository.findById(dto.getEstadoLoteId())
                .orElseThrow(() -> new EntityNotFoundException("EstadoLote no encontrado"));
        lote.setEstadoLote(estado);

        Distrito distrito = distritoRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new EntityNotFoundException("Distrito no encontrado"));
        lote.setDistrito(distrito);
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
                lote.getActivo()
        );
    }

    public List<LoteResponseDTO> buscarPorDistritoId(UUID distritoId) {
        return loteRepository.findByDistritoDistritoId(distritoId).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public Page<LoteResponseDTO> listarLotesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lote> lotePage = loteRepository.findAll(pageable);

        List<LoteResponseDTO> loteDTOs = lotePage.stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());

        return new PageImpl<>(loteDTOs, pageable, lotePage.getTotalElements());
    }
}
