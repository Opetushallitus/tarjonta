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

    @ApiModelProperty(value = "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;

    @ApiModelProperty(value = "Opintojen laajuuden arvo", required = true)
    private KoodiV1RDTO opintojenLaajuusarvo;

    public KomoV1RDTO() {
        super(null); //todo???
    }

    /**
     * @return the oppilaitostyyppis
     */
    public KoodiUrisV1RDTO getOppilaitostyyppis() {
        if (oppilaitostyyppis == null) {
            oppilaitostyyppis = new KoodiUrisV1RDTO();
        }

        return oppilaitostyyppis;
    }

    /**
     * @param oppilaitostyyppis the oppilaitostyyppis to set
     */
    public void setOppilaitostyyppis(KoodiUrisV1RDTO oppilaitostyyppis) {
        this.oppilaitostyyppis = oppilaitostyyppis;
    }

    /**
     * @return the tutkintonimikes
     */
    public KoodiUrisV1RDTO getTutkintonimikes() {
        if (tutkintonimikes == null) {
            tutkintonimikes = new KoodiUrisV1RDTO();
        }

        return tutkintonimikes;
    }

    /**
     * @param tutkintonimikes the tutkintonimikes to set
     */
    public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
        this.tutkintonimikes = tutkintonimikes;
    }

    /**
     * @return the opintojenLaajuusarvo
     */
    public KoodiV1RDTO getOpintojenLaajuusarvo() {
        return opintojenLaajuusarvo;
    }

    /**
     * @param opintojenLaajuusarvo the opintojenLaajuusarvo to set
     */
    public void setOpintojenLaajuusarvo(KoodiV1RDTO opintojenLaajuusarvo) {
        this.opintojenLaajuusarvo = opintojenLaajuusarvo;
    }
}
