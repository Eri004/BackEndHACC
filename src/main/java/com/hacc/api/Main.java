package com.hacc.api;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;

public class Main {

    public static void main(String[] args) {
        Quarkus.run(args);
    }

    public static class App implements QuarkusApplication {
      
//  @Inject
//         ReportService reportService;

        @Override
        public int run(String... args) throws Exception {

//             System.out.println("=== GENERANDO REPORTE DE PRUEBA ===");

//             ReportRequest req = new ReportRequest();
//             req.period = "jun-2026";
//             req.type = "financial";
//             req.format = "pdf";

//             byte[] pdf = reportService.generatePdf(req);

//             String filePath = "reporte.pdf";

// try (FileOutputStream fos = new FileOutputStream(filePath)) {
//     fos.write(pdf);
// }

// System.out.println("PDF guardado en: " + Path.of(filePath).toAbsolutePath());

            return 0;
        }
    }

}
