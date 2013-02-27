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

    /**
     *
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param startPage
     * @param language
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<HakuTyyppi> search(@QueryParam("searchTerms") String searchTerms,
                                   @QueryParam("count") int count,
                                   @QueryParam("startIndex") int startIndex,
                                   @QueryParam("startPage") int startPage,
                                   @QueryParam("language") String language);

    /**
     *
     * @param oid
     * @param language
     * @return
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakuTyyppi getByOID(@PathParam("oid") String oid, @QueryParam("language") String language);

    /**
     *
     * @param oid
     * @param language
     * @return
     */
    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<HakukohdeTyyppi> getByOIDHakukohde(@PathParam("oid") String oid,
                                                   @QueryParam("language") String language);

}
