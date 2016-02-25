/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
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
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
@ApiModel(value = "Koodisto koodi uri:n syötämiseen ja näyttämiseen käytettävä rajapintaolio")
public class KoodiV1RDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "Käytetyn koodisto koodin kieli uri (lisätietoa)")
    private String kieliUri;
    @ApiModelProperty(value = "Käytetyn koodisto koodin kieli uri:n versio (lisätietoa)")
    private Integer kieliVersio;
    @ApiModelProperty(value = "Käytetyn koodisto koodin kieli uri:n iso-kielikoodi (lisätietoa)")
    private String kieliArvo;
    @ApiModelProperty(value = "Käytetyn koodisto koodin kieli uri:n nimen kielikäännös (lisätietoa)")
    private String kieliKaannos;
    @ApiModelProperty(value = "Koodisto koodin uri", required = true)
    private String uri;
    @ApiModelProperty(value = "Koodisto koodin versio, koodisto koodi uri:a syötettäessä pakollinen tieto", required = true)
    private Integer versio;
    @ApiModelProperty(value = "Koodisto koodin uri:n arvo (lisätietoa)")
    private String arvo;
    @ApiModelProperty(value = "Koodisto koodin uri:n nimen kielikäännos (lisätietoa)")
    private String nimi;

    @ApiModelProperty(value = "Monikielisen lisätiedon näyttämiseen tarkoitettu avain-arvopari, jossa avain on koodisto kieli uri ja arvo on rajapintaolio", required = false)
    private Map<String, KoodiV1RDTO> meta;

    public KoodiV1RDTO() {
    }

    public KoodiV1RDTO(String uri, Integer versio, String arvo) {
        this.uri = uri;
        this.versio = versio;
        this.arvo = arvo;
    }

    public KoodiV1RDTO(String uri, Integer versio, String arvo, String nimi) {
        this.uri = uri;
        this.versio = versio;
        this.arvo = arvo;
        this.nimi = nimi;
    }

    public void setKoodi(String uri, Integer versio, String arvo, String nimi) {
        this.uri = uri;
        this.versio = versio;
        this.arvo = arvo;
        this.nimi = nimi;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the versio
     */
    public Integer getVersio() {
        return versio;
    }

    /**
     * @param version the version to set
     */
    public void setVersio(Integer versio) {
        this.versio = versio;
    }

    /**
     * @return the arvo
     */
    public String getArvo() {
        return arvo;
    }

    /**
     * @param arvo the arvo to set
     */
    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    /**
     * @return the nimi
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * @param nimi the kaannos to set
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    /**
     * @return the kieliUri
     */
    public String getKieliUri() {
        return kieliUri;
    }

    /**
     * @param kieliUri the kieliUri to set
     */
    public void setKieliUri(String kieliUri) {
        this.kieliUri = kieliUri;
    }

    /**
     * @return the kieliVersio
     */
    public Integer getKieliVersio() {
        return kieliVersio;
    }

    /**
     * @param kieliVersio the kieliVersio to set
     */
    public void setKieliVersio(Integer kieliVersio) {
        this.kieliVersio = kieliVersio;
    }

    /**
     * @return the kieliArvo
     */
    public String getKieliArvo() {
        return kieliArvo;
    }

    /**
     * @param kieliArvo the kieliArvo to set
     */
    public void setKieliArvo(String kieliArvo) {
        this.kieliArvo = kieliArvo;
    }

    /**
     * @return the kieliKaannos
     */
    public String getKieliKaannos() {
        return kieliKaannos;
    }

    /**
     * @param kieliKaannos the kieliKaannos to set
     */
    public void setKieliKaannos(String kieliKaannos) {
        this.kieliKaannos = kieliKaannos;
    }

    /**
     * @param meta the meta to set
     */
    public void setMeta(Map<String, KoodiV1RDTO> meta) {
        this.meta = meta;
    }

    public Map<String, KoodiV1RDTO> getMeta() {
        return meta;
    }

    public static boolean notEmpty(KoodiV1RDTO dto) {
        return dto != null && !StringUtils.isBlank(dto.getUri());
    }

    public static String stripVersionFromKoodiUri(String koodiUri) {
        return StringUtils.defaultString(koodiUri).split("#")[0];
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "[kieliUri="  + this.kieliUri +
                ", kieliVersio=" + this.kieliVersio +
                ", kieliArvi=" + this.kieliArvo +
                ", kieliKaannos=" + this.kieliKaannos +
                ", uri=" + this.uri +
                ", version=" + this.versio +
                ", arvo=" + this.arvo +
                ", nimi=" + this.nimi +
                "]";
    }
}
