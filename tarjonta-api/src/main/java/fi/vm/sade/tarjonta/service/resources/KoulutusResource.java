/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.resources;

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

import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TekstiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.OidResultDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.ToteutusDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;

/**
 * JSON resource for Tarjonta Application.
 *
 * @author Jani Wilén
 */
@Path("/koulutus")
public interface KoulutusResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String help();

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ToteutusDTO getToteutus(@PathParam("oid") String oid);

    @GET
    @Path("/koulutuskoodi/{koulutuskoodi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ToteutusDTO getKoulutusRelation(@PathParam("koulutuskoodi") String koulutuskoodi);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public OidResultDTO updateToteutus(KorkeakouluDTO dto);

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public OidResultDTO createToteutus(KorkeakouluDTO dto);

    @DELETE
    @Path("{oid}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response deleteToteutus(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public TekstiV1RDTO loadTekstis(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/komoto/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public TekstiV1RDTO loadKomotoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/komoto/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response saveKomotoTekstis(@PathParam("oid") String oid, TekstiV1RDTO<KomotoTeksti> dto);

    @GET
    @Path("{oid}/komo/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public TekstiV1RDTO loadKomoTekstis(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/komo/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response saveKomoTekstis(@PathParam("oid") String oid, TekstiV1RDTO<KomoTeksti> dto);

    @DELETE
    @Path("{oid}/teksti")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response deleteTeksti(@PathParam("oid") String oid, @PathParam("key") String key, @PathParam("uri") String uri);

    @POST
    @Path("{oid}/kuva")
    @Consumes({"image/jpeg", "image/png"})
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
