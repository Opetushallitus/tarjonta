/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
@ApiModel(value = "Monikielisen tekstin syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class NimiV1RDTO extends KoodiV1RDTO {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "Avain-arvopari, jossa avain on koodisto kieli uri ja arvo on kuvausteksti", required = true)
    private Map<String, String> tekstis;

    public NimiV1RDTO() {
    }

    public NimiV1RDTO(Map<String, String> tekstit) {
        this.setTekstis(tekstit);
    }

    /**
     * @return the tekstis
     */
    public Map<String, String> getTekstis() {
        if (tekstis == null) {
            tekstis = new HashMap<String, String>();
        }
        return tekstis;
    }

    /**
     * @param tekstis the tekstis to set
     */
    public void setTekstis(Map<String, String> teksti) {
        this.tekstis = teksti;
    }

    @Override
    public int hashCode() {
        return getTekstis().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NimiV1RDTO) {
            return getTekstis().equals(((NimiV1RDTO) obj).getTekstis());
        }
        return false;
    }
}
