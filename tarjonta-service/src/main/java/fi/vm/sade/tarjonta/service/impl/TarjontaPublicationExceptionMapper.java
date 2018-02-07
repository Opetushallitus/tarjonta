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
package fi.vm.sade.tarjonta.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.fasterxml.jackson.databind.JsonMappingException.Reference;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.createValidationError;

/**
 * Mapping from service exceptions to JAX-RS response objects.
 *
 * @author Jukka Raanamo
 */
@Provider
public class TarjontaPublicationExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = LoggerFactory.getLogger(TarjontaPublicationExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {

        if (e instanceof JsonMappingException) {
            log.error("JsonMappingException", e);

            ResultV1RDTO result = ResultV1RDTO.create(ResultV1RDTO.ResultStatus.ERROR, null, null);

            for (Reference ref : ((JsonMappingException) e).getPath()) {
                result.addError(createValidationError(ref.getFieldName(), String.format("invalid format for field '%s'", ref.getFieldName())));
            }

            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }

        if (e instanceof AccessDeniedException) {
            log.info("AccessDeniedException: " + e.getMessage());

            return Response.status(Response.Status.FORBIDDEN).entity("").build();
        }

        log.error("Sending unmapped exception from API: ", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
    }
}

