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

import fi.vm.sade.tarjonta.service.types.tarjonta.Haku;
import fi.vm.sade.tarjonta.service.types.tarjonta.HaunNimi;
import fi.vm.sade.tarjonta.service.types.tarjonta.SisaisetHakuAjat;


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
    
    private Haku hakuDto;
    
    public HakuViewModel() {
        super();
        hakuDto = new Haku();
    }
    
    public HakuViewModel(Haku hakuDto) {
        super();
        this.hakuDto = hakuDto;
    }

    /**
     * @return the hakuOid
     */
    public String getHakuOid() {
        this.hakuOid = hakuDto.getOid();
        return hakuOid;
    }

    /**
     * @param hakuOid the hakuOid to set
     */
    public void setHakuOid(String hakuOid) {
        hakuDto.setOid(hakuOid);
        this.hakuOid = hakuOid;
    }

    /**
     * @return the hakutyyppi
     */
    public String getHakutyyppi() {
        hakutyyppi = hakuDto.getHakutyyppiUri();
        return hakutyyppi;
    }

    /**
     * @param hakutyyppi the hakutyyppi to set
     */
    public void setHakutyyppi(String hakutyyppi) {
        hakuDto.setHakutyyppiUri(hakutyyppi);
        this.hakutyyppi = hakutyyppi;
    }

    /**
     * @return the hakukausi
     */
    public String getHakukausi() {
        hakukausi = hakuDto.getHakukausiUri();
        return hakukausi;
    }

    /**
     * @param hakukausi the hakukausi to set
     */
    public void setHakukausi(String hakukausi) {
        hakuDto.setHakukausiUri(hakukausi);
        this.hakukausi = hakukausi;
    }

    /**
     * @return the hakuvuosi
     */
    public int getHakuvuosi() {
        hakuvuosi = hakuDto.getHakuVuosi();
        return hakuvuosi;
    }

    /**
     * @param hakuvuosi the hakuvuosi to set
     */
    public void setHakuvuosi(int hakuvuosi) {
        hakuDto.setHakuVuosi(hakuvuosi);
        this.hakuvuosi = hakuvuosi;
    }

    /**
     * @return the koulutuksenAlkamisKausi
     */
    public String getKoulutuksenAlkamisKausi() {
        koulutuksenAlkamisKausi = hakuDto.getKoulutuksenAlkamisKausiUri();
        return koulutuksenAlkamisKausi;
    }

    /**
     * @param koulutuksenAlkamisKausi the koulutuksenAlkamisKausi to set
     */
    public void setKoulutuksenAlkamisKausi(String koulutuksenAlkamisKausi) {
        hakuDto.setKoulutuksenAlkamisKausiUri(koulutuksenAlkamisKausi);
        this.koulutuksenAlkamisKausi = koulutuksenAlkamisKausi;
    }

    /**
     * @return the haunKohdejoukko
     */
    public String getHaunKohdejoukko() {
        haunKohdejoukko = hakuDto.getKohdejoukkoUri();
        return haunKohdejoukko;
    }

    /**
     * @param haunKohdejoukko the haunKohdejoukko to set
     */
    public void setHaunKohdejoukko(String haunKohdejoukko) {
        hakuDto.setKohdejoukkoUri(haunKohdejoukko);
        this.haunKohdejoukko = haunKohdejoukko;
    }

    /**
     * @return the haunTunniste
     */
    public String getHaunTunniste() {
        haunTunniste = hakuDto.getHaunTunniste();
        return haunTunniste;
    }

    /**
     * @param haunTunniste the haunTunniste to set
     */
    public void setHaunTunniste(String haunTunniste) {
        hakuDto.setHaunTunniste(haunTunniste);
        this.haunTunniste = haunTunniste;
    }

    /**
     * @return the alkamisPvm
     */
    public Date getAlkamisPvm() {
        alkamisPvm = hakuDto.getHaunAlkamisPvm();
        return alkamisPvm;
    }

    /**
     * @param alkamisPvm the alkamisPvm to set
     */
    public void setAlkamisPvm(Date alkamisPvm) {
        hakuDto.setHaunAlkamisPvm(alkamisPvm);
        this.alkamisPvm = alkamisPvm;
    }

    /**
     * @return the paattymisPvm
     */
    public Date getPaattymisPvm() {
        paattymisPvm = hakuDto.getHaunLoppumisPvm();
        return paattymisPvm;
    }

    /**
     * @param paattymisPvm the paattymisPvm to set
     */
    public void setPaattymisPvm(Date paattymisPvm) {
        hakuDto.setHaunLoppumisPvm(paattymisPvm);
        this.paattymisPvm = paattymisPvm;
    }

    /**
     * @return the haussaKaytetaanSijoittelua
     */
    public boolean isHaussaKaytetaanSijoittelua() {
        haussaKaytetaanSijoittelua = hakuDto.isSijoittelu();
        return haussaKaytetaanSijoittelua;
    }

    /**
     * @param haussaKaytetaanSijoittelua the haussaKaytetaanSijoittelua to set
     */
    public void setHaussaKaytetaanSijoittelua(boolean haussaKaytetaanSijoittelua) {
        hakuDto.setSijoittelu(haussaKaytetaanSijoittelua);
        this.haussaKaytetaanSijoittelua = haussaKaytetaanSijoittelua;
    }

    /**
     * @return the hakuLomakeUrl
     */
    public String getHakuLomakeUrl() {
        hakuLomakeUrl = hakuDto.getHakulomakeUrl();
        return hakuLomakeUrl;
    }

    /**
     * @param hakuLomakeUrl the hakuLomakeUrl to set
     */
    public void setHakuLomakeUrl(String hakuLomakeUrl) {
        hakuDto.setHakulomakeUrl(hakuLomakeUrl);
        this.hakuLomakeUrl = hakuLomakeUrl;
    }

    /**
     * @return the sisaisetHakuajat
     */
    public List<HakuaikaViewModel> getSisaisetHakuajat() {
        if (sisaisetHakuajat == null) {
            sisaisetHakuajat = new ArrayList<HakuaikaViewModel>();
        }
        sisaisetHakuajat.clear();
        for (SisaisetHakuAjat curHakuaika : hakuDto.getSisaisetHakuajat()) {
            sisaisetHakuajat.add(new HakuaikaViewModel(curHakuaika));    
        }

        return sisaisetHakuajat;
    }
    

    /**
     * @param sisaisetHakuajat the sisaisethakuajat to set
     */
    public void setSisaisetHakuajat(List<HakuaikaViewModel> sisaisetHakuajat) {
        hakuDto.getSisaisetHakuajat().clear();
        for (HakuaikaViewModel curHakuaika : sisaisetHakuajat) {
            hakuDto.getSisaisetHakuajat().add(curHakuaika.getHakuaikaDto());
        }
        this.sisaisetHakuajat = sisaisetHakuajat;
    }


    /**
     * @return the hakutapa
     */
    public String getHakutapa() {
        hakutapa = hakuDto.getHakutapaUri();
        return hakutapa;
    }

    /**
     * @param hakutapa the hakutapa to set
     */
    public void setHakutapa(String hakutapa) {
        hakuDto.setHakutapaUri(hakutapa);
        this.hakutapa = hakutapa;
    }

    /**
     * @return the kaytetaanJarjestelmanHakulomaketta
     */
    public boolean isKaytetaanJarjestelmanHakulomaketta() {
        kaytetaanJarjestelmanHakulomaketta = (hakuDto.getHakulomakeUrl() == null);
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
        nimiFi = this.getKielistettyNimiFromDto("fi");
        return nimiFi;
    }

    /**
     * @param nimiFi the nimiFi to set
     */
    public void setNimiFi(String nimiFi) {
        setKielistettyNimiToDto(nimiFi, "fi");
        this.nimiFi = nimiFi;
    }


    /**
     * @return the nimiSe
     */
    public String getNimiSe() {
        nimiSe = getKielistettyNimiFromDto("sv");
        return nimiSe;
    }

    /**
     * @param nimiSe the nimiSe to set
     */
    public void setNimiSe(String nimiSe) {
        setKielistettyNimiToDto(nimiSe, "sv");
        this.nimiSe = nimiSe;
    }

    /**
     * @return the nimiEn
     */
    public String getNimiEn() {
        nimiEn = getKielistettyNimiFromDto("en");
        return nimiEn;
    }

    /**
     * @param nimiEn the nimiEn to set
     */
    public void setNimiEn(String nimiEn) {
        setKielistettyNimiToDto(nimiEn, "en");
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

    /**
     * @return the hakuDto
     */
    public Haku getHakuDto() {
        return hakuDto;
    }

    private String getKielistettyNimiFromDto(String kieliKoodi) {
        for (HaunNimi haunNimi : hakuDto.getHaunKielistetytNimet()) {
            if (haunNimi.getKielikoodi().equals(kieliKoodi)) {
                return haunNimi.getNimi();
            }
        }
        return null;
    }

    private void setKielistettyNimiToDto(String nimi, String kielikoodi) {
        boolean nimiExists = false;
        for (HaunNimi haunNimi : hakuDto.getHaunKielistetytNimet()) {
            if (haunNimi.getKielikoodi().equals(kielikoodi)) {
                haunNimi.setNimi(nimi);
                nimiExists = true;
            }
        }
        if (!nimiExists) {
            HaunNimi hNimi = new HaunNimi();
            hNimi.setKielikoodi(kielikoodi);
            hNimi.setNimi(nimi);
            hakuDto.getHaunKielistetytNimet().add(hNimi);
        }
        
    }


}
