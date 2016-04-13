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

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mlyly
 */
@ApiModel(value = "Organisaation/tarjoajan syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class OrganisaatioV1RDTO extends BaseV1RDTO {

    @ApiModelProperty(value = "Organisaation yksilöivä tunniste", required = true)
    private String oid;
    private String nimi;
    private List<LokalisointiV1RDTO> _nimet;

    public OrganisaatioV1RDTO() {
    }

    public OrganisaatioV1RDTO(String oid) {
        this.oid = oid;
    }

    public OrganisaatioV1RDTO(String oid, String nimi, List<LokalisointiV1RDTO> _nimet) {
        this.oid = oid;
        this.nimi = nimi;
        this._nimet = _nimet;
    }

    public void addNimi(LokalisointiV1RDTO lokalisointi) {
        getNimet().add(lokalisointi);
    }

    public void addNimi(String kieli, String kieliUri, String arvo) {
        getNimet().add(new LokalisointiV1RDTO(kieli, kieliUri, arvo));
    }

    public List<LokalisointiV1RDTO> getNimet() {
        if (_nimet == null) {
            _nimet = new ArrayList<LokalisointiV1RDTO>();
        }
        return _nimet;
    }

    public void setNimet(List<LokalisointiV1RDTO> _nimet) {
        this._nimet = _nimet;
    }

    /**
     * @return the oid
     */
    public String getOid() {
        return oid;
    }

    /**
     * @param oid the oid to set
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return the nimi
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * @param nimi the nimi to set
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
}
