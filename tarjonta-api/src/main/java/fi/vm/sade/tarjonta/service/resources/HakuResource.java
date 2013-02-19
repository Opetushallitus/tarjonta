package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST services for haku's.
 *
 * @author mlyly
 */
@Path("/haku")
public interface HakuResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakuTyyppi> search(@QueryParam("etsi") String spec);

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HakuTyyppi getByOID(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDHakukohde(@QueryParam("etsi") String spec);

}
