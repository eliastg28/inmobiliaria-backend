package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.VentaRequestDTO;
import com.inmobiliaria.inmobiliariabackend.dto.VentaResponseDTO;
import com.inmobiliaria.inmobiliariabackend.model.*;
import com.inmobiliaria.inmobiliariabackend.repository.*;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final LoteRepository loteRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final MonedaRepository monedaRepository;
    private final AbonoRepository abonoRepository;
    private final EstadoLoteRepository estadoLoteRepository;

    public VentaService(VentaRepository ventaRepository, ClienteRepository clienteRepository,
                        LoteRepository loteRepository, EstadoVentaRepository estadoVentaRepository,
                        MonedaRepository monedaRepository, AbonoRepository abonoRepository,
                        EstadoLoteRepository estadoLoteRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.loteRepository = loteRepository;
        this.estadoVentaRepository = estadoVentaRepository;
        this.monedaRepository = monedaRepository;
        this.abonoRepository = abonoRepository;
        this.estadoLoteRepository = estadoLoteRepository;
    }

    // --- Métodos Auxiliares para IDs de Estado (Necesarios para la lógica) ---

    private UUID obtenerIdEstadoVenta(String nombre) {
        return estadoVentaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("Estado de venta '" + nombre + "' no encontrado."))
                .getEstadoVentaId();
    }

    private UUID obtenerIdEstadoLote(String nombre) {
        return estadoLoteRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException("Estado de lote '" + nombre + "' no encontrado."))
                .getEstadoLoteId();
    }

    // --- CRUD y Lógica de Negocio ---

    public List<VentaResponseDTO> listar() {
        return ventaRepository.findByFechaEliminacionIsNull().stream()
                .map(this::mapearVentaADto)
                .collect(Collectors.toList());
    }

    public List<VentaResponseDTO> listar(String busqueda) {
        List<VentaResponseDTO> todos = listar();
        if (busqueda == null || busqueda.trim().isEmpty()) return todos;

        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        return todos.stream()
                .filter(v -> {
                    String contenido = TextUtil.limpiarAcentos(
                            (v.getClienteNombreCompleto() != null ? v.getClienteNombreCompleto() : "") + " " +
                                    (v.getLoteNombre() != null ? v.getLoteNombre() : "") + " " +
                                    (v.getProyectoNombre() != null ? v.getProyectoNombre() : "") + " " +
                                    (v.getClienteNombreCompleto() != null ? v.getClienteNombreCompleto() : "") + " " +
                                    (v.getEstadoVentaNombre() != null ? v.getEstadoVentaNombre() : "")
                    );
                    return Arrays.stream(palabras).allMatch(p -> contenido.contains(p));
                })
                .collect(Collectors.toList());
    }

    public Optional<VentaResponseDTO> obtenerPorId(UUID id) {
        return ventaRepository.findById(id)
                .filter(v -> v.getFechaEliminacion() == null)
                .map(this::mapearVentaADto);
    }

    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {
        // 1. Validar que el lote no tenga una venta ACTIVA (excluyendo "Cancelada")

        // 🟢 OBTENER ID DEL ESTADO "CANCELADA"
        final UUID ESTADO_VENTA_CANCELADA_ID = obtenerIdEstadoVenta("Cancelada");

        // 🟢 USAR LA NUEVA CONSULTA DEL REPOSITORIO
        Optional<Venta> ventaExistenteActiva = ventaRepository.findByLoteLoteIdAndFechaEliminacionIsNullAndEstadoVenta_EstadoVentaIdIsNot(
                dto.getLoteId(),
                ESTADO_VENTA_CANCELADA_ID // Excluye ventas que estén en estado "Cancelada"
        );

        if (ventaExistenteActiva.isPresent()) {
            throw new IllegalArgumentException("El lote ya se encuentra en una venta activa (No cancelada).");
        }

        // Si llegamos aquí, el lote está libre para una nueva venta.

        Venta nuevaVenta = mapearDtoAVenta(dto);

        // 2. LÓGICA DE CAMBIO DE ESTADO DE LOTE A "Reservado"
        Lote lote = nuevaVenta.getLote();
        EstadoLote estadoReservado = estadoLoteRepository.findByNombre("Reservado")
                .orElseThrow(() -> new EntityNotFoundException("El estado de lote 'Reservado' no existe en la base de datos."));

        lote.setEstadoLote(estadoReservado);
        loteRepository.save(lote);

        // 3. Guardar la nueva venta
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        return mapearVentaADto(ventaGuardada);
    }

    @Transactional
    public VentaResponseDTO actualizar(UUID id, VentaRequestDTO dto) {
        final UUID ESTADO_VENTA_CANCELADA_ID = obtenerIdEstadoVenta("Cancelada");
        final UUID ESTADO_LOTE_DISPONIBLE_ID = obtenerIdEstadoLote("Disponible");

        return ventaRepository.findById(id)
                .map(ventaExistente -> {
                    // Paso 1: Detección del cambio de estado a "Cancelada"
                    boolean estadoCambiadoACancelada = ESTADO_VENTA_CANCELADA_ID.equals(dto.getEstadoVentaId()) &&
                            !ESTADO_VENTA_CANCELADA_ID.equals(ventaExistente.getEstadoVenta().getEstadoVentaId());

                    // Si el lote cambia, validar que el nuevo lote no tenga una venta activa
                    if (!dto.getLoteId().equals(ventaExistente.getLote().getLoteId())) {
                        Optional<Venta> ventaLoteNuevo = ventaRepository.findByLoteLoteIdAndFechaEliminacionIsNull(dto.getLoteId());
                        if (ventaLoteNuevo.isPresent() && !ventaLoteNuevo.get().getVentaId().equals(id)) {
                            // Asegura que no sea la misma venta que estamos editando
                            throw new IllegalArgumentException("El nuevo lote ya se encuentra en una venta activa.");
                        }
                    }

                    // Paso 2: Ejecutar la lógica de cambio de estado del Lote si se cancela la Venta
                    if (estadoCambiadoACancelada) {
                        Lote loteAActualizar = ventaExistente.getLote();

                        EstadoLote estadoDisponible = estadoLoteRepository.findById(ESTADO_LOTE_DISPONIBLE_ID)
                                .orElseThrow(() -> new IllegalStateException("El estado de lote 'Disponible' no se encontró."));

                        loteAActualizar.setEstadoLote(estadoDisponible);
                        loteRepository.save(loteAActualizar);
                    }

                    if( dto.getFechaContrato() != null ) {
                        Lote loteAActualizar = ventaExistente.getLote();

                        EstadoLote estadoVendido = estadoLoteRepository.findByNombre("Vendido")
                                .orElseThrow(() -> new IllegalStateException("El estado de lote 'Vendido' no se encontró."));

                        EstadoVenta estadoConfirmada = estadoVentaRepository.findByNombre("Confirmada")
                                .orElseThrow(() -> new IllegalStateException("El estado de venta 'Confirmada' no se encontró."));

                        loteAActualizar.setEstadoLote(estadoVendido);
                        loteRepository.save(loteAActualizar);

                        ventaExistente.setEstadoVenta(estadoConfirmada);
                        dto.setEstadoVentaId(estadoConfirmada.getEstadoVentaId());
                    }

                    // Paso 3: Actualizar y guardar la Venta
                    Venta ventaActualizada = mapearDtoAVenta(dto, ventaExistente);
                    return mapearVentaADto(ventaRepository.save(ventaActualizada));
                }).orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(UUID id) {
        ventaRepository.findById(id).ifPresent(v -> {
            v.setFechaEliminacion(LocalDateTime.now());
            ventaRepository.save(v);

            // Lógica de reversión de estado del Lote a "Disponible"
            EstadoLote estadoDisponible = estadoLoteRepository.findByNombre("Disponible")
                    .orElseThrow(() -> new EntityNotFoundException("El estado de lote 'Disponible' no existe en la base de datos."));

            Lote lote = v.getLote();
            lote.setEstadoLote(estadoDisponible);
            loteRepository.save(lote);
        });
    }

    // --- Mapeadores ---

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

        venta.setFechaContrato(dto.getFechaContrato());
        venta.setNroCuotas(dto.getNroCuotas());
        venta.setMontoTotal(dto.getMontoTotal());

        return venta;
    }

    // Método auxiliar para calcular el total abonado
    private Double calcularTotalAbonado(UUID ventaId) {
        return abonoRepository.findByVenta_VentaId(ventaId).stream()
                .mapToDouble(Abono::getMontoAbonado)
                .sum();
    }


    private VentaResponseDTO mapearVentaADto(Venta venta) {

        Double montoAbonado = calcularTotalAbonado(venta.getVentaId());
        Double montoTotal = venta.getMontoTotal() != null ? venta.getMontoTotal() : 0.0;
        Double saldoPendiente = montoTotal - montoAbonado;
        UUID proyectoId = venta.getLote().getProyecto() != null ? venta.getLote().getProyecto().getProyectoId() : null;
        String proyectoNombre = venta.getLote().getProyecto() != null ? venta.getLote().getProyecto().getNombre() : "";

        return new VentaResponseDTO(
                venta.getVentaId(),
                venta.getCliente().getClienteId(),
                venta.getCliente().getPrimerNombre() + " " + venta.getCliente().getApellidoPaterno(),
                venta.getLote().getLoteId(),
                venta.getLote().getNombre(),
                proyectoId,
                proyectoNombre,
                venta.getEstadoVenta().getEstadoVentaId(),
                venta.getEstadoVenta().getNombre(),
                venta.getMoneda().getMonedaId(),
                venta.getMoneda().getNombre(),

                venta.getFechaContrato(),
                venta.getNroCuotas(),
                venta.getMontoTotal(),

                montoAbonado,
                saldoPendiente,

                venta.getFechaEliminacion() == null
        );
    }
}