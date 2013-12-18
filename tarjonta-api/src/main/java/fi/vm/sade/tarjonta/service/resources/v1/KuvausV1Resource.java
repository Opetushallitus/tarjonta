package fi.vm.sade.tarjonta.service.resources.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import javax.ws.rs.*;
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
    @Path("/{tyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa listan kaikkien kuvausten tunnisteista.",
            notes = "Palauttaa listan kaikkien kuvausten tunnisteista annetulla tyypillä. Esim. SORA tai Valintaperustekuvaus")
    ResultV1RDTO<List<String>> findAllKuvauksesByTyyppi(
            @ApiParam(value = "kuvauksen tyyppi",required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi);

    @GET
    @Path("/{tyyppi}/nimet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa listan kaikkien kuvausten nimistä eri kielillä.",
            notes = "Palauttaa listan kaikkien kuvausten nimistä eri kielillä, esimerkiksi valintaperustekuvauksen valintaryhmien nimet eri kielillä")
    ResultV1RDTO<List<HashMap<String,String>>> getKuvausNimet(
            @ApiParam(value = "kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi);


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
    @Path("/{tyyppi}/{tunniste}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa kuvauksen annetulla tunnisteella", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> findById(
            @ApiParam(value = "kuvauksen tyyppi",required = true, allowableValues = "valintaperustekuvaus,SORA")
            @PathParam("tyyppi") String tyyppi,
            @ApiParam(value ="kuvauksen tunniste", required =  true)
            @PathParam("tunniste") String tunniste);

    @POST
    @Path("/{tyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo uuden annetulle tyyppille uuden kuvauksen", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> createNewKuvaus(@ApiParam(value="Kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA") @PathParam("tyyppi") String tyyppi,
            @ApiParam(value = "Luotava kuvaus", required = true) KuvausV1RDTO kuvausRDTO);


    @PUT
    @Path("/{tyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo uuden annetulle tyyppille uuden kuvauksen", response = KuvausV1RDTO.class)
    ResultV1RDTO<KuvausV1RDTO> updateKuvaus(@ApiParam(value="Kuvauksen tyyppi", required = true, allowableValues = "valintaperustekuvaus,SORA") @PathParam("tyyppi") String tyyppi,
                                               @ApiParam(value = "Luotava kuvaus", required = true) KuvausV1RDTO kuvausRDTO);
}
