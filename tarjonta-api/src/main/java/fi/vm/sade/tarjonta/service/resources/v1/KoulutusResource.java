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

import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KoulutusHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmatillinenPeruskoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusLukioRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusPerusopetuksenLisaopetusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusValmentavaJaKuntouttavaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusKorkeakouluRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusmoduuliRelationRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
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

/**
 *
 * @author mlyly
 */
@Path("/v1/koulutus")
public interface KoulutusResource {

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusRDTO> findByOid(@PathParam("oid") String oid);

    @POST
    @Path("LUKIOKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusLukioRDTO> postLukiokoulutus(ResultRDTO<KoulutusLukioRDTO> koulutus);

    @POST
    @Path("AMMATILLINEN_PERUSKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusAmmatillinenPeruskoulutusRDTO> postAmmatillinenPeruskoulutus(ResultRDTO<KoulutusAmmatillinenPeruskoulutusRDTO> koulutus);

    @POST
    @Path("KORKEAKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusKorkeakouluRDTO> postKorkeakouluKoulutus(ResultRDTO<KoulutusKorkeakouluRDTO> koulutus);

    @POST
    @Path("PERUSOPETUKSEN_LISAOPETUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusPerusopetuksenLisaopetusRDTO> postPerusopetuksenLisaopetusKoulutus(ResultRDTO<KoulutusPerusopetuksenLisaopetusRDTO> koulutus);

    @POST
    @Path("VALMENTAVA_JA_KUNTOUTTAVA_OPETUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusValmentavaJaKuntouttavaRDTO> postValmentavaJaKuntouttavaKoulutus(ResultRDTO<KoulutusValmentavaJaKuntouttavaRDTO> koulutus);

    @DELETE
    @Path("{oid}")
    public Response deleteByOid(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public TekstiRDTO loadTekstis(@PathParam("oid") String oid);

    @GET
    @Path("/koulutuskoodi/{koulutuskoodi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusmoduuliRelationRDTO> getKoulutusRelation(@PathParam("koulutuskoodi") String koulutuskoodi);

    @GET
    @Path("{oid}/komoto/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<TekstiRDTO> loadKomotoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/komoto/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response saveKomotoTekstis(@PathParam("oid") String oid, TekstiRDTO<KomotoTeksti> dto);

    @GET
    @Path("{oid}/komo/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<TekstiRDTO> loadKomoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/komo/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response saveKomoTekstis(@PathParam("oid") String oid, TekstiRDTO<KomoTeksti> dto);

    @DELETE
    @Path("{oid}/teksti")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response deleteTeksti(@PathParam("oid") String oid, @PathParam("key") String key, @PathParam("uri") String uri);

    @POST
    @Path("{oid}/kuva")
    @Consumes({"image/jpeg", "image/png", "image/gif"})
    public Response saveKuva(@PathParam("oid") String oid, InputStream in, @HeaderParam("Content-Type") String fileType, @HeaderParam("Content-Length") long fileSize, @PathParam("uri") String kieliUri) throws IOException;

    @DELETE
    @Path("{oid}/kuva")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response deleteKuva(@PathParam("oid") String oid, @PathParam("uri") String kieliUri);

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
    @Produces(MediaType.TEXT_PLAIN)
    public String updateTila(@PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

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
    public HakutuloksetRDTO<KoulutusHakutulosRDTO> searchInfo(@QueryParam("searchTerms") String searchTerms,
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
    public List<NimiJaOidRDTO> getHakukohteet(@PathParam("oid") String oid);

}
