/*
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */

package fi.vm.sade.tarjonta.ui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * For editing "Haku" in the UI.
 *
 *
 * @author Tuomas Katva
 */
public class HakuViewModel extends BaseUIViewModel {

    private String hakuOid;

    private String hakutyyppi;

    private String hakukausi;

    private int hakuvuosi;

    private String koulutuksenAlkamisKausi;

    private String haunKohdejoukko;

    private String hakutapa;

    private String haunTunniste;

    private Date alkamisPvm;

    private Date paattymisPvm;

    private boolean haussaKaytetaanSijoittelua;

    private boolean kaytetaanJarjestelmanHakulomaketta;

    private String hakuLomakeUrl;

    private List<HakuaikaViewModel> sisaisetHakuajat;

    private String nimiFi;

    private String nimiSe;

    private String nimiEn;

    private boolean hakuValmis;

    /**
     * @return the hakuOid
     */
    public String getHakuOid() {
        return hakuOid;
    }

    /**
     * @param hakuOid the hakuOid to set
     */
    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    /**
     * @return the hakutyyppi
     */
    public String getHakutyyppi() {
        return hakutyyppi;
    }

    /**
     * @param hakutyyppi the hakutyyppi to set
     */
    public void setHakutyyppi(String hakutyyppi) {
        this.hakutyyppi = hakutyyppi;
    }

    /**
     * @return the hakukausi
     */
    public String getHakukausi() {
        return hakukausi;
    }

    /**
     * @param hakukausi the hakukausi to set
     */
    public void setHakukausi(String hakukausi) {
        this.hakukausi = hakukausi;
    }

    /**
     * @return the hakuvuosi
     */
    public int getHakuvuosi() {
        return hakuvuosi;
    }

    /**
     * @param hakuvuosi the hakuvuosi to set
     */
    public void setHakuvuosi(int hakuvuosi) {
        this.hakuvuosi = hakuvuosi;
    }

    /**
     * @return the koulutuksenAlkamisKausi
     */
    public String getKoulutuksenAlkamisKausi() {
        return koulutuksenAlkamisKausi;
    }

    /**
     * @param koulutuksenAlkamisKausi the koulutuksenAlkamisKausi to set
     */
    public void setKoulutuksenAlkamisKausi(String koulutuksenAlkamisKausi) {
        this.koulutuksenAlkamisKausi = koulutuksenAlkamisKausi;
    }

    /**
     * @return the haunKohdejoukko
     */
    public String getHaunKohdejoukko() {
        return haunKohdejoukko;
    }

    /**
     * @param haunKohdejoukko the haunKohdejoukko to set
     */
    public void setHaunKohdejoukko(String haunKohdejoukko) {
        this.haunKohdejoukko = haunKohdejoukko;
    }

    /**
     * @return the haunTunniste
     */
    public String getHaunTunniste() {
        return haunTunniste;
    }

    /**
     * @param haunTunniste the haunTunniste to set
     */
    public void setHaunTunniste(String haunTunniste) {
        this.haunTunniste = haunTunniste;
    }

    /**
     * @return the alkamisPvm
     */
    public Date getAlkamisPvm() {
        return alkamisPvm;
    }

    /**
     * @param alkamisPvm the alkamisPvm to set
     */
    public void setAlkamisPvm(Date alkamisPvm) {
        this.alkamisPvm = alkamisPvm;
    }

    /**
     * @return the paattymisPvm
     */
    public Date getPaattymisPvm() {
        return paattymisPvm;
    }

    /**
     * @param paattymisPvm the paattymisPvm to set
     */
    public void setPaattymisPvm(Date paattymisPvm) {
        this.paattymisPvm = paattymisPvm;
    }

    /**
     * @return the haussaKaytetaanSijoittelua
     */
    public boolean isHaussaKaytetaanSijoittelua() {
        return haussaKaytetaanSijoittelua;
    }

    /**
     * @param haussaKaytetaanSijoittelua the haussaKaytetaanSijoittelua to set
     */
    public void setHaussaKaytetaanSijoittelua(boolean haussaKaytetaanSijoittelua) {
        this.haussaKaytetaanSijoittelua = haussaKaytetaanSijoittelua;
    }

    /**
     * @return the hakuLomakeUrl
     */
    public String getHakuLomakeUrl() {
        return hakuLomakeUrl;
    }

    /**
     * @param hakuLomakeUrl the hakuLomakeUrl to set
     */
    public void setHakuLomakeUrl(String hakuLomakeUrl) {
        this.hakuLomakeUrl = hakuLomakeUrl;
    }

    /**
     * @return the sisaisetHakuajat
     */
    public List<HakuaikaViewModel> getSisaisetHakuajat() {
        if (sisaisetHakuajat == null) {
            sisaisetHakuajat = new ArrayList<HakuaikaViewModel>();
        }

        return sisaisetHakuajat;
    }


    /**
     * @return the hakutapa
     */
    public String getHakutapa() {
        return hakutapa;
    }

    /**
     * @param hakutapa the hakutapa to set
     */
    public void setHakutapa(String hakutapa) {
        this.hakutapa = hakutapa;
    }

    /**
     * @return the kaytetaanJarjestelmanHakulomaketta
     */
    public boolean isKaytetaanJarjestelmanHakulomaketta() {
        return kaytetaanJarjestelmanHakulomaketta;
    }

    /**
     * @param kaytetaanJarjestelmanHakulomaketta the kaytetaanJarjestelmanHakulomaketta to set
     */
    public void setKaytetaanJarjestelmanHakulomaketta(boolean kaytetaanJarjestelmanHakulomaketta) {
        this.kaytetaanJarjestelmanHakulomaketta = kaytetaanJarjestelmanHakulomaketta;
    }

    /**
     * @return the nimiFi
     */
    public String getNimiFi() {
        return nimiFi;
    }

    /**
     * @param nimiFi the nimiFi to set
     */
    public void setNimiFi(String nimiFi) {
        this.nimiFi = nimiFi;
    }

    /**
     * @return the nimiSe
     */
    public String getNimiSe() {
        return nimiSe;
    }

    /**
     * @param nimiSe the nimiSe to set
     */
    public void setNimiSe(String nimiSe) {
        this.nimiSe = nimiSe;
    }

    /**
     * @return the nimiEn
     */
    public String getNimiEn() {
        return nimiEn;
    }

    /**
     * @param nimiEn the nimiEn to set
     */
    public void setNimiEn(String nimiEn) {
        this.nimiEn = nimiEn;
    }

    /**
     * @return the hakuValmis
     */
    public boolean isHakuValmis() {
        return hakuValmis;
    }

    /**
     * @param hakuValmis the hakuValmis to set
     */
    public void setHakuValmis(boolean hakuValmis) {
        this.hakuValmis = hakuValmis;
    }




}
