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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 * REST DTO for LOS / Komo / Koulutusmoduuli.
 *
 * @author mlyly
 */
public class KomoDTO extends BaseRDTO {

    private static final long serialVersionUID = 1L;

    private TarjontaTila _tila;
    private List<String> _ylaModuulit;
    private List<String> _alaModuulit;
    private String _organisaatioOid;
    private String _tarjoajaOid; // onko sama kuin organisaatioOid?
    private String _laajuusArvo; //value
    private String laajuusArvoUri; //koodi uri
    private String _laajuusYksikkoUri;
    private String _eqfLuokitusUri;
    private String _koulutusAsteUri;
    private String _koulutusLuokitusKoodiUri;
    private String _koulutusAlaUri;
    private String _koulutusOhjelmaKoodiUri;
    private String _moduuliTyyppiUri;
    private String _nqfLuokitusUri;
    private String _opintoalaUri;
    private String _tutkintoOhjelmanNimiUri;
    private String _tutkintonimikeUri;
    private String _koulutusTyyppiUri;
    private String _lukiolinjaUri;
    private String _ulkoinenTunniste;
    private String _moduuliTyyppi;
    private Map<String, String> _nimi;
    private Map<KomoTeksti, Map<String, String>> _tekstit;
    private String _koulutusKoodiUri;
    private String koulutustyyppi;
    private boolean isPseudo;

    // ------------------------------------------------------------------------------
    // Getters and setters
    //
    public Map<KomoTeksti, Map<String, String>> getTekstit() {
        if (_tekstit == null) {
            _tekstit = new EnumMap<KomoTeksti, Map<String, String>>(KomoTeksti.class);
        }
        return _tekstit;
    }

    public void setTekstit(Map<KomoTeksti, Map<String, String>> _tekstit) {
        this._tekstit = _tekstit;
    }

    public TarjontaTila getTila() {
        return _tila;
    }

    public void setTila(TarjontaTila tila) {
        this._tila = tila;
    }

    public List<String> getYlaModuulit() {
        return _ylaModuulit;
    }

    public void setYlaModuulit(List<String> ylaModuulit) {
        this._ylaModuulit = ylaModuulit;
    }

    public List<String> getAlaModuulit() {
        return _alaModuulit;
    }

    public void setAlaModuulit(List<String> alaModuulit) {
        this._alaModuulit = alaModuulit;
    }

    public String getOrganisaatioOid() {
        return _organisaatioOid;
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        this._organisaatioOid = organisaatioOid;
    }

    public String getTarjoajaOid() {
        return _tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this._tarjoajaOid = tarjoajaOid;
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

    public String getEqfLuokitusUri() {
        return _eqfLuokitusUri;
    }

    public void setEqfLuokitusUri(String eqfLuokitusUri) {
        this._eqfLuokitusUri = eqfLuokitusUri;
    }

    public String getKoulutusAsteUri() {
        return _koulutusAsteUri;
    }

    public void setKoulutusAsteUri(String koulutusAsteUri) {
        this._koulutusAsteUri = koulutusAsteUri;
    }

    public String getKoulutusLuokitusKoodiUri() {
        return _koulutusLuokitusKoodiUri;
    }

    public void setKoulutusLuokitusKoodiUri(String koulutusLuokitusKoodiUri) {
        this._koulutusLuokitusKoodiUri = koulutusLuokitusKoodiUri;
    }

    public String getKoulutusAlaUri() {
        return _koulutusAlaUri;
    }

    public void setKoulutusAlaUri(String koulutusAlaUri) {
        this._koulutusAlaUri = koulutusAlaUri;
    }

    public String getKoulutusOhjelmaKoodiUri() {
        return _koulutusOhjelmaKoodiUri;
    }

    public void setKoulutusOhjelmaKoodiUri(String koulutusOhjelmaKoodiUri) {
        this._koulutusOhjelmaKoodiUri = koulutusOhjelmaKoodiUri;
    }

    public String getModuuliTyyppiUri() {
        return _moduuliTyyppiUri;
    }

    public void setModuuliTyyppiUri(String moduuliTyyppiUri) {
        this._moduuliTyyppiUri = moduuliTyyppiUri;
    }

    public String getNqfLuokitusUri() {
        return _nqfLuokitusUri;
    }

    public void setNqfLuokitusUri(String nqfLuokitusUri) {
        this._nqfLuokitusUri = nqfLuokitusUri;
    }

    public String getOpintoalaUri() {
        return _opintoalaUri;
    }

    public void setOpintoalaUri(String opintoalaUri) {
        this._opintoalaUri = opintoalaUri;
    }

    public String getTutkintoOhjelmanNimiUri() {
        return _tutkintoOhjelmanNimiUri;
    }

    public void setTutkintoOhjelmanNimiUri(String tutkintoOhjelmanNimiUri) {
        this._tutkintoOhjelmanNimiUri = tutkintoOhjelmanNimiUri;
    }

    public String getTutkintonimikeUri() {
        return _tutkintonimikeUri;
    }

    public void setTutkintonimikeUri(String tutkintonimikeUri) {
        this._tutkintonimikeUri = tutkintonimikeUri;
    }

    public String getKoulutusTyyppiUri() {
        return _koulutusTyyppiUri;
    }

    public void setKoulutusTyyppiUri(String koulutusTyyppiUri) {
        this._koulutusTyyppiUri = koulutusTyyppiUri;
    }

    public String getLukiolinjaUri() {
        return _lukiolinjaUri;
    }

    public void setLukiolinjaUri(String lukiolinjaUri) {
        this._lukiolinjaUri = lukiolinjaUri;
    }

    public String getUlkoinenTunniste() {
        return _ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this._ulkoinenTunniste = ulkoinenTunniste;
    }

    public String getModuuliTyyppi() {
        return _moduuliTyyppi;
    }

    public void setModuuliTyyppi(String _moduuliTyyppi) {
        this._moduuliTyyppi = _moduuliTyyppi;
    }

    // -----------------------------------------------
    // Multilanguage metadata
    //
    public Map<String, String> getNimi() {
        return _nimi;
    }

    public void setNimi(Map<String, String> _nimi) {
        this._nimi = _nimi;
    }

    @Deprecated
    public Map<String, String> getKoulutuksenRakenne() {
        return getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE);
    }

    @Deprecated
    public void setKoulutuksenRakenne(Map<String, String> _koulutuksenRakenne) {
        getTekstit().put(KomoTeksti.KOULUTUKSEN_RAKENNE, _koulutuksenRakenne);
    }

    @Deprecated
    public Map<String, String> getJatkoOpintoMahdollisuudet() {
        return getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET);
    }

    @Deprecated
    public void setJatkoOpintoMahdollisuudet(Map<String, String> _jatkoOpintoMahdollisuudet) {
        getTekstit().put(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, _jatkoOpintoMahdollisuudet);
    }

    @Deprecated
    public Map<String, String> getTavoitteet() {
        return getTekstit().get(KomoTeksti.TAVOITTEET);
    }

    @Deprecated
    public void setTavoitteet(Map<String, String> _tavoitteet) {
        getTekstit().put(KomoTeksti.TAVOITTEET, _tavoitteet);
    }

    public String getKoulutusKoodiUri() {
        return _koulutusKoodiUri;
    }

    public void setKoulutusKoodiUri(String _koulutusKoodiUri) {
        this._koulutusKoodiUri = _koulutusKoodiUri;
    }

    /**
     * @return the laajuusArvoUri
     */
    public String getLaajuusArvoUri() {
        return laajuusArvoUri;
    }

    /**
     * @param laajuusArvoUri the laajuusArvoUri to set
     */
    public void setLaajuusArvoUri(String laajuusArvoUri) {
        this.laajuusArvoUri = laajuusArvoUri;
    }

    /**
     * @return the koulutustyyppi
     */
    public String getKoulutustyyppi() {
        return koulutustyyppi;
    }

    /**
     * @param koulutustyyppi the koulutustyyppi to set
     */
    public void setKoulutustyyppi(String koulutustyyppi) {
        this.koulutustyyppi = koulutustyyppi;
    }

    public boolean isPseudo() {
        return isPseudo;
    }

    public void setPseudo(boolean isPseudo) {
        this.isPseudo = isPseudo;
    }
}
