package com.hacc.api.resource;

import java.util.List;

import com.hacc.api.application.service.ResidenteService;
import com.hacc.api.domain.model.Residente;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/residentes")

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResidenteResource {
     
    @Inject 
    ResidenteService residenteService;

    @GET
    public List<Residente> listarResidentes() {
        return residenteService.listar();
    }

    @POST
    public void crearResidente(Residente residente) {
        residenteService.crear(residente);
    }

    @DELETE
    @Path("/{id_residente}")
    public void eliminarResidente(@PathParam("id_residente") Integer id_residente) {
        residenteService.eliminar(id_residente);
    }

    @PUT
    @Path("/{id_residente}")
    public void actualizarResidente(@PathParam("id_residente") Integer id_residente, Residente residente) {
        residenteService.actualizar(id_residente, residente);
    }



}
 