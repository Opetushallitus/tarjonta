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
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author mlyly
 */
@ApiModel(value = "Koulutuksien yleiset tiedot sisältä rajapintaolio")
public abstract class KoulutusV1RDTO extends KoulutusmoduuliRelationV1RDTO {

    @ApiModelProperty(value = "Koulutusmoduulin yksilöivä tunniste")
    private String komoOid;
    @ApiModelProperty(value = "Koulutusmoduulin totetuksen yksilöivä tunniste")
    private String komotoOid;
    @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduuliin")
    private String parentKomoOid;
    @ApiModelProperty(value = "Suhde hierarkian parent koulutusmoduulin toteutukseen")
    private String parentKomotoOid;

    @ApiModelProperty(value = "Tarjoaja tai organisaation johon koulutus on liitetty", required = true)
    private OrganisaatioV1RDTO organisaatio;

    //OTHER DATA
    @ApiModelProperty(value = "Koulutuksen julkaisun tila", required = true) // allowableValues = "LUONNOS,VALMIS,JULKAISTU,PERUTTU,KOPIOITU"
    private TarjontaTila tila;
    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin tyyppi", required = true)
    private KoulutusmoduuliTyyppi koulutusmoduuliTyyppi;
    @ApiModelProperty(value = "Koulutuksen koulutusastetyyppi", required = true)
    private KoulutusasteTyyppi koulutusasteTyyppi;

    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin monikieliset kuvaustekstit")
    private KuvausV1RDTO<KomoTeksti> kuvausKomo;
    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit")
    private KuvausV1RDTO<KomotoTeksti> kuvausKomoto;

    @ApiModelProperty(value = "Koulutuksen suunntellun keston arvo", required = true)
    private String suunniteltuKestoArvo;
    @ApiModelProperty(value = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)", required = true)
    private KoodiV1RDTO suunniteltuKestoTyyppi;

    public KoulutusV1RDTO() {
    }

    public String getKomoOid() {
        return komoOid;
    }

    public void setKomoOid(String _komoOid) {
        this.komoOid = _komoOid;
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

}
