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
@ApiModel(value = "Tilastokeskuksen korkeakoulun koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliKorkeakouluRelationV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

    //KOODISTO KOMO DATA OBJECTS:
    @ApiModelProperty(value = "OPH tutkintonimike-koodit", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;
    @ApiModelProperty(value = "Opintojen laajuuden arvot", required = true)
    private KoodiUrisV1RDTO opintojenLaajuusarvos;

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

    /**
     * @return the opintojenLaajuusarvos
     */
    public KoodiUrisV1RDTO getOpintojenLaajuusarvos() {
        if (opintojenLaajuusarvos == null) {
            opintojenLaajuusarvos = new KoodiUrisV1RDTO();
            opintojenLaajuusarvos.setUris(new HashMap<String, Integer>());
        }
        return opintojenLaajuusarvos;
    }

    /**
     * @param opintojenLaajuusarvos the opintojenLaajuusarvos to set
     */
    public void setOpintojenLaajuusarvos(KoodiUrisV1RDTO opintojenLaajuusarvos) {
        this.opintojenLaajuusarvos = opintojenLaajuusarvos;
    }
}
