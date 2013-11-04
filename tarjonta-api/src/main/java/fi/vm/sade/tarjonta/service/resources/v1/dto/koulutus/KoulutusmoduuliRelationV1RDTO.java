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

import fi.vm.sade.tarjonta.service.resources.v1.dto.BaseV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiV1RDTO;

/**
 *
 * @author jani
 */
public class KoulutusmoduuliRelationV1RDTO extends BaseV1RDTO {

    //KOODISTO KOMO DATA OBJECTS:
    private UiV1RDTO koulutuskoodi;
    private UiV1RDTO koulutusaste;
    private UiV1RDTO koulutusala;
    private UiV1RDTO opintoala;
    private UiV1RDTO tutkinto;
    private UiV1RDTO tutkintonimike;
    private UiV1RDTO eqf;
    private UiV1RDTO opintojenLaajuus;

    /**
     * @return the koulutuskoodi
     */
    public UiV1RDTO getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(UiV1RDTO koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    /**
     * @return the koulutusaste
     */
    public UiV1RDTO getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(UiV1RDTO koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the koulutusala
     */
    public UiV1RDTO getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(UiV1RDTO koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the opintoala
     */
    public UiV1RDTO getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(UiV1RDTO opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the tutkinto
     */
    public UiV1RDTO getTutkinto() {
        return tutkinto;
    }

    /**
     * @param tutkinto the tutkinto to set
     */
    public void setTutkinto(UiV1RDTO tutkinto) {
        this.tutkinto = tutkinto;
    }

    /**
     * @return the tutkintonimike
     */
    public UiV1RDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(UiV1RDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the eqf
     */
    public UiV1RDTO getEqf() {
        return eqf;
    }

    /**
     * @param eqf the eqf to set
     */
    public void setEqf(UiV1RDTO eqf) {
        this.eqf = eqf;
    }

    /**
     * @return the opintojenLaajuus
     */
    public UiV1RDTO getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(UiV1RDTO opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }

}
