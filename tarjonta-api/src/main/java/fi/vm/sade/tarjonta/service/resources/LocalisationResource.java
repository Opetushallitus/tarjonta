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
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mlyly
 */
@Path("/localisation")
public interface LocalisationResource {

    /**
     * Retrieve localizations.
     *
     * @param requestedLocale locale "fi", "en", "sv".
     * @param requestedLocale include also map of all other language translations for same keys
     * @return List of translations
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Map<String, LocalisationRDTO> getLocalisations(@QueryParam("locale") String requestedLocale, @QueryParam("allLanguages") boolean includeAllLanguages);

    @GET
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO getLocalisation(@PathParam("key") String key);

    @PUT
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void updateLocalization(@PathParam("key") String key, LocalisationRDTO data);

    @POST
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO createLocalization(@PathParam("key") String key, LocalisationRDTO data);
}
