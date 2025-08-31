package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.VentaRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.VentaResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.*;
import com.inmobiliaria.inmobiliariabackend.repository.*;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final LoteRepository loteRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final MonedaRepository monedaRepository;

    public VentaService(VentaRepository ventaRepository, ClienteRepository clienteRepository,
                        LoteRepository loteRepository, EstadoVentaRepository estadoVentaRepository,
                        MonedaRepository monedaRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.loteRepository = loteRepository;
        this.estadoVentaRepository = estadoVentaRepository;
        this.monedaRepository = monedaRepository;
    }

    public List<VentaResponseDTO> listar() {
        return ventaRepository.findByFechaEliminacionIsNull().stream()
                .map(this::mapearVentaADto)
                .collect(Collectors.toList());
    }

    public Optional<VentaResponseDTO> obtenerPorId(UUID id) {
        return ventaRepository.findById(id)
                .filter(v -> v.getFechaEliminacion() == null)
                .map(this::mapearVentaADto);
    }

    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {
        // ✨ Validar que el lote no tenga una venta activa
        Optional<Venta> ventaExistente = ventaRepository.findByLoteLoteIdAndFechaEliminacionIsNull(dto.getLoteId());
        if (ventaExistente.isPresent()) {
            throw new IllegalArgumentException("El lote ya se encuentra en una venta activa.");
        }

        Venta nuevaVenta = mapearDtoAVenta(dto);
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        return mapearVentaADto(ventaGuardada);
    }

    @Transactional
    public VentaResponseDTO actualizar(UUID id, VentaRequestDTO dto) {
        return ventaRepository.findById(id)
                .map(ventaExistente -> {
                    // ✨ Si el lote cambia, validar que el nuevo lote no tenga una venta activa
                    if (!dto.getLoteId().equals(ventaExistente.getLote().getLoteId())) {
                        Optional<Venta> ventaLoteNuevo = ventaRepository.findByLoteLoteIdAndFechaEliminacionIsNull(dto.getLoteId());
                        if (ventaLoteNuevo.isPresent()) {
                            throw new IllegalArgumentException("El nuevo lote ya se encuentra en una venta activa.");
                        }
                    }

                    Venta ventaActualizada = mapearDtoAVenta(dto, ventaExistente);
                    return mapearVentaADto(ventaRepository.save(ventaActualizada));
                }).orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));
    }

    public void eliminar(UUID id) {
        ventaRepository.findById(id).ifPresent(v -> {
            v.setFechaEliminacion(LocalDateTime.now());
            ventaRepository.save(v);
        });
    }

    private Venta mapearDtoAVenta(VentaRequestDTO dto) {
        return mapearDtoAVenta(dto, new Venta());
    }

    private Venta mapearDtoAVenta(VentaRequestDTO dto, Venta venta) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Lote lote = loteRepository.findById(dto.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        EstadoVenta estadoVenta = estadoVentaRepository.findById(dto.getEstadoVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Estado de Venta no encontrado"));
        Moneda moneda = monedaRepository.findById(dto.getMonedaId())
                .orElseThrow(() -> new IllegalArgumentException("Moneda no encontrada"));

        venta.setCliente(cliente);
        venta.setLote(lote);
        venta.setEstadoVenta(estadoVenta);
        venta.setMoneda(moneda);
        venta.setFechaVenta(dto.getFechaVenta());
        venta.setMontoTotal(dto.getMontoTotal());

        return venta;
    }

    private VentaResponseDTO mapearVentaADto(Venta venta) {
        return new VentaResponseDTO(
                venta.getVentaId(),
                venta.getCliente().getClienteId(),
                venta.getCliente().getPrimerNombre() + " " + venta.getCliente().getApellidoPaterno(),
                venta.getLote().getLoteId(),
                venta.getLote().getNombre(),
                venta.getEstadoVenta().getEstadoVentaId(),
                venta.getEstadoVenta().getNombre(),
                venta.getMoneda().getMonedaId(),
                venta.getMoneda().getNombre(),
                venta.getFechaVenta(),
                venta.getMontoTotal(),
                venta.getFechaEliminacion() == null
        );
    }
}