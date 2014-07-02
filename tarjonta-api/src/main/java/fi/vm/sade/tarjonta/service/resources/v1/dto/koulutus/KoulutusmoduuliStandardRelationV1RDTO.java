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
import fi.vm.sade.tarjonta.service.resources.v1.dto.BaseV1RDTO;
import java.util.HashMap;

/**
 *
 * @author jani
 */
@ApiModel(value = "Tilastokeskuksen koulutuskoodiin liittyvät relaatiot")
public class KoulutusmoduuliStandardRelationV1RDTO extends BaseV1RDTO {

    //KOODISTO KOMO DATA OBJECTS:
    @ApiModelProperty(value = "Kuusinumeroinen tilastokeskuksen koulutuskoodi", required = true)
    private KoodiV1RDTO koulutuskoodi;
    @ApiModelProperty(value = "OPH2002 koulutusaste-koodi", required = true)
    private KoodiV1RDTO koulutusaste;
    @ApiModelProperty(value = "OPH2002 koulutusala-koodi", required = true)
    private KoodiV1RDTO koulutusala;
    @ApiModelProperty(value = "OPH2002 opintoala-koodi", required = true)
    private KoodiV1RDTO opintoala;
    @ApiModelProperty(value = "OPH tutkinto-koodi", required = true)
    private KoodiV1RDTO tutkinto;
    @ApiModelProperty(value = "EQF-koodi", required = true)
    private KoodiV1RDTO eqf;
    @ApiModelProperty(value = "NQF-koodi", required = true)
    private KoodiV1RDTO nqf;
    @ApiModelProperty(value = "Opintojen laajusyksikko-koodi", required = true)
    private KoodiV1RDTO opintojenLaajuusyksikko;
    @ApiModelProperty(value = "Koulutustyyppi-koodi", required = true)
    private KoodiV1RDTO koulutustyyppi;

    @ApiModelProperty(value = "Kaikki haettuun koodiin sisaltyvat koulutusohjelma-, osaamisala- tai lukiolinja-tyyppiset koodit.")
    private KoodiUrisV1RDTO ohjelmas;

    /**
     * @return the koulutuskoodi
     */
    public KoodiV1RDTO getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(KoodiV1RDTO koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    /**
     * @return the koulutusaste
     */
    public KoodiV1RDTO getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(KoodiV1RDTO koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * @return the koulutusala
     */
    public KoodiV1RDTO getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusala the koulutusala to set
     */
    public void setKoulutusala(KoodiV1RDTO koulutusala) {
        this.koulutusala = koulutusala;
    }

    /**
     * @return the opintoala
     */
    public KoodiV1RDTO getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(KoodiV1RDTO opintoala) {
        this.opintoala = opintoala;
    }

    /**
     * @return the tutkinto
     */
    public KoodiV1RDTO getTutkinto() {
        return tutkinto;
    }

    /**
     * @param tutkinto the tutkinto to set
     */
    public void setTutkinto(KoodiV1RDTO tutkinto) {
        this.tutkinto = tutkinto;
    }

    /**
     * @return the eqf
     */
    public KoodiV1RDTO getEqf() {
        return eqf;
    }

    /**
     * @param eqf the eqf to set
     */
    public void setEqf(KoodiV1RDTO eqf) {
        this.eqf = eqf;
    }

    /**
     * @return the opintojenLaajuusyksikko
     */
    public KoodiV1RDTO getOpintojenLaajuusyksikko() {
        return opintojenLaajuusyksikko;
    }

    /**
     * @param opintojenLaajuusyksikko the opintojenLaajuusyksikko to set
     */
    public void setOpintojenLaajuusyksikko(KoodiV1RDTO opintojenLaajuusyksikko) {
        this.opintojenLaajuusyksikko = opintojenLaajuusyksikko;
    }

    /**
     * @return the nqf
     */
    public KoodiV1RDTO getNqf() {
        return nqf;
    }

    /**
     * @param nqf the nqf to set
     */
    public void setNqf(KoodiV1RDTO nqf) {
        this.nqf = nqf;
    }

    /**
     * @return the koulutustyyppi
     */
    public KoodiV1RDTO getKoulutustyyppi() {
        return koulutustyyppi;
    }

    /**
     * @param koulutustyyppi the koulutustyyppi to set
     */
    public void setKoulutustyyppi(KoodiV1RDTO koulutustyyppi) {
        this.koulutustyyppi = koulutustyyppi;
    }

    /**
     * @return the ohjelmas
     */
    public KoodiUrisV1RDTO getOhjelmas() {
        if (ohjelmas == null) {
            ohjelmas = new KoodiUrisV1RDTO();
            ohjelmas.setUris(new HashMap<String, Integer>());
        }

        return ohjelmas;
    }

    /**
     * @param ohjelmas the ohjelmas to set
     */
    public void setOhjelmas(KoodiUrisV1RDTO ohjelmas) {
        this.ohjelmas = ohjelmas;
    }

}
