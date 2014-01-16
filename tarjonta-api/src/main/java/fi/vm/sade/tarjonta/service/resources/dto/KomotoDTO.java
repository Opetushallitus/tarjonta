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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * REST DTO for LOI / Komoto / Koulutusmoduulin toteutus
 *
 * @author mlyly
 */
public class KomotoDTO extends BaseRDTO {

	private static final long serialVersionUID = 1L;

	private Date _koulutuksenAlkamisDate;
    private boolean _maksullisuus;
    private String _komoOid;
    private String _pohjakoulutusVaatimusUri;
    private Map<String, String> _webLinkkis;
    private List<String> _avainsanatUris;
    private List<String> _ammattinimikeUris;
    private List<String> _koulutuslajiUris;
    private List<String> _lukiodiplomitUris;
    private List<String> _opetuskieletUris;
    private List<String> _opetusmuodotUris;
    private List<String> _teematUris;
    private String _laajuusArvo;
    private String _laajuusYksikkoUri;
    private String _tarjoajaOid; // onko sama kuin organisaatioOid?
    private TarjontaTila _tila;
    private String _ulkoinenTunniste;
    private String _parentKomotoOid;
    private String koulutusohjelmanNimi;  // vapaavalintainen nimi, er OVT-6619

    public String getKoulutusohjelmanNimi() {
        return koulutusohjelmanNimi;
    }

    public void setKoulutusohjelmanNimi(String koulutusohjelmanNimi) {
        this.koulutusohjelmanNimi = koulutusohjelmanNimi;
    }

    private Map<KomotoTeksti, Map<String,String>> _tekstit;

    private List<YhteyshenkiloRDTO> _yhteyshenkilos;

    // Lukio
    private Map<String, List<String>> _tarjotutKielet;

    public Map<KomotoTeksti, Map<String, String>> getTekstit() {
    	if (_tekstit==null) {
    		_tekstit = new EnumMap<KomotoTeksti, Map<String,String>>(KomotoTeksti.class);
    	}
		return _tekstit;
	}

    public void setTekstit(Map<KomotoTeksti, Map<String, String>> _tekstit) {
		this._tekstit = _tekstit;
	}

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
    @Deprecated
    public Map<String, String> getArviointiKriteerit() {
    	return getTekstit().get(KomotoTeksti.ARVIOINTIKRITEERIT);
    }

    @Deprecated
    public void setArviointiKriteerit(Map<String, String> arviointiKriteerit) {
    	getTekstit().put(KomotoTeksti.ARVIOINTIKRITEERIT, arviointiKriteerit);
    }

    @Deprecated
    public Map<String, String> getKansainvalistyminen() {
    	return getTekstit().get(KomotoTeksti.KANSAINVALISTYMINEN);
    }

    @Deprecated
    public void setKansainvalistyminen(Map<String, String> kansainvalistyminen) {
    	getTekstit().put(KomotoTeksti.KANSAINVALISTYMINEN, kansainvalistyminen);
    }

    @Deprecated
    public Map<String, String> getKuvailevatTiedot() {
    	return getTekstit().get(KomotoTeksti.KUVAILEVAT_TIEDOT);
    }

    @Deprecated
    public void setKuvailevatTiedot(Map<String, String> kuvailevatTiedot) {
    	getTekstit().put(KomotoTeksti.KUVAILEVAT_TIEDOT, kuvailevatTiedot);
    }

    @Deprecated
    public Map<String, String> getLoppukoeVaatimukset() {
    	return getTekstit().get(KomotoTeksti.LOPPUKOEVAATIMUKSET);
    }

    @Deprecated
    public void setLoppukoeVaatimukset(Map<String, String> loppukoeVaatimukset) {
    	getTekstit().put(KomotoTeksti.LOPPUKOEVAATIMUKSET, loppukoeVaatimukset);
    }

    @Deprecated
    public Map<String, String> getMaksullisuusKuvaus() {
    	return getTekstit().get(KomotoTeksti.MAKSULLISUUS);
    }

    @Deprecated
    public void setMaksullisuusKuvaus(Map<String, String> maksullisuusKuvaus) {
    	getTekstit().put(KomotoTeksti.MAKSULLISUUS, maksullisuusKuvaus);
    }

    @Deprecated
    public Map<String, String> getSijoittuminenTyoelamaan() {
    	return getTekstit().get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN);
    }

    @Deprecated
    public void setSijoittuminenTyoelamaan(Map<String, String> sijoittuminenTyoelamaan) {
    	getTekstit().put(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN, sijoittuminenTyoelamaan);
    }

    @Deprecated
    public Map<String, String> getSisalto() {
    	return getTekstit().get(KomotoTeksti.SISALTO);
    }

    @Deprecated
    public void setSisalto(Map<String, String> sisalto) {
    	getTekstit().put(KomotoTeksti.SISALTO, sisalto);
    }

    @Deprecated
    public Map<String, String> getYhteistyoMuidenToimijoidenKanssa() {
    	return getTekstit().get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA);
    }

    @Deprecated
    public void setYhteistyoMuidenToimijoidenKanssa(Map<String, String> yhteistyoMuidenToimijoidenKanssa) {
    	getTekstit().put(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, yhteistyoMuidenToimijoidenKanssa);
    }

    @Deprecated
    public Map<String, String> getKoulutusohjelmanValinta() {
    	return getTekstit().get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
    }

    @Deprecated
    public void setKoulutusohjelmanValinta(Map<String, String> koulutusohjelmanValinta) {
    	getTekstit().put(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA, koulutusohjelmanValinta);
    }

    @Deprecated
    public Map<String, String> getPainotus() {
    	return getTekstit().get(KomotoTeksti.PAINOTUS);
    }

    @Deprecated
    public void setPainotus(Map<String, String> painotus) {
    	getTekstit().put(KomotoTeksti.PAINOTUS, painotus);
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

    public String getLaajuusArvo() {
        return _laajuusArvo;
    }

    public void setLaajuusArvo(String laajuusArvo) {
        this._laajuusArvo = laajuusArvo;
    }

    public String getLaajuusYksikkoUri() {
        return _laajuusYksikkoUri;
    }

    public void setLaajuusYksikkoUri(String laajuusYksikkoUri) {
        this._laajuusYksikkoUri = laajuusYksikkoUri;
    }

    public String getTarjoajaOid() {
        return _tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this._tarjoajaOid = tarjoajaOid;
    }

    public TarjontaTila getTila() {
        return _tila;
    }

    public void setTila(TarjontaTila tila) {
        this._tila = tila;
    }

    public String getUlkoinenTunniste() {
        return _ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this._ulkoinenTunniste = ulkoinenTunniste;
    }

    /**
     * This is actually pretty sick(ish)...
     *
     * this komoto --> parent komo --> parent parent komo --> parent komoto with same pohjakoulutus and tarjoaja... at the moment
     *
     * @return
     */
    public String getParentKomotoOid() {
        return _parentKomotoOid;
    }

    public void setParentKomotoOid(String parentKomotoOid) {
        this._parentKomotoOid = parentKomotoOid;
    }

    public List<YhteyshenkiloRDTO> getYhteyshenkilos() {
        if (_yhteyshenkilos == null) {
            _yhteyshenkilos = new ArrayList<YhteyshenkiloRDTO>();
        }
        return _yhteyshenkilos;
    }

    public void setYhteyshenkilos(List<YhteyshenkiloRDTO> yhteyshenkilos) {
        this._yhteyshenkilos = yhteyshenkilos;
    }

    public Map<String, List<String>> getTarjotutKielet() {
        if (_tarjotutKielet == null) {
            _tarjotutKielet = new HashMap<String, List<String>>();
        }
        return _tarjotutKielet;
    }

    public void setTarjotutKielet(Map<String, List<String>> _tarjotutKielet) {
        this._tarjotutKielet = _tarjotutKielet;
    }

}
