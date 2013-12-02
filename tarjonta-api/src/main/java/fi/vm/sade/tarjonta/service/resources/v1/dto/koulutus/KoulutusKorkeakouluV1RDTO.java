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

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 *
 * @author mlyly
 */
@ApiModel(value = "Korkeakoulutuksen luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KoulutusKorkeakouluV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Tutkinto-ohjelman nimi monella kielella, ainakin yksi kieli pitää olla täytetty", required = true)
    private NimiV1RDTO koulutusohjelma;

    @ApiModelProperty(value = "Tutkinto-ohjelman tunniste, oppilaitoksen oma tunniste järjestettävälle koulutukselle", required = true)
    private String tunniste;

    @ApiModelProperty(value = "Koulutuksen opetuskielet, ainakin yksi kieli pitää olla syötetty (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetuskielis;

    @ApiModelProperty(value = "Koulutuksen opetusmuodot (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusmuodos;

    @ApiModelProperty(value = "Koulutuksen opetusajat (esim. Iltaopetus) (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusAikas;

    @ApiModelProperty(value = "Koulutuksen opetuspaikat (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusPaikkas;

    @ApiModelProperty(value = "Koulutuksen pohjakoulutusvaatimukset (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO pohjakoulutusvaatimukset;

    @ApiModelProperty(value = "Koulutuksen teemat (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO teemas;

    @ApiModelProperty(value = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO aihees;

    @ApiModelProperty(value = "Koulutuksen alkamispvm", required = true)
    private Date koulutuksenAlkamisPvm;
    @ApiModelProperty(value = "Koulutuksen ammattinimikkeet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO ammattinimikkeet;

    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;
    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi")
    private Double hinta;

    public KoulutusKorkeakouluV1RDTO() {
    }

    /**
     * @return the koulutusohjelma
     */
    public NimiV1RDTO getKoulutusohjelma() {
        if (koulutusohjelma == null) {
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

    public KoodiUrisV1RDTO getOpetusAikas() {
        return opetusAikas;
    }

    public void setOpetusAikas(KoodiUrisV1RDTO opetusAikas) {
        this.opetusAikas = opetusAikas;
    }

    public KoodiUrisV1RDTO getOpetusPaikkas() {
        return opetusPaikkas;
    }

    public void setOpetusPaikkas(KoodiUrisV1RDTO opetusPaikkas) {
        this.opetusPaikkas = opetusPaikkas;
    }

    public KoodiUrisV1RDTO getAihees() {
        return aihees;
    }

    public void setAihees(KoodiUrisV1RDTO aihees) {
        this.aihees = aihees;
    }
}
