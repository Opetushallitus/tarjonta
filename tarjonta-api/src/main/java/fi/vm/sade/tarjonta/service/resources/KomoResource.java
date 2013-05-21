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

import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST services for KOMO's.
 *
 * <pre>
 * /komo/hello
 * /komo  (?searchTerms=xxx&count=100&startIndex=50)
 * /komo/{OID}
 * /komo/{OID}/komoto
 * /komo/{OID}/komoto  (?count=100&startIndex=50)
 * </pre>
 *
 * @author mlyly
 */
@Path("/komo")
public interface KomoResource {

    /**
     * Test that API responds.
     *
     * @return
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello();

    /**
     * /komo/{oid}
     *
     * @param oid
     * @return KoulutusmoduuliKoosteTyyppi
     */
    @GET
    @Path("{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public KomoDTO getByOID(@PathParam("oid") String oid);

    /**
     * Search KOMO's
     *
     * @param searchTerms
     * @param count
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return list of KOMO OID's that match
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<String> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);

    /**
     * GET Komoto's by Komo.
     *
     * @param oid
     * @param count
     * @param startIndex
     * @return list of KOMOTO OID's belonging to given KOMO
     */
    @GET
    @Path("{oid}/komoto")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<String> getKomotosByKomoOID(
            @PathParam("oid") String oid,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex);

}
