package com.hacc.api.request;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.hacc.api.domain.model.Pago;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class PdfGenerator {

    public static byte[] buildFinancialReport(List<Pago> pagos) {

        double total = pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("REPORTE FINANCIERO"));
            doc.add(new Paragraph("Total recaudado: $" + total));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("DETALLE DE PAGOS"));

            for (Pago p : pagos) {
                doc.add(new Paragraph(
                        p.getFecha() + " | "
                        + p.getTitulo() + " | $"
                        + p.getMonto()
                ));
            }

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}