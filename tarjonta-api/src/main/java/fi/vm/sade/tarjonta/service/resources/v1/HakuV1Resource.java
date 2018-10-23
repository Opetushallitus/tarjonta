/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.tarjonta.service.resources.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Path("/v1/haku")
@Api(value = "/v1/haku", description = "Haun REST-rajapinnan versio 1 operaatiot")
public interface HakuV1Resource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa hakuehtojen puitteissa hakujen oid:t",
            notes = "Listaa hakujen oidit", response = OidV1RDTO.class)
    public ResultV1RDTO<List<String>> search(@QueryParam("") GenericSearchParamsV1RDTO params,
                                             @QueryParam("c") List<HakuSearchCriteria> hakuSearchCriteria,
                                             @Context UriInfo uriInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/multi")
    @ApiOperation(value = "Palauttaa haut annetuilla oideilla", notes = "palauttaa haut", response = OidV1RDTO.class)
    public ResultV1RDTO<List<HakuV1RDTO>> multiGet(@QueryParam("oid") List<String> oids);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa haun annetulla oid:lla", notes = "Palauttaa haun annetulla oid:lla", response = HakuV1RDTO.class)
    public ResultV1RDTO<HakuV1RDTO> findByOid(@PathParam("oid") String oid);

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo haun", notes = "Luo haun", response = HakuV1RDTO.class)
    public ResultV1RDTO<HakuV1RDTO> createHaku(HakuV1RDTO haku, @Context HttpServletRequest request);

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää haun", notes = "Päivittää haun", response = HakuV1RDTO.class)
    @Path("/{oid}")
    public ResultV1RDTO<HakuV1RDTO> updateHaku(HakuV1RDTO haku, @Context HttpServletRequest request);

    @GET
    @Path("/findAll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa kaikki haut", notes = "Palauttaa kaikki haut", response = HakuV1RDTO.class)
    public ResultV1RDTO<List<HakuV1RDTO>> findAllHakus();

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa kaikki haut hakuparametrien mukaisesti", notes = "Palauttaa kaikki haut hakuparametrien mukaisesti", response = HakuV1RDTO.class)
    public ResultV1RDTO<List<HakuV1RDTO>> find(@QueryParam("") HakuSearchParamsV1RDTO params, @Context UriInfo uriInfo);

    @GET
    @Path("/findByAlkamisvuosi/{alkamisVuosi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa haut annetulla koulutuksen alkamisvuodella", notes = "Palauttaa haut annetulla koulutuksen alkamisvuodella", response = HakuV1RDTO.class)
    public ResultV1RDTO<List<HakuV1RDTO>> findByAlkamisvuosi(@PathParam("alkamisVuosi") Integer alkamisVuosi);

    @DELETE
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poistaa haun", notes = "Poistaa haun annettulla oid:lla", response = Boolean.class)
    public ResultV1RDTO<Boolean> deleteHaku(@PathParam("oid") String oid, @Context HttpServletRequest request);

    @GET
    @Path("/{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palautaa haun hakukohteeet", notes = "Palauttaa annetun haun oid:n perusteella haun hakukohteet", response = OidV1RDTO.class)
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesForHaku(@PathParam("oid") String oid, @QueryParam("") GenericSearchParamsV1RDTO params);

    @GET
    @Path("/{oid}/state")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palautaa haun tilan", notes = "Palauttaa annetun haun oid:n perusteella haun tilan", response = String.class)
    public ResultV1RDTO<String> getHakuState(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/stateChangeCheck")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Tutkii onko esitetty tilamuutos mahdollinen", notes = "Palauttaa annetun haun oid:n perusteella haun tilan", response = Boolean.class)
    public ResultV1RDTO<Boolean> isStateChangePossible(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

    @PUT
    @Path("/{oid}/state")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää haun tilan ja julkaisee samalla siihen liitetyt hakukohteet ja koulutukset jos onlyHaku=false", notes = "Päivittää annetun haun oid:n perusteella haun tilan", response = Tilamuutokset.class)
    public ResultV1RDTO<Tilamuutokset> setHakuState(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila, @QueryParam("onlyHaku") @DefaultValue("false") boolean onlyHaku, @Context HttpServletRequest request);

    @PUT
    @Path("/{oid}/copy")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakuun liittyvän tiedon kopiointi seuraavalle kaudelle.", notes = "Kopioi massana hakuun liittyvät hakukohteet ja koulutukset seuraavalle kaudelle.", response = Tilamuutokset.class)
    public ResultV1RDTO<String> copyHaku(@PathParam("oid") String fromOid, @QueryParam("step") String step, @Context HttpServletRequest request);

    @GET
    @Path("{oid}/hakukohdeTulos")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HakukohdeTulosV1RDTO getHakukohdeTulos(@PathParam("oid") String oid,
                                                  @QueryParam("searchTerms") String searchTerms,
                                                  @QueryParam("count") int count,
                                                  @QueryParam("startIndex") int startIndex,
                                                  @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
                                                  @QueryParam("lastModifiedSince") Date lastModifiedSince,
                                                  @QueryParam("organisationOids") String organisationOidsStr,
                                                  @QueryParam("hakukohdeTilas") String hakukohdeTilasStr,
                                                  @QueryParam("alkamisVuosi") Integer alkamisVuosi,
                                                  @QueryParam("alkamisKausi") String alkamisKausi
    );

    @GET
    @Path("{oid}/hakukohteidenOrganisaatiot")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<Set<String>> getHakukohteidenOrganisaatioOids(@PathParam("oid") String oid);

    @GET
    @Path("/findOidsToSyncTarjontaFor")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakee oidit hauille, joille tulee suorittaa automaattinen tarjonnan synkronointi.")
    public ResultV1RDTO<Set<String>> findOidsToSyncTarjontaFor();

    @GET
    @Path("/ataru/all")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakee listauksen käytössäolevista Lomake-editorin lomakkeista ja niihin liittyvät haut.", notes = "Hakee listauksen käytössäolevista Lomake-editorin lomakkeista ja niihin liittyvät haut.", response = AtaruLomakkeetV1RDTO.class)
    public ResultV1RDTO<List<AtaruLomakkeetV1RDTO>> findAtaruFormUsage(
      @ApiParam(value = "Organisaation oid:t listauksen rajaamiseksi organisaatioiden lomakkeisiin. Jos parametri puuttuu, palautetaan kaikkien organisaatioiden tiedot.", required = false) @QueryParam("oid") final List<String> organisationOids
    );
}
