package com.hacc.api.domain.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hacc.api.domain.enums.EstadoServicio;
import com.hacc.api.domain.enums.NombreServicio;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicio_proveedor")
public class ServicioProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "ser_nombre", nullable = false, length = 20)
    private NombreServicio nombre; 

    @Column(name = "ser_mes", nullable = false, length = 7)
    private String mes; 

    @Column(name = "ser_monto_facturado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoFacturado;

    @Column(name = "ser_monto_pagado", precision = 12, scale = 2)
    private BigDecimal montoPagado; 

    @Enumerated(EnumType.STRING)
    @Column(name = "ser_estado", nullable = false, length = 20)
    private EstadoServicio estado; 

    @Column(name = "ser_fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_transaccion_id")
    private FinanzaTransaccion transaccion; 
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Propietario propietario;

    @Column(name = "id_propietario", insertable = false, updatable = false)
    private Integer idPropietario;

    @CreationTimestamp
    @Column(name = "ser_creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "ser_actualizado_en")
    private LocalDateTime actualizadoEn;

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public NombreServicio getNombre() {
        return nombre;
    }

    public void setNombre(NombreServicio nombre) {
        this.nombre = nombre;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public BigDecimal getMontoFacturado() {
        return montoFacturado;
    }

    public void setMontoFacturado(BigDecimal montoFacturado) {
        this.montoFacturado = montoFacturado;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(EstadoServicio estado) {
        this.estado = estado;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public FinanzaTransaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(FinanzaTransaccion transaccion) {
        this.transaccion = transaccion;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    @Override
    public String toString() {
        return "ServicioProveedor [idServicio=" + idServicio + ", nombre=" + nombre + ", mes=" + mes
                + ", montoFacturado=" + montoFacturado + ", montoPagado=" + montoPagado + ", estado=" + estado
                + ", fechaVencimiento=" + fechaVencimiento + ", transaccion=" + transaccion + ", creadoEn=" + creadoEn
                + ", actualizadoEn=" + actualizadoEn + "]";
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Integer getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(Integer idPropietario) {
        this.idPropietario = idPropietario;
    }
}