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
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TilaV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * REST palvelu tarjonnan staattiselle tilainformaatiolle. Client puolen tilasiirtymien hallintaan.
 *
 * <pre>
 * GET / tila
 *
 * //{
 * //  "result" : {
 * //    "LUONNOS" : {
 * //      "mutable" : true,
 * //      "cancellable" : false,
 * //      "removable" : true,
 * //      "transitions" : [ "LUONNOS", "VALMIS" ],
 * //      "public" : false
 * //    },
 * //    "VALMIS" : {
 * //      "mutable" : true,
 * //      "cancellable" : true,
 * //      "removable" : true,
 * //      "transitions" : [ "VALMIS", "JULKAISTU" ],
 * //      "public" : false
 * //    },
 * //    "JULKAISTU" : {
 * //      "mutable" : true,
 * //      "cancellable" : true,
 * //      "removable" : false,
 * //      "transitions" : [ "JULKAISTU", "PERUTTU" ],
 * //      "public" : true
 * //    },
 * //    "PERUTTU" : {
 * //      "mutable" : true,
 * //      "cancellable" : false,
 * //      "removable" : false,
 * //      "transitions" : [ "JULKAISTU", "PERUTTU" ],
 * //      "public" : true
 * //    },
 * //    "KOPIOITU" : {
 * //      "mutable" : true,
 * //      "cancellable" : false,
 * //      "removable" : true,
 * //      "transitions" : [ "VALMIS", "KOPIOITU" ],
 * //      "public" : false
 * //    }
 * //  },
 * //  "status" : "OK"
 * //}
 * </pre>
 *
 * @author Timo Santasalo / Teknokala Ky
 */
@Path("/v1/tila")
@Api(value = "/v1/tila", description = "Hakee tarjonnnan server-puolen tilat ja säännöt js-client puolelle.")
public interface TilaV1Resource {

    /**
     * Returns static information about tarjonta states for client side state management.
     *
     * @see TarjontaTila enum for server side client management.
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Tarjonnan tilat ja tilasiirtymäsäännöt.", notes = "Tarjonnan tilat ja tilasiirtymäsäännöt.")
    public ResultV1RDTO<Map<TarjontaTila, TilaV1RDTO>> getTilat();

}
