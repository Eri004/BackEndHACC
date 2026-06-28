package com.hacc.api.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hacc.api.domain.enums.EstadoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @SequenceGenerator(name = "pago_seq", sequenceName = "pago_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pago_seq")
    @Column(name = "id_pago")
    private Integer id_pago;
    @Column(name = "id_residente")
    private Integer idResidente;

    @Column(name = "pag_titulo")
    private String titulo;

    @Column(name = "pag_monto")
    private Double monto;
    
    @Column(name = "pag_monto_esperado", nullable = false)
    private Double montoEsperado;
    
    @Column(name = "pag_monto_pagado")
    private Double montoPagado;
    
    @Column(name = "pag_intereses")
    private Double intereses;
    
    @Column(name = "pag_fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "pag_fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento; 
    
    @Column(name = "pag_fecha_pago")
    private LocalDate fechaPago; 
    
    @Column(name = "pag_periodo", nullable = false, length = 7)
    private String periodo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pag_estado", nullable = false, length = 20)
    private EstadoPago estado; 
    
    @Column(name = "pag_observacion", length = 500)
    private String observacion;
    
    @Column(name = "pag_fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "pag_fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_residente", insertable = false, updatable = false)
    private Residente residente;

    public Integer getId_pago() {
        return id_pago;
    }

    public void setId_pago(Integer id_pago) {
        this.id_pago = id_pago;
    }

    public Integer getIdResidente() {
        return idResidente;
    }

    public void setIdResidente(Integer idResidente) {
        this.idResidente = idResidente;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public Residente getResidente() {
        return residente;
    }
    public void setResidente(Residente residente) {
        this.residente = residente;
    }
    
    @Override
    public String toString() {
        return "Pago [id_pago=" + id_pago + ", idResidente=" + idResidente + ", titulo=" + titulo + ", monto=" + monto
                + ", montoEsperado=" + montoEsperado + ", montoPagado=" + montoPagado + ", intereses=" + intereses
                + ", fecha=" + fecha + ", fechaVencimiento=" + fechaVencimiento + ", fechaPago=" + fechaPago
                + ", periodo=" + periodo + ", estado=" + estado + ", observacion=" + observacion + ", fechaCreacion="
                + fechaCreacion + ", fechaActualizacion=" + fechaActualizacion + ", residente=" + residente + "]";
    }

    public Double getMontoEsperado() {
        return montoEsperado;
    }

    public void setMontoEsperado(Double montoEsperado) {
        this.montoEsperado = montoEsperado;
    }

    public Double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(Double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public Double getIntereses() {
        return intereses;
    }

    public void setIntereses(Double intereses) {
        this.intereses = intereses;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
