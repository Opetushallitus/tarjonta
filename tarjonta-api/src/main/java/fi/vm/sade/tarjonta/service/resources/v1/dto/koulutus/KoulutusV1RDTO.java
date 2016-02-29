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
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OppiaineV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jwilen
 */
@ApiModel(value = "Koulutuksien yleiset tiedot sisältävä rajapintaolio")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "toteutustyyppi", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
    @Type(value = AmmattitutkintoV1RDTO.class, name = "AMMATTITUTKINTO"),
    @Type(value = ErikoisammattitutkintoV1RDTO.class, name = "ERIKOISAMMATTITUTKINTO"),
    @Type(value = KoulutusAmmatillinenPeruskoulutusErityisopetuksenaV1RDTO.class, name = "AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA"),
    @Type(value = KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.class, name = "AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA"),
    @Type(value = KoulutusAmmatillinenPerustutkintoV1RDTO.class, name = "AMMATILLINEN_PERUSTUTKINTO"),
    @Type(value = KoulutusAmmatilliseenPeruskoulutukseenOhjaavaJaValmistavaV1RDTO.class, name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS"),
    @Type(value = KoulutusKorkeakouluV1RDTO.class, name = "KORKEAKOULUTUS"),
    @Type(value = KorkeakouluOpintoV1RDTO.class, name = "KORKEAKOULUOPINTO"),
    @Type(value = KoulutusLukioAikuistenOppimaaraV1RDTO.class, name = "LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA"),
    @Type(value = KoulutusEbRpIshV1RDTO.class, name = "EB_RP_ISH"),
    @Type(value = KoulutusLukioV1RDTO.class, name = "LUKIOKOULUTUS"),
    @Type(value = KoulutusAmmatilliseenPeruskoulutukseenValmentavaV1RDTO.class, name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA"),
    @Type(value = KoulutusAmmatilliseenPeruskoulutukseenValmentavaERV1RDTO.class, name = "AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER"),
    @Type(value = KoulutusMaahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaV1RDTO.class, name = "MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS"),
    @Type(value = KoulutusMaahanmuuttajienJaVieraskielistenLukiokoulutukseenValmistavaV1RDTO.class, name = "MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS"),
    @Type(value = KoulutusPerusopetuksenLisaopetusV1RDTO.class, name = "PERUSOPETUKSEN_LISAOPETUS"),
    @Type(value = KoulutusValmentavaJaKuntouttavaV1RDTO.class, name = "VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS"),
    @Type(value = KoulutusVapaanSivistystyonV1RDTO.class, name = "VAPAAN_SIVISTYSTYON_KOULUTUS"),
    @Type(value = KoulutusAikuistenPerusopetusV1RDTO.class, name = "AIKUISTEN_PERUSOPETUS")
})
public abstract class KoulutusV1RDTO extends KoulutusmoduuliStandardRelationV1RDTO {

    @ApiModelProperty(value = "Koulutuksen toteutuksen tarkasti yksiloiva enumeraatio", required = true)
    private ToteutustyyppiEnum toteutustyyppi;

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

    @ApiModelProperty(value = "Tutkinto-ohjelman tunniste, oppilaitoksen oma tunniste järjestettävälle koulutukselle", required = false)
    private String tunniste;

    @ApiModelProperty(value = "Oppilaitoksen globaalisti uniikki tunniste koulutukselle", required = false)
    private String uniqueExternalId;

    @ApiModelProperty(value = "Hakijalle näytettävä tunniste", required = false)
    private String hakijalleNaytettavaTunniste;

    //OTHER DATA
    @ApiModelProperty(value = "Koulutuksen julkaisun tila", required = true)
    // allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU"
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

    @ApiModelProperty(value = "Opintojen järjestäjät", required = false)
    private Set<String> opetusJarjestajat;

    @ApiModelProperty(value = "Opintojen tarjoajat", required = false)
    private Set<String> opetusTarjoajat;

    @ApiModelProperty(value = "Koulutuksen ammattinimikkeet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO ammattinimikkeet;

    @ApiModelProperty(value = "Koulutuksen aiheet (sisältää koodisto koodi uri:a)")
    private KoodiUrisV1RDTO aihees;

    @ApiModelProperty(value = "Koulutuksen yläpuoliset kouloutukset")
    private Set<String> parents;

    @ApiModelProperty(value = "Koulutuksen lapset")
    private Set<String> children;

    @ApiModelProperty(value = "Koulutuksen hinta (korvaa vanhan Double-tyyppisen hinnan, koska pitää tukea myös muita kun numeroita)")
    private String hintaString;

    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi", required = false)
    private Double hinta;

    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;

    @ApiModelProperty(value = "Onko koulutus avoimen yliopiston/ammattikorkeakoulun koulutus")
    private Boolean isAvoimenYliopistonKoulutus;

    @ApiModelProperty(value = "Oppiaineet")
    private Set<OppiaineV1RDTO> oppiaineet;

    @ApiModelProperty(value = "Opintopolussa näytettävä koulutuksen alkaminen")
    private Map opintopolkuAlkamiskausi;

    @ApiModelProperty(value = "Map-rakenne ylimääräisille parametreille, joita voi tarvittaessa hyödyntää tallennuksen yhteydessä")
    private Map<String, String> extraParams;

    @ApiModelProperty(value = "Koulutukseen sisältyvät koulutuskoodit", required = false)
    private KoodiUrisV1RDTO sisaltyvatKoulutuskoodit;

    @ApiModelProperty(value = "Koulutukset, joihin tämä koulutus sisältyy", required = false)
    private Set<KoulutusIdentification> sisaltyyKoulutuksiin;

    @ApiModelProperty(value = "Koulutuslaji-koodi", required = false)
    private KoodiV1RDTO koulutuslaji;

    public KoulutusV1RDTO(ToteutustyyppiEnum toteutustyyppi, ModuulityyppiEnum moduulityyppi) {
        this.setToteutustyyppi(toteutustyyppi);
        this.moduulityyppi = moduulityyppi;
    }

    // Default constructor for JSON deserializing
    public KoulutusV1RDTO() {
    }

    public void setHintaString(String hintaString) {
        this.hintaString = hintaString;
    }

    public String getHintaString() {
        return hintaString;
    }

    public Double getHinta() {
        return hinta;
    }

    public void setHinta(Double hinta) {
        this.hinta = hinta;
    }

    public Boolean getOpintojenMaksullisuus() {
        return opintojenMaksullisuus;
    }

    public void setOpintojenMaksullisuus(Boolean opintojenMaksullisuus) {
        this.opintojenMaksullisuus = opintojenMaksullisuus;
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
        return organisaatio;
    }

    /**
     * @param organisaatio the organisaatio to set
     */
    public void setOrganisaatio(OrganisaatioV1RDTO organisaatio) {
        this.organisaatio = organisaatio;
    }

    public Boolean getIsAvoimenYliopistonKoulutus() {
        return isAvoimenYliopistonKoulutus;
    }

    public void setIsAvoimenYliopistonKoulutus(Boolean isAvoimenYliopistonKoulutus) {
        this.isAvoimenYliopistonKoulutus = isAvoimenYliopistonKoulutus;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the kuvausKomo
     */
    public KuvausV1RDTO<KomoTeksti> getKuvausKomo() {
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
        return opetusmuodos;
    }

    /**
     * @param opetusmuodos the opetusmuodos to set
     */
    public void setOpetusmuodos(KoodiUrisV1RDTO opetusmuodos) {
        this.opetusmuodos = opetusmuodos;
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
     * @param toteutustyyppi the toteutustyyppi to set
     */
    public void setToteutustyyppi(ToteutustyyppiEnum toteutustyyppi) {
        this.toteutustyyppi = toteutustyyppi;
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

    /**
     * @return list of opetus organisators oids
     */
    public Set<String> getOpetusJarjestajat() {
        return opetusJarjestajat;
    }

    /**
     * Set organisation oids for koulutus organizers.
     *
     * @param opetusJarjestajat
     */
    public void setOpetusJarjestajat(Set<String> opetusJarjestajat) {
        opetusJarjestajat = (opetusJarjestajat != null) ? opetusJarjestajat : new HashSet<String>();
        this.opetusJarjestajat = opetusJarjestajat;
    }

    /**
     * @return "offerer" oids for koulutus
     */
    public Set<String> getOpetusTarjoajat() {
        return opetusTarjoajat;
    }

    /**
     * Set the "offerers" for koulutus.
     *
     * @param opetusTarjoajat list of oids offering the koulutus.
     */
    public void setOpetusTarjoajat(Set<String> opetusTarjoajat) {
        opetusTarjoajat = (opetusTarjoajat != null) ? opetusTarjoajat : new HashSet<String>();
        this.opetusTarjoajat = opetusTarjoajat;
    }


    /**
     * @return the ammattinimikkeet
     */
    public KoodiUrisV1RDTO getAmmattinimikkeet() {
        return ammattinimikkeet;
    }

    /**
     * @param ammattinimikkeet the ammattinimikkeet to set
     */
    public void setAmmattinimikkeet(KoodiUrisV1RDTO ammattinimikkeet) {
        this.ammattinimikkeet = ammattinimikkeet;
    }

    public KoodiUrisV1RDTO getAihees() {
        return aihees;
    }

    public void setAihees(KoodiUrisV1RDTO aihees) {
        this.aihees = aihees;
    }

    public void setParents(Set<String> parents) {
        this.parents = parents;
    }

    public Set<String> getParents() {
        return parents;
    }

    public void setChildren(Set<String> children) {
        this.children = children;
    }

    public Set<String> getChildren() {
        return children;
    }

    public Set<OppiaineV1RDTO> getOppiaineet() {
        return oppiaineet;
    }

    public void setOppiaineet(Set<OppiaineV1RDTO> oppiaineet) {
        this.oppiaineet = oppiaineet;
    }

    public Map getOpintopolkuAlkamiskausi() {
        return opintopolkuAlkamiskausi;
    }

    public void setOpintopolkuAlkamiskausi(Map opintopolkuAlkamiskausi) {
        this.opintopolkuAlkamiskausi = opintopolkuAlkamiskausi;
    }

    public Map<String, String> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(Map<String, String> extraParams) {
        this.extraParams = extraParams;
    }

    public KoodiUrisV1RDTO getSisaltyvatKoulutuskoodit() {
        return sisaltyvatKoulutuskoodit;
    }

    public void setSisaltyvatKoulutuskoodit(KoodiUrisV1RDTO sisaltyvatKoulutuskoodit) {
        this.sisaltyvatKoulutuskoodit = sisaltyvatKoulutuskoodit;
    }

    public String getHakijalleNaytettavaTunniste() {
        return hakijalleNaytettavaTunniste;
    }

    public void setHakijalleNaytettavaTunniste(String hakijalleNaytettavaTunniste) {
        this.hakijalleNaytettavaTunniste = hakijalleNaytettavaTunniste;
    }

    public Set<KoulutusIdentification> getSisaltyyKoulutuksiin() {
        return sisaltyyKoulutuksiin;
    }

    public void setSisaltyyKoulutuksiin(Set<KoulutusIdentification> sisaltyyKoulutuksiin) {
        this.sisaltyyKoulutuksiin = sisaltyyKoulutuksiin;
    }

    public String getUniqueExternalId() {
        return uniqueExternalId;
    }

    public void setUniqueExternalId(String uniqueExternalId) {
        this.uniqueExternalId = uniqueExternalId;
    }

    public KoodiV1RDTO getKoulutuslaji() {
        return koulutuslaji;
    }

    public void setKoulutuslaji(KoodiV1RDTO koulutuslaji) {
        this.koulutuslaji = koulutuslaji;
    }
}
