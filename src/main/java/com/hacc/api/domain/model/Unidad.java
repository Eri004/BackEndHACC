package com.hacc.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

public class Unidad {
    @Id
    @SequenceGenerator(name = "unidad_seq", sequenceName = "unidad_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidad_seq")
    @Column(name = "id_unidad")
    private Long idUnidad;

    @Column(name = "numero", nullable = false, length = 10)
    private String numero;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "area")
    private Double area;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoUnidad estado = EstadoUnidad.DISPONIBLE;
}

enum EstadoUnidad {
    OCUPADO,
    DISPONIBLE,
    MANTENIMIENTO
}