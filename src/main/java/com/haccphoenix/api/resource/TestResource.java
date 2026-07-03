package com.haccphoenix.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test")
public class TestResource {

    @GET
    public String test() {
        return "HACCPHOENIX Backend OK";
    }
}
