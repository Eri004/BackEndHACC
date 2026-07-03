package com.haccphoenix.api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "departamentos",
    uniqueConstraints = @UniqueConstraint(name = "departamentos_edificio_id_numero_key", columnNames = {"edificio_id", "numero"})
)
public class Departamento extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edificio_id", nullable = false)
    private Edificio edificio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "alicuota", nullable = false, precision = 10, scale = 2)
    private BigDecimal alicuota;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getIdentificadorCompleto() {
        return (edificio != null ? edificio.getNombre() + " - " : "") + numero;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Edificio getEdificio() { return edificio; }
    public void setEdificio(Edificio edificio) { this.edificio = edificio; }

    public Propietario getPropietario() { return propietario; }
    public void setPropietario(Propietario propietario) { this.propietario = propietario; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public BigDecimal getAlicuota() { return alicuota; }
    public void setAlicuota(BigDecimal alicuota) { this.alicuota = alicuota; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
