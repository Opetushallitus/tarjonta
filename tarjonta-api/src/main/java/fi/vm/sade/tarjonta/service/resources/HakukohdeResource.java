package fi.vm.sade.tarjonta.service.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

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
     * Hakukysely tarjonnan käyttöliittymää varten.
     *
     * @param searchTerms
     * @param organisationOids filter result to be in or "under" given organisations
     * @param hakukohdeTilas  filter result to be only in states given
     * @return
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakutuloksetRDTO<HakukohdeHakutulosRDTO> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("organisationOid") List<String> organisationOids,
            @QueryParam("tila") List<String> hakukohdeTilas,
            @QueryParam("alkamisKausi") String alkamisKausi,
            @QueryParam("alkamisVuosi") Integer alkamisVuosi
            );


    @POST
    @Path("/ui")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HakukohdeRDTO insertHakukohde(HakukohdeRDTO hakukohdeRDTO);

    @PUT
    @Path("/ui")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HakukohdeRDTO updateUiHakukohde(HakukohdeRDTO hakukohdeRDTO);

    @GET
    @Path("/ui/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeRDTO findByOid(@PathParam("oid") String oid);
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


    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateHakukohde(HakukohdeDTO hakukohdeDTO);

    @DELETE
    @Path("{oid}")
    public void deleteHakukohde(@PathParam("oid") String hakukohdeOid);
    
    /**
     * Päivittää hakukohteen tilan (olettaen että kyseinen tilasiirtymä on sallittu).
     * 
     * @param oid Hakukohteen oid.
     * @param tila Kohdetila.
     * @return Tila ( {@link TarjontaTila#toString()} ), jossa hakukohde on tämän kutsun jälkeen (eli kohdetila tai edellinen tila, jos siirtymä ei ollut sallittu).
     */
    @POST
    @Path("{oid}/tila")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateTila(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

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
     * /hakukohde/OID/koulutukset
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/koulutukset")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<NimiJaOidRDTO> getKoulutukset(@PathParam("oid") String oid);

}
