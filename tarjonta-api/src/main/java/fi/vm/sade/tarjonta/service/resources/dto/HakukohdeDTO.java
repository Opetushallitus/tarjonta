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
 *
 * @author mlyly
 */
public class HakukohdeDTO implements Serializable {

    private String _oid;
    private int _version;
    private double _alinHyvaksyttavaKeskiarvo;
    private int _alinValintaPistemaara;
    private int _aloituspaikatLkm;
    private int _edellisenVuodenHakijatLkm;
    private int _valintojenAloituspaikatLkm;
    private int _ylinValintapistemaara;
    private String _hakukelpoisuusvaatimusUri;
    private String _hakukohdeKoodistoNimi;
    private String _hakukohdeNimiUri;
    private String _sahkoinenToimitusOsoite;
    private String _soraKuvausKoodiUri;
    private String _tila;
    private String _valintaperustekuvausKoodiUri;
    private Date _updated;
    private String _updatedByOid;
    private Date _liitteidenToimitusPvm;
    private Map<String, String> _lisatiedot;

    public String getOid() {
        return _oid;
    }

    public void setOid(String _oid) {
        this._oid = _oid;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int _version) {
        this._version = _version;
    }

    public double getAlinHyvaksyttavaKeskiarvo() {
        return _alinHyvaksyttavaKeskiarvo;
    }

    public void setAlinHyvaksyttavaKeskiarvo(double alinHyvaksyttavaKeskiarvo) {
        this._alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
    }

    public int getAlinValintaPistemaara() {
        return _alinValintaPistemaara;
    }

    public void setAlinValintaPistemaara(int alinValintaPistemaara) {
        this._alinValintaPistemaara = alinValintaPistemaara;
    }

    public int getAloituspaikatLkm() {
        return _aloituspaikatLkm;
    }

    public void setAloituspaikatLkm(int aloituspaikatLkm) {
        this._aloituspaikatLkm = aloituspaikatLkm;
    }

    public int getEdellisenVuodenHakijatLkm() {
        return _edellisenVuodenHakijatLkm;
    }

    public void setEdellisenVuodenHakijatLkm(int edellisenVuodenHakijatLkm) {
        this._edellisenVuodenHakijatLkm = edellisenVuodenHakijatLkm;
    }

    public int getValintojenAloituspaikatLkm() {
        return _valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(int valintojenAloituspaikatLkm) {
        this._valintojenAloituspaikatLkm = valintojenAloituspaikatLkm;
    }

    public int getYlinValintapistemaara() {
        return _ylinValintapistemaara;
    }

    public void setYlinValintapistemaara(int ylinValintapistemaara) {
        this._ylinValintapistemaara = ylinValintapistemaara;
    }

    public String getHakukelpoisuusvaatimusUri() {
        return _hakukelpoisuusvaatimusUri;
    }

    public void setHakukelpoisuusvaatimusUri(String hakukelpoisuusvaatimusUri) {
        this._hakukelpoisuusvaatimusUri = hakukelpoisuusvaatimusUri;
    }

    public String getHakukohdeKoodistoNimi() {
        return _hakukohdeKoodistoNimi;
    }

    public void setHakukohdeKoodistoNimi(String hakukohdeKoodistoNimi) {
        this._hakukohdeKoodistoNimi = hakukohdeKoodistoNimi;
    }

    public String getHakukohdeNimiUri() {
        return _hakukohdeNimiUri;
    }

    public void setHakukohdeNimiUri(String hakukohdeNimi) {
        this._hakukohdeNimiUri = hakukohdeNimi;
    }

    public String getSahkoinenToimitusOsoite() {
        return _sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this._sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }

    public String getSoraKuvausKoodiUri() {
        return _soraKuvausKoodiUri;
    }

    public void setSoraKuvausKoodiUri(String soraKuvausKoodiUri) {
        this._soraKuvausKoodiUri = soraKuvausKoodiUri;
    }

    public String getTila() {
        return _tila;
    }

    public void setTila(String tila) {
        this._tila = tila;
    }

    public String getValintaperustekuvausKoodiUri() {
        return _valintaperustekuvausKoodiUri;
    }

    public void setValintaperustekuvausKoodiUri(String valintaperustekuvausKoodiUri) {
        this._valintaperustekuvausKoodiUri = valintaperustekuvausKoodiUri;
    }

    public Date getUpdated() {
        return _updated;
    }

    public void setUpdated(Date updated) {
        this._updated = updated;
    }

    public String getUpdatedByOid() {
        return _updatedByOid;
    }

    public void setUpdatedByOid(String updatedByOid) {
        this._updatedByOid = updatedByOid;
    }

    public Date getLiitteidenToimitusPvm() {
        return _liitteidenToimitusPvm;
    }

    public void setLiitteidenToimitusPvm(Date liitteidenToimitusPvm) {
        this._liitteidenToimitusPvm = liitteidenToimitusPvm;
    }

    public Map<String, String> getLisatiedot() {
        return _lisatiedot;
    }

    public void setLisatiedot(Map<String, String> lisatiedot) {
        this._lisatiedot = lisatiedot;
    }
}
