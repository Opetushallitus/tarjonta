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
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/**
 *
 * @author Jani
 */
@ApiModel(value = "Ysittäisen koulutusmoduulin luontiin ja tiedon hakemiseen käytettävä rajapintaolio")
public class KomoV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Koulutusmoduulin yksilöivä tunniste")
    private String komoOid;

    @ApiModelProperty(value = "Tarjoaja tai organisaation johon koulutus on liitetty", required = true)
    private OrganisaatioV1RDTO organisaatio;

    @ApiModelProperty(value = "Tutkinto-ohjelman tunniste, oppilaitoksen oma tunniste järjestettävälle koulutukselle", required = true)
    private String tunniste;

    @ApiModelProperty(value = "Tutkinto-ohjelman nimi monella kielella, ainakin yksi kieli pitää olla täytetty", required = false)
    private NimiV1RDTO koulutusohjelma;

    //OTHER DATA
    @ApiModelProperty(value = "Moduulin julkaisun tila", required = true) // allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU"
    private TarjontaTila tila;
    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin tyyppi", required = true)
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    @ApiModelProperty(value = "Koulutuksen koulutusastetyyppi", required = true)
    private KoulutusasteTyyppi koulutusasteTyyppi;

    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin monikieliset kuvaustekstit")
    private KuvausV1RDTO<KomoTeksti> kuvausKomo;

    @ApiModelProperty(value = "Koulutuksen suunntellun keston arvo", required = true)
    private String suunniteltuKestoArvo;
    @ApiModelProperty(value = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)", required = true)
    private KoodiV1RDTO suunniteltuKestoTyyppi;

    @ApiModelProperty(value = "OPH oppilaitostyyppi-koodit (vain ammatillisella- ja lukio-koulutuksella) Huom! Tieto saattaa poistu tulevissa versioissa-", required = true)
    private KoodiUrisV1RDTO oppilaitostyyppis;

    @ApiModelProperty(value = "OPH tutkintonimike-koodit (korkeakoulutuksella eri koodistot kuin ammatillisella- ja lukio-koulutuksella)", required = true)
    private KoodiUrisV1RDTO tutkintonimikes;

    @ApiModelProperty(value = "Opintojen laajuuden arvo", required = true)
    private KoodiV1RDTO opintojenLaajuusarvo;

    @ApiModelProperty(value = "OPH koulutustyyppi-koodit", required = false)
    private KoodiUrisV1RDTO koulutustyyppis;

    @ApiModelProperty(value = "lukiolinja-koodi", required = true)
    private KoodiV1RDTO lukiolinja;

    @ApiModelProperty(value = "osaamisala-koodi", required = true)
    private KoodiV1RDTO osaamisala;

    public KomoV1RDTO() {
    }

    /**
     * @return the oppilaitostyyppis
     */
    public KoodiUrisV1RDTO getOppilaitostyyppis() {
        if (oppilaitostyyppis == null) {
            oppilaitostyyppis = new KoodiUrisV1RDTO();
        }

        return oppilaitostyyppis;
    }

    /**
     * @param oppilaitostyyppis the oppilaitostyyppis to set
     */
    public void setOppilaitostyyppis(KoodiUrisV1RDTO oppilaitostyyppis) {
        this.oppilaitostyyppis = oppilaitostyyppis;
    }

    /**
     * @return the tutkintonimikes
     */
    public KoodiUrisV1RDTO getTutkintonimikes() {
        if (tutkintonimikes == null) {
            tutkintonimikes = new KoodiUrisV1RDTO();
        }

        return tutkintonimikes;
    }

    /**
     * @param tutkintonimikes the tutkintonimikes to set
     */
    public void setTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes) {
        this.tutkintonimikes = tutkintonimikes;
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

    /**
     * @return the komoOid
     */
    public String getKomoOid() {
        return komoOid;
    }

    /**
     * @param komoOid the komoOid to set
     */
    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
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
     * @return the tila
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the koulutusmoduuliTyyppi
     */
    public KoulutusmoduuliTyyppi getKoulutusmoduuliTyyppi() {
        return koulutusmoduuliTyyppi;
    }

    /**
     * @param koulutusmoduuliTyyppi the koulutusmoduuliTyyppi to set
     */
    public void setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi koulutusmoduuliTyyppi) {
        this.koulutusmoduuliTyyppi = koulutusmoduuliTyyppi;
    }

    /**
     * @return the koulutusasteTyyppi
     */
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    /**
     * @param koulutusasteTyyppi the koulutusasteTyyppi to set
     */
    public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    /**
     * @return the kuvausKomo
     */
    public KuvausV1RDTO<KomoTeksti> getKuvausKomo() {
        if (kuvausKomo == null) {
            kuvausKomo = new KuvausV1RDTO<KomoTeksti>();
        }

        return kuvausKomo;
    }

    /**
     * @param kuvausKomo the kuvausKomo to set
     */
    public void setKuvausKomo(KuvausV1RDTO<KomoTeksti> kuvausKomo) {
        this.kuvausKomo = kuvausKomo;
    }

    /**
     * @return the suunniteltuKestoArvo
     */
    public String getSuunniteltuKestoArvo() {
        return suunniteltuKestoArvo;
    }

    /**
     * @param suunniteltuKestoArvo the suunniteltuKestoArvo to set
     */
    public void setSuunniteltuKestoArvo(String suunniteltuKestoArvo) {
        this.suunniteltuKestoArvo = suunniteltuKestoArvo;
    }

    /**
     * @return the suunniteltuKestoTyyppi
     */
    public KoodiV1RDTO getSuunniteltuKestoTyyppi() {
        return suunniteltuKestoTyyppi;
    }

    /**
     * @param suunniteltuKestoTyyppi the suunniteltuKestoTyyppi to set
     */
    public void setSuunniteltuKestoTyyppi(KoodiV1RDTO suunniteltuKestoTyyppi) {
        this.suunniteltuKestoTyyppi = suunniteltuKestoTyyppi;
    }

    /**
     * @return the koulutusohjelma
     */
    public NimiV1RDTO getKoulutusohjelma() {
        return koulutusohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setKoulutusohjelma(NimiV1RDTO koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
    }

    /**
     * @return the organisaatio
     */
    public OrganisaatioV1RDTO getOrganisaatio() {
        if (organisaatio == null) {
            organisaatio = new OrganisaatioV1RDTO();
        }

        return organisaatio;
    }

    /**
     * @param organisaatio the organisaatio to set
     */
    public void setOrganisaatio(OrganisaatioV1RDTO organisaatio) {
        this.organisaatio = organisaatio;
    }

    /**
     * @return the koulutustyyppis
     */
    public KoodiUrisV1RDTO getKoulutustyyppis() {
        if (koulutustyyppis == null) {
            koulutustyyppis = new KoodiUrisV1RDTO();
        }

        return koulutustyyppis;
    }

    /**
     * @param koulutustyyppis the koulutustyyppis to set
     */
    public void setKoulutustyyppis(KoodiUrisV1RDTO koulutustyyppis) {
        this.koulutustyyppis = koulutustyyppis;
    }

    /**
     * @return the lukiolinja
     */
    public KoodiV1RDTO getLukiolinja() {
        return lukiolinja;
    }

    /**
     * @param lukiolinja the lukiolinja to set
     */
    public void setLukiolinja(KoodiV1RDTO lukiolinja) {
        this.lukiolinja = lukiolinja;
    }

    /**
     * @return the osaamisala
     */
    public KoodiV1RDTO getOsaamisala() {

        return osaamisala;
    }

    /**
     * @param osaamisala the osaamisala to set
     */
    public void setOsaamisala(KoodiV1RDTO osaamisala) {
        this.osaamisala = osaamisala;
    }

}
