package com.haccphoenix.api.generator;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.haccphoenix.api.domain.model.Pago;
import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.request.EstadoPagoDepartamento;
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
    private static final Color COLOR_ADVERTENCIA = new DeviceRgb(192, 57, 43);
    private static final Color COLOR_PARCIAL = new DeviceRgb(243, 156, 18);
    private static final Color COLOR_SIN_CARGOS = new DeviceRgb(149, 165, 166);
    private static final Color COLOR_BORDE = new DeviceRgb(189, 195, 199);
    private static final Color COLOR_TEXTO_CLARO = new DeviceRgb(255, 255, 255);
    private static final Color COLOR_FILA_ALT = new DeviceRgb(245, 247, 250);

    public static byte[] buildFinancialReport(List<Pago> pagos,
                                              List<ResidenteDetalle> residentes,
                                              List<EstadoPagoDepartamento> estadoPagos) {
        double total = pagos.stream()
                .filter(p -> p.getMontoTotal() != null)
                .mapToDouble(p -> p.getMontoTotal().doubleValue())
                .sum();

        int totalDeptos = estadoPagos == null ? 0 : estadoPagos.size();
        long pagados = estadoPagos == null ? 0 : estadoPagos.stream().filter(e -> "PAGADO".equals(e.estadoCuota)).count();
        long parcial = estadoPagos == null ? 0 : estadoPagos.stream().filter(e -> "PARCIAL".equals(e.estadoCuota)).count();
        long pendiente = estadoPagos == null ? 0 : estadoPagos.stream().filter(e -> "PENDIENTE".equals(e.estadoCuota)).count();
        long sinCargos = estadoPagos == null ? 0 : estadoPagos.stream().filter(e -> "SIN CARGOS".equals(e.estadoCuota)).count();
        long inquilinosActivos = residentes == null ? 0
                : residentes.stream().filter(r -> "ACTIVO".equalsIgnoreCase(r.inquilinoEstado)).count();
        long inquilinosInactivos = residentes == null ? 0
                : residentes.stream().filter(r -> r.inquilinoEstado != null && !"ACTIVO".equalsIgnoreCase(r.inquilinoEstado)).count();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

            Paragraph titulo = new Paragraph("REPORTE FINANCIERO")
                    .setFont(boldFont).setFontSize(24).setFontColor(COLOR_PRIMARIO)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(10);
            doc.add(titulo);

            Paragraph linea = new Paragraph("")
                    .setBorderBottom(new SolidBorder(COLOR_PRIMARIO, 2))
                    .setMarginBottom(20);
            doc.add(linea);

            String fechaActual = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Paragraph fecha = new Paragraph("Generado: " + fechaActual)
                    .setFont(regularFont).setFontSize(10).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginBottom(15);
            doc.add(fecha);

            addSummaryCards(doc, boldFont, regularFont, total, totalDeptos, pagados, parcial, pendiente, sinCargos, inquilinosActivos, inquilinosInactivos);
            addPagosTable(doc, pagos, boldFont, regularFont);
            addEstadoPagosSection(doc, estadoPagos, boldFont, regularFont);
            addResidentesSection(doc, residentes, boldFont, regularFont);

            Paragraph pie = new Paragraph("Reporte generado automaticamente")
                    .setFont(regularFont).setFontSize(8).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(30);
            doc.add(pie);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private static void addSummaryCards(Document doc, PdfFont boldFont, PdfFont regularFont,
                                        double total, int totalDeptos, long pagados, long parcial,
                                        long pendiente, long sinCargos, long inqAct, long inqInact) {
        Table grid = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        Cell c1 = tarjeta("TOTAL RECAUDADO", "$ " + String.format("%,.2f", total), COLOR_PRIMARIO, boldFont);
        Cell c2 = tarjeta("DEPTOS PAGADOS", pagados + " / " + totalDeptos, COLOR_SECUNDARIO, boldFont);
        Cell c3 = tarjeta("DEPTOS PENDIENTES", pendiente + "", COLOR_ADVERTENCIA, boldFont);
        grid.addCell(c1); grid.addCell(c2); grid.addCell(c3);

        Table grid2 = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(25);
        grid2.addCell(tarjetaSmall("Parcial", parcial + "", COLOR_PARCIAL, boldFont));
        grid2.addCell(tarjetaSmall("Sin cargos", sinCargos + "", COLOR_SIN_CARGOS, boldFont));
        grid2.addCell(tarjetaSmall("Inq. activos", inqAct + "", COLOR_SECUNDARIO, boldFont));
        grid2.addCell(tarjetaSmall("Inq. inactivos", inqInact + "", COLOR_ADVERTENCIA, boldFont));

        doc.add(grid);
        doc.add(grid2);
    }

    private static Cell tarjeta(String titulo, String valor, Color bg, PdfFont bold) {
        return new Cell()
                .add(new Paragraph(titulo).setFont(bold).setFontSize(10).setFontColor(COLOR_TEXTO_CLARO)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(valor).setFont(bold).setFontSize(18).setFontColor(COLOR_TEXTO_CLARO)
                        .setTextAlignment(TextAlignment.CENTER).setMarginTop(5))
                .setBackgroundColor(bg).setPadding(14).setBorder(Border.NO_BORDER);
    }

    private static Cell tarjetaSmall(String titulo, String valor, Color bg, PdfFont bold) {
        return new Cell()
                .add(new Paragraph(titulo).setFont(bold).setFontSize(8).setFontColor(COLOR_TEXTO_CLARO)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(valor).setFont(bold).setFontSize(14).setFontColor(COLOR_TEXTO_CLARO)
                        .setTextAlignment(TextAlignment.CENTER).setMarginTop(3))
                .setBackgroundColor(bg).setPadding(8).setBorder(Border.NO_BORDER);
    }

    private static void addPagosTable(Document doc, List<Pago> pagos, PdfFont boldFont, PdfFont regularFont) {
        Paragraph subtitulo = new Paragraph("Detalle de Pagos")
                .setFont(boldFont).setFontSize(14).setFontColor(COLOR_PRIMARIO)
                .setMarginBottom(10);
        doc.add(subtitulo);

        if (pagos == null || pagos.isEmpty()) {
            doc.add(new Paragraph("No hay pagos en el periodo seleccionado.")
                    .setFont(regularFont).setFontSize(10).setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(15));
            return;
        }

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 3, 4, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        String[] headers = {"FECHA", "DEPARTAMENTO", "PROPIETARIO", "MONTO", "METODO"};
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setFont(boldFont).setFontColor(COLOR_TEXTO_CLARO)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(COLOR_PRIMARIO).setPadding(10)
                    .setBorder(new SolidBorder(COLOR_BORDE, 1));
            tabla.addCell(headerCell);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Pago p : pagos) {
            String fechaStr = p.getFecha() != null ? p.getFecha().format(dateFormatter) : "N/A";
            tabla.addCell(celda(fechaStr, regularFont, TextAlignment.CENTER));

            String deptoStr = p.getDepartamento() != null ? p.getDepartamento().getIdentificadorCompleto() : "N/A";
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

    private static void addEstadoPagosSection(Document doc, List<EstadoPagoDepartamento> estadoPagos,
                                              PdfFont boldFont, PdfFont regularFont) {
        if (estadoPagos == null || estadoPagos.isEmpty()) return;

        Paragraph subtitulo = new Paragraph("Estado de Cuotas por Departamento")
                .setFont(boldFont).setFontSize(14).setFontColor(COLOR_ADVERTENCIA)
                .setMarginTop(15).setMarginBottom(10);
        doc.add(subtitulo);

        Table tabla = new Table(UnitValue.createPercentArray(
                new float[]{2, 1.2f, 2.5f, 1.5f, 1.2f, 1.2f, 1.5f, 1.2f}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        String[] headers = {
            "EDIFICIO", "DEPTO", "PROPIETARIO", "ESTADO", "PAGADO", "PENDIENTE", "INQUILINO", "EST. INQ."
        };
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setFont(boldFont).setFontColor(COLOR_TEXTO_CLARO)
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(8))
                    .setBackgroundColor(COLOR_ADVERTENCIA).setPadding(8)
                    .setBorder(new SolidBorder(COLOR_BORDE, 1));
            tabla.addCell(headerCell);
        }

        boolean alt = false;
        for (EstadoPagoDepartamento e : estadoPagos) {
            Color rowBg = alt ? COLOR_FILA_ALT : null;
            alt = !alt;
            tabla.addCell(celdaConFondo(n(e.edificioNombre), regularFont, TextAlignment.LEFT, rowBg, 7));
            tabla.addCell(celdaConFondo(n(e.departamentoNumero), regularFont, TextAlignment.CENTER, rowBg, 7));
            tabla.addCell(celdaConFondo(propStr(e.propietarioNombre, e.propietarioCedula), regularFont, TextAlignment.LEFT, rowBg, 7));
            tabla.addCell(celdaConFondo(n(e.estadoCuota), regularFont, TextAlignment.CENTER, rowBg, 7, colorForEstado(e.estadoCuota)));
            tabla.addCell(celdaConFondo(money(e.montoPagado), regularFont, TextAlignment.RIGHT, rowBg, 7));
            tabla.addCell(celdaConFondo(money(e.montoPendiente), regularFont, TextAlignment.RIGHT, rowBg, 7));
            tabla.addCell(celdaConFondo(n(e.inquilinoNombre), regularFont, TextAlignment.LEFT, rowBg, 7));
            tabla.addCell(celdaConFondo(n(e.inquilinoEstado), regularFont, TextAlignment.CENTER, rowBg, 7,
                    "ACTIVO".equalsIgnoreCase(e.inquilinoEstado) ? COLOR_SECUNDARIO
                            : (e.inquilinoEstado != null && !e.inquilinoEstado.isEmpty()) ? COLOR_ADVERTENCIA : null));
        }

        doc.add(tabla);
    }

    private static void addResidentesSection(Document doc, List<ResidenteDetalle> residentes,
                                             PdfFont boldFont, PdfFont regularFont) {
        if (residentes == null || residentes.isEmpty()) return;

        Paragraph subtitulo = new Paragraph("Detalle de Residentes")
                .setFont(boldFont).setFontSize(14).setFontColor(COLOR_SECUNDARIO)
                .setMarginTop(15).setMarginBottom(10);
        doc.add(subtitulo);

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
                    .add(new Paragraph(header).setFont(boldFont).setFontColor(COLOR_TEXTO_CLARO)
                            .setTextAlignment(TextAlignment.CENTER).setFontSize(8))
                    .setBackgroundColor(COLOR_SECUNDARIO).setPadding(8)
                    .setBorder(new SolidBorder(COLOR_BORDE, 1));
            tabla.addCell(headerCell);
        }

        for (ResidenteDetalle r : residentes) {
            tabla.addCell(celdaSmall(n(r.edificioNombre), regularFont));
            tabla.addCell(celdaSmall(n(r.departamentoNumero), regularFont));
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

    private static Color colorForEstado(String est) {
        if (est == null) return null;
        return switch (est) {
            case "PAGADO" -> COLOR_SECUNDARIO;
            case "PARCIAL" -> COLOR_PARCIAL;
            case "PENDIENTE" -> COLOR_ADVERTENCIA;
            case "SIN CARGOS" -> COLOR_SIN_CARGOS;
            default -> null;
        };
    }

    private static String propStr(String nombre, String cedula) {
        String n = n(nombre), c = n(cedula);
        if (n.isEmpty() && c.isEmpty()) return "N/A";
        if (c.isEmpty()) return n;
        return n.isEmpty() ? c : n + " (" + c + ")";
    }

    private static String money(BigDecimal v) {
        if (v == null) return "$ 0.00";
        return "$ " + String.format("%,.2f", v.doubleValue());
    }

    private static String n(String v) {
        return v == null ? "" : v;
    }

    private static Cell celda(String texto, PdfFont font, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(texto).setFont(font).setFontSize(10).setTextAlignment(align))
                .setPadding(8).setBorder(new SolidBorder(COLOR_BORDE, 1));
    }

    private static Cell celdaConFondo(String texto, PdfFont font, TextAlignment align, Color bg) {
        Cell c = new Cell()
                .add(new Paragraph(texto).setFont(font).setFontSize(8).setTextAlignment(align))
                .setPadding(6).setBorder(new SolidBorder(COLOR_BORDE, 1));
        if (bg != null) c.setBackgroundColor(bg);
        return c;
    }

    private static Cell celdaConFondo(String texto, PdfFont font, TextAlignment align, Color bg,
                                      int fontSize) {
        Cell c = new Cell()
                .add(new Paragraph(texto).setFont(font).setFontSize(fontSize).setTextAlignment(align))
                .setPadding(6).setBorder(new SolidBorder(COLOR_BORDE, 1));
        if (bg != null) c.setBackgroundColor(bg);
        return c;
    }

    private static Cell celdaConFondo(String texto, PdfFont font, TextAlignment align, Color bg,
                                      int fontSize, Color textColor) {
        Cell c = new Cell()
                .add(new Paragraph(texto).setFont(font).setFontSize(fontSize)
                        .setTextAlignment(align)
                        .setFontColor(textColor != null ? textColor : ColorConstants.BLACK))
                .setPadding(6).setBorder(new SolidBorder(COLOR_BORDE, 1));
        if (bg != null) c.setBackgroundColor(bg);
        return c;
    }

    private static Cell celdaSmall(String texto, PdfFont font) {
        return new Cell()
                .add(new Paragraph(texto).setFont(font).setFontSize(8))
                .setPadding(6).setBorder(new SolidBorder(COLOR_BORDE, 1));
    }
}