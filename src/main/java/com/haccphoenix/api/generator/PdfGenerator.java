package com.haccphoenix.api.generator;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.haccphoenix.api.domain.model.Pago;
import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.request.ResidenteDetalle;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class PdfGenerator {

    private static final Color COLOR_PRIMARIO = new DeviceRgb(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new DeviceRgb(39, 174, 96);
    private static final Color COLOR_BORDE = new DeviceRgb(189, 195, 199);

    public static byte[] buildFinancialReport(List<Pago> pagos, List<ResidenteDetalle> residentes) {
        double total = pagos.stream()
                .filter(p -> p.getMontoTotal() != null)
                .mapToDouble(p -> p.getMontoTotal().doubleValue())
                .sum();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

            Paragraph titulo = new Paragraph("REPORTE FINANCIERO")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(COLOR_PRIMARIO)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            doc.add(titulo);

            Paragraph linea = new Paragraph("")
                    .setBorderBottom(new SolidBorder(COLOR_PRIMARIO, 2))
                    .setMarginBottom(20);
            doc.add(linea);

            String fechaActual = java.time.LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Paragraph fecha = new Paragraph("Generado: " + fechaActual)
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(15);
            doc.add(fecha);

            Table totalCard = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(25);

            Cell totalCell = new Cell()
                    .add(new Paragraph("TOTAL RECAUDADO")
                            .setFont(boldFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("$ " + String.format("%,.2f", total))
                            .setFont(boldFont)
                            .setFontSize(28)
                            .setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(5))
                    .setBackgroundColor(COLOR_PRIMARIO)
                    .setPadding(20)
                    .setBorder(Border.NO_BORDER);

            totalCard.addCell(totalCell);
            doc.add(totalCard);

            addPagosTable(doc, pagos, boldFont, regularFont);
            addResidentesSection(doc, residentes, boldFont, regularFont);

            Paragraph pie = new Paragraph("Reporte generado automaticamente")
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            doc.add(pie);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private static void addPagosTable(Document doc, List<Pago> pagos, PdfFont boldFont, PdfFont regularFont) {
        Paragraph subtitulo = new Paragraph("Detalle de Pagos")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(10);
        doc.add(subtitulo);

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 3, 4, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        String[] headers = {"FECHA", "DEPARTAMENTO", "PROPIETARIO", "MONTO", "METODO"};
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header)
                            .setFont(boldFont)
                            .setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(COLOR_PRIMARIO)
                    .setPadding(10)
                    .setBorder(new SolidBorder(COLOR_BORDE, 1));
            tabla.addCell(headerCell);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Pago p : pagos) {
            String fechaStr = p.getFecha() != null
                    ? p.getFecha().format(dateFormatter)
                    : "N/A";
            tabla.addCell(celda(fechaStr, regularFont, TextAlignment.CENTER));

            String deptoStr = p.getDepartamento() != null
                    ? p.getDepartamento().getIdentificadorCompleto()
                    : "N/A";
            tabla.addCell(celda(deptoStr, regularFont, TextAlignment.LEFT));

            String propietarioStr = "N/A";
            if (p.getDepartamento() != null) {
                Propietario prop = p.getDepartamento().getPropietario();
                if (prop != null) {
                    String nombre = "";
                    Usuario u = prop.getUsuario();
                    if (u != null) {
                        nombre = (u.getNombre() != null ? u.getNombre() : "")
                                + (u.getApellido() != null ? " " + u.getApellido() : "");
                    }
                    String cedula = prop.getCedula() != null ? prop.getCedula() : "";
                    propietarioStr = (nombre + " (" + cedula + ")").trim();
                }
            }
            tabla.addCell(celda(propietarioStr, regularFont, TextAlignment.LEFT));

            BigDecimal monto = p.getMontoTotal();
            tabla.addCell(celda("$ " + String.format("%,.2f", monto != null ? monto.doubleValue() : 0),
                    regularFont, TextAlignment.RIGHT));

            tabla.addCell(celda(p.getMetodoPago() != null ? p.getMetodoPago() : "N/A",
                    regularFont, TextAlignment.CENTER));
        }

        doc.add(tabla);
    }

    private static void addResidentesSection(Document doc, List<ResidenteDetalle> residentes,
                                             PdfFont boldFont, PdfFont regularFont) {
        if (residentes == null || residentes.isEmpty()) {
            return;
        }

        Paragraph subtitulo = new Paragraph("Detalle de Residentes")
                .setFont(boldFont)
                .setFontSize(14)
                .setFontColor(COLOR_SECUNDARIO)
                .setMarginTop(15)
                .setMarginBottom(10);
        doc.add(subtitulo);

        Paragraph cuenta = new Paragraph("Total de residentes: " + residentes.size())
                .setFont(regularFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setMarginBottom(10);
        doc.add(cuenta);

        Table tabla = new Table(UnitValue.createPercentArray(
                new float[]{2, 1.5f, 1, 3, 2, 2, 3, 2, 1.5f}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        String[] headers = {
            "EDIFICIO", "DEPTO", "PISO",
            "PROPIETARIO", "CEDULA", "TELEFONO",
            "INQUILINO", "CEDULA", "ESTADO"
        };
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header)
                            .setFont(boldFont)
                            .setFontColor(ColorConstants.WHITE)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(8))
                    .setBackgroundColor(COLOR_SECUNDARIO)
                    .setPadding(8)
                    .setBorder(new SolidBorder(COLOR_BORDE, 1));
            tabla.addCell(headerCell);
        }

        for (ResidenteDetalle r : residentes) {
            tabla.addCell(celdaSmall(r.edificioNombre, regularFont));
            tabla.addCell(celdaSmall(r.departamentoNumero, regularFont));
            tabla.addCell(celdaSmall(r.piso != null ? String.valueOf(r.piso) : "", regularFont));
            tabla.addCell(celdaSmall(n(r.propietarioNombre), regularFont));
            tabla.addCell(celdaSmall(n(r.propietarioCedula), regularFont));
            tabla.addCell(celdaSmall(n(r.propietarioTelefono), regularFont));
            tabla.addCell(celdaSmall(n(r.inquilinoNombre), regularFont));
            tabla.addCell(celdaSmall(n(r.inquilinoCedula), regularFont));
            tabla.addCell(celdaSmall(n(r.inquilinoEstado), regularFont));
        }

        doc.add(tabla);
    }

    private static String n(String v) {
        return v == null ? "" : (v.isEmpty() ? "-" : v);
    }

    private static Cell celda(String texto, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(font)
                        .setFontSize(10)
                        .setTextAlignment(align))
                .setPadding(8)
                .setBorder(new SolidBorder(COLOR_BORDE, 1));
    }

    private static Cell celdaSmall(String texto, PdfFont font) {
        return new Cell()
                .add(new Paragraph(texto)
                        .setFont(font)
                        .setFontSize(8))
                .setPadding(6)
                .setBorder(new SolidBorder(COLOR_BORDE, 1));
    }
}