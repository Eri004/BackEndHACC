package com.hacc.api.resource;

import com.hacc.api.domain.model.Propietario;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/propietarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropietarioResource {
    
    @Inject
    EntityManager em;
    
    @POST
    @Transactional
    public Response crearPropietario(Propietario propietario) {
        em.persist(propietario);
        return Response.ok("Propietario creado").build();
    }
    
    @GET
    public Response listarPropietarios() {
        return Response.ok(em.createQuery("SELECT p FROM Propietario p").getResultList()).build();
    }
}