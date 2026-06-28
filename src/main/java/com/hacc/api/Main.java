package com.hacc.api;

import java.io.FileOutputStream;
import java.nio.file.Path;

import com.hacc.api.application.service.ReportService;
import com.hacc.api.request.ReportRequest;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;

public class Main {

    public static void main(String[] args) {
        Quarkus.run(App.class,args);
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
