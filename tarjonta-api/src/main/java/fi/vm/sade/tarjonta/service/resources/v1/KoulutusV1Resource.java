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

import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.TekstiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPeruskoulutusV1RDTO;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusPerusopetuksenLisaopetusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusValmentavaJaKuntouttavaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusmoduuliRelationV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvaV1RDTO;

import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
public interface KoulutusV1Resource {

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusV1RDTO> findByOid(@PathParam("oid") String oid);

    @POST
    @Path("/LUKIOKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusLukioV1RDTO> postLukiokoulutus(KoulutusLukioV1RDTO koulutus);

    @POST
    @Path("/AMMATILLINEN_PERUSKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusAmmatillinenPeruskoulutusV1RDTO> postAmmatillinenPeruskoulutus(KoulutusAmmatillinenPeruskoulutusV1RDTO koulutus);

    @POST
    @Path("/KORKEAKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> postKorkeakouluKoulutus(KoulutusKorkeakouluV1RDTO koulutus);

    @POST
    @Path("/PERUSOPETUKSEN_LISAOPETUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusPerusopetuksenLisaopetusV1RDTO> postPerusopetuksenLisaopetusKoulutus(KoulutusPerusopetuksenLisaopetusV1RDTO koulutus);

    @POST
    @Path("/VALMENTAVA_JA_KUNTOUTTAVA_OPETUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusValmentavaJaKuntouttavaV1RDTO> postValmentavaJaKuntouttavaKoulutus(KoulutusValmentavaJaKuntouttavaV1RDTO koulutus);

    @DELETE
    @Path("{oid}")
    public Response deleteByOid(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public TekstiV1RDTO loadTekstis(@PathParam("oid") String oid);

    @GET
    @Path("/koulutuskoodi/{koulutuskoodi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KoulutusmoduuliRelationV1RDTO> getKoulutusRelation(@PathParam("koulutuskoodi") String koulutuskoodi);

    @GET
    @Path("{oid}/komoto/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<TekstiV1RDTO> loadKomotoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/komoto/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response saveKomotoTekstis(@PathParam("oid") String oid, TekstiV1RDTO<KomotoTeksti> dto);

    @GET
    @Path("{oid}/komo/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<TekstiV1RDTO> loadKomoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/komo/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response saveKomoTekstis(@PathParam("oid") String oid, TekstiV1RDTO<KomoTeksti> dto);

    @DELETE
    @Path("{oid}/teksti")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response deleteTeksti(@PathParam("oid") String oid, @PathParam("key") String key, @PathParam("uri") String uri);

    @GET
    @Path("{oid}/kuva/{kieliUri}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<KuvaV1RDTO> getKuva(String oid, String kieliUri);

    @POST
    @Path("{oid}/kuva/{kieliUri}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response saveKuva(@PathParam("oid") String oid, @PathParam("kieliUri") String kieliUri, @Multipart("file") MultipartBody body);

    @DELETE
    @Path("{oid}/kuva/{kieliUri}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
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
    @Path("{oid}/tila")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
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
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchInfo(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("organisationOid") List<String> organisationOids,
            @QueryParam("tila") String koulutusTila,
            @QueryParam("alkamisKausi") String alkamisKausi,
            @QueryParam("alkamisVuosi") Integer alkamisVuosi
    );

    /**
     * /koulutus/OID/hakukohteet
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/hakukohteet")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<List<NimiJaOidRDTO>> getHakukohteet(@PathParam("oid") String oid);

}
