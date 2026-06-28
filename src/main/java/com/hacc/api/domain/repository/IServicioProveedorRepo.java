package com.hacc.api.domain.repository;

import com.hacc.api.domain.model.ServicioProveedor;
import com.hacc.api.domain.enums.NombreServicio;
import com.hacc.api.domain.enums.EstadoServicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IServicioProveedorRepo {

    // CRUD Básico
    void crear(ServicioProveedor servicio);
    void actualizar(ServicioProveedor servicio);
    void eliminar(Long idServicio);
    Optional<ServicioProveedor> buscarPorId(Long idServicio);
    List<ServicioProveedor> listarTodos();

    // Filtros por propietario
    List<ServicioProveedor> listarPorPropietario(Integer idPropietario);
    List<ServicioProveedor> listarPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado);
    List<ServicioProveedor> listarPorPropietarioYNombre(Integer idPropietario, NombreServicio nombre);

    // Filtros por período
    List<ServicioProveedor> listarPorMes(String mes);
    List<ServicioProveedor> listarPorPropietarioYMes(Integer idPropietario, String mes);

    // Filtros combinados
    List<ServicioProveedor> listarPorPropietarioMesYEstado(Integer idPropietario, String mes, EstadoServicio estado);
    List<ServicioProveedor> listarPorPropietarioMesYNombre(Integer idPropietario, String mes, NombreServicio nombre);

    // Filtros por fecha
    List<ServicioProveedor> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<ServicioProveedor> listarPorPropietarioYRangoFechas(Integer idPropietario, LocalDate fechaInicio, LocalDate fechaFin);

    // Consultas de totales
    BigDecimal sumarMontoFacturadoPorPropietarioYMes(Integer idPropietario, String mes);
    BigDecimal sumarMontoPagadoPorPropietarioYMes(Integer idPropietario, String mes);
    BigDecimal sumarMontoFacturadoPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado);

    // Validaciones
    boolean existeServicioPorId(Long idServicio);
    boolean existeServicioPorPropietarioMesYNombre(Integer idPropietario, String mes, NombreServicio nombre);

    // Consultas para dashboard
    Long contarPorPropietarioYEstado(Integer idPropietario, EstadoServicio estado);
    List<Object[]> agruparPorEstado(Integer idPropietario);
    List<ServicioProveedor> listarVencidosPorPropietario(Integer idPropietario);
}