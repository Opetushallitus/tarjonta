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
package fi.vm.sade.tarjonta.koodisto.model;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import java.util.List;

/**
 * This entity object represents data from {@link KoodistoType} and {@link KoodiType} in a flat format.
 * There are no guarantees that the data is up-to-date.
 *
 * @author Jukka Raanamo
 */
@Entity
@Table(name = "koodisto_koodi")
public class Koodi implements Serializable {

    private static final long serialVersionUID = 3299182141123204971L;

    private static final char VERSION_SEP_TOKEN = '#';

    @Id
    private String id;

    @Column(name = "koodi_uri")
    private String koodiUri;

    @Column(name = "koodi_versio")
    private int koodiVersio;

    @Column(name = "koodisto_uri")
    private String koodistoUri;

    @Column(name = "koodisto_versio")
    private int koodistoVersio;

    @Column(name = "koodi_arvo")
    private String koodiArvo;

    @Column(name = "koodi_nimi_fi")
    private String koodiNimiFi;

    @Column(name = "koodi_nimi_en")
    private String koodiNimiEn;

    @Column(name = "koodi_nimi_sv")
    private String koodiNimiSv;

    /**
     * For JPA
     */
    protected Koodi() {
    }

    /**
     * Copy constructor. Copies all fields.
     *
     * @param copyFrom
     */
    public Koodi(Koodi copyFrom) {

        this.koodiArvo = copyFrom.koodiArvo;
        this.koodiNimiEn = copyFrom.koodiNimiEn;
        this.koodiNimiSv = copyFrom.koodiNimiSv;
        this.koodiUri = copyFrom.koodiUri;
        this.koodiVersio = copyFrom.koodiVersio;
        this.koodistoUri = copyFrom.koodistoUri;
        this.koodistoVersio = copyFrom.koodistoVersio;
        this.koodiNimiFi = copyFrom.koodiNimiFi;

        createId();

    }

    /**
     * Constructs koodi from given values
     *
     * @param koodistoUri not null koodisto uri
     * @param koodistoVersio
     * @param koodiUri
     * @param koodiVersio
     * @param koodiArvo
     */
    public Koodi(String koodistoUri, int koodistoVersio, String koodiUri, int koodiVersio, String koodiArvo) {

        this.koodiUri = koodiUri;
        this.koodiVersio = koodiVersio;
        this.koodistoUri = koodistoUri;
        this.koodistoVersio = koodistoVersio;
        this.koodiArvo = koodiArvo;

        createId();

    }


    public Koodi(String koodistoUri, Integer koodistoVersio, KoodiType koodi) {

        this(koodistoUri, koodistoVersio, koodi.getKoodiUri(), koodi.getVersio(), koodi.getKoodiArvo());
        setMetadata(koodi);

    }


    public final void setMetadata(KoodiType koodi) {

        List<KoodiMetadataType> metadatas = koodi.getMetadata();
        if (metadatas != null && !metadatas.isEmpty()) {

            for (KoodiMetadataType m : metadatas) {
                final KieliType kieli = m.getKieli();
                final String nimi = m.getNimi();
                if (KieliType.FI.equals(kieli)) {
                    setKoodiNimiFi(nimi);
                } else if (KieliType.EN.equals(kieli)) {
                    setKoodiNimiEn(nimi);
                } else if (KieliType.SV.equals(kieli)) {
                    setKoodiNimiSv(nimi);
                }

            }

        }
        // else - should we reset values?

    }



    /**
     * Returns this entity's unique identifier as it is formatted by {@link #makeId(java.lang.String, int) }
     *
     * @return
     */
    public String getId() {

        return id;

    }

    /**
     * Returns a unique identifier as it is used by this entity from given values.
     *
     * @param koodiUri
     * @param koodiVersio
     * @return
     */
    public static String makeId(String koodiUri, int koodiVersio) {

        assert koodiUri != null : "koodiUri must be non null";
        assert koodiVersio > 0 : "koodiVersio must be > 0";

        return new StringBuilder().append(koodiUri).
            append(VERSION_SEP_TOKEN).
            append(koodiVersio).
            toString();
    }

    /**
     * Builds up id from koodi uri and version.
     *
     * @param koodiUri
     * @param koodiVersio
     */
    private void createId() {
        this.id = makeId(koodiUri, koodiVersio);
    }

    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String koodiArvo) {
        this.koodiArvo = koodiArvo;
    }

    public String getKoodiNimiEn() {
        return koodiNimiEn;
    }

    public void setKoodiNimiEn(String koodiNimiEn) {
        this.koodiNimiEn = koodiNimiEn;
    }

    public String getKoodiNimiSv() {
        return koodiNimiSv;
    }

    public void setKoodiNimiSv(String koodiNimiSv) {
        this.koodiNimiSv = koodiNimiSv;
    }

    public String getKoodiUri() {
        return koodiUri;
    }

    public int getKoodiVersio() {
        return koodiVersio;
    }

    public String getKoodistoUri() {
        return koodistoUri;
    }

    public int getKoodistoVersio() {
        return koodistoVersio;
    }

    public String getKoodiNimiFi() {
        return koodiNimiFi;
    }

    public void setKoodiNimiFi(String koodiNimiFi) {
        this.koodiNimiFi = koodiNimiFi;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Koodi == false) {
            return false;
        }

        Koodi other = (Koodi) o;

        return new EqualsBuilder().append(this.id, other.id).isEquals();

    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(this.id).
            toHashCode();

    }

}

