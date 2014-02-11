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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mlyly
 */
@ApiModel(value = "Korkeakoulutuksen luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KoulutusKorkeakouluV1RDTO extends KoulutusV1RDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Koulutusmoduulin totetuksen yksilöivä tunniste")
    private String komotoOid;

    @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduuliin")
    private String parentKomoOid;

    @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduulin toteutukseen")
    private String parentKomotoOid;

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

    @ApiModelProperty(value = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO aihees;

    @ApiModelProperty(value = "Koulutuksen alkamiskausi koodisto koodi uri, jos ei määritetty ainakin yksi alkamispvm pitää olla valittuna")
    private KoodiV1RDTO koulutuksenAlkamiskausi;

    @ApiModelProperty(value = "Koulutuksen alkamisvuosi, jos ei määritetty ainakin yksi alkamispvm pitää olla valittuna")
    private Integer koulutuksenAlkamisvuosi;

    @ApiModelProperty(value = "Koulutuksen alkamispvm, voi olla tyhjä, jos tyhjä alkamiskausi ja alkamisvuosi pitää olla valittuna")
    private Set<Date> koulutuksenAlkamisPvms;

    @ApiModelProperty(value = "Koulutuksen ammattinimikkeet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO ammattinimikkeet;

    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;
    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi")
    private Double hinta;

    @ApiModelProperty(value = "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;

    @ApiModelProperty(value = "Opintojen laajuuden arvo", required = true)
    private KoodiV1RDTO opintojenLaajuusarvo;

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

        if (opetusAikas == null) {
            opetusAikas = new KoodiUrisV1RDTO();
        }

        return opetusAikas;
    }

    public void setOpetusAikas(KoodiUrisV1RDTO opetusAikas) {
        this.opetusAikas = opetusAikas;
    }

    public KoodiUrisV1RDTO getOpetusPaikkas() {
        if (opetusPaikkas == null) {
            opetusPaikkas = new KoodiUrisV1RDTO();
        }

        return opetusPaikkas;
    }

    public void setOpetusPaikkas(KoodiUrisV1RDTO opetusPaikkas) {
        this.opetusPaikkas = opetusPaikkas;
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

    /**
     * @return the koulutuksenAlkamisPvms
     */
    public Set<Date> getKoulutuksenAlkamisPvms() {
        if (koulutuksenAlkamisPvms == null) {
            koulutuksenAlkamisPvms = new HashSet<Date>();
        }

        return koulutuksenAlkamisPvms;
    }

    /**
     * @param koulutuksenAlkamisPvms the koulutuksenAlkamisPvms to set
     */
    public void setKoulutuksenAlkamisPvms(Set<Date> koulutuksenAlkamisPvms) {
        this.koulutuksenAlkamisPvms = koulutuksenAlkamisPvms;
    }

    /**
     * @return the koulutuksenAlkamiskausi
     */
    public KoodiV1RDTO getKoulutuksenAlkamiskausi() {
        return koulutuksenAlkamiskausi;
    }

    /**
     * @param koulutuksenAlkamiskausi the koulutuksenAlkamiskausi to set
     */
    public void setKoulutuksenAlkamiskausi(KoodiV1RDTO koulutuksenAlkamiskausi) {
        this.koulutuksenAlkamiskausi = koulutuksenAlkamiskausi;
    }

    /**
     * @return the koulutuksenAlkamisvuosi
     */
    public Integer getKoulutuksenAlkamisvuosi() {
        return koulutuksenAlkamisvuosi;
    }

    /**
     * @param koulutuksenAlkamisvuosi the koulutuksenAlkamisvuosi to set
     */
    public void setKoulutuksenAlkamisvuosi(Integer koulutuksenAlkamisvuosi) {
        this.koulutuksenAlkamisvuosi = koulutuksenAlkamisvuosi;
    }

    public String getKomotoOid() {
        return komotoOid;
    }

    public void setKomotoOid(String _komotoOid) {
        this.komotoOid = _komotoOid;
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
     * @return the opintojenLaajuusarvo
     */
    public KoodiV1RDTO getOpintojenLaajuusarvo() {
        return opintojenLaajuusarvo;
    }

    /**
     * @param opintojenLaajuusarvo the opintojenLaajuusarvo to set
     */
    public void setOpintojenLaajuusarvo(KoodiV1RDTO opintojenLaajuusarvo) {
        this.opintojenLaajuusarvo = opintojenLaajuusarvo;
    }
}
