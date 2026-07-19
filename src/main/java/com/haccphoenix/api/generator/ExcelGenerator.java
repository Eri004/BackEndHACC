package com.haccphoenix.api.generator;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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
import com.haccphoenix.api.request.EstadoPagoDepartamento;
import com.haccphoenix.api.request.ResidenteDetalle;

public class ExcelGenerator {

    public static byte[] build(List<Pago> pagos, List<ResidenteDetalle> residentes, List<EstadoPagoDepartamento> estadoPagos) {

        try (Workbook wb = new XSSFWorkbook()) {

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerRed = wb.createCellStyle();
            headerRed.cloneStyleFrom(headerStyle);
            headerRed.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
            headerRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle headerGreen = wb.createCellStyle();
            headerGreen.cloneStyleFrom(headerStyle);
            headerGreen.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            buildPagosSheet(wb, pagos, headerStyle);
            buildEstadoPagosSheet(wb, estadoPagos, headerRed);
            buildResidentesSheet(wb, residentes, headerGreen);

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

            row.createCell(0).setCellValue(p.getFecha() != null ? p.getFecha().toString() : "");

            row.createCell(1).setCellValue(
                    p.getDepartamento() != null
                            && p.getDepartamento().getEdificio() != null
                            && p.getDepartamento().getEdificio().getNombre() != null
                            ? p.getDepartamento().getEdificio().getNombre() : "");

            row.createCell(2).setCellValue(
                    p.getDepartamento() != null && p.getDepartamento().getNumero() != null
                            ? p.getDepartamento().getNumero() : "");

            row.createCell(3).setCellValue(
                    p.getDepartamento() != null && p.getDepartamento().getPiso() != null
                            ? p.getDepartamento().getPiso().intValue() : 0);

            String propietarioNombre = "", propietarioCedula = "", propietarioTelefono = "", propietarioEmail = "";
            if (p.getDepartamento() != null) {
                Propietario prop = p.getDepartamento().getPropietario();
                if (prop != null) {
                    propietarioCedula = s(prop.getCedula());
                    propietarioTelefono = s(prop.getTelefono());
                    Usuario u = prop.getUsuario();
                    if (u != null) {
                        propietarioNombre = (s(u.getNombre())) + (s(u.getApellido()));
                        propietarioEmail = s(u.getEmail());
                    }
                }
            }
            row.createCell(4).setCellValue(propietarioNombre);
            row.createCell(5).setCellValue(propietarioCedula);
            row.createCell(6).setCellValue(propietarioTelefono);
            row.createCell(7).setCellValue(propietarioEmail);

            row.createCell(8).setCellValue(p.getMontoTotal() != null ? p.getMontoTotal().doubleValue() : 0);
            row.createCell(9).setCellValue(s(p.getMetodoPago()));
            row.createCell(10).setCellValue(s(p.getNumeroComprobante()));
            row.createCell(11).setCellValue(s(p.getPagadoPor()));
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private static void buildEstadoPagosSheet(Workbook wb, List<EstadoPagoDepartamento> estado, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("Estado de Cuotas");

        CellStyle cellPagado = wb.createCellStyle();
        cellPagado.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellPagado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle cellParcial = wb.createCellStyle();
        cellParcial.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        cellParcial.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle cellPendiente = wb.createCellStyle();
        cellPendiente.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        cellPendiente.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] headers = {
            "Edificio", "Depto", "Piso", "Propietario", "Cedula", "Telefono", "Email",
            "Alicuota", "Cargos generados", "Cargos pagados", "Cargos parciales", "Cargos pendientes", "Cargos anulados",
            "Monto generado", "Monto pagado", "Monto pendiente", "Estado cuota",
            "Inquilino", "Cedula inquilino", "Estado inquilino", "Fecha ingreso", "Fecha salida"
        };
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (EstadoPagoDepartamento e : estado) {
            Row row = sheet.createRow(rowIdx++);

            int col = 0;
            row.createCell(col++).setCellValue(s(e.edificioNombre));
            row.createCell(col++).setCellValue(s(e.departamentoNumero));
            row.createCell(col++).setCellValue(e.piso != null ? e.piso.intValue() : 0);
            row.createCell(col++).setCellValue(s(e.propietarioNombre));
            row.createCell(col++).setCellValue(s(e.propietarioCedula));
            row.createCell(col++).setCellValue(s(e.propietarioTelefono));
            row.createCell(col++).setCellValue(s(e.propietarioEmail));
            row.createCell(col++).setCellValue(e.alicuota != null ? e.alicuota.doubleValue() : 0);
            row.createCell(col++).setCellValue(e.cargosGenerados != null ? e.cargosGenerados : 0);
            row.createCell(col++).setCellValue(e.cargosPagados != null ? e.cargosPagados : 0);
            row.createCell(col++).setCellValue(e.cargosParciales != null ? e.cargosParciales : 0);
            row.createCell(col++).setCellValue(e.cargosPendientes != null ? e.cargosPendientes : 0);
            row.createCell(col++).setCellValue(e.cargosAnulados != null ? e.cargosAnulados : 0);
            row.createCell(col++).setCellValue(money(e.montoGenerado));
            row.createCell(col++).setCellValue(money(e.montoPagado));
            row.createCell(col++).setCellValue(money(e.montoPendiente));

            org.apache.poi.ss.usermodel.Cell estadoCell = row.createCell(col++);
            estadoCell.setCellValue(s(e.estadoCuota));
            if ("PAGADO".equals(e.estadoCuota)) estadoCell.setCellStyle(cellPagado);
            else if ("PARCIAL".equals(e.estadoCuota)) estadoCell.setCellStyle(cellParcial);
            else if ("PENDIENTE".equals(e.estadoCuota)) estadoCell.setCellStyle(cellPendiente);

            row.createCell(col++).setCellValue(s(e.inquilinoNombre));
            row.createCell(col++).setCellValue(s(e.inquilinoCedula));
            row.createCell(col++).setCellValue(s(e.inquilinoEstado));
            row.createCell(col++).setCellValue(s(e.inquilinoFechaIngreso));
            row.createCell(col++).setCellValue(s(e.inquilinoFechaSalida));
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
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
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private static double money(BigDecimal v) {
        if (v == null) return 0;
        return v.doubleValue();
    }

    private static String s(String v) {
        return v == null ? "" : v;
    }
}