package fi.vm.sade.tarjonta.service.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;

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
 * * /hakukohde/OID/nimi
 * * /hakukohde/OID/valintaperusteet
 *
 * </pre>
 *
 * Internal documentation: http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
 *
 * @author mlyly
 */
@Path("/hakukohdeDisabledAlaEnaaKayta")
public interface HakukohdeResource {

    /**
     * /hakukohde?searchTerms=xxx&count=10&startIndex=100&lastModifiedBefore=X&lastModifiedSince=XX
     *
     * @param searchTerms
     * @param count default value 100, if value is < 0 then Integer.MAX_VALUE will be used.
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
     * NOTE: fixes also Lukio's single valintakoes to two separate valintakoes: Pääsykoe and Lisänäytöt...
     *
     * @param oid
     * @return loaded list Valintakoe's
     */
    @GET
    @Path("{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<ValintakoeRDTO> getValintakoesByHakukohdeOID(@PathParam("oid") String oid);

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

    /**
     * /hakukohde/OID/valintaperusteet
     *
     * Hakukohteen vaaditut avaimet lukion valintaperusteisiin
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/valintaperusteet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeValintaperusteetDTO getHakukohdeValintaperusteet(@PathParam("oid") String oid);

}
