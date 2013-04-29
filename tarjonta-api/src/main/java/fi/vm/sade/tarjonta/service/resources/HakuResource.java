package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import java.util.Date;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST services for haku's.
 *
 * @author mlyly
 */
@Path("/haku")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface HakuResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    /**
     * /haku?searchTerms=xxx&count=10&startIndex=100&lastModifiedBefore=X&lastModifiedSince=XX
     *
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<String> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);

    /**
     * /haku/OID
     *
     * @param oid
     * @return HakuDTO
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakuDTO getByOID(@PathParam("oid") String oid);

    /**
     * /haku/OID/hakukohde
     *
     * @param oid
     * @return list of Hakukohde oid's
     */
    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<String> getByOIDHakukohde(@PathParam("oid") String oid);
}
