package com.haccphoenix.api.generator;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.haccphoenix.api.domain.model.Pago;
import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.request.ResidenteDetalle;

public class ExcelGenerator {

    public static byte[] build(List<Pago> pagos, List<ResidenteDetalle> residentes) {

        try (Workbook wb = new XSSFWorkbook()) {

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            buildPagosSheet(wb, pagos, headerStyle);
            buildResidentesSheet(wb, residentes, headerStyle);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    private static void buildPagosSheet(Workbook wb, List<Pago> pagos, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("Reporte de Pagos");

        Row header = sheet.createRow(0);
        String[] headers = {
            "Fecha", "Edificio", "Departamento", "Piso",
            "Propietario", "Cedula", "Telefono", "Email",
            "Monto", "Metodo de Pago", "Comprobante", "Pagado por"
        };
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (Pago p : pagos) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(
                    p.getFecha() != null ? p.getFecha().toString() : "");

            row.createCell(1).setCellValue(
                    p.getDepartamento() != null
                            && p.getDepartamento().getEdificio() != null
                            && p.getDepartamento().getEdificio().getNombre() != null
                            ? p.getDepartamento().getEdificio().getNombre() : "");

            row.createCell(2).setCellValue(
                    p.getDepartamento() != null
                            && p.getDepartamento().getNumero() != null
                            ? p.getDepartamento().getNumero() : "");

            row.createCell(3).setCellValue(
                    p.getDepartamento() != null
                            && p.getDepartamento().getPiso() != null
                            ? p.getDepartamento().getPiso().intValue() : 0);

            String propietarioNombre = "";
            String propietarioCedula = "";
            String propietarioTelefono = "";
            String propietarioEmail = "";
            if (p.getDepartamento() != null) {
                Propietario prop = p.getDepartamento().getPropietario();
                if (prop != null) {
                    propietarioCedula = prop.getCedula() != null ? prop.getCedula() : "";
                    propietarioTelefono = prop.getTelefono() != null ? prop.getTelefono() : "";
                    Usuario u = prop.getUsuario();
                    if (u != null) {
                        propietarioNombre = (u.getNombre() != null ? u.getNombre() : "")
                                + (u.getApellido() != null ? " " + u.getApellido() : "");
                        propietarioEmail = u.getEmail() != null ? u.getEmail() : "";
                    }
                }
            }
            row.createCell(4).setCellValue(propietarioNombre);
            row.createCell(5).setCellValue(propietarioCedula);
            row.createCell(6).setCellValue(propietarioTelefono);
            row.createCell(7).setCellValue(propietarioEmail);

            row.createCell(8).setCellValue(
                    p.getMontoTotal() != null ? p.getMontoTotal().doubleValue() : 0);

            row.createCell(9).setCellValue(
                    p.getMetodoPago() != null ? p.getMetodoPago() : "");

            row.createCell(10).setCellValue(
                    p.getNumeroComprobante() != null ? p.getNumeroComprobante() : "");

            row.createCell(11).setCellValue(
                    p.getPagadoPor() != null ? p.getPagadoPor() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void buildResidentesSheet(Workbook wb, List<ResidenteDetalle> residentes, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("Detalle de Residentes");

        Row header = sheet.createRow(0);
        String[] headers = {
            "Edificio", "Departamento", "Piso",
            "Propietario", "Cedula", "Telefono", "Email",
            "Inquilino", "Cedula", "Telefono", "Correo", "Estado"
        };
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (ResidenteDetalle r : residentes) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(s(r.edificioNombre));
            row.createCell(1).setCellValue(s(r.departamentoNumero));
            row.createCell(2).setCellValue(r.piso != null ? r.piso.intValue() : 0);
            row.createCell(3).setCellValue(s(r.propietarioNombre));
            row.createCell(4).setCellValue(s(r.propietarioCedula));
            row.createCell(5).setCellValue(s(r.propietarioTelefono));
            row.createCell(6).setCellValue(s(r.propietarioEmail));
            row.createCell(7).setCellValue(s(r.inquilinoNombre));
            row.createCell(8).setCellValue(s(r.inquilinoCedula));
            row.createCell(9).setCellValue(s(r.inquilinoTelefono));
            row.createCell(10).setCellValue(s(r.inquilinoCorreo));
            row.createCell(11).setCellValue(s(r.inquilinoEstado));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static String s(String v) {
        return v == null ? "" : v;
    }
}