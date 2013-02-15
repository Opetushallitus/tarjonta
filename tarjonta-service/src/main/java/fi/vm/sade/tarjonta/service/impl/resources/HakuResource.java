package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * http://localhost:8181/tarjonta-service/rest/haku/hello
 *
 * @author mlyly
 */
@Path("/haku")
public class HakuResource {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResource.class);

/*
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired(required = true)
    private ConversionService conversionService;
*/

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOG.info("hello()");
        return "hello";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakuTyyppi> search(@QueryParam("etsi") String spec) {
        LOG.info("search(spec={})", spec);
        return Collections.EMPTY_LIST;
    }

    // TODO how to set the "@XmlRootElelement" to HakuTyyppi when it's generated! Or how to avoid/go around it!

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HakuTyyppi getByOID(@PathParam("oid") String oid) {
        LOG.info("getByOID({})", oid);

        // TESTING, requires "@XmlRootElement" in HakuTyyppi!

        HakuTyyppi result = new HakuTyyppi();
        result.setHakukausiUri("uri: hakukausi");
        return result;

//        Haku haku = hakuDAO.findByOid(oid);
//        return conversionService.convert(haku, HakuTyyppi.class);
    }

    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HakukohdeTyyppi> getByOIDHakukohde(@QueryParam("etsi") String spec) {
        LOG.info("getByOIDHakukohde(spec={})", spec);
        return Collections.EMPTY_LIST;
    }


}
