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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.valmistava;

import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author jwilen
 */
@ApiModel(value = "Valmistavan osan tiedot sisältävä rajapintaolio")
public class ValmistavaV1RDTO implements Serializable {

    @ApiModelProperty(value = "Koulutuksen koulutusmoduulin toteutuksen monikieliset kuvaustekstit", required = false)
    private KuvausV1RDTO<KomotoTeksti> kuvaus;

    @ApiModelProperty(value = "Koulutuksen suunntellun keston arvo", required = true)
    private String suunniteltuKestoArvo;
    @ApiModelProperty(value = "Koulutuksen suunntellun keston tyyppi (koodisto koodi uri)", required = true)
    private KoodiV1RDTO suunniteltuKestoTyyppi;

    @ApiModelProperty(value = "Koulutuksen hinta (korvaa vanhan Double-tyyppisen hinnan, koska pitää tukea myös muita kun numeroita)")
    private String hintaString;

    @ApiModelProperty(value = "Koulutuksen hinta, on pakollinen jos koulutus on merkitty maksulliseksi", required = false)
    private Double hinta;

    @ApiModelProperty(value = "Valitaan opintojen maksullisuuden (false=koulutus ei vaadi maksua)")
    private Boolean opintojenMaksullisuus;

    @ApiModelProperty(value = "HTTP-linkki opetussuunnitelmaan", required = false)
    private String linkkiOpetussuunnitelmaan;

    @ApiModelProperty(value = "Koulutuksen opetusmuodot (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusmuodos;

    @ApiModelProperty(value = "Koulutuksen opetusajat (esim. Iltaopetus) (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusAikas;

    @ApiModelProperty(value = "Koulutuksen opetuspaikat (sisältää koodisto koodi uri:a)", required = true)
    private KoodiUrisV1RDTO opetusPaikkas;

    public ValmistavaV1RDTO() {

    }

    public void setHintaString(String hintaString) {
        this.hintaString = hintaString;
    }

    public String getHintaString() {
        return hintaString;
    }

    /*
     * Contact persons
     */
    private Set<YhteyshenkiloTyyppi> yhteyshenkilos;

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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the kuvausKomoto
     */
    public KuvausV1RDTO<KomotoTeksti> getKuvaus() {
        if (kuvaus == null) {
            kuvaus = new KuvausV1RDTO<KomotoTeksti>();
        }
        return kuvaus;
    }

    /**
     * @param kuvaus the kuvausKomoto to set
     */
    public void setKuvaus(KuvausV1RDTO<KomotoTeksti> kuvaus) {
        this.kuvaus = kuvaus;
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
     * @return the linkkiOpetussuunnitelmaan
     */
    public String getLinkkiOpetussuunnitelmaan() {
        return linkkiOpetussuunnitelmaan;
    }

    /**
     * @param linkkiOpetussuunnitelmaan the linkkiOpetussuunnitelmaan to set
     */
    public void setLinkkiOpetussuunnitelmaan(String linkkiOpetussuunnitelmaan) {
        this.linkkiOpetussuunnitelmaan = linkkiOpetussuunnitelmaan;
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
}
