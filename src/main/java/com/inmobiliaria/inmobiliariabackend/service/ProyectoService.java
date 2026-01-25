package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.ProyectoRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.ProyectoResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.Departamento;
import com.inmobiliaria.inmobiliariabackend.model.Distrito;
import com.inmobiliaria.inmobiliariabackend.model.Proyecto;
import com.inmobiliaria.inmobiliariabackend.model.Provincia;
import com.inmobiliaria.inmobiliariabackend.repository.DistritoRepository;
import com.inmobiliaria.inmobiliariabackend.repository.ProyectoRepository;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final DistritoRepository distritoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository, DistritoRepository distritoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.distritoRepository = distritoRepository;
    }

    @Transactional
    public ProyectoResponseDTO guardarProyecto(ProyectoRequestDTO dto) {
        // Normalizar nombre
        String nombreNormalizado = dto.getNombre().trim().toLowerCase();
        // Validar unicidad por nombre (ignorando mayúsculas/minúsculas y espacios)
        proyectoRepository.findByNombreNormalizadoAndFechaEliminacionIsNull(nombreNormalizado)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Ya existe un proyecto activo con el nombre: " + dto.getNombre());
                });

        Proyecto proyecto = new Proyecto();
        mapearDtoAProyecto(dto, proyecto);

        // Al guardar, el conteo de lotes es 0
        return mapearProyectoADto(proyectoRepository.save(proyecto), 0L);
    }

    @Transactional
    public ProyectoResponseDTO actualizarProyecto(UUID id, ProyectoRequestDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .filter(p -> p.getFechaEliminacion() == null)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

        // Normalizar nombre
        String nombreNormalizado = dto.getNombre().trim().toLowerCase();
        // Validar unicidad del nombre al actualizar (ignorando mayúsculas/minúsculas y espacios)
        proyectoRepository.findByNombreNormalizadoAndFechaEliminacionIsNull(nombreNormalizado)
                .filter(p -> !p.getProyectoId().equals(id)) // Excluir el proyecto actual
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Ya existe otro proyecto activo con el nombre: " + dto.getNombre());
                });

        mapearDtoAProyecto(dto, proyecto);

        // Para la respuesta de actualización, debemos obtener el conteo actual de lotes.
        // Se realiza una consulta adicional al repositorio. (Se podría optimizar con la relación @OneToMany)
        Long totalLotes = proyectoRepository.countLotesByProyectoId(id).orElse(0L);

        return mapearProyectoADto(proyectoRepository.save(proyecto), totalLotes);
    }

    public ProyectoResponseDTO obtenerPorId(UUID id) {
        Proyecto proyecto = proyectoRepository.findByProyectoIdAndFechaEliminacionIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

        // Obtener el conteo de lotes para la respuesta
        Long totalLotes = proyectoRepository.countLotesByProyectoId(id).orElse(0L);

        return mapearProyectoADto(proyecto, totalLotes);
    }

    public List<ProyectoResponseDTO> listarActivos() {
        // Usamos la consulta optimizada que devuelve la entidad Proyecto y el Long del conteo.
        List<Object[]> results = proyectoRepository.findProyectosActivosWithLoteCount();

        return results.stream()
                .map(result -> {
                    Proyecto proyecto = (Proyecto) result[0];
                    Long totalLotes = (Long) result[1];
                    return mapearProyectoADto(proyecto, totalLotes);
                })
                .collect(Collectors.toList());
    }

    public List<ProyectoResponseDTO> listarActivos(String busqueda) {
        List<ProyectoResponseDTO> activos = listarActivos();
        if (busqueda == null || busqueda.trim().isEmpty()) return activos;

        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        return activos.stream()
                .filter(p -> {
                    String contenido = TextUtil.limpiarAcentos(
                            (p.getNombre() != null ? p.getNombre() : "") + " " +
                                    (p.getDescripcion() != null ? p.getDescripcion() : "")
                    );
                    return Arrays.stream(palabras).allMatch(x -> contenido.contains(x));
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarProyecto(UUID id) {
        proyectoRepository.findById(id).ifPresent(proyecto -> {
            proyecto.setFechaEliminacion(LocalDateTime.now());
            proyectoRepository.save(proyecto);
        });
    }

    // --- Mapeadores ---

    private void mapearDtoAProyecto(ProyectoRequestDTO dto, Proyecto proyecto) {
        Distrito distrito = distritoRepository.findById(dto.getDistritoId())
                .orElseThrow(() -> new EntityNotFoundException("Distrito no encontrado"));

        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setDistrito(distrito);
    }

    /**
     * Mapeador principal usado para Listar Activos.
     * Incluye el total de lotes.
     */
    private ProyectoResponseDTO mapearProyectoADto(Proyecto proyecto, Long totalLotes) {
        // Aseguramos que las relaciones de Distrito, Provincia y Departamento estén cargadas
        Distrito distrito = proyecto.getDistrito();
        Provincia provincia = distrito.getProvincia();
        Departamento departamento = provincia.getDepartamento();

        return new ProyectoResponseDTO(
                proyecto.getProyectoId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),

                // Distrito
                distrito.getDistritoId(),
                distrito.getNombre(),

                // Provincia
                provincia.getProvinciaId(),
                provincia.getNombre(),

                // Departamento
                departamento.getDepartamentoId(),
                departamento.getNombre(),

                totalLotes, // <-- ¡Asignación del total de lotes!
                proyecto.getFechaEliminacion() == null
        );
    }
}