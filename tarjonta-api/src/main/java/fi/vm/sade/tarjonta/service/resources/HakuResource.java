package fi.vm.sade.tarjonta.service.resources;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeTulosDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

/**
 * REST services for haku's.
 * 
 * <pre>
 * /haku/hello
 * /haku  (?searchTerms... - list of { oid : xxx }
 * /haku/OID
 * /haku/OID/hakukohde - list of {oid : xxx}
 * /haku/OID/hakukohdetulos - list of {kokonaismaara: xxx, tulokset: []}
 * /haku/OID/hakukohdeWithName - list of {oid: xxx, fi: xxx, en: xxx} documents
 * </pre>
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
     * /haku?searchTerms=xxx&count=10&startIndex=100&lastModifiedBefore=X&
     * lastModifiedSince=XX
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
    public List<OidRDTO> search(@QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
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
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return list of Haku's Hakokohde OIDs
     */
    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<OidRDTO> getByOIDHakukohde(@PathParam("oid") String oid, @QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count, @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);

    /**
     * /haku/OID/hakukohde
     * 
     * @param oid
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return list of Haku's Hakokohde OIDs
     */
    @GET
    @Path("{oid}/hakukohdetulos")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeTulosDTO getByOIDHakukohdeTulos(@PathParam("oid") String oid,
            @QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);

    /**
     * Same as "getByOIDHakukohde" but resolves the koodisto name for
     * hakukohde...
     * 
     * @param oid
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return
     */
    @GET
    @Path("{oid}/hakukohdeWithName")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<Map<String, String>> getByOIDHakukohdeExtra(@PathParam("oid") String oid,
            @QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);
}
