package fi.vm.sade.tarjonta.service.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/v1/process")
@Api(
    value = "/v1/process",
    description = "Pitkään kestävien prosessien käynnistäminen ja hallinta.")
public interface ProcessResourceV1 {

  @POST
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(value = "Prosessin käynnistäminen", notes = "Prosessin käynnistäminen")
  ProcessV1RDTO start(ProcessV1RDTO processDefinition);

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(value = "Prosessien listaaminen", notes = "Prosessien listaaminen")
  List<ProcessV1RDTO> list();

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(value = "Prosessin tilan lataaminen", notes = "Prosessin tilan lataaminen")
  ProcessV1RDTO get(@PathParam("id") String id);

  @GET
  @Path("/{id}/stop")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(value = "Prosessin keskeyttäminen", notes = "Prosessin keskeyttäminen")
  ProcessV1RDTO stop(@PathParam("id") String id);
}
