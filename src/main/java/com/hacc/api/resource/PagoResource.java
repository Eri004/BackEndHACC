package com.hacc.api.resource;

import java.util.List;

import com.hacc.api.application.service.PagoService;
import com.hacc.api.domain.model.Pago;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/pagos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoResource {

    @Inject 
    PagoService pagoService;

    @GET
    public List<Pago> listarPagos(@QueryParam("idResidente") Integer idResidente) {
        if (idResidente != null) {
            return pagoService.listarPorResidente(idResidente);
        }
        return pagoService.listar();
    }

    @GET
    @Path("/{id_pago}")
    public Pago obtenerPago(@PathParam("id_pago") Integer id_pago) {
        return pagoService.obtener(id_pago);
    }

    @POST
    public void crearPago(Pago pago) {
        pagoService.crear(pago);
    }

    @DELETE
    @Path("/{id_pago}")
    public void eliminarPago(@PathParam("id_pago") Integer id_pago) {
        pagoService.eliminar(id_pago);
    }

    @PATCH  
    @Path("/{id_pago}")
    public void actualizarPago(@PathParam("id_pago") Integer id_pago, Pago pago) {
        pagoService.actualizar(id_pago, pago);
    }
}
