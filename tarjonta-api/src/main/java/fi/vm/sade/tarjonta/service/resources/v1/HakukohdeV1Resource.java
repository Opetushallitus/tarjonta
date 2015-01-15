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

import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.HashMap;
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

/**
 *
 * @author mlyly
 */
@Path("/v1/hakukohde")
@Api(value = "/v1/hakukohde", description = "Hakukohteen versio 1 operaatiot")
public interface HakukohdeV1Resource {

    /**
     * Päivittää hakukohteen tilan (olettaen että kyseinen tilasiirtymä on
     * sallittu).
     *
     * @param oid Hakukohteen oid.
     * @param tila Kohdetila.
     * @return Tila ( {@link TarjontaTila#toString()} ), jossa hakukohde on
     * tämän kutsun jälkeen (eli kohdetila tai edellinen tila, jos siirtymä ei
     * ollut sallittu).
     */
    @POST
    @Path("/{oid}/tila")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää hakukohteen tilan", notes = "Operaatio päivittää hakukohteen tilan, mikäli kyseinen tilasiirtymä on sallittu.")
    public ResultV1RDTO<Tilamuutokset> updateTila(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

    @GET
    @ApiOperation(value = "Palauttaa kaikki hakukohteiden oid:t", notes = "Listaa kaikki hakukohteiden oidit", response = OidV1RDTO.class)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<List<OidV1RDTO>> search();

    @GET
    @Path("/{oid}")
    @ApiOperation(value = "Palauttaa hakukohteen oid:lla", notes = "Operaatio palauttaa versio 1 mukaisen hakukohteen", response = HakukohdeV1RDTO.class)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeV1RDTO> findByOid(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String oid);

    @GET
    @Path("/{oid}/valintaperusteet")
    @ApiOperation(value = "Palauttaa hakukohteen valintaperusteet", notes = "Operaatio palauttaa hakukohteen valintaperusteet", response = HakukohdeValintaperusteetV1RDTO.class)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> findValintaperusteetByOid(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String oid);

    @GET
    @Path("/{tarjoajaOid}/{ulkoinenTunniste}")
    @ApiOperation(value = "Palauttaa hakukohteen ulkoisella tunnisteella")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeV1RDTO> findByUlkoinenTunniste(@ApiParam(value = "Tarjoajan oid", required = true) @PathParam("tarjoajaOid") String tarjoajaOid,
            @ApiParam(value = "Ulkoinen tunniste", required = true) @PathParam("ulkoinenTunniste") String ulkoinenTunniste);

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo uuden hakukohteen", notes = "Operaatio luo uuden hakukohteen", response = HakukohdeV1RDTO.class)
    public ResultV1RDTO<HakukohdeV1RDTO> createHakukohde(@ApiParam(value = "Luotava hakukohde", required = true) HakukohdeV1RDTO hakukohde);

    @PUT
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää hakukohteen", notes = "Operaatio päivittää hakukohteen", response = HakukohdeV1RDTO.class)
    public ResultV1RDTO<HakukohdeV1RDTO> updateHakukohde(
            @ApiParam(value = "Päivitettävän hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Päivitetty hakukohde", required = true) HakukohdeV1RDTO hakukohde);

    @DELETE
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poistaa hakukohteen", notes = "Operaatio poistaa hakukohteen")
    public ResultV1RDTO<Boolean> deleteHakukohde(@ApiParam(value = "Poistettavan hakukohteen oid", required = true) @PathParam("oid") String oid);

    @GET
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Listaa hakukohteen valintakokeet", notes = "Operaatio listaa hakukohteen valintakokeet, parametrina annetaan hakukohteen oid", response = ValintakoeV1RDTO.class)
    public ResultV1RDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(
            @ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

    @POST
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Lisää hakukohteelle valintakokeen", notes = "Lisätään hakukohteelle valintakoe, parametrina annetaan hakukohteen oid ja payloadina valintakoe", response = ValintakoeV1RDTO.class)
    public ResultV1RDTO<ValintakoeV1RDTO> insertValintakoe(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Hakukohteelle lisättävä valintakoe", required = true) ValintakoeV1RDTO valintakoeV1RDTO);

    @PUT
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää hakukohteelle valintakokeen", notes = "Päivittää hakukohteen valintakokeen, parametrina annetaan hakukohteen oid ja payloadina valintakoe", response = ValintakoeV1RDTO.class)
    public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Päivitettävä valintakoe", required = true) ValintakoeV1RDTO valintakoeV1RDTO);

    @DELETE
    @Path("/{oid}/valintakoe/{valintakoeId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poistaa hakukohteelta valintakokeen", notes = "Poistaa valintakokeen annetulta hakukohteelta.", response = ValintakoeV1RDTO.class)
    public ResultV1RDTO<Boolean> removeValintakoe(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Poistettavan valintakokeen oid", required = true) @PathParam("valintakoeId") String valintakoeOid);

    @GET
    @Path("/{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Listaa hakukohteen liitteet", notes = "Operaatio listaa hakukohteen liitteet, parametrinä annetaan hakukohteen oid", response = HakukohdeLiiteV1RDTO.class)
    public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(
            @ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

    @POST
    @Path("/{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Lisää hakukohteelle liitteen", notes = "Lisätään hakukohteelle liite, parametrina annetaan hakukohteen oid ja payloadina liite.", response = HakukohdeLiiteV1RDTO.class)
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> insertHakukohdeLiite(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Lisättävä hakukohteen liite", required = true) HakukohdeLiiteV1RDTO liiteV1RDTO);

    @PUT
    @Path("/{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää hakukohteen liitteen", notes = "Operaatio päivittää hakukohteen liitteen.")
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Päivitettävä hakukohteen liite", required = true) HakukohdeLiiteV1RDTO liiteV1RDTO);

    @DELETE
    @Path("/{oid}/liite/{liiteId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää hakukohteen liitteen", notes = "Päivitetään hakukohteelle liite, parametrina annetaan hakukohteen oid ja payloadina liite.", response = HakukohdeLiiteV1RDTO.class)
    public ResultV1RDTO<Boolean> deleteHakukohdeLiite(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Poistettava hakukohteen liite", required = true) @PathParam("liiteId") String liiteId);

    @GET
    @Path("/{oid}/valintaperustekuvaus")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakukohteen valintaperusteet", notes = "Listaa hakukohteen valintaperusteet, parametrinä annetaan hakukohteen oid.", response = HashMap.class)
    public ResultV1RDTO<HashMap<String, String>> findHakukohdeValintaperusteet(
            @ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

    @POST
    @Path("/{oid}/valintaperustekuvaus")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Lisää hakukohteelle valintaperusteet", notes = "Lisää hakukohteelle valintaperusteet, poistaa mahdolliset vanhat.", response = HashMap.class)
    public ResultV1RDTO<HashMap<String, String>> insertHakukohdeValintaPerusteet(
            @ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Lisättävät hakukohteen valintaperusteet", required = true) HashMap<String, String> valintaPerusteet);

    @GET
    @Path("/{oid}/sora")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakukohteen sora-kuvaukset", notes = "Listaa hakukohteen sora-kuvaukset, parametrinä annetaan hakukohteen oid.", response = HashMap.class)
    public ResultV1RDTO<HashMap<String, String>> findHakukohdeSoraKuvaukset(
            @ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

    @POST
    @Path("/{oid}/sora")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Lisää hakukohteelle sora-kuvaukset", notes = "Lisää hakukohteelle sora-kuvaukset, poistaa mahdolliset vanhat.", response = HashMap.class)
    public ResultV1RDTO<HashMap<String, String>> insertHakukohdeSora(
            @ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Hakukohteelle lisättävät sora-kuvaukset", required = true) HashMap<String, String> sorat);

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
    @ApiOperation(value = "Palauttaa listan hakukohteista annetuilla parametreilla", notes = "Palauttaa listan hakukohteista annetuilla parametreilla.", response = HakukohdeHakutulosV1RDTO.class)
    public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> search(
            @ApiParam(value = "Hakutermit", required = false) @QueryParam("searchTerms") String searchTerms,
            @ApiParam(value = "Lista organisaatioiden oid:tä", required = false) @QueryParam("organisationOid") List<String> organisationOids,
            @ApiParam(value = "Lista hakukohteen tiloja", required = false) @QueryParam("tila") List<String> hakukohdeTilas,
            @ApiParam(value = "Alkamiskausi", required = false) @QueryParam("alkamisKausi") String alkamisKausi,
            @ApiParam(value = "Alkamisvuosi", required = false) @QueryParam("alkamisVuosi") Integer alkamisVuosi,
            @ApiParam(value = "Hakukohteen oid", required = false) @QueryParam("hakukohdeOid") String hakukohdeOid,
            @ApiParam(value = "Lista koulutusasteen tyyppejä", required = false) @QueryParam("koulutusastetyyppi") List<KoulutusasteTyyppi> koulutusastetyyppi,
            @ApiParam(value = "Haun oid", required = false) @QueryParam("hakuOid") String hakuOid,
            @ApiParam(value = "Hakukohderyhmä", required = false) @QueryParam("organisaatioRyhmaOid") String organisaatioRyhmaOid,
            @ApiParam(value = "Lista toteutustyyppejä", required = false) @QueryParam("koulutustyyppi") List<ToteutustyyppiEnum> koulutustyypit,
            @ApiParam(value = "Lista koulutusmoduuli tyyppejä", required = false) @QueryParam("koulutusmoduuliTyyppi") List<KoulutusmoduuliTyyppi> koulutusmoduulityypit,
            @QueryParam("defaultTarjoaja") String defaultTarjoaja,
            @QueryParam("hakutapa") String hakutapa,
            @QueryParam("hakutyyppi") String hakutyyppi,
            @QueryParam("koulutuslaji") String koulutuslaji,
            @QueryParam("kohdejoukko") String kohdejoukko,
            @QueryParam("oppilaitostyyppi") String oppilaitostyyppi,
            @QueryParam("kunta") String kunta,
            @QueryParam("opetuskielet") List<String> opetuskielet);

    /**
     * Hakukohteen koulutuksten nimi ja oid, muut tiedot saa /search
     * rajapinnasta /hakukohde/OID/koulutukset
     *
     * @param oid
     * @return
     */
    @GET
    @Path("/{oid}/koulutukset")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa hakukohteen koulutukset", notes = "Operaatio palauttaa listan hakukohteen koulutuksia.")
    public ResultV1RDTO<List<NimiJaOidRDTO>> getKoulutukset(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String oid);

    @POST
    @Path("/{oid}/koulutukset")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poistaa annetun hakukohteen ja koulutusten välisen relaation", notes = "Poistaa annetun hakukohteen ja koulutusten välisen relaation, huom. mikäli hakukohteelle ei jää yhtään koulutusrelaatiota se poistetaan")
    public ResultV1RDTO<List<String>> removeKoulutuksesFromHakukohde(@ApiParam(value = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Lista hakukohteelta poistettavista koulutuksista") List<KoulutusTarjoajaV1RDTO> koulutukses);

    @POST
    @Path("/{oid}/koulutukset/lisaa")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Liittää annetut koulutukset hakukohteelle")
    public ResultV1RDTO<List<String>> lisaaKoulutuksesToHakukohde(@ApiParam(value = "Hakukohteen oid jolle koulutukset liitetään", required = true) @PathParam("oid") String hakukohdeOid,
            @ApiParam(value = "Koulutusten tiedot jotka liitetään hakukohteelle", required = true) List<KoulutusTarjoajaV1RDTO> koulutukses);

    @GET
    @Path("/{oid}/stateChangeCheck")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Tutkii onko esitetty tilamuutos mahdollinen", response = Boolean.class)
    public ResultV1RDTO<Boolean> isStateChangePossible(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

    @POST
    @Path("/ryhmat/operate")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Liittää/poistaa annetut ryhmat hakukohteelle, ei muuta olemassaolevia liitoksia")
    public ResultV1RDTO<Boolean> lisaaRyhmatHakukohteille(
            @ApiParam(value = "Lista hakukohteiden liittamis/poistamis toimintoja", required = true) List<HakukohdeRyhmaV1RDTO> data);

    @GET
    @Path("/komotoSelectedCheck")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Tutkii onko valituista koulutuksista mahdollista luoda uusi hakukohde", response = ResultV1RDTO.class)
    public ResultV1RDTO<ValitutKoulutuksetV1RDTO> isValidKoulutusSelection(@QueryParam("oid") List<String> oids);

    @POST
    @Path("/updateValintakokeetToNewStructure")
    public void updateValintakokeetToNewStructure();

    @POST
    @Path("/updateLiitteetToNewStructure")
    public void updateLiitteetToNewStructure();

    @GET
    @Path("/findHakukohdesByKuvausId/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa hakukohteet, jotka on liittetty annettuun valintaperuste/SORA -kuvaukseen", response = ResultV1RDTO.class)
    public ResultV1RDTO<List<HakukohdeV1RDTO>> findHakukohdesByKuvausId(@PathParam("id") Long id);
}
