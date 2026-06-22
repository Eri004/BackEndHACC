package com.hacc.api.domain.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "residente")
public class Residente {

    @Id
    @SequenceGenerator(name = "residente_seq", sequenceName = "residente_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "residente_seq")
    @Column(name = "id_residente")
    Integer id_residente;

    @Column(name = "res_nombre")
    String nombre;

    @Column(name = "res_apellido")
    String apellido;

    @Column(name = "res_departamento")
    String departamento;

    @Column(name = "res_telefono")
    String telefono;

    @Column(name = "res_ultimo_pago")   
    LocalDate ultimoPago;

    @Column(name = "res_deuda")
    Long deuda;

    @Column(name = "res_estado")
    String estado;

    @Column(name = "res_email")
    String email;

    @Column(name = "res_contrasena")
    String contrasena;

    @Column(name = "res_cedula")
    String cedula;

    @OneToMany(mappedBy = "residente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Pago> pagos;
    
    public Integer getId_residente() {
        return id_residente;
    }

    public void setId_residente(Integer id_residente) {
        this.id_residente = id_residente;
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getUltimoPago() {
        return ultimoPago;
    }

    public void setUltimoPago(LocalDate ultimoPago) {
        this.ultimoPago = ultimoPago;
    }

    public Long getDeuda() {
        return deuda;
    }

    public void setDeuda(Long deuda) {
        this.deuda = deuda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Pago> getPagos() {
        return pagos;
    }
    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
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


    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }


    @Override
    public String toString() {
        return "Residente{" +
                "id_residente=" + id_residente +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", departamento='" + departamento + '\'' +
                ", telefono=" + telefono +
                ", ultimoPago=" + ultimoPago +
                ", deuda=" + deuda +
                ", estado='" + estado + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    

}
