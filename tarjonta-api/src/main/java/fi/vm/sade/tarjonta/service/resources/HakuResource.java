package fi.vm.sade.tarjonta.service.resources;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeTulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KelaHakukohteetDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;

/**
 * REST services for haku's.
 *
 * <pre>
 * GET    /haku/hello
 * GET    /haku  (?searchTerms... - list of { oid : xxx }
 *
 * GET    /haku/OID
 * POST   /haku/OID - create haku
 * PUT    /haku/OID - replace existing haku
 * DELETE /haku/OID - delete haku
 *
 * PUT    /haku/OID/state - update state (state string as body)
 *
 * GET    /haku/OID/hakukohde - list of {oid : xxx}
 * GET    /haku/OID/hakukohdetulos - list of {kokonaismaara: xxx, tulokset: []}
 * GET    /haku/OID/hakukohdeWithName - list of {oid: xxx, fi: xxx, en: xxx} documents
 *
 * PUT    /haku/OID/hakuaika - creates a hakuaika for a haku
 *
 * PUT    /haku/hakuaika/OID - replaces existing hakuaika
 * DELETE /haku/hakuaika/OID - deletes a hakuaika
 *
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
     * @param count default value 100 used if count == 0, if count < 0 value is Integer.MAX_VALUE
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



    @GET
    @Path("/findAll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<HakuDTO> findAllHakus();
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
     * @param count default value 100 used if count == 0, if count < 0 value is Integer.MAX_VALUE
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @param organisationOidsStr  limit result under these OIDs
     * @param hakukohdeTilasStr  return only results with these states
     * @return list of Haku's Hakokohde OIDs
     */
    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<OidRDTO> getByOIDHakukohde(@PathParam("oid") String oid, @QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count, @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince,
            @QueryParam("organisationOids") String organisationOidsStr,
            @QueryParam("hakukohdeTilas") String hakukohdeTilasStr);


    /**
     * /haku/OID/hakukohdeTulos
     *
     * @param oid
     * @param searchTerms
     * @param count default value 100 used if count == 0, if count < 0 value is Integer.MAX_VALUE
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @param organisationOidsStr
     * @param hakukohdeTilasStr
     * @return list of Haku's HakukohdeTulosRDTOs
     */
    @GET
    @Path("{oid}/hakukohdeTulos")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeTulosRDTO getByOIDHakukohdeTulos(@PathParam("oid") String oid,
            @QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince,
            @QueryParam("organisationOids") String organisationOidsStr,
            @QueryParam("hakukohdeTilas") String hakukohdeTilasStr,
            @QueryParam("alkamisVuosi") Integer alkamisVuosi,
            @QueryParam("alkamisKausi") String alkamisKausi
            );


    /**
     * Same as "getByOIDHakukohde" but resolves the koodisto name for
     * hakukohde...
     *
     * @param oid
     * @param searchTerms
     * @param count default value 100 used if count == 0, if count < 0 value is Integer.MAX_VALUE
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @param organisationOidsStr
     * @param hakukohdeTilasStr
     * @return
     */
    @GET
    @Path("{oid}/hakukohdeWithName")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<Map<String, String>> getByOIDHakukohdeExtra(@PathParam("oid") String oid,
            @QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince,
            @QueryParam("organisationOids") String organisationOidsStr,
            @QueryParam("hakukohdeTilas") String hakukohdeTilasStr);

    /**
     * Creates a haku.
     *
     * @return Oid of the created haku.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createHaku(HakuDTO dto);

    /**
     * Updates a haku.
     */
    @PUT
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void replaceHaku(HakuDTO dto);

    /**
     * Deletes a haku by oid.
     */
    @DELETE
    @Path("{oid}")
    public void deleteHaku(@PathParam("oid") String hakuOid);

    /**
     * Updates state of a haku.
     */
    @PUT
    @Path("{oid}/state")
    @Consumes(MediaType.TEXT_PLAIN)
    public void updateHakuState(@PathParam("oid") String hakuOid, String state);

    @GET
    @Path("{oid}/kela/export")
    public KelaHakukohteetDTO haeHakukohteetKela();

}
