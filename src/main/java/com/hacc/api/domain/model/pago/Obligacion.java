package com.hacc.api.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "obligaciones")
public class Obligacion {

    @Id
    @SequenceGenerator(name = "obligacion_seq", sequenceName = "obligacion_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obligacion_seq")
    @Column(name = "id_obligacion")
    private Long idObligacion;

    @Column(name = "periodo", nullable = false, length = 7)
    private String periodo; // '2026-06'

    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoObligacion estado = EstadoObligacion.PENDIENTE;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "monto_pagado")
    private Double montoPagado;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "comprobante_pago", length = 255)
    private String comprobantePago;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // RELACIÓN CON UNIDAD
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    // RELACIÓN CON RESIDENTE (quién debe pagar)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residente_id")
    private Residente residente;

    // RELACIÓN CON CONCEPTO DE PAGO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concepto_id", nullable = false)
    private ConceptoPago concepto;

    // RELACIÓN CON TRANSACCIÓN (si ya fue pagada)
    @OneToOne(mappedBy = "obligacion", cascade = CascadeType.ALL)
    private Transaccion transaccion;

    // Métodos helper
    public boolean estaPagada() {
        return this.estado == EstadoObligacion.PAGADO;
    }

    public boolean estaVencida() {
        return this.estado == EstadoObligacion.VENCIDO;
    }

    public Double getSaldoPendiente() {
        if (this.montoPagado == null) return this.monto;
        return this.monto - this.montoPagado;
    }
}

// Enum para estado de obligación
enum EstadoObligacion {
    PENDIENTE,
    PAGADO,
    VENCIDO,
    ANULADO,
    PARCIAL
}