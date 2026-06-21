package com.hacc.api.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidad")
public class Unidad {
    @Id
    @SequenceGenerator(name = "unidad_seq", sequenceName = "unidad_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidad_seq")
    @Column(name = "id_unidad")
    private Long idUnidad;

    @Column(name = "uni_numero", nullable = false, length = 10)
    private String numero;

    @Column(name = "uni_piso")
    private Integer piso;

    @Column(name = "uni_area")
    private Double area;

    @Column(name = "uni_estado")
    @Enumerated(EnumType.STRING)
    private EstadoUnidad estado = EstadoUnidad.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_edificio", nullable = false)
    private Edificio edificio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario")
    private Propietario propietario;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Residente> residentes = new HashSet<>();

    public String getIdentificadorCompleto() {
        return edificio != null ? edificio.getNombre() + " - " + numero : numero;
    }

    public Long getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Long idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public EstadoUnidad getEstado() {
        return estado;
    }

    public void setEstado(EstadoUnidad estado) {
        this.estado = estado;
    }

    public Edificio getEdificio() {
        return edificio;
    }

    public void setEdificio(Edificio edificio) {
        this.edificio = edificio;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Set<Residente> getResidentes() {
        return residentes;
    }

    public void setResidentes(Set<Residente> residentes) {
        this.residentes = residentes;
    }

    
}

enum EstadoUnidad {
    OCUPADO,
    DISPONIBLE,
    MANTENIMIENTO
}