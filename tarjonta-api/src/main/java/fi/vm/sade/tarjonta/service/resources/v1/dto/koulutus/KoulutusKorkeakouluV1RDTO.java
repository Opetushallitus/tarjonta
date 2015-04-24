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

import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import java.util.HashMap;
import java.util.Map;

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

    @ApiModelProperty(value = "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;

    @ApiModelProperty(value = "Maisterin koulutukseen (maisteri+kandi) liitettävän kandidaatin koulutuksen koulutuskoodi", required = false)
    private KoodiV1RDTO kandidaatinKoulutuskoodi;

    @ApiModelProperty(value = "Opintojen rakenteen kuvat eroteltuna kooditon kieli uri:lla.", required = false)
    private Map<String, KuvaV1RDTO> opintojenRakenneKuvas;

    @ApiModelProperty(value = "tunniste, joka yksilöi KK-koulutuksen (aiemmin tähän käytettiin komonOidia, katso KJOH-973)")
    private String koulutuksenTunnisteOid;

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
        super(ToteutustyyppiEnum.KORKEAKOULUTUS, ModuulityyppiEnum.KORKEAKOULUTUS);
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

    /**
     * @return the opintojenRakenneKuvas
     */
    public Map<String, KuvaV1RDTO> getOpintojenRakenneKuvas() {
        if (opintojenRakenneKuvas == null) {
            opintojenRakenneKuvas = new HashMap<String, KuvaV1RDTO>();
        }

        return opintojenRakenneKuvas;
    }

    /**
     * @param opintojenRakenneKuvas the opintojenRakenneKuvas to set
     */
    public void setOpintojenRakenneKuvas(Map<String, KuvaV1RDTO> opintojenRakenneKuvas) {
        this.opintojenRakenneKuvas = opintojenRakenneKuvas;
    }

    public String getKoulutuksenTunnisteOid() {
        return koulutuksenTunnisteOid;
    }

    public void setKoulutuksenTunnisteOid(String koulutuksenTunnisteOid) {
        this.koulutuksenTunnisteOid = koulutuksenTunnisteOid;
    }
}
