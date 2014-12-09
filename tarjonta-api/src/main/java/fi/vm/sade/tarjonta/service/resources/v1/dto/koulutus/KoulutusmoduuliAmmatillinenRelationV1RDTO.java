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

import java.util.HashMap;

/**
 *
 * @author jani
 */
@ApiModel(value = "Tilastokeskuksen koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliAmmatillinenRelationV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

    //KOODISTO KOMO DATA OBJECTS:
    @ApiModelProperty(value = "OPH tutkintonimike-koodit", required = true)
    private KoodiV1RDTO tutkintonimike;

    @ApiModelProperty(value = "OPH tutkintonimike-koodit", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;

    @ApiModelProperty(value = "Opintojen laajuuden arvot", required = true)
    private KoodiV1RDTO opintojenLaajuusarvo;

    @ApiModelProperty(value = "Pohjakoulutusvaatimus-koodi", required = true)
    private KoodiV1RDTO pohjakoulutusvaatimus;

    @ApiModelProperty(value = "Koulutuslaji-koodi", required = true)
    private KoodiV1RDTO koulutuslaji;

    @ApiModelProperty(value = "Osaamisala-koodi", required = true)
    private KoodiV1RDTO osaamisala;

    @ApiModelProperty(value = "koulutusohjelma-koodi")
    private KoodiV1RDTO koulutusohjelma;

    /**
     * @return the tutkintonimike
     */
    public KoodiV1RDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(KoodiV1RDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
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

    /**
     * @return the pohjakoulutusvaatimus
     */
    public KoodiV1RDTO getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @param pohjakoulutusvaatimus the pohjakoulutusvaatimus to set
     */
    public void setPohjakoulutusvaatimus(KoodiV1RDTO pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    /**
     * @return the koulutuslaji
     */
    public KoodiV1RDTO getKoulutuslaji() {
        return koulutuslaji;
    }

    /**
     * @param koulutuslaji the koulutuslaji to set
     */
    public void setKoulutuslaji(KoodiV1RDTO koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }

    /**
     * @return the koulutusohjelma
     */
    public KoodiV1RDTO getKoulutusohjelma() {
        return koulutusohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setKoulutusohjelma(KoodiV1RDTO koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
    }

    /**
     * @return the osaamisala
     */
    public KoodiV1RDTO getOsaamisala() {
        return osaamisala;
    }

    /**
     * @param osaamisala the osaamisala to set
     */
    public void setOsaamisala(KoodiV1RDTO osaamisala) {
        this.osaamisala = osaamisala;
    }

    /**
     * @return the tutkintonimikes
     */
    public KoodiUrisV1RDTO getTutkintonimikes() {
        if (tutkintonimikes == null) {
            tutkintonimikes = new KoodiUrisV1RDTO();
            tutkintonimikes.setUris(new HashMap<String, Integer>());
        }

        return tutkintonimikes;
    }

    /**
     * @param tutkintonimikes the tutkintonimikes to set
     */
    public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
        this.tutkintonimikes = tutkintonimikes;
    }

}
