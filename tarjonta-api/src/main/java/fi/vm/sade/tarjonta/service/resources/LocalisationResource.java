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
import java.util.Map;
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
 * <pre>
 * http://localhost:8084/tarjonta-service/rest/localisation?locale=sv&allLanguages=true
 * http://localhost:8084/tarjonta-service/rest/localisation/xxx (GET, PUT, POST, DELETE)
 * </pre>
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

    /**
     * Retrieves localisations by key.
     *
     * @param key
     * @return
     */
    @GET
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Map<String, LocalisationRDTO> getLocalisation(@PathParam("key") String key);

    /**
     * Update localisation.
     *
     * @param key
     * @param data
     */
    @PUT
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void updateLocalization(@PathParam("key") String key, LocalisationRDTO data);

    /**
     * Create new localisation.
     *
     * @param key
     * @param data
     * @return
     */
    @POST
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public LocalisationRDTO createLocalization(@PathParam("key") String key, LocalisationRDTO data);

    /**
     * Remove localisation by key.
     *
     * @param key
     */
    @DELETE
    @Path("{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public void deleteLocalization(@PathParam("key") String key);
}
