package com.hacc.api;

import com.hacc.api.application.service.ReportService;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;

public class Main {

    public static void main(String[] args) {
        Quarkus.run(args);
    }

    public static class App implements QuarkusApplication {
      
 @Inject
        ReportService reportService;

        @Override
        public int run(String... args) throws Exception {

            System.out.println("=== GENERANDO REPORTE DE PRUEBA ===");
            return 0;
        }
    }

}
