package fi.vm.sade.tarjonta.service.resources.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;

/**
 * Koulutuksen yhteyshenkilöiden hallinta.
 */
@Path("/v1/yhteyshenkilo")
@Api(value = "/v1/yhteyshenkilo", description = "Koulutuksen yhteyshenkilöt")
public interface YhteyshenkiloV1Resource {

    /**
     * GET /yhteyshenkilo/{tarjoajaOid}/{searchTerm}
     *
     * @param tarjoajaOid
     * @param searchTerm
     * @return
     */
    @GET
    @Path("/{tarjoajaOid}/{searchTerm}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakee yhteyshenkilöt annetulle tarjoajalle.", notes = "Hakee yhteyshenkilöt annetulle tarjoajalle.")
    public List<YhteyshenkiloRDTO> getByOID(@PathParam("tarjoajaOid") String tarjoajaOid, @PathParam("searchTerm") String searchTerm);

}
