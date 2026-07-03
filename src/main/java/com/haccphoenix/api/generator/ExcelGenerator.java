package com.haccphoenix.api.generator;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.haccphoenix.api.domain.model.Pago;

public class ExcelGenerator {

    public static byte[] build(List<Pago> pagos) {

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Reporte de Pagos");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Fecha");
            header.createCell(1).setCellValue("Departamento");
            header.createCell(2).setCellValue("Monto");
            header.createCell(3).setCellValue("Metodo de Pago");
            header.createCell(4).setCellValue("Comprobante");
            header.createCell(5).setCellValue("Pagado por");

            int rowIdx = 1;

            for (Pago p : pagos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(
                        p.getFecha() != null ? p.getFecha().toString() : ""
                );

                row.createCell(1).setCellValue(
                        p.getDepartamento() != null
                            ? p.getDepartamento().getIdentificadorCompleto()
                            : ""
                );

                row.createCell(2).setCellValue(
                        p.getMontoTotal() != null ? p.getMontoTotal().doubleValue() : 0
                );

                row.createCell(3).setCellValue(
                        p.getMetodoPago() != null ? p.getMetodoPago() : ""
                );

                row.createCell(4).setCellValue(
                        p.getNumeroComprobante() != null ? p.getNumeroComprobante() : ""
                );

                row.createCell(5).setCellValue(
                        p.getPagadoPor() != null ? p.getPagadoPor() : ""
                );
            }

            for (int i = 0; i < 6; i++) {
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
