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

import fi.vm.sade.tarjonta.service.resources.v1.dto.GenericSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
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
 * Supported operations.
 *
 * <pre>
 * GET    /             ?count=100 & startIndex=0    -- list of oids
 * GET    oid                                        -- json of haku
 * GET    oid/hakukohde ?count=100 & startIndex=0    -- list of oids
 * GET    oid/state                                  -- state
 * PUT    oid/state                                  -- update state
 * POST   /                                          -- create haku
 * PUT    /oid                                       -- update hakue
 * DELETE oid                                        -- remove haku
 * </pre>
 *
 * @author mlyly
 */
@Path("/v1/haku")
@Api(value = "/v1/haku", description = "Haun REST-rajapinnan versio 1 operaatiot")
public interface HakuV1Resource {

    //HAUN HAKUEHTO
    public static class HakuSearchCriteria {
        
        public static class Builder{
            List<HakuSearchCriteria> criteria = new ArrayList<HakuV1Resource.HakuSearchCriteria>();

            public Builder mustMatch(HakuSearchCriteria.Field field, Object value){
                criteria.add(new HakuSearchCriteria(field, value, Match.MUST));
                return this;
            }

            public Builder lessThan(HakuSearchCriteria.Field field, Object value){
                criteria.add(new HakuSearchCriteria(field, value, Match.LESS_THAN));
                return this;
            }

            public Builder moreThan(HakuSearchCriteria.Field field, Object value){
                criteria.add(new HakuSearchCriteria(field, value, Match.MORE_THAN));
                return this;
            }

            public List<HakuSearchCriteria> build() {
                return criteria;
            }
        }

        public static enum Match {
            MUST,LESS_THAN,MORE_THAN,MUST_NOT;
        }
        public static enum Field {
            TILA, HAKUKAUSI, HAKUVUOSI, KOULUTUKSEN_ALKAMISKAUSI, KOULUTUKSEN_ALKAMISVUOSI, HAKUTAPA, HAKUTYYPPI, KOHDEJOUKKO;
        }

        HakuSearchCriteria(Field field, Object value,Match match) {
            this.match = match;
            this.field = field;
            this.value = value;
        }
        
        public Match getMatch() {
            return match;
        }
        public Field getField() {
            return field;
        }
        public Object getValue() {
            return value;
        }

        private final Match match;
        private final Field field;
        private final Object value;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa kaikki hakujen oid:t", notes = "Listaa kaikki hakujen oidit", response = OidV1RDTO.class)
    public ResultV1RDTO<List<OidV1RDTO>> search(@QueryParam("") GenericSearchParamsV1RDTO params, List<HakuSearchCriteria> hakuSearchCriteria);

    @GET
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa haun annetulla oid:lla", notes = "Palauttaa haun annetulla oid:lla", response = HakuV1RDTO.class)
    public ResultV1RDTO<HakuV1RDTO> findByOid(@PathParam("oid") String oid);

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Luo haun", notes = "Luo haun", response = HakuV1RDTO.class)
    public ResultV1RDTO<HakuV1RDTO> createHaku(HakuV1RDTO haku);

    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää haun", notes = "Päivittää haun", response = HakuV1RDTO.class)
    @Path("/{oid}")
    public ResultV1RDTO<HakuV1RDTO> updateHaku(HakuV1RDTO haku);

    @GET
    @Path("/findAll")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palauttaa kaikki haut", notes = "Palauttaa kaikki haut", response = HakuV1RDTO.class)
    public ResultV1RDTO<List<HakuV1RDTO>> findAllHakus();

    @DELETE
    @Path("/{oid}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Poistaa haun", notes = "Poistaa haun annettulla oid:lla", response = Boolean.class)
    public ResultV1RDTO<Boolean> deleteHaku(@PathParam("oid") String oid);

    @GET
    @Path("/{oid}/hakukohde")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palautaa haun hakukohteeet", notes = "Palauttaa annetun haun oid:n perusteella haun hakukohteet", response = OidV1RDTO.class)
    public ResultV1RDTO<List<OidV1RDTO>> getHakukohdesForHaku(@PathParam("oid") String oid, @QueryParam("") GenericSearchParamsV1RDTO params);


    @GET
    @Path("/{oid}/state")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Palautaa haun tilan", notes = "Palauttaa annetun haun oid:n perusteella haun tilan", response = String.class)
    public ResultV1RDTO<String> getHakuState(@PathParam("oid") String oid);

    @PUT
    @Path("/{oid}/state")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Päivittää haun tilan", notes = "Päivittää annetun haun oid:n perusteella haun tilan", response = String.class)
    public ResultV1RDTO<String> setHakuState(@PathParam("oid") String oid, String state);
}
