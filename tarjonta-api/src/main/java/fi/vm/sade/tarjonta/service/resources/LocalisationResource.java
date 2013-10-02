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
package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.LocalisationRDTO;
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
 * <pre>
 * http://localhost:8084/tarjonta-service/rest/localisation?locale=sv&allLanguages=true
 * http://localhost:8084/tarjonta-service/rest/localisation/xxx (GET, PUT, POST, DELETE)
 * </pre>
 *
 * @author mlyly
 */
@Path("/localisation")
public interface LocalisationResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<LocalisationRDTO> getLocalisations();

    @GET
    @Path("{locale}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<LocalisationRDTO> getLocalisationsByLocale(@PathParam("locale") String locale);

    @GET
    @Path("{locale}/{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO getLocalisationByLocaleAndKey(@PathParam("locale") String locale, @PathParam("key") String key);

    @PUT
    @Path("{locale}/{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO updateLocalisationByLocaleAndKey(@PathParam("locale") String locale, @PathParam("key") String key, LocalisationRDTO data);

    @POST
    @Path("{locale}/{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO createLocalisationByLocaleAndKey(@PathParam("locale") String locale, @PathParam("key") String key, LocalisationRDTO data);

    @DELETE
    @Path("{locale}/{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO deleteLocalisationByLocaleAndKey(@PathParam("locale") String locale, @PathParam("key") String key);
}
