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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import java.util.List;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST services for KOMOTO's.
 *
 * @author mlyly
 */
@Path("/komoto")
@CrossOriginResourceSharing(allowAllOrigins = true)
public interface KomotoResource {

    /**
     * /komoto/hello
     *
     * @return
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    /**
     * /komoto/{oid}
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    public KoulutusmoduuliKoosteTyyppi getByOID(@PathParam("oid") String oid);

    /**
     * /komoto?searchTerms=xxx
     *
     * @param searchTerms may be null
     * @param count
     * @param startIndex
     * @param startPage
     * @param language
     * @return list of KoulutusmoduuliKoosteTyyppi's
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<KoulutusmoduuliKoosteTyyppi> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex,
            @QueryParam("startPage") int startPage,
            @QueryParam("language") String language);
}
