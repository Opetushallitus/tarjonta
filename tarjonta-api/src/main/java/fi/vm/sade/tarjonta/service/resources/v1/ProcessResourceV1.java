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
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import java.util.List;
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
@Path("/v1/process")
@Api(value = "/v1/process", description = "Pitkään kestävien prosessien käynnistäminen ja hallinta.")
public interface ProcessResourceV1 {

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Prosessin käynnistäminen", notes = "Prosessin käynnistäminen")
    ProcessV1RDTO start(ProcessV1RDTO processDefinition);

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Prosessien listaaminen", notes = "Prosessien listaaminen")
    List<ProcessV1RDTO> list();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Prosessin tilan lataaminen", notes = "Prosessin tilan lataaminen")
    ProcessV1RDTO get(@PathParam("id") String id);

    @GET
    @Path("/{id}/stop")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Prosessin keskeyttäminen", notes = "Prosessin keskeyttäminen")
    ProcessV1RDTO stop(@PathParam("id") String id);

}
