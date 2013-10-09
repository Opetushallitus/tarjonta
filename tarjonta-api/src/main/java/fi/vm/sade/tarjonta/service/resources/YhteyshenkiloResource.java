package fi.vm.sade.tarjonta.service.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;

@Path("/yhteyshenkilo")
public interface YhteyshenkiloResource {

    /**
     * /yhteyshenkilo/{tarjoajaOid}
     *
     * @param tarjoajaOid
     * @return
     */
    @GET
    @Path("{tarjoajaOid}/{searchTerm}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<YhteyshenkiloRDTO> getByOID(@PathParam("tarjoajaOid") String tarjoajaOid, @PathParam("searchTerm") String searchTerm);
    
}
