package com.inmobiliaria.inmobiliariabackend.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReporteMensualPdfService {

    private static final Color PRIMARY = new Color(10, 102, 194); // color principal (azul)
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
    private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, PRIMARY);
    private static final Font NORMAL = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
    private static final Font BOLD = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));

    public ByteArrayInputStream generarReporteProfesional(Map<String, Object> data, String logoPath) {
        Document document = new Document(PageSize.A4, 36, 36, 80, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // HEADER: logo + title (drawn manually so header repeats)
            writer.setPageEvent(new HeaderFooterPageEvent(logoPath));

            document.open();

            // Título central
            Paragraph titulo = new Paragraph("REPORTE MENSUAL - INFORME GERENCIAL", TITLE_FONT);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(12f);
            document.add(titulo);

            // Fecha de generación (alineada a la derecha)
            Paragraph fecha = new Paragraph(
                    "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY)
            );
            fecha.setAlignment(Element.ALIGN_RIGHT);
            fecha.setSpacingAfter(12f);
            document.add(fecha);

            // --- Tarjetas resumen: ventas del mes y top proyecto ---
            PdfPTable resumenTable = new PdfPTable(2);
            resumenTable.setWidthPercentage(100);
            resumenTable.setWidths(new int[]{1, 2});
            resumenTable.setSpacingAfter(14f);

            // Tarjeta 1: Ventas del mes (big)
            PdfPCell ventasCell = createCardCell();
            Paragraph ventasTitle = new Paragraph("Ventas del mes", SUBTITLE_FONT);
            ventasTitle.setAlignment(Element.ALIGN_LEFT);
            ventasCell.addElement(ventasTitle);

            Integer ventasMes = safeInt(data.get("ventas_mes"));
            Paragraph ventasValor = new Paragraph(String.valueOf(ventasMes), new Font(Font.HELVETICA, 24, Font.BOLD, PRIMARY));
            ventasValor.setSpacingBefore(6f);
            ventasCell.addElement(ventasValor);
            resumenTable.addCell(ventasCell);

            // Tarjeta 2: Proyecto top (nombre y cantidad)
            PdfPCell proyectoTopCell = createCardCell();
            Map<String, Integer> proyectos = safeMapInt(data.get("proyectos_con_mas_ventas"));
            if (!proyectos.isEmpty()) {
                String topProyecto = proyectos.keySet().iterator().next();
                Integer topVentas = proyectos.get(topProyecto);
                proyectoTopCell.addElement(new Paragraph("Proyecto con mayor movimiento", SUBTITLE_FONT));
                proyectoTopCell.addElement(new Paragraph(topProyecto + " — " + topVentas + " venta(s)", new Font(Font.HELVETICA, 14, Font.BOLD)));
            } else {
                proyectoTopCell.addElement(new Paragraph("Proyecto con mayor movimiento", SUBTITLE_FONT));
                proyectoTopCell.addElement(new Paragraph("Sin datos", NORMAL));
            }
            resumenTable.addCell(proyectoTopCell);

            document.add(resumenTable);

            // --- Tabla: Proyectos con más ventas ---
            document.add(new Paragraph("Proyectos con más ventas", SUBTITLE_FONT));
            document.add(Chunk.NEWLINE);
            PdfPTable tablaProyectos = new PdfPTable(2);
            tablaProyectos.setWidthPercentage(100);
            tablaProyectos.setWidths(new int[]{4, 1});
            addTableHeader(tablaProyectos, new String[]{"Proyecto", "Ventas"});
            if (!proyectos.isEmpty()) {
                for (Map.Entry<String, Integer> e : proyectos.entrySet()) {
                    tablaProyectos.addCell(createBodyCell(e.getKey()));
                    tablaProyectos.addCell(createBodyCell(String.valueOf(e.getValue())));
                }
            } else {
                tablaProyectos.addCell(createBodyCell("No hay datos"));
                tablaProyectos.addCell(createBodyCell("-"));
            }
            tablaProyectos.setSpacingAfter(12f);
            document.add(tablaProyectos);

            // --- Tabla: Zonas top ---
            document.add(new Paragraph("Zonas con mayor movimiento", SUBTITLE_FONT));
            document.add(Chunk.NEWLINE);
            PdfPTable tablaZonas = new PdfPTable(2);
            tablaZonas.setWidthPercentage(100);
            tablaZonas.setWidths(new int[]{4, 1});
            addTableHeader(tablaZonas, new String[]{"Zona", "Ventas"});
            Map<String, Integer> zonas = safeMapInt(data.get("zonas_top"));
            if (!zonas.isEmpty()) {
                for (Map.Entry<String, Integer> e : zonas.entrySet()) {
                    tablaZonas.addCell(createBodyCell(e.getKey()));
                    tablaZonas.addCell(createBodyCell(String.valueOf(e.getValue())));
                }
            } else {
                tablaZonas.addCell(createBodyCell("No hay datos"));
                tablaZonas.addCell(createBodyCell("-"));
            }
            tablaZonas.setSpacingAfter(12f);
            document.add(tablaZonas);

            // --- Tabla: Ticket promedio por proyecto ---
            document.add(new Paragraph("Ticket promedio por proyecto", SUBTITLE_FONT));
            document.add(Chunk.NEWLINE);
            PdfPTable tablaTicket = new PdfPTable(2);
            tablaTicket.setWidthPercentage(100);
            tablaTicket.setWidths(new int[]{4, 2});
            addTableHeader(tablaTicket, new String[]{"Proyecto", "Ticket promedio"});
            Map<String, Double> tickets = safeMapDouble(data.get("ticket_promedio_proyecto"));
            if (!tickets.isEmpty()) {
                for (Map.Entry<String, Double> e : tickets.entrySet()) {
                    tablaTicket.addCell(createBodyCell(e.getKey()));
                    tablaTicket.addCell(createBodyCell(MONEDA.format(e.getValue())));
                }
            } else {
                tablaTicket.addCell(createBodyCell("No hay datos"));
                tablaTicket.addCell(createBodyCell("-"));
            }
            tablaTicket.setSpacingAfter(12f);
            document.add(tablaTicket);

            // --- Recomendaciones (lista con icono) ---
            document.add(new Paragraph("Recomendaciones", SUBTITLE_FONT));
            document.add(Chunk.NEWLINE);
            List<String> recs = safeListString(data.get("recomendaciones"));
            if (!recs.isEmpty()) {
                for (String r : recs) {
                    Paragraph p = new Paragraph("• " + r, NORMAL);
                    p.setSpacingBefore(4f);
                    document.add(p);
                }
            } else {
                document.add(new Paragraph("No hay recomendaciones.", NORMAL));
            }

            // Espacio final y pie simple
            //document.add(Chunk.NEWLINE);
            //Paragraph footerNote = new Paragraph("Reporte generado automáticamente. Fuente: sistema inmobiliario.", new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY));
            //footerNote.setSpacingBefore(18f);
            //footerNote.setAlignment(Element.ALIGN_CENTER);
            //document.add(footerNote);

            document.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    // Helpers de diseño
    private static PdfPCell createCardCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(12f);
        cell.setBackgroundColor(new Color(245, 247, 250));
        return cell;
    }

    private static void addTableHeader(PdfPTable table, String[] headers) {
        Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setBackgroundColor(PRIMARY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }
    }

    private static PdfPCell createBodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL));
        cell.setPadding(6);
        return cell;
    }

    // Helpers de seguridad / casting
    @SuppressWarnings("unchecked")
    private static Map<String, Integer> safeMapInt(Object obj) {
        if (obj instanceof Map) return (Map<String, Integer>) obj;
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Double> safeMapDouble(Object obj) {
        if (obj instanceof Map) return (Map<String, Double>) obj;
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    private static List<String> safeListString(Object obj) {
        if (obj instanceof List) return (List<String>) obj;
        return List.of();
    }

    private static Integer safeInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(obj.toString()); } catch (Exception e) { return 0; }
    }

    // Evento para header/footer (logo opcional)
    static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private String logoPath;
        HeaderFooterPageEvent(String logoPath) { this.logoPath = logoPath; }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfPTable header = new PdfPTable(2);
                header.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
                header.setWidths(new int[]{1, 4});
                header.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                // Logo (si existe)
                if (logoPath != null && !logoPath.isBlank()) {
                    try {
                        Image img = Image.getInstance(logoPath);
                        img.scaleToFit(60, 60);
                        PdfPCell logoCell = new PdfPCell(img, false);
                        logoCell.setBorder(Rectangle.NO_BORDER);
                        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.addCell(logoCell);
                    } catch (Exception ex) {
                        header.addCell(createBlankCell());
                    }
                } else {
                    header.addCell(createBlankCell());
                }

                // Título pequeño en header
                PdfPCell titleCell = new PdfPCell();
                titleCell.setBorder(Rectangle.NO_BORDER);
                Paragraph p = new Paragraph("AVERCO S.A.C.", new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY));
                p.setAlignment(Element.ALIGN_RIGHT);
                titleCell.addElement(p);
                header.addCell(titleCell);

                header.writeSelectedRows(0, -1, document.leftMargin(), document.getPageSize().getHeight() - 10, writer.getDirectContent());

                // Pie simple (número de página)
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_CENTER,
                        new Phrase(String.format("Página %d", writer.getPageNumber()), new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY)),
                        (document.getPageSize().getWidth()) / 2,
                        document.bottom() - 10, 0);

            } catch (Exception e) {
                // no romper si header falla
            }
        }

        private PdfPCell createBlankCell() {
            PdfPCell c = new PdfPCell();
            c.setBorder(Rectangle.NO_BORDER);
            return c;
        }
    }
}
