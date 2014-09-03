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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author jwilen
 */
@ApiModel(value = "Koulutuksien yleiset tiedot sisältävä rajapintaolio")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "toteutustyyppi")
@JsonSubTypes({
    @Type(value = AmmattitutkintoV1RDTO.class, name = "AMMATTITUTKINTO"),
    @Type(value = ErikoisammattitutkintoV1RDTO.class, name = "ERIKOISAMMATTITUTKINTO"),
    @Type(value = KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO.class, name = "AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA"),
    @Type(value = KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class, name = "AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA"),
    @Type(value = KoulutusAmmatillinenPerustutkintoV1RDTO.class, name = "AMMATILLINEN_PERUSTUTKINTO"),
    @Type(value = KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO.class, name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS"),
    @Type(value = KoulutusKorkeakouluV1RDTO.class, name = "KORKEAKOULUTUS"),
    @Type(value = KoulutusLukioAikuistenOppimaaraV1RDTO.class, name = "LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA"),
    @Type(value = KoulutusLukioV1RDTO.class, name = "LUKIOKOULUTUS"),
    @Type(value = KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO.class, name = "MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS"),
    @Type(value = KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO.class, name = "MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS"),
    @Type(value = KoulutusPerusopetuksenLisaopetusV1RDTO.class, name = "PERUSOPETUKSEN_LISAOPETUS"),
    @Type(value = KoulutusValmentavaJaKuntouttavaV1RDTO.class, name = "VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS"),
    @Type(value = KoulutusVapaanSivistystyonV1RDTO.class, name = "VAPAAN_SIVISTYSTYON_KOULUTUS")
})
public abstract class KoulutusV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

    @ApiModelProperty(value = "Koulutuksen toteutuksen tarkasti yksiloiva enumeraatio", required = true)
    @JsonTypeId
    private final ToteutustyyppiEnum toteutustyyppi;

    @ApiModelProperty(value = "Koulutusmoduulin karkeasti yksilöivä enumeraatio", required = true)
    private ModuulityyppiEnum moduulityyppi;

    @ApiModelProperty(value = "Koulutusmoduulin yksilöivä tunniste")
    private String komoOid;

    @ApiModelProperty(value = "Koulutusmoduulin totetuksen yksilöivä tunniste")
    private String komotoOid;

    @ApiModelProperty(value = "Tarjoaja tai organisaation johon koulutus on liitetty", required = true)
    private OrganisaatioV1RDTO organisaatio;

    @ApiModelProperty(value = "Tutkinto-ohjelman nimi monella kielella, ainakin yksi kieli pitää olla täytetty", required = true)
    private NimiV1RDTO koulutusohjelma;

    @ApiModelProperty(value = "Tutkinto-ohjelman tunniste, oppilaitoksen oma tunniste järjestettävälle koulutukselle", required = true)
    private String tunniste;

    //OTHER DATA
    @ApiModelProperty(value = "Koulutuksen julkaisun tila", required = true) // allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU"
    private TarjontaTila tila;

    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin tyyppi", required = true)
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;

    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin monikieliset kuvaustekstit")
    private KuvausV1RDTO<KomoTeksti> kuvausKomo;
    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit")
    private KuvausV1RDTO<KomotoTeksti> kuvausKomoto;

    @ApiModelProperty(value = "Koulutuksen suunntellun keston arvo", required = true)
    private String suunniteltuKestoArvo;
    @ApiModelProperty(value = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)", required = true)
    private KoodiV1RDTO suunniteltuKestoTyyppi;

    @ApiModelProperty(value = "Koulutuksen alkamiskausi koodisto koodi uri, jos ei määritetty ainakin yksi alkamispvm pitää olla valittuna")
    private KoodiV1RDTO koulutuksenAlkamiskausi;

    @ApiModelProperty(value = "Koulutuksen alkamisvuosi, jos ei määritetty ainakin yksi alkamispvm pitää olla valittuna")
    private Integer koulutuksenAlkamisvuosi;

    @ApiModelProperty(value = "Koulutuksen alkamispvm, voi olla tyhjä, jos tyhjä alkamiskausi ja alkamisvuosi pitää olla valittuna")
    private Set<Date> koulutuksenAlkamisPvms;

    @ApiModelProperty(value = "Koulutuksen opetuskielet, ainakin yksi kieli pitää olla syötetty (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetuskielis;

    @ApiModelProperty(value = "Koulutuksen opetusmuodot (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusmuodos;

    @ApiModelProperty(value = "Koulutuksen opetusajat (esim. Iltaopetus) (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusAikas;

    @ApiModelProperty(value = "Koulutuksen opetuspaikat (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusPaikkas;

    @ApiModelProperty(value = "Opintojen laajuuden arvo", required = true)
    private KoodiV1RDTO opintojenLaajuusarvo;

    public KoulutusV1RDTO(ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
        this.toteutustyyppi = toteutustyyppi;
        this.moduulityyppi = moduulityyppi;
    }

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String _komoOid) {
        this.komoOid = _komoOid;
    }

    /*
     * Contact persons
     */
    private Set<YhteyshenkiloTyyppi> yhteyshenkilos;

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
     * @return the yhteyshenkilos
     */
    public Set<YhteyshenkiloTyyppi> getYhteyshenkilos() {
        if (yhteyshenkilos == null) {
            yhteyshenkilos = new HashSet<YhteyshenkiloTyyppi>();
        }
        return yhteyshenkilos;
    }

    /**
     * @param yhteyshenkilos the yhteyshenkilos to set
     */
    public void setYhteyshenkilos(Set<YhteyshenkiloTyyppi> yhteyshenkilos) {
        this.yhteyshenkilos = yhteyshenkilos;
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    /**
     * Will be removed in the future, use the moduulityyppi or toteutustyyppi
     * enum.
     *
     * @return the koulutusasteTyyppi
     */
    @Deprecated
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        if (moduulityyppi != null) {
            return moduulityyppi.getKoulutusasteTyyppi();
        }
        return null;
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
     * @return the kuvausKomoto
     */
    public KuvausV1RDTO<KomotoTeksti> getKuvausKomoto() {
        if (kuvausKomoto == null) {
            kuvausKomoto = new KuvausV1RDTO<KomotoTeksti>();
        }
        return kuvausKomoto;
    }

    /**
     * @param kuvausKomoto the kuvausKomoto to set
     */
    public void setKuvausKomoto(KuvausV1RDTO<KomotoTeksti> kuvausKomoto) {
        this.kuvausKomoto = kuvausKomoto;
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

    public String getKomotoOid() {
        return komotoOid;
    }

    public void setKomotoOid(String _komotoOid) {
        this.komotoOid = _komotoOid;
    }

    /**
     * @return the toteutustyyppi
     */
    public ToteutustyyppiEnum getToteutustyyppi() {
        return toteutustyyppi;
    }

    /**
     * @return the moduulityyppi
     */
    public ModuulityyppiEnum getModuulityyppi() {
        return moduulityyppi;
    }

    /**
     * @param moduulityyppi the moduulityyppi to set
     */
    public void setModuulityyppi(ModuulityyppiEnum moduulityyppi) {
        this.moduulityyppi = moduulityyppi;
    }

}
