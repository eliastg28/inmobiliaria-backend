package com.inmobiliaria.inmobiliariabackend.service;

import com.inmobiliaria.inmobiliariabackend.dto.ClienteDTO;
import com.inmobiliaria.inmobiliariabackend.model.Cliente;
import com.inmobiliaria.inmobiliariabackend.model.TipoDocumento;
import com.inmobiliaria.inmobiliariabackend.repository.ClienteRepository;
import com.inmobiliaria.inmobiliariabackend.repository.TipoDocumentoRepository;
import com.inmobiliaria.inmobiliariabackend.util.TextUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public ClienteService(ClienteRepository clienteRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.clienteRepository = clienteRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    public List<Cliente> listarClientes(String busqueda) {
        // 1. Obtenemos todos los activos (ya tienes este método)
        List<Cliente> todos = clienteRepository.findByFechaEliminacionIsNull();

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return todos;
        }

        // 2. Normalizamos la búsqueda
        String busquedaLimpia = TextUtil.limpiarAcentos(busqueda);
        String[] palabras = busquedaLimpia.split("\\s+");

        // 3. Filtramos TODO en memoria con TextUtil
        return todos.stream()
                .filter(c -> {
                    String contenidoSocio = TextUtil.limpiarAcentos(
                            (c.getPrimerNombre() != null ? c.getPrimerNombre() : "") + " " +
                                    (c.getSegundoNombre() != null ? c.getSegundoNombre() : "") + " " +
                                    (c.getApellidoPaterno() != null ? c.getApellidoPaterno() : "") + " " +
                                    (c.getApellidoMaterno() != null ? c.getApellidoMaterno() : "") + " " +
                                    (c.getNumeroDocumento() != null ? c.getNumeroDocumento() : "")
                    );

                    // Verificamos que todas las palabras de la búsqueda estén en el contenido
                    for (String p : palabras) {
                        if (!contenidoSocio.contains(p)) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public Optional<Cliente> obtenerClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .filter(cliente -> cliente.getFechaEliminacion() == null);
    }

    public Cliente crearCliente(ClienteDTO dto) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado."));

        // Validar unicidad
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
        nuevoCliente.setIngresosMensuales(dto.getIngresosMensuales());

        return clienteRepository.save(nuevoCliente);
    }

    public Cliente actualizarCliente(UUID id, ClienteDTO dto) {
        return clienteRepository.findById(id)
                .map(existente -> {
                    TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
                            .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado."));

                    // 🔍 Validar unicidad (excepto el mismo cliente)
                    clienteRepository.findByNumeroDocumentoAndTipoDocumento(dto.getNumeroDocumento(), tipoDocumento)
                            .filter(cliente -> cliente.getFechaEliminacion() == null) // solo activos
                            .filter(cliente -> !cliente.getClienteId().equals(id))   // distinto al que edito
                            .ifPresent(c -> {
                                throw new IllegalArgumentException("Ya existe un cliente con este tipo y número de documento.");
                            });

                    // Actualizar campos
                    existente.setPrimerNombre(dto.getPrimerNombre());
                    existente.setSegundoNombre(dto.getSegundoNombre());
                    existente.setApellidoPaterno(dto.getApellidoPaterno());
                    existente.setApellidoMaterno(dto.getApellidoMaterno());
                    existente.setTipoDocumento(tipoDocumento);
                    existente.setNumeroDocumento(dto.getNumeroDocumento());
                    existente.setCorreo(dto.getCorreo());
                    existente.setTelefono(dto.getTelefono());
                    existente.setIngresosMensuales(dto.getIngresosMensuales());

                    return clienteRepository.save(existente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
    }


    public void eliminarCliente(UUID id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setFechaEliminacion(LocalDateTime.now()); // borrado lógico
            clienteRepository.save(cliente);
        });
    }
}
