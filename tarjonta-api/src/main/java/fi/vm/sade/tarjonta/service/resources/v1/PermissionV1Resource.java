package fi.vm.sade.tarjonta.service.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.UserV1RDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Path("/v1/permission")
@Api(value = "/v1/permission", description = "Permissioiden tarkistaminen ja sessioiden luominen.")
public interface PermissionV1Resource {

  @GET
  @Path("/authorize")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public ResultV1RDTO<String> authorize();

  @GET
  @Path("/user")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public ResultV1RDTO<UserV1RDTO> getUser();

  @POST
  @Path("/recordUiStacktrace")
  public void recordUiStacktrace(String stacktrace);

  @GET
  @Path("/permissions/{type}/{key}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Permissioiden kysyminen kohteelta",
      notes = "Permissioiden kysyminen kohteelta.")
  public Map getPermissions(@PathParam("type") String type, @PathParam("key") String key);
}
