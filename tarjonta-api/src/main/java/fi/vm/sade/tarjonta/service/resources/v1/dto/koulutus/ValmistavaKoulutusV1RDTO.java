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

import java.util.Map;

import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

/**
 *
 * @author alexGofore
 */
public class ValmistavaKoulutusV1RDTO extends KoulutusGenericV1RDTO {

    /**
     * Valmistavilla koulutuksilla ei (aina?) ole koodistosta tulevaa
     * opintojen laajuuden arvoa, vaan virkailijat voivat käsin syöttää
     * laajuuden arvon.
     */
    @ApiModelProperty(value = "Opintojen laajuuden arvo (ei koodistosta)", required = false)
    private String opintojenLaajuusarvoKannassa;

    /**
     * Osalla koulutuksista (esim. valmetava ja kuntouttava) koulutusohjelman
     * nimi tulee suoraan kannasta, eikä käytetä koodistosta tulevaa arvoa.
     */
    @ApiModelProperty(value = "Koulutusohjelman nimi kannassa", required = false)
    private Map<String, String> koulutusohjelmanNimiKannassa;

    public ValmistavaKoulutusV1RDTO(ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
        super(toteutustyyppi, moduulityyppi);
    }

    // Default constructor for JSON deserializing
    public ValmistavaKoulutusV1RDTO() {
        super();
    }

    /**
     * @param opintojenLaajuusarvo the opintojenLaajuusarvoKannassa to set
     */
    public void setOpintojenLaajuusarvoKannassa(String opintojenLaajuusarvo) {
        this.opintojenLaajuusarvoKannassa = opintojenLaajuusarvo;
    }

    /**
     * @return the opintojenLaajuusarvoKannassa
     */
    public String getOpintojenLaajuusarvoKannassa() {
        return opintojenLaajuusarvoKannassa;
    }

    public Map<String, String> getKoulutusohjelmanNimiKannassa() {
        return koulutusohjelmanNimiKannassa;
    }

    public void setKoulutusohjelmanNimiKannassa(Map<String, String> koulutusohjelmanNimiKannassa) {
        this.koulutusohjelmanNimiKannassa = koulutusohjelmanNimiKannassa;
    }
}
