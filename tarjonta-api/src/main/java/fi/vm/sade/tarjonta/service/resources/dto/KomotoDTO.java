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

/**
 *
 * @author mlyly
 */
public class KomotoDTO extends KomoDTO {

    private Date koulutuksenAlkamisDate;
    private boolean _maksullisuus;
    private String _komoOid;
    private String _pohjakoulutusVaatimusUri;

//    public static String MKT_POHJAKOULUTUS_VAATIMUS = "MKT_POHJAKOULUTUS_VAATIMUS";
    public static String MKT_ARVIOINTIKRITEERIT = "MKT_ARVIOINTIKRITEERIT";
    public static String MKT_KANSAINVALISTYMINEN = "MKT_KANSAINVALISTYMINEN";
    public static String MKT_KUVAILEVAT_TIEDOT = "MKT_KUVAILEVAT_TIEDOT";
    public static String MKT_LOPPUKOE_VAATIMUKSET = "MKT_LOPPUKOE_VAATIMUKSET";
    public static String MKT_MAKSULLISUUS = "MKT_MAKSULLISUUS";
    public static String MKT_SIJOITTUMINEN_TYOELAMAAN = "MKT_SIJOITTUMINEN_TYOELAMAAN";
    public static String MKT_SISALTO = "MKT_SISALTO";
    public static String MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA = "MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA";
    public static String MKT_KOULUTUSOHJELMAN_VALINTA = "MKT_KOULUTUSOHJELMAN_VALINTA";
    public static String MKT_WEB_LINKKIS = "MKT_WEB_LINKKIS";
    public static String MKT_PAINOTUS = "MKT_PAINOTUS";

    public Date getKoulutuksenAlkamisDate() {
        return koulutuksenAlkamisDate;
    }

    public void setKoulutuksenAlkamisDate(Date koulutuksenAlkamisDate) {
        this.koulutuksenAlkamisDate = koulutuksenAlkamisDate;
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

//    public MonikielinenTekstis getPohjakoulutusvaatimus() {
//        return getMonikielinenData(MKT_POHJAKOULUTUS_VAATIMUS);
//    }
//
//    public void setPohjakoulutusvaatimus(MonikielinenTekstis v) {
//        getMonikielinenData().put(MKT_POHJAKOULUTUS_VAATIMUS, v);
//    }

    public MonikielinenTekstisDTO getArviointikriteerit() {
        return getMonikielinenData(MKT_ARVIOINTIKRITEERIT);
    }

    public void setArviointikriteerit(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_ARVIOINTIKRITEERIT, v);
    }

    public MonikielinenTekstisDTO getKansainvalistyminen() {
        return getMonikielinenData(MKT_KANSAINVALISTYMINEN);
    }

    public void setKansainvalistyminen(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_KANSAINVALISTYMINEN, v);
    }

    public MonikielinenTekstisDTO getKuvailevatTiedot() {
        return getMonikielinenData(MKT_KUVAILEVAT_TIEDOT);
    }

    public void setKuvailevatTiedot(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_KUVAILEVAT_TIEDOT, v);
    }

    public MonikielinenTekstisDTO getLoppukoeVaatimukset() {
        return getMonikielinenData(MKT_LOPPUKOE_VAATIMUKSET);
    }

    public void setLoppukoeVaatimukset(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_LOPPUKOE_VAATIMUKSET, v);
    }

    public MonikielinenTekstisDTO getMaksullisuusTeksti() {
        return getMonikielinenData(MKT_MAKSULLISUUS);
    }

    public void setMaksullisuusTeksti(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_MAKSULLISUUS, v);
    }

    public MonikielinenTekstisDTO getSijoittuminenTyoelamaan() {
        return getMonikielinenData(MKT_SIJOITTUMINEN_TYOELAMAAN);
    }

    public void setSijoittuminenTyoelamaan(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_SIJOITTUMINEN_TYOELAMAAN, v);
    }

    public MonikielinenTekstisDTO getSisalto() {
        return getMonikielinenData(MKT_SISALTO);
    }

    public void setSisalto(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_SISALTO, v);
    }

    public MonikielinenTekstisDTO getYhteistyoMuidenToimijoidenKanssa() {
        return getMonikielinenData(MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA);
    }

    public void setYhteistyoMuidenToimijoidenKanssa(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, v);
    }

    public MonikielinenTekstisDTO getKoulutusohjelmanValinta() {
        return getMonikielinenData(MKT_KOULUTUSOHJELMAN_VALINTA);
    }

    public void setKoulutusohjelmanValinta(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_KOULUTUSOHJELMAN_VALINTA, v);
    }

    public MonikielinenTekstisDTO getWebLinkkis() {
        return getMonikielinenData(MKT_WEB_LINKKIS);
    }

    public void setWebLinkkis(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_WEB_LINKKIS, v);
    }

    public MonikielinenTekstisDTO getPainotus() {
        return getMonikielinenData(MKT_PAINOTUS);
    }

    public void setPainotus(MonikielinenTekstisDTO v) {
        getMonikielinenData().put(MKT_PAINOTUS, v);
    }


    // -----------------------------------------
    // URI LISTS
    //

    public static String URIS_AVAINSANAT = "URIS_AVAINSANAT";
    public static String URIS_AMMATTINIMIKE = "URIS_AMMATTINIMIKE";
    public static String URIS_KOULUTUSLAJI = "URIS_KOULUTUSLAJI";
    public static String URIS_LUKIODIPLOMIT = "URIS_LUKIODIPLOMIT";
    public static String URIS_OPETUSKIELET = "URIS_OPETUSKIELET";
    public static String URIS_OPETUSMUODOT = "URIS_OPETUSMUODOT";
    public static String URIS_TEEMAT = "URIS_TEEMAT";

    public List<String> getAvainsanaUris() {
        return getKoodistoUris(URIS_AVAINSANAT);
    }

    public void setAvainsanaUris(List<String> uris) {
        setKoodistoUris(URIS_AVAINSANAT, uris);
    }

    public List<String> getAmmattinimikeUris() {
        return getKoodistoUris(URIS_AMMATTINIMIKE);
    }

    public void setAmmattinimikeUris(List<String> uris) {
        setKoodistoUris(URIS_AMMATTINIMIKE, uris);
    }

    public List<String> getKoulutuslajiUris() {
        return getKoodistoUris(URIS_KOULUTUSLAJI);
    }

    public void setKoulutuslajiUris(List<String> uris) {
        setKoodistoUris(URIS_KOULUTUSLAJI, uris);
    }

    public void setLukiodiplomiUris(List<String> uris) {
        setKoodistoUris(URIS_LUKIODIPLOMIT, uris);
    }

    public List<String> getLukiodiplomiUris() {
        return getKoodistoUris(URIS_LUKIODIPLOMIT);
    }

    public void setOpetusKieletUris(List<String> uris) {
        setKoodistoUris(URIS_OPETUSKIELET, uris);
    }

    public List<String> getOpetusKieletUris() {
        return getKoodistoUris(URIS_OPETUSKIELET);
    }

    public void setOpetusmuotoUris(List<String> uris) {
        setKoodistoUris(URIS_OPETUSMUODOT, uris);
    }

    public List<String> getOpetusmuotoUris() {
        return getKoodistoUris(URIS_OPETUSMUODOT);
    }

    public void setTeemaUris(List<String> uris) {
        setKoodistoUris(URIS_OPETUSMUODOT, uris);
    }

    public List<String> getTeemaUris() {
        return getKoodistoUris(URIS_TEEMAT);
    }
}
