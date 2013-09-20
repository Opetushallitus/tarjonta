package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeNimiRDTO;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * REST service for hakukohde's.
 *
 * <pre>
 * /hakukohde?searchTerms..
 * /hakukohde/OID
 * /hakukohde/OID/haku
 * /hakukohde/OID/komoto
 *
 * /hakukohde/OID/paasykoe
 * /hakukohde/OID/valintakoe
 *
 *  REMOVED: /hakukohde/OID/liite
 * </pre>
 *
 * Internal documentation: http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
 *
 * @author mlyly
 */
@Path("/hakukohde")
public interface HakukohdeResource {

    /**
     * /hakukohde?searchTerms=xxx&count=10&startIndex=100&lastModifiedBefore=X&lastModifiedSince=XX
     *
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @param organisationOids filter result to be in or "under" given organisations
     * @param hakukohdeTilas  filter result to be only in states given
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<OidRDTO> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince,
            @QueryParam("organisationOid") List<String> organisationOids,
            @QueryParam("hakukohdeTila") List<String> hakukohdeTilas);

    /**
     * /hakukohde/{oid}
     *
     * @param oid
     * @return loaded Hakukohde
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeDTO getByOID(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/haku
     *
     * @param oid
     * @return loaded Haku
     */
    @GET
    @Path("{oid}/haku")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakuDTO getHakuByHakukohdeOID(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/komoto
     *
     * @param oid
     * @return loaded list of komoto oids
     */
    @GET
    @Path("{oid}/komoto")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<OidRDTO> getKomotosByHakukohdeOID(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/paasykoe
     *
     * @param oid
     * @return loaded list Paasykoe's
     */
    @GET
    @Path("{oid}/paasykoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<String> getPaasykoesByHakukohdeOID(@PathParam("oid") String oid);

    /**
     * /hakukohde/{oid}/valintakoe
     *
     * @param oid
     * @return loaded list Valintakoe's
     */
    @GET
    @Path("{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<String> getValintakoesByHakukohdeOID(@PathParam("oid") String oid);

    /**
     * /hakukohde/OID/nimi
     *
     * Resolves the Hakukohde name from the tarjoaja and hakukohde.
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/nimi")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeNimiRDTO getHakukohdeNimi(@PathParam("oid") String oid);

}
