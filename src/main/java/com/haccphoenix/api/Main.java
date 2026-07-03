package com.haccphoenix.api;

import java.io.FileOutputStream;
import java.nio.file.Path;

import com.haccphoenix.api.application.service.ReportService;
import com.haccphoenix.api.request.ReportRequest;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public class Main {

    public static void main(String[] args) {
        Quarkus.run(App.class, args);
    }

    public static class App implements QuarkusApplication {

        @Inject
        ReportService reportService;

        @Override
        @Transactional
        public int run(String... args) throws Exception {

            System.out.println("=== GENERANDO REPORTE DE PRUEBA ===");

            ReportRequest req = new ReportRequest();
            req.period = "jun-2026";
            req.type = "financial";
            req.format = "pdf";

            byte[] pdf = reportService.generatePdf(req);

            String filePath = "reporte.pdf";

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdf);
            }

            System.out.println("PDF guardado en: " + Path.of(filePath).toAbsolutePath());

            return 0;
        }
    }
}
