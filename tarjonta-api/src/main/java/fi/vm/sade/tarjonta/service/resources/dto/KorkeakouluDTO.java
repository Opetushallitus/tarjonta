/*
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
package fi.vm.sade.tarjonta.service.resources.dto;

import java.util.Date;

/**
 *
 * @author Jani Wilén
 */
public class KorkeakouluDTO extends ToteutusDTO {

    private UiListDTO koulutusohjelma;
    private static final long serialVersionUID = 1L;
    private String tunniste; //tutkinto-ohjelman tunniste
    /*
     * Other user selected form input data
     */
    private UiDTO suunniteltuKesto;
    private UiDTO suunniteltuKestoTyyppi;
    private UiListDTO opetuskielis;
    private UiListDTO opetusmuodos;
    /*
     * KK
     */
    private Boolean opintojenMaksullisuus;
    private UiListDTO pohjakoulutusvaatimukset;
    private UiListDTO teemas;
    private UiDTO opintojenLaajuus;
    private Date koulutuksenAlkamisPvm;

    public KorkeakouluDTO() {
    }

    /**
     * @return the koulutusohjelma
     */
    public UiListDTO getKoulutusohjelma() {
        return koulutusohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setKoulutusohjelma(UiListDTO koulutusohjelma) {
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
    public UiListDTO getOpetuskielis() {
        return opetuskielis;
    }

    /**
     * @param opetuskielis the opetuskielis to set
     */
    public void setOpetuskielis(UiListDTO opetuskielis) {
        this.opetuskielis = opetuskielis;
    }

    /**
     * @return the opetusmuodos
     */
    public UiListDTO getOpetusmuodos() {
        return opetusmuodos;
    }

    /**
     * @param opetusmuodos the opetusmuodos to set
     */
    public void setOpetusmuodos(UiListDTO opetusmuodos) {
        this.opetusmuodos = opetusmuodos;
    }

    /**
     * @return the pohjakoulutusvaatimukset
     */
    public UiListDTO getPohjakoulutusvaatimukset() {
        return pohjakoulutusvaatimukset;
    }

    /**
     * @param pohjakoulutusvaatimukset the pohjakoulutusvaatimukset to set
     */
    public void setPohjakoulutusvaatimukset(UiListDTO pohjakoulutusvaatimukset) {
        this.pohjakoulutusvaatimukset = pohjakoulutusvaatimukset;
    }

    /**
     * @return the teemas
     */
    public UiListDTO getTeemas() {
        return teemas;
    }

    /**
     * @param teemas the teemas to set
     */
    public void setTeemas(UiListDTO teemas) {
        this.teemas = teemas;
    }

    /**
     * @return the suunniteltuKestoTyyppi
     */
    public UiDTO getSuunniteltuKestoTyyppi() {
        return suunniteltuKestoTyyppi;
    }

    /**
     * @param suunniteltuKestoTyyppi the suunniteltuKestoTyyppi to set
     */
    public void setSuunniteltuKestoTyyppi(UiDTO suunniteltuKestoTyyppi) {
        this.suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
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
     * @return the suunniteltuKesto
     */
    public UiDTO getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    /**
     * @param suunniteltuKesto the suunniteltuKesto to set
     */
    public void setSuunniteltuKesto(UiDTO suunniteltuKesto) {
        this.suunniteltuKesto = suunniteltuKesto;
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
}
