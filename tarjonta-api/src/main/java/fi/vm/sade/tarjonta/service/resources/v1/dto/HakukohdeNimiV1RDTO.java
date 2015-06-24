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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class HakukohdeNimiV1RDTO implements Serializable {

    private String hakukohdeOid;

    private String tarjoajaOid;
    private Map<String, String> tarjoajaNimi;

    private String hakukohdeNameUri;
    private Map<String, String> hakukohdeNimi;
    private String hakukohdeTila;

    private int hakuVuosi;
    private int koulutusVuosi;

    private Map<String, String> hakuKausi;
    private Map<String, String> koulutusKausi;

    private List<String> opetuskielet;

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public Map<String, String> getTarjoajaNimi() {
        return tarjoajaNimi;
    }

    public void setTarjoajaNimi(Map<String, String> tarjoajaNimi) {
        this.tarjoajaNimi = tarjoajaNimi;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public Map<String, String> getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    public void setHakukohdeNimi(Map<String, String> hakukohdeNimi) {
        this.hakukohdeNimi = hakukohdeNimi;
    }

    public String getHakukohdeNameUri() {
        return hakukohdeNameUri;
    }

    public void setHakukohdeNameUri(String hakukohdeNameUri) {
        this.hakukohdeNameUri = hakukohdeNameUri;
    }

    public String getHakukohdeTila() {
        return hakukohdeTila;
    }

    public void setHakukohdeTila(String hakukohdeTila) {
        this.hakukohdeTila = hakukohdeTila;
    }

    public int getHakuVuosi() {
        return hakuVuosi;
    }

    public void setHakuVuosi(int hakuVuosi) {
        this.hakuVuosi = hakuVuosi;
    }

    public Map<String, String> getHakuKausi() {
        return hakuKausi;
    }

    public void setHakuKausi(Map<String, String> hakuKausi) {
        this.hakuKausi = hakuKausi;
    }

    public void setKoulutusVuosi(int koulutusVuosi) {
        this.koulutusVuosi = koulutusVuosi;
    }

    public int getKoulutusVuosi() {
        return koulutusVuosi;
    }

    public Map<String, String> getKoulutusKausi() {
        return koulutusKausi;
    }

    public void setKoulutusKausi(Map<String, String> koulutusKausi) {
        this.koulutusKausi = koulutusKausi;
    }

    public List<String> getOpetuskielet() {
        return opetuskielet;
    }

    public void setOpetuskielet(List<String> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

}
