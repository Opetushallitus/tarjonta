package fi.vm.sade.tarjonta.service.resources.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.RyhmaliitosV1RDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/v1/organisaatioryhma")
@Api(value = "/v1/organisaatioryhma", description = "Hakukohteiden organisaatioryhmiin liittyvät operaatiot")
public interface OrganisaatioryhmaV1Resource {

    @POST
    @Path("/{oid}/lisaa")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Lisää hakukohteet annettuun organisaatioryhmään",
            response = ResultV1RDTO.class)
    public ResultV1RDTO addRyhmaliitokset(@ApiParam(value = "Organisaatioryhmän oid", required = true)
                                          @PathParam("oid") String ryhmaOid,
                                          Set<RyhmaliitosV1RDTO> ryhmaliitokset);

    @POST
    @Path("/{oid}/poista")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Poistaa hakukohteet annetusta organisaatioryhmästä",
            response = ResultV1RDTO.class)
    public ResultV1RDTO removeRyhmaliitokset(@ApiParam(value = "Organisaatioryhmän oid", required = true)
                                             @PathParam("oid") String ryhmaOid,
                                             @ApiParam(value = "Lista hakukohde oideja", required = false)
                                             Set<String> hakukohdeOids);
}
