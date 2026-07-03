package com.haccphoenix.api.generator;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.haccphoenix.api.domain.model.Pago;
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
    private static final Color COLOR_FONDO_TABLA = new DeviceRgb(236, 240, 241);
    private static final Color COLOR_BORDE = new DeviceRgb(189, 195, 199);

    public static byte[] buildFinancialReport(List<Pago> pagos) {
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

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 4, 2, 3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            String[] headers = {"FECHA", "DEPARTAMENTO", "MONTO", "METODO"};
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
                tabla.addCell(new Cell()
                        .add(new Paragraph(fechaStr)
                                .setFont(regularFont)
                                .setFontSize(10)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setPadding(8)
                        .setBorder(new SolidBorder(COLOR_BORDE, 1)));

                String deptoStr = p.getDepartamento() != null
                        ? p.getDepartamento().getIdentificadorCompleto()
                        : "N/A";
                tabla.addCell(new Cell()
                        .add(new Paragraph(deptoStr)
                                .setFont(regularFont)
                                .setFontSize(10))
                        .setPadding(8)
                        .setBorder(new SolidBorder(COLOR_BORDE, 1)));

                BigDecimal monto = p.getMontoTotal();
                tabla.addCell(new Cell()
                        .add(new Paragraph("$ " + String.format("%,.2f", monto != null ? monto.doubleValue() : 0))
                                .setFont(regularFont)
                                .setFontSize(10)
                                .setTextAlignment(TextAlignment.RIGHT))
                        .setPadding(8)
                        .setBorder(new SolidBorder(COLOR_BORDE, 1)));

                tabla.addCell(new Cell()
                        .add(new Paragraph(p.getMetodoPago() != null ? p.getMetodoPago() : "N/A")
                                .setFont(regularFont)
                                .setFontSize(10)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setPadding(8)
                        .setBorder(new SolidBorder(COLOR_BORDE, 1)));
            }

            doc.add(tabla);

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
}
