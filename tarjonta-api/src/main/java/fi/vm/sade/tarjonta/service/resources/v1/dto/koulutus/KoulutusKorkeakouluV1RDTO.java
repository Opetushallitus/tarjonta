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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.util.Date;

/**
 *
 * @author mlyly
 */
public class KoulutusKorkeakouluV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;
    private NimiV1RDTO koulutusohjelma;

    private String tunniste; //tutkinto-ohjelman tunniste
    /*
     * Other user selected form input data
     */

    private KoodiUrisV1RDTO opetuskielis;
    private KoodiUrisV1RDTO opetusmuodos;
    /*
     * KK
     */
    private Boolean opintojenMaksullisuus;
    private KoodiUrisV1RDTO pohjakoulutusvaatimukset;
    private KoodiUrisV1RDTO teemas;
    private Date koulutuksenAlkamisPvm;
    private KoodiUrisV1RDTO ammattinimikkeet;
    private Double hinta;

    public KoulutusKorkeakouluV1RDTO() {
    }

    /**
     * @return the koulutusohjelma
     */
    public NimiV1RDTO getKoulutusohjelma() {
        if(koulutusohjelma == null){
            koulutusohjelma = new NimiV1RDTO();
        }
        
        return koulutusohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setKoulutusohjelma(NimiV1RDTO koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
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
     * @return the opetuskielis
     */
    public KoodiUrisV1RDTO getOpetuskielis() {
        if (opetuskielis == null) {
            opetuskielis = new KoodiUrisV1RDTO();
        }

        return opetuskielis;
    }

    /**
     * @param opetuskielis the opetuskielis to set
     */
    public void setOpetuskielis(KoodiUrisV1RDTO opetuskielis) {
        this.opetuskielis = opetuskielis;
    }

    /**
     * @return the opetusmuodos
     */
    public KoodiUrisV1RDTO getOpetusmuodos() {
        if (opetusmuodos == null) {
            opetusmuodos = new KoodiUrisV1RDTO();
        }

        return opetusmuodos;
    }

    /**
     * @param opetusmuodos the opetusmuodos to set
     */
    public void setOpetusmuodos(KoodiUrisV1RDTO opetusmuodos) {
        this.opetusmuodos = opetusmuodos;
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
     * @return the pohjakoulutusvaatimukset
     */
    public KoodiUrisV1RDTO getPohjakoulutusvaatimukset() {
        if (pohjakoulutusvaatimukset == null) {
            pohjakoulutusvaatimukset = new KoodiUrisV1RDTO();
        }

        return pohjakoulutusvaatimukset;
    }

    /**
     * @param pohjakoulutusvaatimukset the pohjakoulutusvaatimukset to set
     */
    public void setPohjakoulutusvaatimukset(KoodiUrisV1RDTO pohjakoulutusvaatimukset) {
        this.pohjakoulutusvaatimukset = pohjakoulutusvaatimukset;
    }

    /**
     * @return the teemas
     */
    public KoodiUrisV1RDTO getTeemas() {
        if (teemas == null) {
            teemas = new KoodiUrisV1RDTO();
        }

        return teemas;
    }

    /**
     * @param teemas the teemas to set
     */
    public void setTeemas(KoodiUrisV1RDTO teemas) {
        this.teemas = teemas;
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
     * @return the ammattinimikkeet
     */
    public KoodiUrisV1RDTO getAmmattinimikkeet() {
        if (ammattinimikkeet == null) {
            ammattinimikkeet = new KoodiUrisV1RDTO();
        }

        return ammattinimikkeet;
    }

    /**
     * @param ammattinimikkeet the ammattinimikkeet to set
     */
    public void setAmmattinimikkeet(KoodiUrisV1RDTO ammattinimikkeet) {
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
