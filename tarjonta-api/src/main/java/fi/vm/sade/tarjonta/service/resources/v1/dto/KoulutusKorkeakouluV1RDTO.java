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
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import fi.vm.sade.tarjonta.service.resources.dto.kk.SuunniteltuKestoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.UiMetaDTO;
import java.util.Date;

/**
 *
 * @author mlyly
 */
public class KoulutusKorkeakouluV1RDTO extends KoulutusV1RDTO {

    private UiMetaDTO koulutusohjelma;
    private static final long serialVersionUID = 1L;
    private String tunniste; //tutkinto-ohjelman tunniste
    /*
     * Other user selected form input data
     */
    private SuunniteltuKestoDTO suunniteltuKesto;
    private UiMetaDTO opetuskielis;
    private UiMetaDTO opetusmuodos;
    /*
     * KK
     */
    private Boolean opintojenMaksullisuus;
    private UiMetaDTO pohjakoulutusvaatimukset;
    private UiMetaDTO teemas;
    private UiDTO opintojenLaajuus;
    private Date koulutuksenAlkamisPvm;
    private UiMetaDTO ammattinimikkeet;
    private Double hinta;

    public KoulutusKorkeakouluV1RDTO() {
    }

    /**
     * @return the koulutusohjelma
     */
    public UiMetaDTO getKoulutusohjelma() {
        if (koulutusohjelma == null) {
            koulutusohjelma = new UiMetaDTO();
        }
        return koulutusohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setKoulutusohjelma(UiMetaDTO koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
    }

    /**
     * @return the opintojenMaksullisuus
     */
    public Boolean getOpintojenMaksullisuus() {
        return opintojenMaksullisuus;
    }

    /**
     * @param opintojenMaksullisuus the opintojenMaksullisuus to set
     */
    public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
        this.opintojenMaksullisuus = opintojenMaksullisuus;
    }

    /**
     * @return the opetuskielis
     */
    public UiMetaDTO getOpetuskielis() {
        if (opetuskielis == null) {
            opetuskielis = new UiMetaDTO();
        }

        return opetuskielis;
    }

    /**
     * @param opetuskielis the opetuskielis to set
     */
    public void setOpetuskielis(UiMetaDTO opetuskielis) {
        this.opetuskielis = opetuskielis;
    }

    /**
     * @return the opetusmuodos
     */
    public UiMetaDTO getOpetusmuodos() {
        if (opetusmuodos == null) {
            opetusmuodos = new UiMetaDTO();
        }

        return opetusmuodos;
    }

    /**
     * @param opetusmuodos the opetusmuodos to set
     */
    public void setOpetusmuodos(UiMetaDTO opetusmuodos) {
        this.opetusmuodos = opetusmuodos;
    }

    /**
     * @return the pohjakoulutusvaatimukset
     */
    public UiMetaDTO getPohjakoulutusvaatimukset() {
        if (pohjakoulutusvaatimukset == null) {
            pohjakoulutusvaatimukset = new UiMetaDTO();
        }

        return pohjakoulutusvaatimukset;
    }

    /**
     * @param pohjakoulutusvaatimukset the pohjakoulutusvaatimukset to set
     */
    public void setPohjakoulutusvaatimukset(UiMetaDTO pohjakoulutusvaatimukset) {
        this.pohjakoulutusvaatimukset = pohjakoulutusvaatimukset;
    }

    /**
     * @return the teemas
     */
    public UiMetaDTO getTeemas() {
        if (teemas == null) {
            teemas = new UiMetaDTO();
        }

        return teemas;
    }

    /**
     * @param teemas the teemas to set
     */
    public void setTeemas(UiMetaDTO teemas) {
        this.teemas = teemas;
    }

    /**
     * @return the tunniste
     */
    public String getTunniste() {
        return tunniste;
    }

    /**
     * @param tunniste the tunniste to set
     */
    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    /**
     * @return the opintojenLaajuus
     */
    public UiDTO getOpintojenLaajuus() {
        return opintojenLaajuus;
    }

    /**
     * @param opintojenLaajuus the opintojenLaajuus to set
     */
    public void setOpintojenLaajuus(UiDTO opintojenLaajuus) {
        this.opintojenLaajuus = opintojenLaajuus;
    }

    /**
     * @return the koulutuksenAlkamisPvm
     */
    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    /**
     * @param koulutuksenAlkamisPvm the koulutuksenAlkamisPvm to set
     */
    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    /**
     * @return the suunniteltuKesto
     */
    public SuunniteltuKestoDTO getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    /**
     * @param suunniteltuKesto the suunniteltuKesto to set
     */
    public void setSuunniteltuKesto(SuunniteltuKestoDTO suunniteltuKesto) {
        this.suunniteltuKesto = suunniteltuKesto;
    }

    /**
     * @return the ammattinimikkeet
     */
    public UiMetaDTO getAmmattinimikkeet() {
        return ammattinimikkeet;
    }

    /**
     * @param ammattinimikkeet the ammattinimikkeet to set
     */
    public void setAmmattinimikkeet(UiMetaDTO ammattinimikkeet) {
        this.ammattinimikkeet = ammattinimikkeet;
    }

    /**
     * @return the hinta
     */
    public Double getHinta() {
        return hinta;
    }

    /**
     * @param hinta the hinta to set
     */
    public void setHinta(Double hinta) {
        this.hinta = hinta;
    }
}
