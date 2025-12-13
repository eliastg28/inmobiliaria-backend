package com.inmobiliaria.inmobiliariabackend.controller;

import com.inmobiliaria.inmobiliariabackend.service.ReporteMensualPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReportePdfController {

    @Autowired
    private ReporteMensualPdfService reporteService;

    private final String IA_URL = "http://127.0.0.1:8001/api/v1/reportes/api/reportes/mensual";

    @GetMapping(value = "/mensual/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generarReportePdf() {
        try {
            RestTemplate rt = new RestTemplate();
            Map<String, Object> data = rt.getForObject(IA_URL, Map.class);

            // logoPath puede ser una ruta absoluta o classpath resource; pasar null si no hay.
            String logoPath = null; // "/path/to/logo.png"   o null
            ByteArrayInputStream bis = reporteService.generarReporteProfesional(data, logoPath);

            byte[] bytes = bis.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_mensual.pdf");
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
