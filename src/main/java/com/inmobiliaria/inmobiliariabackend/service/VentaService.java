package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Venta;
import com.inmobiliaria.inmobiliariabackend.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public List<Venta> listar() {
        return ventaRepository.findByActivoTrue();
    }

    public Optional<Venta> obtenerPorId(UUID id) {
        return ventaRepository.findById(id).filter(Venta::getActivo);
    }

    public Venta crear(Venta venta) {
        venta.setActivo(true);
        return ventaRepository.save(venta);
    }

    public Venta actualizar(UUID id, Venta venta) {
        return ventaRepository.findById(id)
                .map(existente -> {
                    existente.setCliente(venta.getCliente());
                    existente.setLote(venta.getLote());
                    existente.setEstadoVenta(venta.getEstadoVenta());
                    existente.setMoneda(venta.getMoneda());
                    existente.setFechaVenta(venta.getFechaVenta());
                    existente.setMontoTotal(venta.getMontoTotal());
                    return ventaRepository.save(existente);
                })
                .orElse(null);
    }

    public void eliminar(UUID id) {
        ventaRepository.findById(id).ifPresent(v -> {
            v.setActivo(false);
            ventaRepository.save(v);
        });
    }
}
