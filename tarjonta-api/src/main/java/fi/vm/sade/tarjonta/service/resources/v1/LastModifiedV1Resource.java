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
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * List changes in the tarjonta data.
 *
 * For example:
 * <pre>
 * {
 *   "koulutusmoduuli" : ["oid1", "oid2", "oid3", ...],
 *   "koulutusmoduuliToteutus" : ["oid1", "oid2", "oid3", ...],
 *   "haku" : ["oid1", "oid2", "oid3", ...],
 *   "hakukohde" : ["oid1", "oid2", "oid3", ...]
 * }
 * </pre>
 *
 * @author mlyly
 */
@Path("/v1/lastmodified")
@Api(value = "/v1/lastmodified", description = "Muutosten listaaminen tarjonnan tiedoista.")
public interface LastModifiedV1Resource {

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa hakuehtojen puitteissa muutosten oid:t ryhmiteltynä.", notes = "Listaa muutosten oidit", response = Map.class)
    public Map<String, List<String>> lastModified(@QueryParam("lastModified") long lastModifiedTs);

}
