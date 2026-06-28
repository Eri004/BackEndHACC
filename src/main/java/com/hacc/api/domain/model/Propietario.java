package com.hacc.api.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "propietario")
public class Propietario {
    @Id
    @SequenceGenerator(name = "propietario_seq", sequenceName = "propietario_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "propietario_seq")
    @Column(name = "id_propietario")
    private Integer id_propietario;
    @Column(name = "prop_nombre")
    private String nombre;
    @Column(name = "prop_apellido")
    private String apellido;
    @Column(name = "prop_email")
    private String email;
    @Column(name = "prop_telefono")
    private String telefono;
    @Column(name = "prop_contrasena", nullable = false)
    private String contrasena;
    @Column(name = "prop_cedula", nullable = false)
    private String cedula;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Unidad> unidades = new ArrayList<>();
    
    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FinanzaTransaccion> transacciones = new ArrayList<>();

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ServicioProveedor> servicios = new ArrayList<>();

    public Integer getId_propietario() {
        return id_propietario;
    }
    public void setId_propietario(Integer id_propietario) {
        this.id_propietario = id_propietario;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    
    @Override
    public String toString() {
        return "Propietario [id_propietario=" + id_propietario + ", nombre=" + nombre + ", apellido=" + apellido
                + ", email=" + email + ", telefono=" + telefono + "]";
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public List<Unidad> getUnidades() {
        return unidades;
    }
    public void setUnidades(List<Unidad> unidades) {
        this.unidades = unidades;
    }
    public List<FinanzaTransaccion> getTransacciones() {
        return transacciones;
    }
    public void setTransacciones(List<FinanzaTransaccion> transacciones) {
        this.transacciones = transacciones;
    }
    public List<ServicioProveedor> getServicios() {
        return servicios;
    }
    public void setServicios(List<ServicioProveedor> servicios) {
        this.servicios = servicios;
    }
}
