package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST service for hakukohde's.
 *
 * @author mlyly
 */
@Path("/hakukohde")
public interface HakukohdeResource {

    /**
     * /hakukohde?searchTerms=xxx
     *
     * @param searchTerms may be null
     * @param count
     * @param startIndex
     * @param startPage
     * @param language
     * @return list of HakukohdeTyyppi's
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<HakukohdeTyyppi> search(@QueryParam("searchTerms") String searchTerms,
                                        @QueryParam("count") int count,
                                        @QueryParam("startIndex") int startIndex,
                                        @QueryParam("startPage") int startPage,
                                        @QueryParam("language") String language);

    /**
     * /hakukohde/{oid}
     *
     * @param oid
     * @param language
     * @return loaded HakukohdeTyyppi
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON  + ";charset=UTF-8")
    public HakukohdeTyyppi getByOID(@PathParam("oid") String oid, @QueryParam("language") String language);

    /**
     * /hakukohde/{oid}/koulutus
     *
     * @param oid
     * @param language
     * @return
     */
    @GET
    @Path("{oid}/koulutus")
    @Produces(MediaType.APPLICATION_JSON  + ";charset=UTF-8")
    public List<HakukohdeTyyppi> getByOIDKoulutus(@PathParam("oid") String oid, @QueryParam("language") String language);

    /**
     * /hakukohde/{oid}/paasykoe
     *
     * @param oid
     * @param language
     * @return
     */
    @GET
    @Path("{oid}/paasykoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<HakukohdeTyyppi> getByOIDPaasykoe(@PathParam("oid") String oid, @QueryParam("language") String language);

    /**
     * /hakukohde/{oid}/liite
     *
     * @param oid
     * @param language
     * @return
     */
    @GET
    @Path("{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<HakukohdeTyyppi> getByOIDLiite(@PathParam("oid") String oid, @QueryParam("language") String language);

}
