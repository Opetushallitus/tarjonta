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

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ModuuliTuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KoulutustyyppiUri;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jani
 */
@Path("/v1/komo")
@Api(value = "/v1/komo", description = "KOulutusmoduulin versio 1 operaatiot")
public interface KomoV1Resource {

    @DELETE
    @Path("/{oid}")
    @ApiOperation(
            value = "Poistaa koulutusmoduulin annetulla koulutusmoduulin oid:lla",
            notes = "Operaatio poistaa koulutusmoduulin annetulla koulutusmoduulin oid:lla")
    public ResultV1RDTO deleteByOid(@PathParam("oid") String oid);

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Luo uuden yksittäisen koulutusmoduulin",
            notes = "Operaatio luo uuden yksittäisen koulutusmoduulin",
            response = KomoV1RDTO.class)
    public ResultV1RDTO<KomoV1RDTO> postKomo(KomoV1RDTO dto);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää yhden koulutusmoduulin annetulla oid:lla",
            notes = "Operaatio näyttää yhden koulutusmoduulin annetulla oid:lla."
            + " Muut parametrit : "
            + "1. meta=false poistaa koodisto-palvelun metatietoa haettavaan koulutuksen dataan. "
            + "2. lang=FI näyttää yksittäisen metadatan annetun kielikoodin mukaan.", response = KomoV1RDTO.class)
    public ResultV1RDTO<KomoV1RDTO> findKomoByOid(
            @PathParam("oid") String oid,
            @QueryParam("meta") Boolean meta,
            @QueryParam("lang") String lang);

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää koulutusmoduulien tulosjoukon annetuilla parametreillä",
            notes = "Operaatio näyttää koulutusmoduulien tulosjoukon annetuilla parametreillä",
            response = ResultV1RDTO.class)
    public ResultV1RDTO<List<KomoV1RDTO>> searchInfo(
            @QueryParam("koulutus") String koulutuskoodi,
            @QueryParam("meta") Boolean meta,
            @QueryParam("lang") String lang);

    @GET
    @Path("/search/{koulutustyyppi}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
            notes = "Operaatio näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
            response = ResultV1RDTO.class)
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
            @PathParam("koulutustyyppi") KoulutustyyppiUri koulutustyyppiUri,
            @QueryParam("koulutus") String koulutuskoodiUri,
            @QueryParam("tila") String tila);

    @GET
    @Path("/search/{koulutustyyppi}/{moduuli}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
            notes = "Operaatio näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
            response = ResultV1RDTO.class)
    public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
            @PathParam("koulutustyyppi") KoulutustyyppiUri koulutustyyppiUri,
            @PathParam("moduuli") KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
            @QueryParam("koulutus") String koulutuskoodiUri,
            @QueryParam("tila") String tila);

    @GET
    @Path("/{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(
            value = "Näyttää koulutusmoduulin monikieliset tekstit",
            notes = "Operaatio näyttää koulutusmoduulin monikieliset tekstit",
            response = ResultV1RDTO.class)
    public ResultV1RDTO<KuvausV1RDTO> loadKomoTekstis(@PathParam("oid") String oid);
}
