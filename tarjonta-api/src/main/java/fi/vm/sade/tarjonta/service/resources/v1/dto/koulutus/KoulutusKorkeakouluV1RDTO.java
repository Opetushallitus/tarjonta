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

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;


/**
 *
 * @author mlyly
 */
@ApiModel(value = "Korkeakoulutuksen luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KoulutusKorkeakouluV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduuliin")
    private String parentKomoOid;

    @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduulin toteutukseen")
    private String parentKomotoOid;

    @ApiModelProperty(value = "Koulutuksen pohjakoulutusvaatimukset (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO pohjakoulutusvaatimukset;

    @ApiModelProperty(value = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO aihees;

    @ApiModelProperty(value = "Koulutuksen ammattinimikkeet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO ammattinimikkeet;

    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;
    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi")
    private Double hinta;

    @ApiModelProperty(value = "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;


    @ApiModelProperty(value = "Maisterin koulutukseen (maisteri+kandi) liitettävän kandidaatin koulutuksen koulutuskoodi", required = false)
    private KoodiV1RDTO kandidaatinKoulutuskoodi;

    /**
     * @return the tutkintonimike
     */
    public KoodiUrisV1RDTO getTutkintonimikes() {
        if (this.tutkintonimikes == null) {
            this.tutkintonimikes = new KoodiUrisV1RDTO();
        }

        return tutkintonimikes;
    }

    /**
     * @param tutkintonimikes the tutkintonimikes to set
     */
    public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
        this.tutkintonimikes = tutkintonimikes;
    }

    public KoulutusKorkeakouluV1RDTO() {
        super(KoulutusasteTyyppi.KORKEAKOULUTUS);
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

    public KoodiUrisV1RDTO getAihees() {
        if (aihees == null) {
            aihees = new KoodiUrisV1RDTO();
        }

        return aihees;
    }

    public void setAihees(KoodiUrisV1RDTO aihees) {
        this.aihees = aihees;
    }

    public String getParentKomoOid() {
        return parentKomoOid;
    }

    public void setParentKomoOid(String _parentKomoOid) {
        this.parentKomoOid = _parentKomoOid;
    }

    public String getParentKomotoOid() {
        return parentKomotoOid;
    }

    public void setParentKomotoOid(String _parentKomotoOid) {
        this.parentKomotoOid = _parentKomotoOid;
    }

 

    /**
     * @return the kandidaatinKoulutuskoodi
     */
    public KoodiV1RDTO getKandidaatinKoulutuskoodi() {
        return kandidaatinKoulutuskoodi;
    }

    /**
     * @param kandidaatinKoulutuskoodi the kandidaatinKoulutuskoodi to set
     */
    public void setKandidaatinKoulutuskoodi(KoodiV1RDTO kandidaatinKoulutuskoodi) {
        this.kandidaatinKoulutuskoodi = kandidaatinKoulutuskoodi;
    }
}
