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
package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * REST DTO for Hakukohde liite.
 *
 * @author mlyly
 */
public class HakukohdeLiiteDTO extends BaseRDTO {

    private Date erapaiva;
    private Map<String, String> kuvaus;
    private String liitteenTyyppiUri;
    private String liitteenTyyppiKoodistonNimi;
    private String sahkoinenToimitusosoite;
    private OsoiteRDTO toimitusosoite;

    public Date getErapaiva() {
        return erapaiva;
    }

    public void setErapaiva(Date erapaiva) {
        this.erapaiva = erapaiva;
    }

    public Map<String, String> getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(Map<String, String> kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getLiitteenTyyppiKoodistonNimi() {
        return liitteenTyyppiKoodistonNimi;
    }

    public void setLiitteenTyyppiKoodistonNimi(String liitteenTyyppiKoodistonNimi) {
        this.liitteenTyyppiKoodistonNimi = liitteenTyyppiKoodistonNimi;
    }

    public String getLiitteenTyyppiUri() {
        return liitteenTyyppiUri;
    }

    public void setLiitteenTyyppiUri(String liitteenTyyppiUri) {
        this.liitteenTyyppiUri = liitteenTyyppiUri;
    }

    public String getSahkoinenToimitusosoite() {
        return sahkoinenToimitusosoite;
    }

    public void setSahkoinenToimitusosoite(String sahkoinenToimitusosoite) {
        this.sahkoinenToimitusosoite = sahkoinenToimitusosoite;
    }

    public OsoiteRDTO getToimitusosoite() {
        return toimitusosoite;
    }

    public void setToimitusosoite(OsoiteRDTO toimitusosoite) {
        this.toimitusosoite = toimitusosoite;
    }

}
