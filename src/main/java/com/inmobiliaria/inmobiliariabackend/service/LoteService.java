package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.LoteRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.LoteResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.*;
import com.inmobiliaria.inmobiliariabackend.repository.*;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final EstadoLoteRepository estadoLoteRepository;
    private final ProyectoRepository proyectoRepository;
    private final VentaRepository ventaRepository;
    private final EstadoVentaRepository estadoVentaRepository;

    public LoteService(LoteRepository loteRepository, EstadoLoteRepository estadoLoteRepository, ProyectoRepository proyectoRepository, VentaRepository ventaRepository, EstadoVentaRepository estadoVentaRepository) {
        this.loteRepository = loteRepository;
        this.estadoLoteRepository = estadoLoteRepository;
        this.proyectoRepository = proyectoRepository;
        this.ventaRepository = ventaRepository;
        this.estadoVentaRepository = estadoVentaRepository;
    }

    // ----------------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------------

    public LoteResponseDTO guardarLote(LoteRequestDTO dto) {
        // Buscamos el Proyecto (usando el proyectoId del DTO)
        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Validación de unicidad del nombre del lote por PROYECTO
        Optional<Lote> loteExistente = loteRepository.findByNombreAndProyecto(dto.getNombre(), proyecto);
        if (loteExistente.isPresent() && loteExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un lote con este nombre en el proyecto especificado.");
        }

        Lote lote = new Lote();
        mapearDtoALote(dto, lote);
        return mapearLoteADto(loteRepository.save(lote));
    }

    private UUID obtenerIdEstadoVenta(String nombre) {
        return estadoVentaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("Estado de venta '" + nombre + "' no encontrado."))
                .getEstadoVentaId();
    }

    public LoteResponseDTO actualizarLote(UUID id, LoteRequestDTO dto) {

        final UUID ESTADO_VENTA_CANCELADA_ID = obtenerIdEstadoVenta("Cancelada");

        Optional<Venta> ventaAsociada = ventaRepository.findByLoteLoteIdAndFechaEliminacionIsNullAndEstadoVenta_EstadoVentaIdIsNot(
                id,
                ESTADO_VENTA_CANCELADA_ID
        );
        if (ventaAsociada.isPresent()) {
            throw new IllegalArgumentException("No se puede actualizar el lote porque está asociado a una venta.");
        }

        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));

        // Buscamos el nuevo Proyecto
        Proyecto nuevoProyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Validación de unicidad para la actualización por PROYECTO
        if (!dto.getNombre().equalsIgnoreCase(lote.getNombre()) || !nuevoProyecto.equals(lote.getProyecto())) {
            Optional<Lote> loteExistente = loteRepository.findByNombreAndProyecto(dto.getNombre(), nuevoProyecto);
            // Comprobamos que el lote existente no sea el mismo que estamos actualizando
            if (loteExistente.isPresent() && loteExistente.get().getFechaEliminacion() == null && !loteExistente.get().getLoteId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un lote con este nombre en el proyecto especificado.");
            }
        }

        mapearDtoALote(dto, lote);
        return mapearLoteADto(loteRepository.save(lote));
    }

    public LoteResponseDTO obtenerPorId(UUID id) {
        Lote lote = loteRepository.findById(id)
                .filter(l -> l.getFechaEliminacion() == null)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        return mapearLoteADto(lote);
    }

    // ----------------------------------------------------------------------
    // LISTADO Y BÚSQUEDA
    // ----------------------------------------------------------------------

    public List<LoteResponseDTO> listarActivos() {
        return loteRepository.findByFechaEliminacionIsNull().stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> listarActivos(String busqueda) {
        List<LoteResponseDTO> todos = listarActivos();
        if (busqueda == null || busqueda.trim().isEmpty()) return todos;

        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        return todos.stream().filter(dto -> {
            String contenido = TextUtil.limpiarAcentos(
                    (dto.getNombre() != null ? dto.getNombre() : "") + " " +
                            (dto.getDescripcion() != null ? dto.getDescripcion() : "") + " " +
                            (dto.getDireccion() != null ? dto.getDireccion() : "") + " " +
                            (dto.getProyectoNombre() != null ? dto.getProyectoNombre() : "")  + " " +
                            (dto.getEstadoLoteNombre() != null ? dto.getEstadoLoteNombre() : "")
            );
            return java.util.Arrays.stream(palabras).allMatch(p -> contenido.contains(p));
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene lotes disponibles, opcionalmente filtrados por Proyecto.
     * @param proyectoId (Opcional) ID del proyecto a filtrar.
     * @return Lista de LoteResponseDTO disponibles.
     */
    // 🟢 MODIFICADO: Unificamos la lógica de listar disponibles con el filtro de proyecto
    public List<LoteResponseDTO> listarDisponibles(Optional<UUID> proyectoId) {
        final String ESTADO_DISPONIBLE = "Disponible";

        List<Lote> lotes;
        if (proyectoId.isPresent()) {
            // Buscamos disponibles Y por Proyecto
            lotes = loteRepository.findByFechaEliminacionIsNullAndEstadoLote_NombreAndProyecto_ProyectoId(ESTADO_DISPONIBLE, proyectoId.get());
        } else {
            // Buscamos solo disponibles (todos)
            lotes = loteRepository.findByFechaEliminacionIsNullAndEstadoLote_Nombre(ESTADO_DISPONIBLE);
        }

        return lotes.stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> listarDisponibles(Optional<UUID> proyectoId, String busqueda) {
        List<LoteResponseDTO> todos = listarDisponibles(proyectoId);
        if (busqueda == null || busqueda.trim().isEmpty()) return todos;

        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        return todos.stream().filter(dto -> {
            String contenido = TextUtil.limpiarAcentos(
                    (dto.getNombre() != null ? dto.getNombre() : "") + " " +
                            (dto.getDescripcion() != null ? dto.getDescripcion() : "") + " " +
                            (dto.getDireccion() != null ? dto.getDireccion() : "") + " " +
                            (dto.getProyectoNombre() != null ? dto.getProyectoNombre() : "")
            );
            return java.util.Arrays.stream(palabras).allMatch(p -> contenido.contains(p));
        }).collect(Collectors.toList());
    }

    // 🟢 NOTA: El método 'buscarPorProyectoId' que solo busca por Activos (no necesariamente Disponibles)
    // se mantiene si lo usas en otro contexto.
    public List<LoteResponseDTO> buscarPorProyectoId(UUID proyectoId) {
        return loteRepository.findByProyectoProyectoIdAndFechaEliminacionIsNull(proyectoId).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public List<LoteResponseDTO> buscarPorEstado(String estado) {
        return loteRepository.findByEstadoLoteNombreIgnoreCaseAndFechaEliminacionIsNull(estado).stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());
    }

    public Page<LoteResponseDTO> listarLotesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lote> lotePage = loteRepository.findByFechaEliminacionIsNull(pageable);

        List<LoteResponseDTO> loteDTOs = lotePage.stream()
                .map(this::mapearLoteADto)
                .collect(Collectors.toList());

        return new PageImpl<>(loteDTOs, pageable, lotePage.getTotalElements());
    }

    public Page<LoteResponseDTO> listarLotesPaginados(int page, int size, String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) return listarLotesPaginados(page, size);

        // Para un filtrado sencillo, obtenemos todos los activos, filtramos y luego paginamos en memoria
        List<LoteResponseDTO> filtrados = listarActivos(busqueda);
        int fromIndex = Math.min(page * size, filtrados.size());
        int toIndex = Math.min(fromIndex + size, filtrados.size());
        List<LoteResponseDTO> pageList = filtrados.subList(fromIndex, toIndex);
        return new PageImpl<>(pageList, PageRequest.of(page, size), filtrados.size());
    }

    public void eliminarLote(UUID id) {
        loteRepository.findById(id).ifPresent(lote -> {
            // Borrado lógico
            lote.setFechaEliminacion(LocalDateTime.now());
            loteRepository.save(lote);
        });
    }

    // ----------------------------------------------------------------------
    // MAPEO
    // ----------------------------------------------------------------------

    private void mapearDtoALote(LoteRequestDTO dto, Lote lote) {
        lote.setNombre(dto.getNombre());
        lote.setDescripcion(dto.getDescripcion());
        lote.setPrecio(dto.getPrecio());
        lote.setArea(dto.getArea());
        lote.setDireccion(dto.getDireccion());

        EstadoLote estado = estadoLoteRepository.findById(dto.getEstadoLoteId())
                .orElseThrow(() -> new IllegalArgumentException("EstadoLote no encontrado"));
        lote.setEstadoLote(estado);

        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        lote.setProyecto(proyecto);
    }

    private LoteResponseDTO mapearLoteADto(Lote lote) {
        // La ubicación se accede a través de la jerarquía: Lote -> Proyecto -> Distrito
        Proyecto proyecto = lote.getProyecto();
        Distrito distrito = proyecto.getDistrito();
        Provincia provincia = distrito.getProvincia();
        Departamento departamento = provincia.getDepartamento();

        return new LoteResponseDTO(
                lote.getLoteId(),
                lote.getNombre(),
                lote.getDescripcion(),
                lote.getPrecio(),
                lote.getArea(),
                lote.getEstadoLote().getNombre(),
                distrito.getNombre(), // Nombre del distrito (a través de Proyecto)
                lote.getDireccion(),
                lote.getFechaEliminacion() == null,

                // CAMPOS DE PROYECTO AÑADIDOS AL DTO
                proyecto.getProyectoId(),
                proyecto.getNombre(),

                // Información geográfica mantenida
                distrito.getDistritoId(),
                provincia.getProvinciaId(),
                departamento.getDepartamentoId()
        );
    }
}