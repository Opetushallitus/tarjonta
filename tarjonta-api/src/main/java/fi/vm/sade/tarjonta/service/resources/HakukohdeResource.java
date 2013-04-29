package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import java.util.Date;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST service for hakukohde's.
 *
 * @author mlyly
 */
@Path("/hakukohde")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface HakukohdeResource {

    /**
     * /hakukohde?searchTerms=xxx&count=10&startIndex=100&lastModifiedBefore=X&lastModifiedSince=XX
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
    public List<String> getKomotosByHakukohdeOID(@PathParam("oid") String oid);


//   /**
//     * /hakukohde/{oid}/valintakoe
//     *
//     * @param oid
//     * @return loaded list of valintakoe objects
//     */
//    @GET
//    @Path("{oid}/valintakoe")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    public List<Valintakoe> getValintakoesByHakukohdeOID(@PathParam("oid") String oid);
//
//       /**
//     * /hakukohde/{oid}/valintakoe
//     *
//     * @param oid
//     * @return loaded list of valintakoe objects
//     */
//    @GET
//    @Path("{oid}/hakukohdeliite")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    public List<HakukohdeLiite> getHakukohdeLiitesByHakukohdeOID(@PathParam("oid") String oid);





//    /**
//     * /hakukohde/{oid}/koulutus
//     *
//     * @param oid
//     * @param language
//     * @return
//     */
//    @GET
//    @Path("{oid}/koulutus")
//    @Produces(MediaType.APPLICATION_JSON  + ";charset=UTF-8")
//    public List<HakukohdeTyyppi> getByOIDKoulutus(@PathParam("oid") String oid, @QueryParam("language") String language);
//
//    /**
//     * /hakukohde/{oid}/paasykoe
//     *
//     * @param oid
//     * @param language
//     * @return
//     */
//    @GET
//    @Path("{oid}/paasykoe")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    public List<HakukohdeTyyppi> getByOIDPaasykoe(@PathParam("oid") String oid, @QueryParam("language") String language);
//
//    /**
//     * /hakukohde/{oid}/liite
//     *
//     * @param oid
//     * @param language
//     * @return
//     */
//    @GET
//    @Path("{oid}/liite")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    public List<HakukohdeTyyppi> getByOIDLiite(@PathParam("oid") String oid, @QueryParam("language") String language);
}
