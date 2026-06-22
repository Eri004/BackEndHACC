package com.hacc.api.domain.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "pag_fecha")
    private LocalDate fecha;
    
    @JsonIgnore
    @ManyToOne
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
                + ", fecha=" + fecha + "]";
    }
}
