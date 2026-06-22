package com.hacc.api.resource;

import com.hacc.api.application.service.ReportService;
import com.hacc.api.request.ReportRequest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/reports")
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    ReportService reportService;

    @POST
    @Path("/generate")
    public Response generate(ReportRequest request) {

        if (request.format == null) {
            return Response.status(400)
                    .entity("Formato requerido (pdf o excel)")
                    .build();
        }

        byte[] file;

        if ("excel".equalsIgnoreCase(request.format)) {

            file = reportService.generateExcel(request);

            return Response.ok(file)
                    .header("Content-Disposition",
                            "attachment; filename=report.xlsx")
                    .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .build();
        }

        file = reportService.generatePdf(request);

        return Response.ok(file)
                .header("Content-Disposition",
                        "attachment; filename=report.pdf")
                .type("application/pdf")
                .build();
    }
}