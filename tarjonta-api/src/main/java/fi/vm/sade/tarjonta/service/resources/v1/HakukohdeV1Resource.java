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

import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mlyly
 */
@Path("/v1/hakukohde")
public interface HakukohdeV1Resource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<List<OidV1RDTO>> search();

    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeV1RDTO> findByOid(@PathParam("oid") String oid);

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeV1RDTO> createHaku(HakukohdeV1RDTO hakukohde);

    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeV1RDTO> updateHaku(HakukohdeV1RDTO hakukohde);

    @DELETE
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<Boolean> deleteHaku(@PathParam("oid") String oid);


    @GET
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(@PathParam("oid") String hakukohdeOid);

    @POST
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<ValintakoeV1RDTO> insertValintakoe(@PathParam("oid") String hakukohdeOid,ValintakoeV1RDTO valintakoeV1RDTO);

    @PUT
    @Path("/{oid}/valintakoe")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(@PathParam("oid") String hakukohdeOid, ValintakoeV1RDTO valintakoeV1RDTO);

    @DELETE
    @Path("/{oid}/valintakoe/{valintakoeId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<Boolean> removeValintakoe(@PathParam("oid") String hakukohdeOid,@PathParam("valintakoeId") String valintakoeOid);

    @GET
    @Path("/{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(@PathParam("oid") String hakukohdeOid);

    @POST
    @Path("/{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> insertHakukohdeLiite(@PathParam("oid") String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO);

    @PUT
    @Path("/{oid}/liite")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(@PathParam("oid") String hakukohdeOid, HakukohdeLiiteV1RDTO liiteV1RDTO);


    @DELETE
    @Path("/{oid}/liite/{liiteId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<Boolean> deleteHakukohdeLiite(@PathParam("oid") String hakukohdeOid,@PathParam("liiteId") String liiteId);
}
