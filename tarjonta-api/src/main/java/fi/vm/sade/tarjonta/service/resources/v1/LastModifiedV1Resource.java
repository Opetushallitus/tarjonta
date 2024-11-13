package fi.vm.sade.tarjonta.service.resources.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/v1/lastmodified")
@Api(value = "/v1/lastmodified", description = "Muutosten listaaminen tarjonnan tiedoista.")
public interface LastModifiedV1Resource {

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Palauttaa hakuehtojen puitteissa muutosten oid:t ryhmiteltynä.",
      notes = "Listaa muutosten oidit",
      response = Map.class)
  public Map<String, List<String>> lastModified(
      @QueryParam("lastModified") long lastModifiedTs,
      @QueryParam("deleted") @DefaultValue("false") Boolean deleted);
}
