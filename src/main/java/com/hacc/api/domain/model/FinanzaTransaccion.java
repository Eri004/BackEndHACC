package com.hacc.api.domain.model;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hacc.api.domain.enums.CategoriaTransaccion;
import com.hacc.api.domain.enums.TipoTransaccion;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "finanza_transaccion")
public class FinanzaTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Long idTransaccion;

    @Column(name = "fin_titulo", nullable = false, length = 150)
    private String titulo; 

    @Column(name = "fin_monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fin_fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "fin_tipo", nullable = false, length = 20)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "fin_categoria", nullable = false, length = 30)
    private CategoriaTransaccion categoria;

    @Column(name = "fin_subcategoria", length = 50)
    private String subcategoria;

    @Column(name = "fin_comprobante", length = 255)
    private String comprobante; 

    @Column(name = "fin_periodo", nullable = false, length = 7)
    private String periodo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Propietario propietario;

    @Column(name = "id_propietario", insertable = false, updatable = false)
    private Integer idPropietario;

    @CreationTimestamp
    @Column(name = "fin_creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "fin_actualizado_en")
    private LocalDateTime actualizadoEn;

    public Long getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Long idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public CategoriaTransaccion getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTransaccion categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
        return "FinanzaTransaccion [idTransaccion=" + idTransaccion + ", titulo=" + titulo + ", monto=" + monto
                + ", fecha=" + fecha + ", tipo=" + tipo + ", categoria=" + categoria + ", subcategoria=" + subcategoria
                + ", comprobante=" + comprobante + ", periodo=" + periodo + ", creadoEn=" + creadoEn
                + ", actualizadoEn=" + actualizadoEn + "]";
    }
    // quién registró la transacción
    // @ManyToOne
    // @JoinColumn(name = "id_usuario_registra")
    // private Usuario usuarioRegistra;

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
