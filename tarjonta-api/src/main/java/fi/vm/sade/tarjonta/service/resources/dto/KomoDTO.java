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

import static fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO.MKT_KOULUTUSOHJELMAN_VALINTA;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mlyly
 */
public class KomoDTO implements Serializable {

    public static String MKT_NIMI = "MKT_NIMI";
    public static String MKT_TAVOITTEET = "MKT_TAVOITTEET";
    public static String MKT_KOULUTUKSEN_RAKENNE = "MKT_KOULUTUKSEN_RAKENNE";
    public static String MKT_JATKO_OPINTO_MAHDOLLISUUDET = "MKT_JATKO_OPINTO_MAHDOLLISUUDET";

    private String _oid;
    private int _version;
    private String _tila;

    private Date _updated;
    private String _updateByOid;
    private Date _created;
    private String _createdByOid;

    private List<String> _ylaModuulit;
    private List<String> _alaModuulit;

    private String _organisaatioOid;
    private String _tarjoajaOid; // onko sama kuin organisaatioOid?

    private String _laajuusArvo;

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

//    private String _oppilaitosTyyppi;
    private String _ulkoinenTunniste;

    // ------------------------------------------------------------------------------
    // Multilanguage support with keys
    //

    private Map<String, List<String>> _koodistoUrisMap = new HashMap<String, List<String>>();

    /**
     * Get koodisto uri list by "key"
     *
     * @param key ex. "Ammattinimikkeet"
     * @return list of uris, if not exists list will be created as empty
     */
    protected List<String> getKoodistoUris(String key) {
        List<String> result = _koodistoUrisMap.get(key);
        if (result == null) {
            result = new ArrayList<String>();
            _koodistoUrisMap.put(key, result);
        }
        return result;
    }

    /**
     * Add single uri to a given koodisto list by "key". (like Ammattinimikkeet etc.)
     *
     * @param key ex. Ammattinimikkeet
     * @param uri ex. "pianonvirittaja#1"
     */
    protected void addKoodistoUri(String key, String uri) {
        getKoodistoUris(key).add(uri);
    }

    /**
     * Set uris for koodisto list by "key".
     *
     * @param key ex. "Ammattinimikkeet"
     * @param uris  ex. ["maalari#1", "suutari#2", "juoppo#1"]
     */
    protected void setKoodistoUris(String key, List<String> uris) {
        _koodistoUrisMap.put(key, uris);
    }

    // ------------------------------------------------------------------------------
    // Multilanguage support with keys
    //

    private Map<String, MonikielinenTekstisDTO> _monikielinenData;

    protected void setMonikielinenData(Map<String, MonikielinenTekstisDTO> _monikielinenData) {
        this._monikielinenData = _monikielinenData;
    }

    protected Map<String, MonikielinenTekstisDTO> getMonikielinenData() {
        if (_monikielinenData == null) {
            _monikielinenData = new HashMap<String, MonikielinenTekstisDTO>();
        }
        return _monikielinenData;
    }

    protected MonikielinenTekstisDTO getMonikielinenData(String key) {
        return getMonikielinenData().get(key);
    }

    protected void addMonikielinenData(String key, MonikielinenTekstiDTO mkt) {
        MonikielinenTekstisDTO mkts = getMonikielinenData().get(key);
        if (mkts == null) {
            mkts = new MonikielinenTekstisDTO();
            getMonikielinenData().put(key, mkts);
        }

        mkts.addKieli(mkt);
    }

    protected void addMonikielinenData(String key, String kieliUri, String arvo) {
        MonikielinenTekstiDTO mkt = new MonikielinenTekstiDTO();
        mkt.setArvo(arvo);
        mkt.setKieliUri(kieliUri);
        addMonikielinenData(key, mkt);
    }

    // ------------------------------------------------------------------------------
    // Getters and setters
    //

    public String getOid() {
        return _oid;
    }

    public void setOid(String oid) {
        this._oid = oid;
    }

    public int getVersion() {
        return _version;
    }

    public void setVersion(int version) {
        this._version = version;
    }

    public String getTila() {
        return _tila;
    }

    public void setTila(String tila) {
        this._tila = tila;
    }

    public Date getUpdated() {
        return _updated;
    }

    public void setUpdated(Date updated) {
        this._updated = updated;
    }

    public String getUpdateByOid() {
        return _updateByOid;
    }

    public void setUpdateByOid(String updateByOid) {
        this._updateByOid = updateByOid;
    }

    public Date getCreated() {
        return _created;
    }

    public void setCreated(Date created) {
        this._created = created;
    }

    public String getCreatedByOid() {
        return _createdByOid;
    }

    public void setCreatedByOid(String createdByOid) {
        this._createdByOid = createdByOid;
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



    // -----------------------------------------------
    // Multilanguage metadata
    //

    public MonikielinenTekstisDTO getNimi() {
        return getMonikielinenData(MKT_NIMI);
    }

    public void setNimi(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_NIMI, v);
    }

    public MonikielinenTekstisDTO getKoulutuksenRakenne() {
        return getMonikielinenData(MKT_KOULUTUKSEN_RAKENNE);
    }

    public void setKoulutuksenRakenne(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_KOULUTUKSEN_RAKENNE, v);
    }

    public MonikielinenTekstisDTO getJatkoOpintoMahdollisuudet() {
        return getMonikielinenData(MKT_JATKO_OPINTO_MAHDOLLISUUDET);
    }

    public void setJatkoOpintoMahdollisuudet(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_JATKO_OPINTO_MAHDOLLISUUDET, v);
    }

    public MonikielinenTekstisDTO getTavoitteet() {
        return getMonikielinenData(MKT_TAVOITTEET);
    }

    public void setTavoitteet(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_TAVOITTEET, v);
    }

}
