package com.haccphoenix.api.request;

import java.math.BigDecimal;

public class EstadoPagoDepartamento {
    public String edificioNombre;
    public String edificioEstado;
    public String departamentoNumero;
    public Integer piso;
    public String propietarioCedula;
    public String propietarioNombre;
    public String propietarioEmail;
    public String propietarioTelefono;
    public String inquilinoCedula;
    public String inquilinoNombre;
    public String inquilinoEstado;
    public String inquilinoFechaIngreso;
    public String inquilinoFechaSalida;
    public BigDecimal alicuota;
    public Integer cargosGenerados;
    public Integer cargosPagados;
    public Integer cargosPendientes;
    public Integer cargosParciales;
    public Integer cargosAnulados;
    public BigDecimal montoGenerado;
    public BigDecimal montoPagado;
    public BigDecimal montoPendiente;
    public String estadoCuota;
}