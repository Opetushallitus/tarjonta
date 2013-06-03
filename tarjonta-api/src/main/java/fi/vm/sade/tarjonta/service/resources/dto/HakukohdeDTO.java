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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * REST DTO for Hakukohde. (Application Option)
 *
 * @author mlyly
 */
public class HakukohdeDTO extends BaseRDTO {

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
    private Date _liitteidenToimitusPvm;
    private Map<String, String> _lisatiedot;
    private Map<String, String> _sorakuvaus;
    private Map<String, String> _valintaperustekuvaus;
    private Map<String, String> _hakukelpoisuusvaatimus;
    private List<List<String>> _painotettavatOppiaineet;
    private boolean _kaytetaanHaunPaattymisenAikaa;
    private List<HakukohdeLiiteDTO> _liitteet;

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

    public void setPainotettavatOppiaineet(List<List<String>> _painotettavatOppiaineet) {
        this._painotettavatOppiaineet = _painotettavatOppiaineet;
    }

    public List<List<String>> getPainotettavatOppiaineet() {
        return _painotettavatOppiaineet;
    }

    public Map<String, String> getSorakuvaus() {
        return _sorakuvaus;
    }

    public void setSorakuvaus(Map<String, String> _sorakuvaus) {
        this._sorakuvaus = _sorakuvaus;
    }

    public Map<String, String> getValintaperustekuvaus() {
        return _valintaperustekuvaus;
    }

    public void setValintaperustekuvaus(Map<String, String> _valintaperustekuvaus) {
        this._valintaperustekuvaus = _valintaperustekuvaus;
    }

    public boolean isKaytetaanHaunPaattymisenAikaa() {
        return _kaytetaanHaunPaattymisenAikaa;
    }

    public void setKaytetaanHaunPaattymisenAikaa(boolean _kaytetaanHaunPaattymisenAikaa) {
        this._kaytetaanHaunPaattymisenAikaa = _kaytetaanHaunPaattymisenAikaa;
    }

    public List<HakukohdeLiiteDTO> getLiitteet() {
        return _liitteet;
    }

    public void setLiitteet(List<HakukohdeLiiteDTO> _liitteet) {
        this._liitteet = _liitteet;
    }

    public Map<String, String> getHakukelpoisuusvaatimus() {
        return _hakukelpoisuusvaatimus;
    }

    public void setHakukelpoisuusvaatimus(Map<String, String> _hakukelpoisuusvaatimus) {
        this._hakukelpoisuusvaatimus = _hakukelpoisuusvaatimus;
    }

}
