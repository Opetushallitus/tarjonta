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
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.dto.YhteyshenkiloRDTO;

/**
 * Koulutuksen yhteyshenkilöiden hallinta.
 */
@Path("/v1/yhteyshenkilo")
@Api(value = "/v1/yhteyshenkilo", description = "Koulutuksen yhteyshenkilöt")
public interface YhteyshenkiloV1Resource {

    /**
     * GET /yhteyshenkilo/{tarjoajaOid}/{searchTerm}
     *
     * @param tarjoajaOid
     * @param searchTerm
     * @return
     */
    @GET
    @Path("/{tarjoajaOid}/{searchTerm}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Hakee yhteyshenkilöt annetulle tarjoajalle.", notes = "Hakee yhteyshenkilöt annetulle tarjoajalle.")
    public List<YhteyshenkiloRDTO> getByOID(@PathParam("tarjoajaOid") String tarjoajaOid, @PathParam("searchTerm") String searchTerm);

}
