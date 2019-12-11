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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * List changes in the tarjonta data.
 *
 * For exampe, see the modified stuff in "luokka" environment since week ago:
 *
 * https://itest-virkailija.oph.ware.fi/tarjonta-service/rest/v1/lastmodified?lastModified=-604800000
 *
 * For example:
 * <pre>
 * {
 *   "lastModifiedTs" : [ "1392206820523", "12.02.2014 14:07:00" ],
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

    /**
     * If lastModifiedTs &lt; 0 then the current time minux given ms is used. (ie. -600000 eguals 5 minutes back).
     * If lastModifiedTs equals 0 then default 5 minutes is used.
     * Otherwise value is used as is.
     *
     * @param lastModifiedTs
     * @param deleted
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa hakuehtojen puitteissa muutosten oid:t ryhmiteltynä.", notes = "Listaa muutosten oidit", response = Map.class)
    public Map<String, List<String>> lastModified(@QueryParam("lastModified") long lastModifiedTs,
                                                  @QueryParam("deleted") @DefaultValue("false") Boolean deleted);

}
