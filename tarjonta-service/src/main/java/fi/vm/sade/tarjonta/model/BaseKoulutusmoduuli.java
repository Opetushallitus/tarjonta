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
package fi.vm.sade.tarjonta.model;

import java.util.Date;

import javax.persistence.*;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.io.Serializable;

/**
 * Yhteinen abstrakti perusluokka (ei entiteetti) Koulutusmoduuli:lle seka
 * Koulutusmoduulintoteutukselle.
 */
@MappedSuperclass
public abstract class BaseKoulutusmoduuli extends TarjontaBaseEntity implements Serializable {

    public static final String OID_COLUMN_NAME = "oid";

    public static final String TILA_COLUMN_NAME = "tila";

    private static final long serialVersionUID = -8023508784857174305L;

    @Column(name = OID_COLUMN_NAME, nullable = false, insertable = true, updatable = false, unique = true)
    private String oid;

    @Column(name = TILA_COLUMN_NAME)
    @Enumerated(EnumType.STRING)
    private TarjontaTila tila = TarjontaTila.LUONNOS;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "viimPaivitysPvm")
    private Date updated;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusala_uri")
    private String koulutusalaUri;
    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "eqf_uri")
    private String eqfUri;
    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "nqf_uri")
    private String nqfUri;
    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusaste_uri")
    private String koulutusasteUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutus_uri")
    private String koulutusUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusohjelma_uri")
    private String koulutusohjelmaUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "tutkinto_uri")
    private String tutkintoUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "opintojen_laajuusarvo_uri")
    private String opintojenLaajuusarvoUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "opintojen_laajuusyksikko_uri")
    private String opintojenLaajuusyksikkoUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "lukiolinja_uri")
    private String lukiolinjaUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutustyyppi_uri")
    private String koulutustyyppiUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "opintoala_uri")
    private String opintoalaUri;

    @Column(name = "ulkoinentunniste")
    private String ulkoinenTunniste;

    @Column(name = "kandi_koulutus_uri")
    private String kandidaatinKoulutusUri;

    @PreUpdate
    protected void beforeUpdate() {
        updated = new Date();
    }

    @PrePersist
    protected void beforePersist() {
        updated = new Date();
    }

    /**
     * OID of this Koulutus. On database level, this does not uniquely identify
     * Koulutus. For that version needs to be specified.
     *
     * @return the koulutusOid
     */
    public String getOid() {
        return oid;
    }

    /**
     * Assing the OID. Once this entity is persisted - OID cannot be changed.
     *
     * @param koulutusOid the koulutusOid to set
     */
    public void setOid(String koulutusOid) {
        this.oid = koulutusOid;
    }

    /**
     * Returns timestamp when this Koulutusmoduuli was updated or null if has
     * never been persisted.
     *
     * @return
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * Returns uri to koodisto representing current state of this Koulutus.
     *
     * @return
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * Set uri to koodisto representing the current state of this Koulutus.
     *
     * @param tila
     */
    public void setTila(TarjontaTila tila) {
        // todo: since states come from koodisto, can we do any state lifecycle validation??
        this.tila = tila;
    }

    /**
     * <p>
     * Koulutusjärjestelmän mukaisia koulutuksia koskeva luokittelu, joka kuvaa
     * koulutusten sijoittumista tieteen, yhteiskunnan tai työelämän aloille ja
     * jota käytetään koulutusten suunnitteluun, seurantaan ja säätelyyn
     * Opetushallinnon Koulutusala 2002 -luokittelun mukaisia koulutusaloja ovat
     * esimerkiksi kulttuuriala sekä tekniikan ja liikenteen ala.
     * Koulutusalaluokitteluja on tällä hetkellä (kesäkuu 2012) neljä:
     * opetushallinnon Koulutusala 2002- ja Koulutusala 1995 -luokittelut,
     * ISCED-luokittelu sekä Tilastokeskuksen koulutusalaluokittelu.
     * <br/>
     * Lähde: OKSA sanasto:
     * https://confluence.csc.fi/pages/viewpage.action?pageId=8688189
     * </p>
     *
     * @return uri koodistoon
     */
    public String getKoulutusalaUri() {
        return koulutusalaUri;
    }

    /**
     * @param koulutusAla the koulutusAla to set
     */
    public void setKoulutusalaUri(String koulutusAla) {
        this.koulutusalaUri = koulutusAla;
    }

    /**
     * EU classification
     *
     * @see https://confluence.csc.fi/display/oppija/EQF-luokitus
     * @see http://en.wikipedia.org/wiki/European_Qualifications_Framework
     *
     * @return the eqfLuokitus
     */
    public String getEqfUri() {
        return eqfUri;
    }

    /**
     * @param eqfUri the eqfLuokitus to set
     */
    public void setEqfUri(String eqfUri) {
        this.eqfUri = eqfUri;
    }

    /**
     * National Qualification Framework. Describes how Finnish qualification
     * system connects with EQF.
     *
     * @see http://www.oph.fi/mobility/qualifications_frameworks
     *
     * @return the nqfLuokitus
     */
    public String getNqfUri() {
        return nqfUri;
    }

    /**
     * @param nqfUri the nqfLuokitus to set
     */
    public void setNqfUri(String nqfUri) {
        this.nqfUri = nqfUri;
    }

    /**
     * Sample content of the actual data:
     * <pre>
     * 0	Esiaste
     * 1	Alempi perusaste
     * 2	Ylempi perusaste
     * 3	Keskiaste
     * 5	Alin korkea-aste
     * 6	Alempi korkeakouluaste
     * 7	Ylempi korkeakouluaste
     * 8	Tutkijakoulutusaste
     * 9	Koulutusaste tuntematon
     * </pre>
     *
     * @see http://www.stat.fi/meta/luokitukset/koulutusaste/versio.html
     *
     * @return uri to koodisto
     */
    public String getKoulutusasteUri() {
        return koulutusasteUri;
    }

    /**
     * @param koulutusasteUri the koulutusasteUri to set
     */
    public void setKoulutusasteUri(String koulutusasteUri) {
        this.koulutusasteUri = koulutusasteUri;
    }

    /**
     *
     * @see #setKoulutusUri(java.lang.String)
     * @return
     */
    public String getKoulutusUri() {
        return koulutusUri;
    }

    /**
     * Tilastokeskuksen maarittelema koulutus luokitus koodi. Arvona on uri
     * koodistoon joka esittää kyseistä luokitus koodia.
     *
     *
     * @see http://www.stat.fi/meta/luokitukset/koulutus/001-2010/index.html
     * @param koulutusKoodiUri
     */
    public void setKoulutusUri(String koulutusKoodiUri) {
        this.koulutusUri = koulutusKoodiUri;
    }

    /**
     * <p>
     * Pääaineen koulutusohjelman tai vastaavan nimi. Tämä attribuutti on pätevä
     * silloin kun {@link Koulutusmoduuli#moduuliTyyppi} on
     * {@link KoulutusmoduuliTyyppi#TUTKINTO_OHJELMA}.
     * </p>
     *
     * <p>
     * KV vastaavuus: ects:DegreeProgrammeTitle.
     * </p>
     *
     * @return the tutkintoOhjelmanNimi
     */
    public String getTutkintoUri() {
        return tutkintoUri;
    }

    /**
     * @param tutkintoUri the tutkintoOhjelmanNimi to set
     */
    public void setTutkintoUri(String tutkintoUri) {
        this.tutkintoUri = tutkintoUri;
    }

    /**
     * Palauttaa koodisto uri:n joka viittaa valittuun koulutusohjelmaan.
     *
     * Esimerkki koulutusohjelmasta: "Ympäristön suunnittelun ja rakentamisen
     * koulutusohjelma (1603)"
     *
     * @return
     */
    public String getKoulutusohjelmaUri() {
        return koulutusohjelmaUri;
    }

    public void setKoulutusohjelmaUri(String koulutusohjelmaUri) {
        this.koulutusohjelmaUri = koulutusohjelmaUri;
    }

    /**
     * Laajuuden arvo. Esim. 30.
     *
     * @return
     */
    public String getOpintojenLaajuusarvoUri() {
        return opintojenLaajuusarvoUri;
    }

    /**
     * Laajuuden yksikko. Sisalto ilmeisesti koodisto uri mutta esitetty tieto
     * esim. "opintoviikko"
     *
     * @return
     */
    public String getOpintojenLaajuusyksikkoUri() {
        return opintojenLaajuusyksikkoUri;
    }

    /**
     * @return the opintoala
     */
    public String getOpintoalaUri() {
        return opintoalaUri;
    }

    /**
     * @param opintoalaUri the opintoala to set
     */
    public void setOpintoalaUri(String opintoalaUri) {
        this.opintoalaUri = opintoalaUri;
    }

    public String getLukiolinjaUri() {
        return lukiolinjaUri;
    }

    public void setLukiolinjaUri(String lukiolinjaUri) {
        this.lukiolinjaUri = lukiolinjaUri;
    }

    /**
     * Koulutuksen laajuus. yksikkoUri on Koodisto uri joka kertoo laajuuden
     * yksikon kuten "vuosi", "kuukausi" "opintojakso". Arvo on arvo
     * edellämainitussa yksikössä.
     *
     * @param yksikkoUri
     * @param arvo
     */
    public void setOpintojenLaajuus(String yksikkoUri, String arvo) {
        setOpintojenLaajuusyksikkoUri(yksikkoUri);
        setOpintojenLaajuusarvoUri(arvo);
    }

    /**
     * @return the koulutustyyppiUri
     */
    public String getKoulutustyyppiUri() {
        return koulutustyyppiUri;
    }

    /**
     * @param koulutustyyppiUri the koulutustyyppiUri to set
     */
    public void setKoulutustyyppiUri(String koulutustyyppiUri) {
        this.koulutustyyppiUri = koulutustyyppiUri;
    }

    /**
     * @param opintojenLaajuusyksikkoUri the opintojenLaajuusyksikkoUri to set
     */
    public void setOpintojenLaajuusyksikkoUri(String opintojenLaajuusyksikkoUri) {
        this.opintojenLaajuusyksikkoUri = opintojenLaajuusyksikkoUri;
    }

    /**
     * @param opintojenLaajuusarvoUri the opintojenLaajuusarvoUri to set
     */
    public void setOpintojenLaajuusarvoUri(String opintojenLaajuusarvoUri) {
        this.opintojenLaajuusarvoUri = opintojenLaajuusarvoUri;
    }

    /**
     * Ulkoinen tunniste on koulutusmoduulin yksiloiva tunniste toisessa
     * jarjestelmassa. Esim. jos tama koulutusmoduuli on tuoto era-ajona
     * toisesta jarjestelmasta. Sisallon muotoon ei oteta kantaa.
     *
     * @return
     */
    public String getUlkoinenTunniste() {
        return ulkoinenTunniste;
    }

    /**
     * @see #getUlkoinenTunniste()
     * @param ulkoinenTunniste
     */
    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this.ulkoinenTunniste = ulkoinenTunniste;
    }

    /**
     * An optional Koodisto koulutus-koodi uri for kandidate.
     */
    public String getKandidaatinKoulutusUri() {
        return kandidaatinKoulutusUri;
    }

    /**
     * An optional Koodisto koulutus-koodi uri for kandidate.
     */
    public void setKandidaatinKoulutusUri(String kandidaatinKoulutuskoodi) {
        this.kandidaatinKoulutusUri = kandidaatinKoulutuskoodi;
    }

}
