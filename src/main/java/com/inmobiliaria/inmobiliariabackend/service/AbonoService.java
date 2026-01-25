package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.AbonoDTO;
import com.inmobiliaria.inmobiliariabackend.model.Abono;
import com.inmobiliaria.inmobiliariabackend.model.EstadoVenta;
import com.inmobiliaria.inmobiliariabackend.model.EstadoLote; // 🟢 Importación para el estado del Lote
import com.inmobiliaria.inmobiliariabackend.model.Lote; // 🟢 Importación para manipular el Lote
import com.inmobiliaria.inmobiliariabackend.model.Venta;
import com.inmobiliaria.inmobiliariabackend.repository.AbonoRepository;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoVentaRepository;
import com.inmobiliaria.inmobiliariabackend.repository.EstadoLoteRepository; // 🟢 NUEVO: Repositorio de EstadoLote
import com.inmobiliaria.inmobiliariabackend.repository.LoteRepository; // 🟢 NUEVO: Repositorio de Lote (para guardar el estado)
import com.inmobiliaria.inmobiliariabackend.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class AbonoService {

    private final AbonoRepository abonoRepository;
    private final VentaRepository ventaRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final EstadoLoteRepository estadoLoteRepository; // 🟢 Inyección del nuevo repositorio
    private final LoteRepository loteRepository; // 🟢 Inyección del repositorio de Lote

    // 🟢 Constructor actualizado para inyectar EstadoLoteRepository y LoteRepository
    public AbonoService(AbonoRepository abonoRepository, VentaRepository ventaRepository,
                        EstadoVentaRepository estadoVentaRepository, EstadoLoteRepository estadoLoteRepository,
                        LoteRepository loteRepository) {
        this.abonoRepository = abonoRepository;
        this.ventaRepository = ventaRepository;
        this.estadoVentaRepository = estadoVentaRepository;
        this.estadoLoteRepository = estadoLoteRepository;
        this.loteRepository = loteRepository;
    }

    // Método auxiliar para buscar el estado "Confirmada" de la Venta
    private EstadoVenta obtenerEstadoConfirmada() {
        return estadoVentaRepository.findByNombre("Confirmada")
                .orElseThrow(() -> new EntityNotFoundException("El estado 'Confirmada' no existe en la base de datos."));
    }

    // Método auxiliar para buscar el estado "Vendido" del Lote
    private EstadoLote obtenerEstadoVendido() {
        return estadoLoteRepository.findByNombre("Vendido")
                .orElseThrow(() -> new EntityNotFoundException("El estado de lote 'Vendido' no existe en la base de datos."));
    }

    // Método para calcular el total abonado hasta el momento
    private Double calcularTotalAbonado(UUID ventaId) {
        return abonoRepository.findByVenta_VentaId(ventaId).stream()
                .mapToDouble(Abono::getMontoAbonado)
                .sum();
    }

    /**
     * Registra un nuevo abono a una venta. Incluye validación de saldo y cambio de estado.
     */
    @Transactional
    public Abono crearAbono(AbonoDTO abonoDto) {
        Venta venta = ventaRepository.findById(abonoDto.getVentaId())
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + abonoDto.getVentaId()));

        Double totalAbonadoActual = calcularTotalAbonado(venta.getVentaId());
        Double nuevoMontoAbonado = abonoDto.getMontoAbonado();
        Double montoTotal = venta.getMontoTotal();
        Double saldoPendiente = montoTotal - totalAbonadoActual;

        // 🟢 VALIDACIÓN CRUCIAL: Asegurar que el abono no exceda el saldo pendiente
        // Se usa un pequeño delta (0.001) para manejar la precisión de los flotantes.
        if (nuevoMontoAbonado > saldoPendiente + 0.001) {
            throw new IllegalArgumentException(
                    "El monto del abono (" + nuevoMontoAbonado + ") excede el saldo pendiente de la venta (" + saldoPendiente + ")."
            );
        }

        Abono abono = new Abono();
        abono.setVenta(venta);
        abono.setMontoAbonado(nuevoMontoAbonado);
        abono.setFechaAbono(abonoDto.getFechaAbono());

        // 🟢 LÓGICA DE CAMBIO DE ESTADO
        Double nuevoTotalPagado = totalAbonadoActual + nuevoMontoAbonado;

        // Compara si el nuevo total pagado iguala el monto total (venta completada)
//        if (Double.compare(nuevoTotalPagado, montoTotal) >= 0) {
//
//            // 1. Actualiza el estado de la Venta a "Confirmada"
//            EstadoVenta estadoConfirmada = obtenerEstadoConfirmada();
//            venta.setEstadoVenta(estadoConfirmada);
//            ventaRepository.save(venta); // Guardar el cambio de estado de la Venta
//
//            // 2. 🟢 Actualiza el estado del Lote a "Vendido"
//            EstadoLote estadoVendido = obtenerEstadoVendido();
//            Lote lote = venta.getLote();
//            lote.setEstadoLote(estadoVendido);
//            loteRepository.save(lote); // Guardar el cambio de estado del Lote
//        }

        return abonoRepository.save(abono);
    }

    /**
     * Obtiene todos los abonos para una venta específica.
     */
    public List<Abono> obtenerAbonosPorVenta(UUID ventaId) {
        return abonoRepository.findByVenta_VentaId(ventaId);
    }
}