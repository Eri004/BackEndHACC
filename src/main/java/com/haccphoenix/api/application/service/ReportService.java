package com.haccphoenix.api.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.haccphoenix.api.domain.model.Departamento;
import com.haccphoenix.api.domain.model.Edificio;
import com.haccphoenix.api.domain.model.Inquilino;
import com.haccphoenix.api.domain.model.Pago;
import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.DepartamentoRepository;
import com.haccphoenix.api.domain.repository.InquilinoRepository;
import com.haccphoenix.api.domain.repository.PagoRepository;
import com.haccphoenix.api.generator.ExcelGenerator;
import com.haccphoenix.api.generator.PdfGenerator;
import com.haccphoenix.api.request.ReportRequest;
import com.haccphoenix.api.request.ResidenteDetalle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReportService {

    @Inject
    PagoRepository pagoRepository;

    @Inject
    DepartamentoRepository departamentoRepository;

    @Inject
    InquilinoRepository inquilinoRepository;

    public byte[] generatePdf(ReportRequest req) {
        List<Pago> pagos = getPagos(req);
        List<ResidenteDetalle> residentes = getResidentes(req.edificioId);
        return PdfGenerator.buildFinancialReport(pagos, residentes);
    }

    public byte[] generateExcel(ReportRequest req) {
        List<Pago> pagos = getPagos(req);
        if (pagos == null || pagos.isEmpty()) {
            throw new RuntimeException("No hay datos para generar el reporte");
        }
        List<ResidenteDetalle> residentes = getResidentes(req.edificioId);
        return ExcelGenerator.build(pagos, residentes);
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

        if (req.edificioId != null) {
            return pagoRepository.listarPorPeriodoYEdificio(start, end, req.edificioId);
        }
        return pagoRepository.listarPorPeriodo(start, end);
    }

    private List<ResidenteDetalle> getResidentes(Integer edificioId) {
        List<Departamento> deptos = (edificioId != null)
                ? departamentoRepository.listarPorEdificio(edificioId)
                : departamentoRepository.listAll();

        List<ResidenteDetalle> out = new ArrayList<>();
        for (Departamento d : deptos) {
            ResidenteDetalle r = new ResidenteDetalle();

            Edificio ed = d.getEdificio();
            r.edificioNombre = ed != null && ed.getNombre() != null ? ed.getNombre() : "";
            r.departamentoNumero = d.getNumero() != null ? d.getNumero() : "";
            r.piso = d.getPiso();

            Propietario p = d.getPropietario();
            if (p != null) {
                r.propietarioCedula = n(p.getCedula());
                r.propietarioTelefono = n(p.getTelefono());
                Usuario u = p.getUsuario();
                if (u != null) {
                    String nombre = n(u.getNombre());
                    String apellido = n(u.getApellido());
                    r.propietarioNombre = (nombre + " " + apellido).trim();
                    r.propietarioEmail = n(u.getEmail());
                }
            }

            Optional<Inquilino> inqOpt = inquilinoRepository.findByDepartamentoOptional(d.getId());
            if (inqOpt.isPresent()) {
                Inquilino i = inqOpt.get();
                r.inquilinoCedula = n(i.getCedula());
                String nombre = n(i.getNombre());
                String apellido = n(i.getApellido());
                r.inquilinoNombre = (nombre + " " + apellido).trim();
                r.inquilinoTelefono = n(i.getTelefono());
                r.inquilinoCorreo = n(i.getCorreo());
                r.inquilinoEstado = n(i.getEstado());
            }

            out.add(r);
        }
        return out;
    }

    private static String n(String v) {
        return v == null ? "" : v;
    }
}