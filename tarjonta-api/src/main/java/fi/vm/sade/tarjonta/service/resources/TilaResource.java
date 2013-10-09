package fi.vm.sade.tarjonta.service.resources;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.TilaRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * REST palvelu tarjonnan staattiselle tilainformaatiolle.
 *
 * <pre>
 * GET    /tila
 * </pre>
 *
 * @author Timo Santasalo / Teknokala Ky
 */
@Path("/tila")
public interface TilaResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<TarjontaTila, TilaRDTO> getTilat();

}
