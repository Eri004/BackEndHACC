package com.hacc.api.domain.model;

import com.hacc.api.domain.enums.NombreServicio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hacc.api.domain.enums.FrecuenciaServicio;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "plantilla_servicio")
public class PlantillaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Long idPlantilla;

    @Enumerated(EnumType.STRING)
    @Column(name = "pla_nombre", nullable = false, length = 20)
    private NombreServicio nombre;

    @Column(name = "pla_descripcion", length = 255)
    private String descripcion;

    @Column(name = "pla_monto_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "pla_frecuencia", nullable = false, length = 20)
    private FrecuenciaServicio frecuencia; // MENSUAL, TRIMESTRAL, SEMESTRAL, ANUAL

    @Column(name = "pla_dia_vencimiento", nullable = false)
    private Integer diaVencimiento; // Día del mes (1-31)

    @Column(name = "pla_mes_inicio", length = 7)
    private String mesInicio; // yyyy-MM

    @Column(name = "pla_fecha_fin")
    private LocalDate fechaFin; // null = indefinido

    @Column(name = "pla_activo", nullable = false)
    private Boolean activo = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    @JsonIgnore
    private Propietario propietario;

    @Column(name = "id_propietario", insertable = false, updatable = false)
    private Integer idPropietario;

    @Column(name = "pla_creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "pla_actualizado_en")
    private LocalDateTime actualizadoEn;

    // Getters y Setters
    public Long getIdPlantilla() { return idPlantilla; }
    public void setIdPlantilla(Long idPlantilla) { this.idPlantilla = idPlantilla; }

    public NombreServicio getNombre() { return nombre; }
    public void setNombre(NombreServicio nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getMontoBase() { return montoBase; }
    public void setMontoBase(BigDecimal montoBase) { this.montoBase = montoBase; }

    public FrecuenciaServicio getFrecuencia() { return frecuencia; }
    public void setFrecuencia(FrecuenciaServicio frecuencia) { this.frecuencia = frecuencia; }

    public Integer getDiaVencimiento() { return diaVencimiento; }
    public void setDiaVencimiento(Integer diaVencimiento) { this.diaVencimiento = diaVencimiento; }

    public String getMesInicio() { return mesInicio; }
    public void setMesInicio(String mesInicio) { this.mesInicio = mesInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Propietario getPropietario() { return propietario; }
    public void setPropietario(Propietario propietario) { this.propietario = propietario; }

    public Integer getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}