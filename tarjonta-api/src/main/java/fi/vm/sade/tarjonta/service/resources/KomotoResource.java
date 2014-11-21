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

import java.util.List;

import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST services for KOMOTO's.
 *
 * <pre>
 * /komoto?searchTerms=xxx&count=100&(startIndex=50|startPage=10)&language=fi
 * /komoto
 * /komoto/hello
 * /komoto/{OID}
 * /komoto/{OID}/komo
 * /komoto/{OID}/hakukohde
*
 * TODO
 * /komoto/{OID}/hakukohde
 * </pre>
 *
 * Internal documentation: http://liitu.hard.ware.fi/confluence/display/PROG/Tarjonnan+REST+palvelut
 *
 * @author mlyly
 */
@Path("/komotoDisabledAlaEnaaKayta")
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
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public KomotoDTO getByOID(@PathParam("oid") String oid);

    /**
     * Get KOMO for given KOMOTO.
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/komo")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public KomoDTO getKomoByKomotoOID(@PathParam("oid") String oid);

    /**
     * Get list of KOMOTO's.
     *
     * /komoto?searchTerms=xxx&count=x&startIndex=XX&language=XXX
     *
     * @param searchTerms
     * @param count count if count < 0 then count = Integer.MAX_VALUE, if count == 0, then count == 100, else not modified
     * @param startIndex
     * @param lastModifiedBefore
     * @param lastModifiedSince
     * @return list of KOMOTO OID's which you can then load by oid
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<OidRDTO> search(@QueryParam("searchTerms") String searchTerms,
            @QueryParam("count") int count,
            @QueryParam("startIndex") int startIndex,
            @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
            @QueryParam("lastModifiedSince") Date lastModifiedSince);


    /**
     * Get list of Hakukohde OIDs that given komoto belongs to.
     *
     * @param oid
     * @return
     */
    @GET
    @Path("{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<OidRDTO> getHakukohdesByKomotoOID(@PathParam("oid") String oid);

}
