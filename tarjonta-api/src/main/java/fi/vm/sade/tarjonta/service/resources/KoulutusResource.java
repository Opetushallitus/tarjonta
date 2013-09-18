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

import fi.vm.sade.tarjonta.service.resources.dto.ToteutusDTO;
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
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void updateToteutus(ToteutusDTO dto);

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void createToteutus(ToteutusDTO dto);

    @DELETE
    @Path("{oid}/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void deleteToteutus(@PathParam("oid") String oid);

    @GET
    @Path("{oid}/tekstis")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String loadMonikielinenTekstis(@PathParam("oid") String oid, @QueryParam("lang") String langUri);

    @POST
    @PUT
    @Path("{oid}/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void saveMonikielinenTeksti(@PathParam("oid") String oid, @QueryParam("lang") String langUri);

    @DELETE
    @Path("{oid}/tekstis")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void deleteMonikielinenTeksti(@PathParam("oid") String oid);

    @POST
    @PUT
    @Path("{oid}/kuva")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void saveKuva(@PathParam("oid") String oid);

    @DELETE
    @Path("{oid}/kuva")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void deleteKuva(@PathParam("oid") String oid);
}
