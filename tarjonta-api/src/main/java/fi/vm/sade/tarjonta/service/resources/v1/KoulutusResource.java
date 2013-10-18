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

import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusAmmatillinenPeruskoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusLukioRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public ResultRDTO<KoulutusLukioRDTO> postAmmatillinenPeruskoulutus(ResultRDTO<KoulutusAmmatillinenPeruskoulutusRDTO> koulutus);

    @POST
    @Path("AMMATTIKORKEAKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusRDTO> postAmmattikorkeakoulutus(ResultRDTO<KoulutusRDTO> koulutus);

    @POST
    @Path("YLIOPISTOKOULUTUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusRDTO> postYliopistokoulutus(ResultRDTO<KoulutusRDTO> koulutus);

    @POST
    @Path("PERUSOPETUKSEN_LISAOPETUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusRDTO> postPerusopetuksenLisaopetusKoulutus(ResultRDTO<KoulutusRDTO> koulutus);

    @POST
    @Path("VALMENTAVA_JA_KUNTOUTTAVA_OPETUS")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<KoulutusRDTO> postValmentavaJaKuntouttavaKoulutus(ResultRDTO<KoulutusRDTO> koulutus);
}
