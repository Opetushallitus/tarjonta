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
     * /hakukohde?etsi=xxx
     *
     * @param spec may be null
     * @return list of HakukohdeTyyppi's
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> search(@QueryParam("etsi") String spec);

    /**
     * /hakukohde/{oid}
     *
     * @param oid
     * @return loaded HakukohdeTyyppi
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HakukohdeTyyppi getByOID(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/koulutus
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/koulutus")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDKoulutus(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/paasykoe
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/paasykoe")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDPaasykoe(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/liite
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDLiite(@PathParam("oid") String oid);

}
