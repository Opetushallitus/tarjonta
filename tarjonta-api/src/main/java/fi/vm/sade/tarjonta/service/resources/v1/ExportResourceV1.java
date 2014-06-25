/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mlyly
 */
@Path("/v1/export")
@Api(value = "/v1/export", description = "Tarjonnan export rajapinnan operaatiot")
public interface ExportResourceV1 {
    
    @GET
    @Path("/kela")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa hakuehtojen puitteissa hakujen oid:t", notes = "Listaa hakujen oidit", response = OidV1RDTO.class)
    public boolean exportKela();
}
