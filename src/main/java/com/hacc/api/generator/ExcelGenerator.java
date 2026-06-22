package com.hacc.api.generator;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hacc.api.domain.model.Pago;

public class ExcelGenerator {

    public static byte[] build(List<Pago> pagos) {

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Reporte de Pagos");

            // HEADER
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Fecha");
            header.createCell(1).setCellValue("Título");
            header.createCell(2).setCellValue("Monto");
            header.createCell(3).setCellValue("Residente ID");

            int rowIdx = 1;

            // DATA
            for (Pago p : pagos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(
                        p.getFecha() != null ? p.getFecha().toString() : ""
                );

                row.createCell(1).setCellValue(
                        p.getTitulo() != null ? p.getTitulo() : ""
                );

                row.createCell(2).setCellValue(
                        p.getMonto() != null ? p.getMonto() : 0
                );

                row.createCell(3).setCellValue(
                        p.getIdResidente() != null ? p.getIdResidente() : 0
                );
            }

            // AUTO SIZE (opcional pero recomendado)
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }
}