package com.hacc.api.domain.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "edificio")
public class Edificio {
    @Id
    @SequenceGenerator(name = "edificio_seq", sequenceName = "edificio_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edificio_seq")
    @Column(name = "id_edificio")
    private Long idEdificio;

    @Column(name = "edi_nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "edi_direccion", length = 200)
    private String direccion;

    @Column(name = "edi_total_unidades")
    private Integer totalUnidades;

    @Column(name = "edi_activo")
    private Boolean activo = true;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Propietario propietario;
    
    @JsonIgnore
    @OneToMany(mappedBy = "edificio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Unidad> unidades = new HashSet<>();
    
    public void agregarUnidad(Unidad unidad) {
        this.unidades.add(unidad);
        unidad.setEdificio(this);
    }

    public Long getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(Long idEdificio) {
        this.idEdificio = idEdificio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Integer totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<Unidad> getUnidades() {
        return unidades;
    }

    public void setUnidades(Set<Unidad> unidades) {
        this.unidades = unidades;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }
    
}
