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

import fi.vm.sade.tarjonta.service.resources.v1.dto.GenericSearchParamsRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultRDTO;
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
 * Supported operations.
 *
 * <pre>
 * GET    /             ?count=100 & startIndex=0    -- list of oids
 * GET    oid                                        -- json of haku
 * GET    oid/hakukohde ?count=100 & startIndex=0    -- list of oids
 * GET    oid/state                                  -- state
 * PUT    oid/state                                  -- update state
 * POST   /                                          -- create haku
 * PUT    /                                          -- update hakue
 * DELETE oid                                        -- remove haku
 * </pre>
 *
 * @author mlyly
 */
@Path("/v1/haku")
public interface HakuResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<List<OidRDTO>> search(@QueryParam("") GenericSearchParamsRDTO params);

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<HakuRDTO> findByOid(@PathParam("oid") String oid);

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<HakuRDTO> createHaku(HakuRDTO haku);

    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<HakuRDTO> updateHaku(HakuRDTO haku);

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<Boolean> deleteHaku(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<List<OidRDTO>> getHakukohdesForHaku(@PathParam("oid") String oid, @QueryParam("") GenericSearchParamsRDTO params);

    // TODO: @POST OID/hakukohde - add hakukohde?
    @GET
    @Path("{oid}/state")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<String> getHakuState(@PathParam("oid") String oid);

    @PUT
    @Path("{oid}/state")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultRDTO<String> setHakuState(@PathParam("oid") String oid, String state);
}
