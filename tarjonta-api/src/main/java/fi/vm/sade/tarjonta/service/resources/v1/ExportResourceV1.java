package fi.vm.sade.tarjonta.service.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1/export")
@Api(value = "/v1/export", description = "Tarjonnan export rajapinnan operaatiot")
public interface ExportResourceV1 {

  @GET
  @Path("/kela")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Palauttaa hakuehtojen puitteissa hakujen oid:t",
      notes = "Listaa hakujen oidit",
      response = OidV1RDTO.class)
  public boolean exportKela();
}
