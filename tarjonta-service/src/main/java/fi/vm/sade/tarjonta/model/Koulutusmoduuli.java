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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker;
import org.springframework.beans.factory.annotation.Required;

/**
 * <p>
 * Koulutusmoduuli kuvaa koulutuksen perustietoja, luokittelua nimea jne. Asioita jotka sailyvat yleensa
 * pidempaan eivatka ole aikaan tai paikkaan sidottuja. Kun koulutusmoduulista tehdaan toteutus
 * ({@link KoulutusmoduuliToteutus}) saadaan mukaan aika seka paikka -ulottuvuus.
 * </p>
 * <p>
 * Koulutusrakenne saadaan aikaiseksi lisaamalla Koulutusmoduulille alimoduuleja kayttamalla {@link KoulutusSisaltyvyys}
 * sidosluokkaa.
 * </p>
 * <p>
 * Koska kaikki koulutusrakenteen luodaan kayttamalla samaa Koulutusmoduuli -luokkaa, kaytetaan {@link KoulutusmoduuliTyyppi}:ia
 * kertomaa haluttu tyyppi.
 * </p>
 *
 *
 */
@Entity
@Table(name = Koulutusmoduuli.TABLE_NAME)
public class Koulutusmoduuli extends BaseKoulutusmoduuli implements Serializable {

    public static final String TABLE_NAME = "koulutusmoduuli";

    private static final long serialVersionUID = -3359195324699691606L;

    private static Logger log = LoggerFactory.getLogger(Koulutusmoduuli.class);

    @OneToMany(mappedBy = "ylamoduuli")
    private Set<KoulutusSisaltyvyys> sisaltyvyysList = new HashSet<KoulutusSisaltyvyys>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koulutusmoduuli")
    private Set<KoulutusmoduuliToteutus> toteutusList = new HashSet<KoulutusmoduuliToteutus>();

    @Column(name = "organisaatio")
    private String omistajaOrganisaatioOid;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusala")
    private String koulutusala;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "eqfluokitus")
    private String eqfLuokitus;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "nqfluokitus")
    private String nqfLuokitus;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusaste")
    private String koulutusAste;

    @Enumerated(EnumType.STRING)
    @Column(name = "moduulityyppi")
    private KoulutusmoduuliTyyppi moduuliTyyppi;

    @Column(name = "koulutusluokitus_koodi")
    private String koulutusKoodi;

    @Column(name = "koulutusohjelmakoodi")
    private String koulutusohjelmaKoodi;

    @Column(name = "tutkintoohjelmanimi")
    private String tutkintoOhjelmanNimi;

    /**
     * JPA konstruktori
     */
    protected Koulutusmoduuli() {
        super();
    }

    /**
     *
     * @param tyyppi
     */
    public Koulutusmoduuli(KoulutusmoduuliTyyppi tyyppi) {
        moduuliTyyppi = tyyppi;
    }

    /**
     * @return the organisaatioOid
     */
    public String getOmistajaOrganisaatioOid() {
        return omistajaOrganisaatioOid;
    }

    /**
     * @param organisaatioOid the organisaatioOid to set
     */
    public void setOmistajaOrganisaatioOid(String organisaatioOid) {
        omistajaOrganisaatioOid = organisaatioOid;
    }

    /**
     * <p>
     * Koulutusjärjestelmän mukaisia koulutuksia koskeva luokittelu, joka kuvaa koulutusten
     * sijoittumista tieteen, yhteiskunnan tai työelämän aloille ja jota käytetään koulutusten
     * suunnitteluun, seurantaan ja säätelyyn Opetushallinnon Koulutusala 2002 -luokittelun
     * mukaisia koulutusaloja ovat esimerkiksi kulttuuriala sekä tekniikan ja liikenteen ala.
     * Koulutusalaluokitteluja on tällä hetkellä (kesäkuu 2012) neljä: opetushallinnon Koulutusala
     * 2002- ja Koulutusala 1995 -luokittelut, ISCED-luokittelu sekä Tilastokeskuksen
     * koulutusalaluokittelu.
     * <br/>
     * Lähde: OKSA sanasto: https://confluence.csc.fi/pages/viewpage.action?pageId=8688189
     * </p>
     *
     * @return uri koodistoon
     */
    public String getKoulutusala() {
        return koulutusala;
    }

    /**
     * @param koulutusAla the koulutusAla to set
     */
    public void setKoulutusala(String koulutusAla) {
        this.koulutusala = koulutusAla;
    }

    /**
     * EU classification
     *
     * @see https://confluence.csc.fi/display/oppija/EQF-luokitus
     * @see http://en.wikipedia.org/wiki/European_Qualifications_Framework
     *
     * @return the eqfLuokitus
     */
    public String getEqfLuokitus() {
        return eqfLuokitus;
    }

    /**
     * @param eqfLuokitus the eqfLuokitus to set
     */
    public void setEqfLuokitus(String eqfLuokitus) {
        this.eqfLuokitus = eqfLuokitus;
    }

    /**
     * National Qualification Framework. Describes how Finnish qualification system connects with EQF.
     *
     * @see http://www.oph.fi/mobility/qualifications_frameworks
     *
     * @return the nqfLuokitus
     */
    public String getNqfLuokitus() {
        return nqfLuokitus;
    }

    /**
     * @param nqfLuokitus the nqfLuokitus to set
     */
    public void setNqfLuokitus(String nqfLuokitus) {
        this.nqfLuokitus = nqfLuokitus;
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
    public String getKoulutusAste() {
        return koulutusAste;
    }

    /**
     * @param koulutusAste the koulutusAste to set
     */
    public void setKoulutusAste(String koulutusAste) {
        this.koulutusAste = koulutusAste;
    }

    /**
     * Returns true if given child is a direct or non-direct (unlimited depth) child of this Koulutus.
     * Note that as children are lazily loaded, each level will require one more select.
     *
     * @param child
     * @return
     */
    public boolean hasAsChild(Koulutusmoduuli child) {
        return hasAsChild(child, -1);
    }

    /**
     * Returns true if given child is a direct or non-direct child of this Koulutus, depth begin limited to
     * <code>depth</code>.
     *
     * @param child child to match
     * @param depth maximum depth to use while searching
     * @return
     */
    public boolean hasAsChild(Koulutusmoduuli child, int depth) {

        KoulutusTreeWalker.EqualsMatcher matcher = new KoulutusTreeWalker.EqualsMatcher(child);
        new KoulutusTreeWalker(depth, matcher).walk(this);

        return matcher.isFound();

    }

    /**
     * Convenience method that instead of returning set of KoulutusSisaltyvyys,
     * returns only the children from those elements.
     *
     * TODO: since this is only used in unit tests, move to test helper method
     *
     * @return
     */
    public Set<Koulutusmoduuli> getAlamoduuliList() {

        Set<Koulutusmoduuli> result = new HashSet<Koulutusmoduuli>();
        for (KoulutusSisaltyvyys s : sisaltyvyysList) {
            result.addAll(s.getAlamoduuliList());
        }
        return result;

    }

    /**
     *
     * @return
     */
    public Set<KoulutusmoduuliToteutus> getKoulutusmoduuliToteutusList() {
        return Collections.unmodifiableSet(toteutusList);
    }

    /**
     *
     * @param toteutus
     * @return
     */
    public boolean addKoulutusmoduuliToteutus(KoulutusmoduuliToteutus toteutus) {
        if (!toteutusList.contains(toteutus)) {
            toteutusList.add(toteutus);
            toteutus.setKoulutusmoduuli(this);
            return true;
        }
        return false;
    }

    /**
     *
     * @param toteutus
     * @return
     */
    public boolean removeKoulutusmoduuliToteutus(KoulutusmoduuliToteutus toteutus) {
        if (toteutusList.remove(toteutus)) {
            toteutus.setKoulutusmoduuli(null);
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public Set<KoulutusSisaltyvyys> getSisaltyvyysList() {
        return Collections.unmodifiableSet(sisaltyvyysList);
    }

    /**
     *
     * @see #setKoulutusKoodi(java.lang.String)
     * @return
     */
    public String getKoulutusKoodi() {
        return koulutusKoodi;
    }

    /**
     * Tilastokeskuksen maarittelema koulutus luokitus koodi. Arvona on uri koodistoon joka esittää kyseistä luokitus koodia.
     *
     *
     * @see http://www.stat.fi/meta/luokitukset/koulutus/001-2010/index.html
     * @param koulutusKoodiUri
     */
    public void setKoulutusKoodi(String koulutusKoodiUri) {
        this.koulutusKoodi = koulutusKoodiUri;
    }

    /**
     *
     * @return
     */
    public String getKoulutusNimi() {
        return getNimi();
    }

    /**
     * If the value comes from Tilastokeskus - should we group setting the code and name?
     *
     * @param koulutusNimi
     */
    public void setKoulutusNimi(String koulutusNimi) {
        setNimi(koulutusNimi);
    }

    /**
     * <p>
     * Pääaineen koulutusohjelman tai vastaavan nimi. Tämä attribuutti on pätevä silloin kun {@link Koulutusmoduuli#moduuliTyyppi}
     * on {@link KoulutusmoduuliTyyppi#TUTKINTO_OHJELMA}.
     * </p>
     *
     * <p>
     * KV vastaavuus: ects:DegreeProgrammeTitle.
     * </p>
     *
     * @return the tutkintoOhjelmanNimi
     */
    public String getTutkintoOhjelmanNimi() {
        return tutkintoOhjelmanNimi;
    }

    /**
     * @param tutkintoOhjelmanNimi the tutkintoOhjelmanNimi to set
     */
    public void setTutkintoOhjelmanNimi(String tutkintoOhjelmanNimi) {
        this.tutkintoOhjelmanNimi = tutkintoOhjelmanNimi;
    }

    /**
     * Palautttaa moduulin tyypin joka osaltaa kertoo mikä joukko attribuutteja on päteviä tällä koulutusmoduulilla.
     *
     * @return
     */
    public KoulutusmoduuliTyyppi getModuuliTyyppi() {
        return moduuliTyyppi;
    }

    /**
     * Palauttaa koodisto uri:n joka viittaa valittuun koulutusohjelmaan.
     *
     * Esimerkki koulutusohjelmasta: "Ympäristön suunnittelun ja rakentamisen koulutusohjelma (1603)"
     *
     * @return
     */
    public String getKoulutusohjelmaKoodi() {
        return koulutusohjelmaKoodi;
    }

    public void setKoulutusohjelmaKoodi(String koulutusohjelmaKoodi) {
        this.koulutusohjelmaKoodi = koulutusohjelmaKoodi;
    }

}

