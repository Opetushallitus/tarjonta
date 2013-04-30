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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mlyly
 */
public class KomotoDTO extends KomoDTO {

    private Date _koulutuksenAlkamisDate;
    private boolean _maksullisuus;
    private String _komoOid;
    private String _pohjakoulutusVaatimusUri;

    private Map<String, String> _arviointiKriteerit;
    private Map<String, String> _kansainvalistyminen;
    private Map<String, String> _kuvailevatTiedot;
    private Map<String, String> _loppukoeVaatimukset;
    private Map<String, String> _maksullisuusKuvaus;
    private Map<String, String> _sijoittuminenTyoelamaan;
    private Map<String, String> _sisalto;
    private Map<String, String> _yhteistyoMuidenToimijoidenKanssa;
    private Map<String, String> _koulutusohjelmanValinta;
    private Map<String, String> _painotus;
    private Map<String, String> _webLinkkis;

    private List<String> _avainsanatUris;
    private List<String> _ammattinimikeUris;
    private List<String> _koulutuslajiUris;
    private List<String> _lukiodiplomitUris;
    private List<String> _opetuskieletUris;
    private List<String> _opetusmuodotUris;
    private List<String> _teematUris;

    public Date getKoulutuksenAlkamisDate() {
        return _koulutuksenAlkamisDate;
    }

    public void setKoulutuksenAlkamisDate(Date koulutuksenAlkamisDate) {
        this._koulutuksenAlkamisDate = koulutuksenAlkamisDate;
    }

    public boolean isMaksullisuus() {
        return _maksullisuus;
    }

    public void setMaksullisuus(boolean maksullisuus) {
        this._maksullisuus = maksullisuus;
    }

    public String getKomoOid() {
        return _komoOid;
    }

    public void setKomoOid(String _komoOid) {
        this._komoOid = _komoOid;
    }

    public void setPohjakoulutusVaatimusUri(String _pohjakoulutusVaatimusUri) {
        this._pohjakoulutusVaatimusUri = _pohjakoulutusVaatimusUri;
    }

    public String getPohjakoulutusVaatimusUri() {
        return _pohjakoulutusVaatimusUri;
    }

    // ------------------------------------------
    // Multilingual text data
    //

    public Map<String, String> getArviointiKriteerit() {
        return _arviointiKriteerit;
    }

    public void setArviointiKriteerit(Map<String, String> arviointiKriteerit) {
        this._arviointiKriteerit = arviointiKriteerit;
    }

    public Map<String, String> getKansainvalistyminen() {
        return _kansainvalistyminen;
    }

    public void setKansainvalistyminen(Map<String, String> kansainvalistyminen) {
        this._kansainvalistyminen = kansainvalistyminen;
    }

    public Map<String, String> getKuvailevatTiedot() {
        return _kuvailevatTiedot;
    }

    public void setKuvailevatTiedot(Map<String, String> kuvailevatTiedot) {
        this._kuvailevatTiedot = kuvailevatTiedot;
    }

    public Map<String, String> getLoppukoeVaatimukset() {
        return _loppukoeVaatimukset;
    }

    public void setLoppukoeVaatimukset(Map<String, String> loppukoeVaatimukset) {
        this._loppukoeVaatimukset = loppukoeVaatimukset;
    }

    public Map<String, String> getMaksullisuusKuvaus() {
        return _maksullisuusKuvaus;
    }

    public void setMaksullisuusKuvaus(Map<String, String> maksullisuusKuvaus) {
        this._maksullisuusKuvaus = maksullisuusKuvaus;
    }

    public Map<String, String> getSijoittuminenTyoelamaan() {
        return _sijoittuminenTyoelamaan;
    }

    public void setSijoittuminenTyoelamaan(Map<String, String> sijoittuminenTyoelamaan) {
        this._sijoittuminenTyoelamaan = sijoittuminenTyoelamaan;
    }

    public Map<String, String> getSisalto() {
        return _sisalto;
    }

    public void setSisalto(Map<String, String> sisalto) {
        this._sisalto = sisalto;
    }

    public Map<String, String> getYhteistyoMuidenToimijoidenKanssa() {
        return _yhteistyoMuidenToimijoidenKanssa;
    }

    public void setYhteistyoMuidenToimijoidenKanssa(Map<String, String> yhteistyoMuidenToimijoidenKanssa) {
        this._yhteistyoMuidenToimijoidenKanssa = yhteistyoMuidenToimijoidenKanssa;
    }

    public Map<String, String> getKoulutusohjelmanValinta() {
        return _koulutusohjelmanValinta;
    }

    public void setKoulutusohjelmanValinta(Map<String, String> koulutusohjelmanValinta) {
        this._koulutusohjelmanValinta = koulutusohjelmanValinta;
    }

    public Map<String, String> getPainotus() {
        return _painotus;
    }

    public void setPainotus(Map<String, String> painotus) {
        this._painotus = painotus;
    }

    public Map<String, String> getWebLinkkis() {
        return _webLinkkis;
    }

    public void setWebLinkkis(Map<String, String> webLinkkis) {
        this._webLinkkis = webLinkkis;
    }

    // -----------------------------------------
    // URI LISTS
    //

    public List<String> getAvainsanatUris() {
        return _avainsanatUris;
    }

    public void setAvainsanatUris(List<String> avainsanatUris) {
        this._avainsanatUris = avainsanatUris;
    }

    public List<String> getAmmattinimikeUris() {
        return _ammattinimikeUris;
    }

    public void setAmmattinimikeUris(List<String> ammattinimikeUris) {
        this._ammattinimikeUris = ammattinimikeUris;
    }

    public List<String> getKoulutuslajiUris() {
        return _koulutuslajiUris;
    }

    public void setKoulutuslajiUris(List<String> koulutuslajiUris) {
        this._koulutuslajiUris = koulutuslajiUris;
    }

    public List<String> getLukiodiplomitUris() {
        return _lukiodiplomitUris;
    }

    public void setLukiodiplomitUris(List<String> lukiodiplomitUris) {
        this._lukiodiplomitUris = lukiodiplomitUris;
    }

    public List<String> getOpetuskieletUris() {
        return _opetuskieletUris;
    }

    public void setOpetuskieletUris(List<String> opetuskieletUris) {
        this._opetuskieletUris = opetuskieletUris;
    }

    public List<String> getOpetusmuodotUris() {
        return _opetusmuodotUris;
    }

    public void setOpetusmuodotUris(List<String> opetusmuodotUris) {
        this._opetusmuodotUris = opetusmuodotUris;
    }

    public List<String> getTeematUris() {
        return _teematUris;
    }

    public void setTeematUris(List<String> teematUris) {
        this._teematUris = teematUris;
    }

}
