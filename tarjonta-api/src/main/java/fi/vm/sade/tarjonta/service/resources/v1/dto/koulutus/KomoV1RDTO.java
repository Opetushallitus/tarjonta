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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Jani
 */
@ApiModel(value = "Ysittäisen koulutusmoduulin luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KomoV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "OPH oppilaitostyyppi-koodit (vain ammatillisella- ja lukio-koulutuksella) Huom! Tieto saattaa poistu tulevissa versioissa-", required = true)
    private KoodiUrisV1RDTO oppilaitostyyppis;

    public KomoV1RDTO() {
    }

    /**
     * @return the oppilaitostyyppis
     */
    public KoodiUrisV1RDTO getOppilaitostyyppis() {
        return oppilaitostyyppis;
    }

    /**
     * @param oppilaitostyyppis the oppilaitostyyppis to set
     */
    public void setOppilaitostyyppis(KoodiUrisV1RDTO oppilaitostyyppis) {
        this.oppilaitostyyppis = oppilaitostyyppis;
    }
}
