package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.ClienteDTO;
import com.inmobiliaria.inmobiliariabackend.model.Cliente;
import com.inmobiliaria.inmobiliariabackend.model.TipoDocumento;
import com.inmobiliaria.inmobiliariabackend.repository.ClienteRepository;
import com.inmobiliaria.inmobiliariabackend.repository.TipoDocumentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository; // ✨ Nuevo: Inyectar el repositorio de TipoDocumento

    public ClienteService(ClienteRepository clienteRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.clienteRepository = clienteRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    public List<Cliente> listarClientes() {
        // ✨ Filtrar por fechaEliminacion
        return clienteRepository.findAll()
                .stream()
                .filter(cliente -> cliente.getFechaEliminacion() == null)
                .collect(Collectors.toList());
    }

    public Optional<Cliente> obtenerClientePorId(UUID id) {
        // ✨ Filtrar por fechaEliminacion
        return clienteRepository.findById(id)
                .filter(cliente -> cliente.getFechaEliminacion() == null);
    }

    public Cliente crearCliente(ClienteDTO dto) {
        // Obtener el TipoDocumento desde el repositorio
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado."));

        // ✨ Nuevo: Validar unicidad del documento
        Optional<Cliente> clienteExistente = clienteRepository.findByNumeroDocumentoAndTipoDocumento(dto.getNumeroDocumento(), tipoDocumento);
        if (clienteExistente.isPresent() && clienteExistente.get().getFechaEliminacion() == null) {
            throw new IllegalArgumentException("Ya existe un cliente con este tipo y número de documento.");
        }

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setPrimerNombre(dto.getPrimerNombre());
        nuevoCliente.setSegundoNombre(dto.getSegundoNombre());
        nuevoCliente.setApellidoPaterno(dto.getApellidoPaterno());
        nuevoCliente.setApellidoMaterno(dto.getApellidoMaterno());
        nuevoCliente.setTipoDocumento(tipoDocumento);
        nuevoCliente.setNumeroDocumento(dto.getNumeroDocumento());
        nuevoCliente.setCorreo(dto.getCorreo());
        nuevoCliente.setTelefono(dto.getTelefono());
        nuevoCliente.setVisitasRealizadas(dto.getVisitasRealizadas());
        nuevoCliente.setLlamadasNoAtendidas(dto.getLlamadasNoAtendidas());
        nuevoCliente.setDiasDesdeUltimaVisita(dto.getDiasDesdeUltimaVisita());
        nuevoCliente.setIngresosMensuales(dto.getIngresosMensuales());
        nuevoCliente.setFechaRegistro(LocalDate.now()); // La fecha de registro siempre se establece al crear

        return clienteRepository.save(nuevoCliente);
    }

    public Cliente actualizarCliente(UUID id, ClienteDTO dto) {
        return clienteRepository.findById(id)
                .map(existente -> {
                    // Obtener el TipoDocumento desde el repositorio
                    TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
                            .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado."));

                    // ✨ Nuevo: Validar que el nuevo par de documento no exista en otro cliente
                    if (!dto.getNumeroDocumento().equalsIgnoreCase(existente.getNumeroDocumento()) ||
                            !tipoDocumento.equals(existente.getTipoDocumento())) {
                        Optional<Cliente> clienteExistente = clienteRepository.findByNumeroDocumentoAndTipoDocumento(dto.getNumeroDocumento(), tipoDocumento);
                        if (clienteExistente.isPresent() && clienteExistente.get().getFechaEliminacion() == null && !clienteExistente.get().getClienteId().equals(id)) {
                            throw new IllegalArgumentException("Ya existe un cliente con este tipo y número de documento.");
                        }
                    }

                    existente.setPrimerNombre(dto.getPrimerNombre());
                    existente.setSegundoNombre(dto.getSegundoNombre());
                    existente.setApellidoPaterno(dto.getApellidoPaterno());
                    existente.setApellidoMaterno(dto.getApellidoMaterno());
                    existente.setTipoDocumento(tipoDocumento);
                    existente.setNumeroDocumento(dto.getNumeroDocumento());
                    existente.setCorreo(dto.getCorreo());
                    existente.setTelefono(dto.getTelefono());
                    existente.setVisitasRealizadas(dto.getVisitasRealizadas());
                    existente.setLlamadasNoAtendidas(dto.getLlamadasNoAtendidas());
                    existente.setDiasDesdeUltimaVisita(dto.getDiasDesdeUltimaVisita());
                    existente.setIngresosMensuales(dto.getIngresosMensuales());

                    return clienteRepository.save(existente);
                })
                .orElse(null);
    }

    public void eliminarCliente(UUID id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            // ✨ Borrado lógico
            cliente.setFechaEliminacion(LocalDateTime.now());
            clienteRepository.save(cliente);
        });
    }
}