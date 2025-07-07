package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.model.Cliente;
import com.inmobiliaria.inmobiliariabackend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll()
                .stream()
                .filter(Cliente::getActivo)
                .collect(Collectors.toList());
    }

    public Optional<Cliente> obtenerClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .filter(Cliente::getActivo);
    }

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(UUID id, Cliente nuevo) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombres(nuevo.getNombres());
                    cliente.setApellidos(nuevo.getApellidos());
                    cliente.setCorreo(nuevo.getCorreo());
                    cliente.setTelefono(nuevo.getTelefono());
                    cliente.setEstado(nuevo.getEstado());
                    cliente.setVisitasRealizadas(nuevo.getVisitasRealizadas());
                    cliente.setLlamadasNoAtendidas(nuevo.getLlamadasNoAtendidas());
                    cliente.setDiasDesdeUltimaVisita(nuevo.getDiasDesdeUltimaVisita());
                    cliente.setIngresosMensuales(nuevo.getIngresosMensuales());
                    cliente.setTipoLote(nuevo.getTipoLote());
                    cliente.setFechaRegistro(nuevo.getFechaRegistro());
                    return clienteRepository.save(cliente);
                })
                .orElse(null);
    }

    public void eliminarCliente(UUID id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        });
    }
}
