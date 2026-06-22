package com.hacc.api.application.service;

import java.time.LocalDate;
import java.util.List;

import com.hacc.api.domain.model.Pago;
import com.hacc.api.domain.repository.IPagoRepo;
import com.hacc.api.generator.ExcelGenerator;
import com.hacc.api.generator.PdfGenerator;
import com.hacc.api.request.ReportRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReportService {

    @Inject
    IPagoRepo pagoRepository;

    public byte[] generatePdf(ReportRequest req) {

        List<Pago> pagos = getPagos(req);

        return PdfGenerator.buildFinancialReport(pagos);
    }

    public byte[] generateExcel(ReportRequest req) {

        List<Pago> pagos = getPagos(req);

        if (pagos == null || pagos.isEmpty()) {
            throw new RuntimeException("No hay datos para generar el reporte");
        }

        return ExcelGenerator.build(pagos);
    }

    private List<Pago> getPagos(ReportRequest req) {

        LocalDate start;
        LocalDate end;

        switch (req.period) {
            case "jun-2026" -> {
                start = LocalDate.of(2026, 6, 1);
                end = LocalDate.of(2026, 6, 30);
            }
            default -> {
                start = LocalDate.now().withDayOfMonth(1);
                end = LocalDate.now();
            }
        }

        return pagoRepository.findByPeriodo(start, end);
    }
}
