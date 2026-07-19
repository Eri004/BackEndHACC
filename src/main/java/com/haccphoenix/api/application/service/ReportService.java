package com.haccphoenix.api.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.haccphoenix.api.domain.model.Cargo;
import com.haccphoenix.api.domain.model.Departamento;
import com.haccphoenix.api.domain.model.Edificio;
import com.haccphoenix.api.domain.model.Inquilino;
import com.haccphoenix.api.domain.model.Pago;
import com.haccphoenix.api.domain.model.Propietario;
import com.haccphoenix.api.domain.model.Usuario;
import com.haccphoenix.api.domain.repository.CargoRepository;
import com.haccphoenix.api.domain.repository.DepartamentoRepository;
import com.haccphoenix.api.domain.repository.EdificioRepository;
import com.haccphoenix.api.domain.repository.InquilinoRepository;
import com.haccphoenix.api.domain.repository.PagoRepository;
import com.haccphoenix.api.generator.ExcelGenerator;
import com.haccphoenix.api.generator.PdfGenerator;
import com.haccphoenix.api.request.EstadoPagoDepartamento;
import com.haccphoenix.api.request.ReportRequest;
import com.haccphoenix.api.request.ResidenteDetalle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReportService {

    @Inject
    PagoRepository pagoRepository;

    @Inject
    CargoRepository cargoRepository;

    @Inject
    DepartamentoRepository departamentoRepository;

    @Inject
    InquilinoRepository inquilinoRepository;

    @Inject
    EdificioRepository edificioRepository;

    public byte[] generatePdf(ReportRequest req) {
        List<Pago> pagos = getPagos(req);
        List<ResidenteDetalle> residentes = getResidentes(req);
        List<EstadoPagoDepartamento> estado = getEstadoPagos(req);
        return PdfGenerator.buildFinancialReport(pagos, residentes, estado);
    }

    public byte[] generateExcel(ReportRequest req) {
        List<Pago> pagos = getPagos(req);
        if (pagos == null || pagos.isEmpty()) {
            throw new RuntimeException("No hay datos para generar el reporte");
        }
        List<ResidenteDetalle> residentes = getResidentes(req);
        List<EstadoPagoDepartamento> estado = getEstadoPagos(req);
        return ExcelGenerator.build(pagos, residentes, estado);
    }

    private DateRange getPeriod(ReportRequest req) {
        switch (req.period) {
            case "jun-2026":
                return new DateRange(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
            default:
                return new DateRange(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        }
    }

    private List<Integer> getEdificioIds(ReportRequest req) {
        if (req.edificioIds != null && !req.edificioIds.isEmpty()) {
            return req.edificioIds;
        }
        if (req.edificioId != null) {
            return List.of(req.edificioId);
        }
        return Collections.emptyList();
    }

    private List<Pago> getPagos(ReportRequest req) {
        DateRange r = getPeriod(req);
        List<Integer> ids = getEdificioIds(req);
        if (!ids.isEmpty()) {
            if (ids.size() == 1) {
                return pagoRepository.listarPorPeriodoYEdificio(r.start, r.end, ids.get(0));
            }
            return pagoRepository.listarPorPeriodoYEdificios(r.start, r.end, ids);
        }
        return pagoRepository.listarPorPeriodo(r.start, r.end);
    }

    private List<Departamento> getDepartamentos(ReportRequest req) {
        List<Integer> ids = getEdificioIds(req);
        if (ids.isEmpty()) {
            return departamentoRepository.listAll();
        }
        if (ids.size() == 1) {
            return departamentoRepository.listarPorEdificio(ids.get(0));
        }
        return departamentoRepository.listarPorEdificios(ids);
    }

    private List<ResidenteDetalle> getResidentes(ReportRequest req) {
        List<Departamento> deptos = getDepartamentos(req);
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

    private List<EstadoPagoDepartamento> getEstadoPagos(ReportRequest req) {
        DateRange r = getPeriod(req);
        List<Integer> ids = getEdificioIds(req);
        List<Cargo> cargos;
        if (ids.isEmpty()) {
            cargos = cargoRepository.listarPorPeriodo(r.start, r.end);
        } else if (ids.size() == 1) {
            cargos = cargoRepository.listarPorPeriodoYEdificios(r.start, r.end, ids);
        } else {
            cargos = cargoRepository.listarPorPeriodoYEdificios(r.start, r.end, ids);
        }

        java.util.Map<Integer, List<Cargo>> cargosPorDepto = new java.util.HashMap<>();
        for (Cargo c : cargos) {
            if (c.getDepartamento() == null || c.getDepartamento().getId() == null) continue;
            cargosPorDepto.computeIfAbsent(c.getDepartamento().getId(), k -> new ArrayList<>()).add(c);
        }

        List<Departamento> deptos = getDepartamentos(req);
        List<EstadoPagoDepartamento> out = new ArrayList<>();
        for (Departamento d : deptos) {
            EstadoPagoDepartamento e = new EstadoPagoDepartamento();

            Edificio ed = d.getEdificio();
            e.edificioNombre = ed != null && ed.getNombre() != null ? ed.getNombre() : "";
            e.edificioEstado = ed != null && ed.getEstado() != null ? ed.getEstado() : "";
            e.departamentoNumero = d.getNumero() != null ? d.getNumero() : "";
            e.piso = d.getPiso();
            e.alicuota = d.getAlicuota();

            Propietario p = d.getPropietario();
            if (p != null) {
                e.propietarioCedula = n(p.getCedula());
                e.propietarioTelefono = n(p.getTelefono());
                Usuario u = p.getUsuario();
                if (u != null) {
                    String nombre = n(u.getNombre());
                    String apellido = n(u.getApellido());
                    e.propietarioNombre = (nombre + " " + apellido).trim();
                    e.propietarioEmail = n(u.getEmail());
                }
            }

            Optional<Inquilino> inqOpt = inquilinoRepository.findByDepartamentoOptional(d.getId());
            if (inqOpt.isPresent()) {
                Inquilino i = inqOpt.get();
                e.inquilinoCedula = n(i.getCedula());
                String nombre = n(i.getNombre());
                String apellido = n(i.getApellido());
                e.inquilinoNombre = (nombre + " " + apellido).trim();
                e.inquilinoEstado = n(i.getEstado());
                e.inquilinoFechaIngreso = i.getFechaIngreso() != null ? i.getFechaIngreso().toString() : "";
                e.inquilinoFechaSalida = i.getFechaSalida() != null ? i.getFechaSalida().toString() : "";
            }

            List<Cargo> dCargos = cargosPorDepto.getOrDefault(d.getId(), Collections.emptyList());
            int gen = 0, pag = 0, pen = 0, par = 0, anu = 0;
            BigDecimal mGen = BigDecimal.ZERO, mPag = BigDecimal.ZERO, mPen = BigDecimal.ZERO;
            for (Cargo c : dCargos) {
                if (c.getEstado() == Cargo.EstadoCargo.ANULADO) { anu++; continue; }
                gen++;
                BigDecimal v = c.getValor() != null ? c.getValor() : BigDecimal.ZERO;
                mGen = mGen.add(v);
                switch (c.getEstado()) {
                    case PAGADO -> { pag++; mPag = mPag.add(v); }
                    case PARCIAL -> { par++; mPen = mPen.add(v); }
                    case PENDIENTE -> { pen++; mPen = mPen.add(v); }
                    default -> {}
                }
            }
            e.cargosGenerados = gen;
            e.cargosPagados = pag;
            e.cargosParciales = par;
            e.cargosPendientes = pen;
            e.cargosAnulados = anu;
            e.montoGenerado = mGen;
            e.montoPagado = mPag;
            e.montoPendiente = mPen;

            if (gen == 0) {
                e.estadoCuota = "SIN CARGOS";
            } else if (pen == 0 && par == 0) {
                e.estadoCuota = "PAGADO";
            } else if (pag > 0 || par > 0) {
                e.estadoCuota = "PARCIAL";
            } else {
                e.estadoCuota = "PENDIENTE";
            }

            out.add(e);
        }
        return out;
    }

    private static String n(String v) {
        return v == null ? "" : v;
    }

    private static class DateRange {
        final LocalDate start;
        final LocalDate end;
        DateRange(LocalDate s, LocalDate e) { start = s; end = e; }
    }
}