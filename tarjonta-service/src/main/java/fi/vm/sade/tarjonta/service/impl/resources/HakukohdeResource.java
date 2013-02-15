package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * @author mlyly
 */
@Path("/hakukohde")
public class HakukohdeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> search(@QueryParam("etsi") String spec) {
        LOG.info("search(spec={})", spec);
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HakukohdeTyyppi getByOID(@PathParam("oid") String oid) {
        LOG.info("getByOID({})", oid);
        return null;
    }

    @GET
    @Path("{oid}/koulutus")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDKoulutus(@PathParam("oid") String oid) {
        LOG.info("getByOIDKoulutus({})", oid);
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("{oid}/paasykoe")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDPaasykoe(@PathParam("oid") String oid) {
        LOG.info("getByOIDPaasykoe({})", oid);
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDLiite(@PathParam("oid") String oid) {
        LOG.info("getByOIDLiite({})", oid);
        return Collections.EMPTY_LIST;
    }


}
