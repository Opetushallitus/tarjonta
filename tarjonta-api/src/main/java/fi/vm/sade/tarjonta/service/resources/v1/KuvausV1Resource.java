package fi.vm.sade.tarjonta.service.resources.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausSearchV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

/*
* @author: Tuomas Katva 16/12/13
*/
@Path("/v1/kuvaus")
@Api(value = "/v1/kuvaus", description = "Palauttaa monikielisiä kuvauksia, esimerkiksi valintaperustekuvaus tai SORA-kuvaus.")
public interface KuvausV1Resource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa listan kaikkien kuvausten tunnisteista.",
            notes = "Palauttaa listan kaikkien kuvausten tunnisteista")
    ResultV1RDTO<List<String>> findAllKuvauksesByTyyppi();

    @GET
    @Path("/{tyyppi}/nimet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa listan kaikkien kuvausten nimistä eri kielillä.",
            notes = "Palauttaa listan kaikkien kuvausten nimistä eri kielillä, esimerkiksi valintaperustekuvauksen valintaryhmien nimet eri kielillä")
    ResultV1RDTO<List<HashMap<String,String>>> getKuvausNimet(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi);


    @GET
    @Path("/{tyyppi}/{organisaatioTyyppi}/kuvaustenTiedot")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation( value = "Palauttaa listan kuvausten tiedosta. Tiedot sisältävät kaiken muun paitsi itse kuvaukset", notes = "Palauttaa listan kuvausten tiedosta. Tiedot sisältävät kaiken muun paitsi itse kuvaukset")
    ResultV1RDTO<List<KuvausV1RDTO>> getKuvaustenTiedot(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi,
            @ApiParam(value = "organisaation tyyppi johon kuvaus on sidottu", required = true)
            @PathParam("organisaatioTyyppi")String orgType
    );

    @GET
    @Path("/{tyyppi}/{organisaatioTyyppi}/{vuosi}/kuvaustenTiedot")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation( value = "Palauttaa listan kuvausten tiedosta. Tiedot sisältävät kaiken muun paitsi itse kuvaukset", notes = "Palauttaa listan kuvausten tiedosta. Tiedot sisältävät kaiken muun paitsi itse kuvaukset")
    ResultV1RDTO<List<KuvausV1RDTO>> getKuvaustenTiedotVuodella(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi,
            @ApiParam(value = "kuvauksen vuosi", required = true)
            @PathParam("vuosi") int vuosi,
            @ApiParam(value = "organisaation tyyppi johon kuvaus on sidottu", required = true)
            @PathParam("organisaatioTyyppi")String orgType
    );


    @GET
    @Path("/{tyyppi}/{organisaatioTyyppi}/nimet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa listan kaikkien kuvausten nimistä eri kielillä.",
            notes = "Palauttaa listan kaikkien kuvausten nimistä eri kielillä, esimerkiksi valintaperustekuvauksen valintaryhmien nimet eri kielillä")
    ResultV1RDTO<List<HashMap<String,String>>> getKuvausNimetWithOrganizationType(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi,
            @ApiParam(value = "organisaation tyyppi johon kuvaus on sidottu", required = true)
            @PathParam("organisaatioTyyppi")String orgType);


    @GET
    @Path("/{tyyppi}/{organisaatioTyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa listan kaikista tietyn organisaatiotyypin kuvauksista.",
            notes = "Palauttaa listan kaikista tietyn organisaatiotyypin kuvauksista")
    ResultV1RDTO<List<KuvausV1RDTO>> getKuvauksesWithOrganizationType(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi,
            @ApiParam(value = "organisaation tyyppi johon kuvaus on sidottu", required = true)
            @PathParam("organisaatioTyyppi")String orgType
    );

    @GET
    @Path("/{tunniste}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa kuvauksen annetulla tunnisteella", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> findById(
            @ApiParam(value ="kuvauksen tunniste", required =  true)
            @PathParam("tunniste") String tunniste);


    @DELETE
    @Path("/{tunniste}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poistaa kuvauksen annetulla tunnisteella", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> removeById(
            @ApiParam(value ="kuvauksen tunniste", required =  true)
            @PathParam("tunniste") String tunniste, @Context HttpServletRequest request);

    @GET
    @Path("/{tyyppi}/{oppilaitostyyppi}/{nimi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakee tietyn tyyppiset kuvaukset oppilaitostyypillä ja nimellä ",response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> findByNimiAndOppilaitosTyyppi(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi,
            @ApiParam(value = "oppilaitos tyyppi", required = true)
            @PathParam("oppilaitostyyppi")String oppilaitosTyyppi,
            @ApiParam(value = "kuvauksen nimi", required = true)
            @PathParam("nimi")String nimi
    );

    @POST
    @Path("/{tyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo uuden annetulle tyyppille uuden kuvauksen", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> createNewKuvaus(@ApiParam(value = "Kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA") @PathParam("tyyppi") String tyyppi,
                                               @ApiParam(value = "Luotava kuvaus", required = true) KuvausV1RDTO kuvausRDTO, @Context HttpServletRequest request);

    @POST
    @Path("/{tyyppi}/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    ResultV1RDTO<List<KuvausV1RDTO>> searchKuvaukses(
            @ApiParam(value="Kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA") @PathParam("tyyppi") String tyyppi,
            @ApiParam(value="Haun parametrit" , required = true)
            KuvausSearchV1RDTO searchParam
    );


    @PUT
    @Path("/{tyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo uuden annetulle tyyppille uuden kuvauksen", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> updateKuvaus(@ApiParam(value="Kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA") @PathParam("tyyppi") String tyyppi,
                                               @ApiParam(value = "Luotava kuvaus", required = true) KuvausV1RDTO kuvausRDTO, @Context HttpServletRequest request);
}
