package fi.vm.sade.tarjonta.service.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TilaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Path("/v1/tila")
@Api(
    value = "/v1/tila",
    description = "Hakee tarjonnnan server-puolen tilat ja säännöt js-client puolelle.")
public interface TilaV1Resource {

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Tarjonnan tilat ja tilasiirtymäsäännöt.",
      notes = "Tarjonnan tilat ja tilasiirtymäsäännöt.")
  public ResultV1RDTO<Map<TarjontaTila, TilaV1RDTO>> getTilat();
}
