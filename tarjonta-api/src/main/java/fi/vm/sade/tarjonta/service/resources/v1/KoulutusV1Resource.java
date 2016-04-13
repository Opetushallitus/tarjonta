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

import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusCopyV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusMultiCopyV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliStandardRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutustyyppiKoosteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author mlyly
 */
@Path("/v1/koulutus")
@Api(value = "/v1/koulutus", description = "Koulutuksen versio 1 operaatiot")
public interface KoulutusV1Resource {

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää yhden koulutuksen annetulla koulutuksen oid:lla",
            notes = "Operaatio näyttää yhden koulutuksen annetulla koulutuksen oid:lla."
            + " Muut parametrit : "
            + "1. meta=false poistaa koodisto-palvelun metatietoa haettavaan koulutuksen dataan. "
            + "2. lang=FI näyttää yksittäisen metadatan annetun kielikoodin mukaan.")
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(@PathParam("oid") String oid, @QueryParam("meta") Boolean showMeta, @QueryParam("img") Boolean showImages, @QueryParam("lang") String userLang);

    @DELETE
    @Path("/{oid}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(
            value = "Poistaa (passivoi) koulutuksen annetulla koulutuksen oid:lla",
            notes = "Operaatio Poistaa (passivoi) koulutuksen annetulla koulutuksen oid:lla")
    public ResultV1RDTO deleteByOid(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}/siirra")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(
            value = "Kopioi tai siirtää koulutuksen annetulla koulutuksen oid:lla",
            notes = "Operaatio kopioi tai siirtää koulutuksen annetulla koulutuksen oid:lla")
    public ResultV1RDTO copyOrMove(@PathParam("oid") String oid, KoulutusCopyV1RDTO koulutusCopy);

    @POST
    @Path("/siirra")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(
            value = "Kopioi tai siirtää monta koulutusta",
            notes = "Operaatio kopioi tai siirtää monta koulutusta")
    public ResultV1RDTO copyOrMoveMultiple(KoulutusMultiCopyV1RDTO koulutusMultiCopy);

    @POST
    @PreAuthorize("isAuthenticated()")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Luo uuden koulutuksen",
            notes = "Operaatio luo uuden koulutuksen",
            response = KoulutusV1RDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation successful"),
            @ApiResponse(code = 400, message = "Invalid request payload"),
            @ApiResponse(code = 401, message = "Unauthorized request"),
            @ApiResponse(code = 403, message = "Permission denied")
    })
    public Response postKoulutus(KoulutusV1RDTO koulutus);

    @GET
    @Path("/{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää kaikki koulutuksen monikieliset kuvaustekstit",
            notes = "Operaatio näyttää kaikki koulutuksen monikieliset kuvaustekstit")
    public KuvausV1RDTO loadTekstis(@PathParam("oid") String oid);

    @GET
    @Path("/koodisto/{value}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää koodisto palvelun tarjontatiedon vaatimat relaatiot annettulla koodisto urilla tai koodin arvolla",
            notes = "Operaatio näyttää koodisto palvelun tarjontatiedon vaatimat relaatiot annettulla koodisto urilla tai koodin arvolla",
            response = KoulutusmoduuliStandardRelationV1RDTO.class)
    public ResultV1RDTO<KoulutusmoduuliStandardRelationV1RDTO> getKoodistoRelations(
            @PathParam("value") String value,
            @QueryParam("defaults") String defaults, //an example new String("field:uri, field:uri, ....")
            @QueryParam("meta") Boolean showMeta,
            @QueryParam("lang") String userLang);

    @GET
    @Path("/koodisto/{value}/{koulutustyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää koodisto palvelun tarjontatiedon vaatimat relaatiot annettulla koodisto urilla tai koodin arvolla",
            notes = "Operaatio näyttää koodisto palvelun tarjontatiedon vaatimat relaatiot annettulla koodisto urilla tai koodin arvolla",
            response = KoulutusmoduuliStandardRelationV1RDTO.class)
    public ResultV1RDTO<KoulutusmoduuliStandardRelationV1RDTO> getKoodistoRelations(
            @PathParam("value") String value,
            @PathParam("koulutustyyppi") ToteutustyyppiEnum koulutustyyppiUri,
            @QueryParam("defaults") String defaults, //an example new String("field:uri, field:uri, ....")
            @QueryParam("meta") Boolean showMeta,
            @QueryParam("lang") String userLang);

    @GET
    @Path("/{oid}/tekstis/komoto")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää  koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit",
            notes = "Operaatio näyttää kaikki koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit",
            response = KuvausV1RDTO.class)
    public ResultV1RDTO<KuvausV1RDTO> loadKomotoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @PreAuthorize("isAuthenticated()")
    @Path("/{oid}/tekstis/komoto")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Lisää koulutusmoduulin toteutukseen monikielisen kuvaustekstin",
            notes = "Operaatio lisää koulutusmoduulin toteutukseen monikielisen kuvaustekstin")
    public Response saveKomotoTekstis(@PathParam("oid") String oid, KuvausV1RDTO<KomotoTeksti> dto);

    @GET
    @Path("/{oid}/tekstis/komo")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää kaikki koulutuksen koulutusmoduulin monikieliset kuvaustekstit",
            notes = "Operaatio näyttää kaikki koulutuksen koulutusmoduulin monikieliset kuvaustekstit",
            response = KuvausV1RDTO.class)
    public ResultV1RDTO<KuvausV1RDTO> loadKomoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("/{oid}/tekstis/komo")
    @PreAuthorize("isAuthenticated()")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Lisää koulutusmoduuliin monikielisen kuvaustekstin",
            notes = "Operaatio lisää koulutusmoduuliin monikielisen kuvaustekstin")
    public ResultV1RDTO saveKomoTekstis(@PathParam("oid") String oid, KuvausV1RDTO<KomoTeksti> dto);

    @DELETE
    @Path("/{oid}/teksti/{key}/{kieliUri}")
    @PreAuthorize("isAuthenticated()")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Poistaa yhden monikieliset kuvaustekstit annetuilla avain- ja koodi uri-parametreilla",
            notes = "Operaatio poistaa yhden monikieliset kuvaustekstit annetuilla avain- ja koodi uri-parametreillä (avain on sovelluksen koodissa määritetty enum.)")
    public Response deleteTeksti(@PathParam("oid") String oid, @PathParam("key") String key, @PathParam("kieliUri") String kieliUri);

    @GET
    @Path("/{oid}/kuva/{kieliUri}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Hakee kuvatiedoton koulutusmoduulin totutuksesta annetulla koodi uri:lla",
            notes = "Operaatio hakee kuvatiedoton koulutusmoduulin totutuksesta annetulla koodi uri:lla",
            response = KuvaV1RDTO.class)
    public ResultV1RDTO<KuvaV1RDTO> getKuva(@PathParam("oid") String oid, @PathParam("kieliUri") String kieliUri);

    @GET
    @Path("/{oid}/kuva")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Hakee kaikki kuvatiedostot koulutusmoduulin toteutuksesta",
            notes = "Operaatio hakee kaikki kuvatiedostot koulutusmoduulin toteutuksesta",
            response = List.class)
    public ResultV1RDTO<List<KuvaV1RDTO>> getKuvas(@PathParam("oid") String oid);

    @POST
    @Path("/{oid}/kuva")
    @PreAuthorize("isAuthenticated()")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Lisää kuvatiedoton koulutusmoduulin toteutukseen",
            notes = "Operaatio lisää kuvatiedoton koulutusmoduulin toteutukseen (yhdellä koulutuksella kuvia voi olla vain yksi per koodi uri)")
    public ResultV1RDTO<KuvaV1RDTO> saveHtml5Kuva(@PathParam("oid") String oid, KuvaV1RDTO kuva);

    @POST
    @Path("/{oid}/kuva/{kieliUri}")
    @PreAuthorize("isAuthenticated()")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Lisää kuvatiedoton koulutusmoduulin toteutukseen",
            notes = "Operaatio lisää kuvatiedoton koulutusmoduulin toteutukseen (yhdellä koulutuksella kuvia voi olla vain yksi per koodi uri)")
    public Response saveHtml4Kuva(@PathParam("oid") String oid, @PathParam("kieliUri") String kieliUri, @Multipart("files") MultipartBody body);

    @DELETE
    @Path("/{oid}/kuva/{kieliUri}")
    @PreAuthorize("isAuthenticated()")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Poistaa kuvatiedoton koulutusmoduulin toteutuksesta annetulla koodi uri:lla",
            notes = "Operaatio poistaa kuvatiedoton koulutusmoduulin toteutuksesta annetulla koodi uri:lla")
    public ResultV1RDTO deleteKuva(@PathParam("oid") String oid, @PathParam("kieliUri") String kieliUri);

    /**
     * Päivittää koulutuksen tilan (olettaen että kyseinen tilasiirtymä on
     * sallittu).
     *
     * @param oid Koulutuksen oid.
     * @param tila Kohdetila.
     * @return Tila ( {@link TarjontaTila#toString()} ), jossa koulutus on tämän
     * kutsun jälkeen (eli kohdetila tai edellinen tila, jos siirtymä ei ollut
     * sallittu).
     */
    @POST
    @PUT
    @Path("/{oid}/tila")
    @PreAuthorize("isAuthenticated()")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Päivittää koulutuksen tilan",
            notes = "Operaatio päivittää koulutuksen tilan")
    public ResultV1RDTO<Tilamuutokset> updateTila(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää listausivun tulosjoukon annetuilla parametreillä",
            notes = "Operaatio näyttää listausivun tulosjoukon annetuilla parametreillä (alkamisPvmAlkaen ei vielä tuettu)",
            response = HakutuloksetV1RDTO.class)
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchInfo(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("organisationOid") List<String> organisationOids,
            @QueryParam("koulutusOid") List<String> koulutusOids,
            @QueryParam("tila") String koulutusTila,
            @QueryParam("alkamisKausi") String alkamisKausi,
            @QueryParam("alkamisVuosi") Integer alkamisVuosi,
            @QueryParam("koulutustyyppi") List<String> koulutustyyppi,
            @QueryParam("toteutustyyppi") List<ToteutustyyppiEnum> toteutustyyppi,
            @QueryParam("koulutusmoduuliTyyppi") List<KoulutusmoduuliTyyppi> koulutusmoduuliTyyppi,
            @Deprecated @QueryParam("koulutusastetyyppi") List<KoulutusasteTyyppi> koulutusastetyyppi,
            @QueryParam("komoOid") String komoOid,
            @QueryParam("alkamisPvmAlkaen") String alkamisPvmAlkaenTs,
            @QueryParam("koulutuslaji") String koulutuslaji,
            @QueryParam("defaultTarjoaja") String defaultTarjoaja,
            @QueryParam("hakutapa") String hakutapa,
            @QueryParam("hakutyyppi") String hakutyyppi,
            @QueryParam("kohdejoukko") String kohdejoukko,
            @QueryParam("oppilaitostyyppi") String oppilaitostyyppi,
            @QueryParam("kunta") String kunta,
            @QueryParam("opetuskielet") List<String> opetuskielet,
            @QueryParam("opetusJarjestajat") List<String> opetusJarjestajat,
            @QueryParam("hakukohderyhma") String hakukohderyhma,
            @QueryParam("hakukohdeOid") List<String> hakukohdeOids,
            @QueryParam("koulutuskoodi") List<String> koulutuskoodis,
            @QueryParam("opintoalakoodi") List<String> opintoalakoodis,
            @QueryParam("koulutusalakoodi") List<String> koulutusalakoodis,
            @QueryParam("hakuOid") List<String> hakuOids
    );

    /**
     * /koulutus/OID/hakukohteet
     *
     * @param oid
     * @return
     */
    @GET
    @Path("/{oid}/hakukohteet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää koulutukseen liittyvien hakukohteiden nimen ja oid:n annetulla koulutuksen oid:lla",
            notes = "Operaatio näyttää koulutukseen liittyvien hakukohteiden nimen ja oid:n annetulla koulutuksen oid:lla")
    public ResultV1RDTO<List<NimiJaOidRDTO>> getHakukohteet(@PathParam("oid") String oid);

    @GET
    @Path("/organisaatio/{oid}/koulutustyyppi")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää organisaation oid:lla organisaatioon liittyvät koulutustyypit.",
            notes = "Operaatio näyttää organisaation oid:lla organisaatioon liittyvät koulutustyypit.")
    ResultV1RDTO<KoulutustyyppiKoosteV1RDTO> isAllowedEducationByOrganisationOid(@PathParam("oid") String organisationOid);

    @GET
    @Path("/{oid}/jarjestettavatKoulutukset")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Palauttaa ne koulutukset, jotka on järjestetty annetusta koulutuksesta (oid)",
            notes = "Palauttaa ne koulutukset, jotka on järjestetty annetusta koulutuksesta (oid)"
    )
    ResultV1RDTO<List<KoulutusHakutulosV1RDTO>> getJarjestettavatKoulutukset(@PathParam("oid") String oid);

    @GET
    @Path("/oppiaineet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Palauttaa hakusanaa vastaavat oppiaineet",
            notes = "Palauttaa hakusanaa vastaavat oppiaineet"
    )
    ResultV1RDTO<Set<OppiaineV1RDTO>> getOppiaineet(
            @QueryParam("oppiaine") String oppiaine,
            @QueryParam("kieliKoodi") String kieliKoodi
    );

}
