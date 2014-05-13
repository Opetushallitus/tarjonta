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
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.UserV1RDTO;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Permission checker - causes session to created and authorized.
 *
 * @author jani
 */
@Path("/v1/permission")
@Api(value = "/v1/permission", description = "Permissioiden tarkistaminen ja sessioiden luominen.")
public interface PermissionV1Resource {

    @GET
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<String> authorize();

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public ResultV1RDTO<UserV1RDTO> getUser();
    
    @POST
    @Path("/recordUiStacktrace")
    public void recordUiStacktrace(String stacktrace);

    /**
     * Palauta käyttöoikeudet mapissa.
     *
     * Esim:
     * <pre>
     * /permission/permissions/haku/1.2.3.4.5.6.1
     * /permission/permissions/hakukohde/1.2.3.4.5.6.2
     * /permission/permissions/koulutus/1.2.3.4.5.6.3
     * /permission/permissions/organisation/1.2.3.4.5.6.4
     * 
     * {
     *   // Create
     *   createHaku: true
     *   createHakukohde: true
     *   createKoulutus: true
     * 
     *   // Modify
     *   delete: true,
     *   modify: true,
     *   modifyLimited: false,
     *   copy : true,
     * 
     *   // Sate
     *   to_POISTETTU: true,
     *   to_LUONNOS: true,
     *   to_VALMIS : true,
     *   to_JULKAISTU: false,
     *   to_PERUTTU : true
     *   to_KOPIOITU : true
     * }
     * 
     * </pre>
     *  
     * 
     * @param type "haku", "hakukohde", "koulutus", ...?
     * @param key 
     */
    @GET
    @Path("/permissions/{type}/{key}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Permissioiden kysyminen kohteelta", notes = "Permissioiden kysyminen kohteelta.")
    public Map<String, Boolean> getPermissions(@PathParam("type") String type, @PathParam("key") String key);
    
}
