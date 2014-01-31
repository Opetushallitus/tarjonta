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

import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mlyly
 */
public class HakuV1RDTO extends BaseV1RDTO {

    private String hakukausiUri;

    private String hakukausiArvo;

    private String hakutapaUri;

    private int hakukausiVuosi;

    private String hakulomakeUri;

    private String hakutyyppiUri;

    private String kohdejoukkoUri;

    private int koulutuksenAlkamisVuosi;

    private String koulutuksenAlkamiskausiUri;

    private String tila;

    private boolean sijoittelu;

//    private List<TekstiRDTO> nimi;

    private List<HakuaikaV1RDTO> hakuaikas;

    private List<String> hakukohdeOids;

    private String haunTunniste;

    private String lastUpdatedByOid;

    private Date lastUpdatedDate;

    private Map<String, String> nimi = new HashMap<String, String>();

    private Map<String, KoodiV1RDTO> koodiMeta;


    public void addKoodiMeta(KoodiV1RDTO koodi) {
        if (koodi == null) {
            return;
        }

        if (getKoodiMeta() == null) {
            setKoodiMeta(new HashMap<String, KoodiV1RDTO>());
        }
        getKoodiMeta().put(koodi.getUri(), koodi);
    }

    public Map<String, KoodiV1RDTO> getKoodiMeta() {
        return koodiMeta;
    }

    public void setKoodiMeta(Map<String, KoodiV1RDTO> koodiMeta) {
        this.koodiMeta = koodiMeta;
    }

    public String getHakukausiUri() {
        return hakukausiUri;
    }

    public void setHakukausiUri(String hakukausiUri) {
        this.hakukausiUri = hakukausiUri;
    }

    public String getHakukausiArvo() {
        return hakukausiArvo;
    }

    public void setHakukausiArvo(String hakukausiArvo) {
        this.hakukausiArvo = hakukausiArvo;
    }

    public String getHakutapaUri() {
        return hakutapaUri;
    }

    public void setHakutapaUri(String hakutapaUri) {
        this.hakutapaUri = hakutapaUri;
    }

    public String getHakulomakeUri() {
        return hakulomakeUri;
    }

    public void setHakulomakeUri(String hakulomakeUri) {
        this.hakulomakeUri = hakulomakeUri;
    }

    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    public void setHakutyyppiUri(String hakutyyppiUri) {
        this.hakutyyppiUri = hakutyyppiUri;
    }

    public String getKohdejoukkoUri() {
        return kohdejoukkoUri;
    }

    public void setKohdejoukkoUri(String kohdejoukkoUri) {
        this.kohdejoukkoUri = kohdejoukkoUri;
    }

    public int getKoulutuksenAlkamisVuosi() {
        return koulutuksenAlkamisVuosi;
    }

    public void setKoulutuksenAlkamisVuosi(int koulutuksenAlkamisVuosi) {
        this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String koulutuksenAlkamiskausiUri) {
        this.koulutuksenAlkamiskausiUri = koulutuksenAlkamiskausiUri;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public boolean isSijoittelu() {
        return sijoittelu;
    }

    public void setSijoittelu(boolean sijoittelu) {
        this.sijoittelu = sijoittelu;
    }

//    public List<TekstiRDTO> getNimi() {
//        if (nimi == null) {
//            nimi = new ArrayList<TekstiRDTO>();
//        }
//        return nimi;
//    }
//
//    public void setNimi(List<TekstiRDTO> nimi) {
//        this.nimi = nimi;
//    }

    public List<HakuaikaV1RDTO> getHakuaikas() {
        if (hakuaikas == null) {
            hakuaikas = new ArrayList<HakuaikaV1RDTO>();
        }

        return hakuaikas;
    }

    public void setHakuaikas(List<HakuaikaV1RDTO> hakuaikas) {
        this.hakuaikas = hakuaikas;
    }

    public List<String> getHakukohdeOids() {
        if (hakukohdeOids == null) {
            hakukohdeOids = new ArrayList<String>();
        }
        return hakukohdeOids;
    }

    public void setHakukohdeOids(List<String> hakukohdeOids) {
        this.hakukohdeOids = hakukohdeOids;
    }

    public String getHaunTunniste() {
        return haunTunniste;
    }

    public void setHaunTunniste(String haunTunniste) {
        this.haunTunniste = haunTunniste;
    }

    public String getLastUpdatedByOid() {
        return lastUpdatedByOid;
    }

    public void setLastUpdatedByOid(String lastUpdatedByOid) {
        this.lastUpdatedByOid = lastUpdatedByOid;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public int getHakukausiVuosi() {
        return hakukausiVuosi;
    }

    public void setHakukausiVuosi(int hakukausiVuosi) {
        this.hakukausiVuosi = hakukausiVuosi;
    }

    public void setNimi(Map<String, String> nimi) {
        this.nimi = nimi;
    }

    public Map<String, String> getNimi() {
        return nimi;
    }

}
