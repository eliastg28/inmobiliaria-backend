package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.dto.AbonoDTO;
import com.inmobiliaria.inmobiliariabackend.model.Abono;
import com.inmobiliaria.inmobiliariabackend.service.AbonoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/abonos")
public class AbonoController {

    private final AbonoService abonoService;

    public AbonoController(AbonoService abonoService) {
        this.abonoService = abonoService;
    }

    /**
     * POST /api/abonos : Registra un nuevo abono para una venta.
     * Retorna el abono creado.
     */
    @PostMapping
    public ResponseEntity<Abono> registrarAbono(@Valid @RequestBody AbonoDTO abonoDto) {
        try {
            Abono nuevoAbono = abonoService.crearAbono(abonoDto);
            return new ResponseEntity<>(nuevoAbono, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Maneja el caso de Venta o EstadoVenta no encontrado (404 Not Found)
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            // Maneja el error de validación, como exceder el saldo pendiente (400 Bad Request)
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /api/abonos/venta/{ventaId} : Obtiene todos los abonos de una venta específica.
     * Retorna una lista de abonos.
     */
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<Abono>> obtenerAbonosPorVenta(@PathVariable UUID ventaId) {
        List<Abono> abonos = abonoService.obtenerAbonosPorVenta(ventaId);

        // Si no se encuentran abonos, regresa una lista vacía (200 OK)
        return ResponseEntity.ok(abonos);
    }
}