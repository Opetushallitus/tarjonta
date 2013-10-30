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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;

/**
 *
 * @author jani
 */
public class KoulutusmoduuliRelationRDTO extends BaseRDTO  {

    //KOODISTO KOMO DATA OBJECTS:
    private UiDTO koulutuskoodi;
    private UiDTO koulutusaste;
    private UiDTO koulutusala;
    private UiDTO opintoala;
    private UiDTO tutkinto;
    private UiDTO tutkintonimike;
    private UiDTO eqf;
    private UiDTO opintojenLaajuus;

    /**
     * @return the koulutuskoodi
     */
    public UiDTO getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(UiDTO koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    /**
     * @return the koulutusaste
     */
    public UiDTO getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(UiDTO koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the koulutusala
     */
    public UiDTO getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(UiDTO koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the opintoala
     */
    public UiDTO getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(UiDTO opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the tutkinto
     */
    public UiDTO getTutkinto() {
        return tutkinto;
    }

    /**
     * @param tutkinto the tutkinto to set
     */
    public void setTutkinto(UiDTO tutkinto) {
        this.tutkinto = tutkinto;
    }

    /**
     * @return the tutkintonimike
     */
    public UiDTO getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(UiDTO tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the eqf
     */
    public UiDTO getEqf() {
        return eqf;
    }

    /**
     * @param eqf the eqf to set
     */
    public void setEqf(UiDTO eqf) {
        this.eqf = eqf;
    }

    /**
     * @return the opintojenLaajuus
     */
    public UiDTO getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(UiDTO opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }
}
