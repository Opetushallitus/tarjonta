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

/**
 *
 * @author mlyly
 */
public class Komoto extends Komo {

    private Date koulutuksenAlkamisDate;

    private boolean _maksullisus;

    private String _koulutusmoduuliId; // ???

    private String _painotus; // rakenne?

    public static String MKT_POHJAKOULUTUS_VAATIMUS = "MKT_POHJAKOULUTUS_VAATIMUS";
    public static String MKT_ARVIOINTIKRITEERIT = "MKT_ARVIOINTIKRITEERIT";
    public static String MKT_KANSAINVALISTYMINEN = "MKT_KANSAINVALISTYMINEN";
    public static String MKT_KUVAILEVAT_TIEDOT = "MKT_KUVAILEVAT_TIEDOT";
    public static String MKT_LOPPUKOE_VAATIMUKSET = "MKT_LOPPUKOE_VAATIMUKSET";
    public static String MKT_MAKSULLISUUS = "MKT_MAKSULLISUUS";
    public static String MKT_SIJOITTUMINEN_TYOELAMAAN = "MKT_SIJOITTUMINEN_TYOELAMAAN";
    public static String MKT_SISALTO = "MKT_SISALTO";
    public static String MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA = "MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA";
    public static String MKT_KOULUTUSOHJELMAN_VALINTA = "MKT_KOULUTUSOHJELMAN_VALINTA";

    public Date getKoulutuksenAlkamisDate() {
        return koulutuksenAlkamisDate;
    }

    public void setKoulutuksenAlkamisDate(Date koulutuksenAlkamisDate) {
        this.koulutuksenAlkamisDate = koulutuksenAlkamisDate;
    }

    public boolean isMaksullisus() {
        return _maksullisus;
    }

    public void setMaksullisus(boolean maksullisus) {
        this._maksullisus = maksullisus;
    }

    public String getKoulutusmoduuliId() {
        return _koulutusmoduuliId;
    }

    public void setKoulutusmoduuliId(String koulutusmoduuliId) {
        this._koulutusmoduuliId = koulutusmoduuliId;
    }

    public MonikielinenTekstis getPohjakoulutusvaatimus() {
        return getMonikielinenData(MKT_POHJAKOULUTUS_VAATIMUS);
    }

    public void setPohjakoulutusvaatimus(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_POHJAKOULUTUS_VAATIMUS, v);
    }

    public MonikielinenTekstis getArviointikriteerit() {
        return getMonikielinenData(MKT_ARVIOINTIKRITEERIT);
    }

    public void setArviointikriteerit(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_ARVIOINTIKRITEERIT, v);
    }

    public MonikielinenTekstis getKansainvalistyminen() {
        return getMonikielinenData(MKT_KANSAINVALISTYMINEN);
    }

    public void setKansainvalistyminen(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_KANSAINVALISTYMINEN, v);
    }

    public MonikielinenTekstis getKuvailevatTiedot() {
        return getMonikielinenData(MKT_KUVAILEVAT_TIEDOT);
    }

    public void setKuvailevatTiedot(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_KUVAILEVAT_TIEDOT, v);
    }

    public MonikielinenTekstis getLoppukoeVaatimukset() {
        return getMonikielinenData(MKT_LOPPUKOE_VAATIMUKSET);
    }

    public void setLoppukoeVaatimukset(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_LOPPUKOE_VAATIMUKSET, v);
    }

    public MonikielinenTekstis getMaksullisuusTeksti() {
        return getMonikielinenData(MKT_MAKSULLISUUS);
    }

    public void setMaksullisuusTeksti(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_MAKSULLISUUS, v);
    }

    public MonikielinenTekstis getSijoittuminenTyoelamaan() {
        return getMonikielinenData(MKT_SIJOITTUMINEN_TYOELAMAAN);
    }

    public void setSijoittuminenTyoelamaan(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_SIJOITTUMINEN_TYOELAMAAN, v);
    }

    public MonikielinenTekstis getSisalto() {
        return getMonikielinenData(MKT_SISALTO);
    }

    public void setSisalto(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_SISALTO, v);
    }

    public MonikielinenTekstis getYhteistyoMuidenToimijoidenKanssa() {
        return getMonikielinenData(MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA);
    }

    public void setYhteistyoMuidenToimijoidenKanssa(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, v);
    }

    public MonikielinenTekstis getKoulutusohjelmanValinta() {
        return getMonikielinenData(MKT_KOULUTUSOHJELMAN_VALINTA);
    }

    public void setKoulutusohjelmanValinta(MonikielinenTekstis v) {
        getMonikielinenData().put(MKT_KOULUTUSOHJELMAN_VALINTA, v);
    }

}
