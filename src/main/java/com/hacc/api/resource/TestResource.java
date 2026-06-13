package com.hacc.api.resource;
 
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test")
public class TestResource {

    @GET
    public String test() {
        return "Backend OK";
    }
}
 