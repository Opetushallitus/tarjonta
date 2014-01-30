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
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.List;
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
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(@PathParam("oid") String oid, @QueryParam("meta") Boolean meta, @QueryParam("lang") String lang);

    @DELETE
    @Path("/{oid}")
    @ApiOperation(
            value = "Poistaa koulutuksen annetulla koulutuksen oid:lla",
            notes = "Operaatio poistaa koulutuksen annetulla koulutuksen oid:lla")
    public Response deleteByOid(@PathParam("oid") String oid);

    @POST
    @Path("/KORKEAKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Luo uuden korkeakoulu koulutuksen",
            notes = "Operaatio luo uuden korkeakoulu koulutuksen",
            response = KoulutusKorkeakouluV1RDTO.class)
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> postKorkeakouluKoulutus(KoulutusKorkeakouluV1RDTO koulutus);

    @GET
    @Path("/{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää kaikki koulutuksen monikieliset kuvaustekstit",
            notes = "Operaatio näyttää kaikki koulutuksen monikieliset kuvaustekstit")
    public KuvausV1RDTO loadTekstis(@PathParam("oid") String oid);

    @GET
    @Path("/koulutuskoodi/{koulutuskoodi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää koodisto palvelun koulutuksen tarvitsemat koulutuskoodin relaatiot annetulla kuusinumeroisella tilastokeskuksen koulutuskoodilla tai koulutus-koodiston koodi uri:lla",
            notes = "Operaatio näyttää koodisto palvelun koulutuksen tarvitsemat koulutuskoodin relaatiot annetulla kuusinumeroisella tilastokeskuksen koulutuskoodilla tai koulutus-koodiston koodi uri:lla",
            response = KoulutusmoduuliRelationV1RDTO.class)
    public ResultV1RDTO<KoulutusmoduuliRelationV1RDTO> getKoulutusRelation(@PathParam("koulutuskoodi") String koulutuskoodi, @QueryParam("meta") Boolean meta, @QueryParam("lang") String lang);

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
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Lisää koulutusmoduuliin monikielisen kuvaustekstin",
            notes = "Operaatio lisää koulutusmoduuliin monikielisen kuvaustekstin")
    public Response saveKomoTekstis(@PathParam("oid") String oid, KuvausV1RDTO<KomoTeksti> dto);

    @DELETE
    @Path("/{oid}/teksti/{key}/{kieliUri}")
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

    @POST
    @Path("/{oid}/kuva/{kieliUri}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Lisää kuvatiedoton koulutusmoduulin toteutukseen",
            notes = "Operaatio lisää kuvatiedoton koulutusmoduulin toteutukseen (yhdellä koulutuksella kuvia voi olla vain yksi per koodi uri)")
    public Response saveKuva(@PathParam("oid") String oid, @PathParam("kieliUri") String kieliUri, @Multipart("file") MultipartBody body);

    @DELETE
    @Path("/{oid}/kuva/{kieliUri}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Poistaa kuvatiedoton koulutusmoduulin toteutuksesta annetulla koodi uri:lla",
            notes = "Operaatio poistaa kuvatiedoton koulutusmoduulin toteutuksesta annetulla koodi uri:lla")
    public Response deleteKuva(@PathParam("oid") String oid, @PathParam("kieliUri") String kieliUri);

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
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Päivittää koulutuksen tilan",
            notes = "Operaatio päivittää koulutuksen tilan")
    public ResultV1RDTO<String> updateTila(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

    /**
     * Hakukysely tarjonnan käyttöliittymää varten.
     *
     * @param searchTerms
     * @param organisationOids filter result to be in or "under" given
     * organisations
     * @param hakukohdeTilas filter result to be only in states given
     * @return
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää listausivun tulosjoukon annetuilla parametreillä",
            notes = "Operaatio näyttää listausivun tulosjoukon annetuilla parametreillä",
            response = HakutuloksetV1RDTO.class)
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchInfo(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("organisationOid") List<String> organisationOids,
            @QueryParam("koulutusOid") List<String> koulutusOids,
            @QueryParam("tila") String koulutusTila,
            @QueryParam("alkamisKausi") String alkamisKausi,
            @QueryParam("alkamisVuosi") Integer alkamisVuosi,
            @QueryParam("koulutusastetyyppi") List<KoulutusasteTyyppi> koulutusastetyyppi,
            @QueryParam("komoOid") String komoOid
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

}
