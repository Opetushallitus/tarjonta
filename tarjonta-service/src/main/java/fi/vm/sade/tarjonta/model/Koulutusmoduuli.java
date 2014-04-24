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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import static fi.vm.sade.tarjonta.model.XSSUtil.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;

/**
 * <p>
 * Koulutusmoduuli kuvaa koulutuksen perustietoja, luokittelua nimea jne.
 * Asioita jotka sailyvat yleensa pidempaan eivatka ole aikaan tai paikkaan
 * sidottuja. Kun koulutusmoduulista tehdaan toteutus
 * ({@link KoulutusmoduuliToteutus}) saadaan mukaan aika seka paikka
 * -ulottuvuus.
 * </p>
 * <p>
 * Koulutusrakenne saadaan aikaiseksi lisaamalla Koulutusmoduulille alimoduuleja
 * kayttamalla {@link KoulutusSisaltyvyys} sidosluokkaa.
 * </p>
 * <p>
 * Koska kaikki koulutusrakenteen luodaan kayttamalla samaa Koulutusmoduuli
 * -luokkaa, kaytetaan {@link KoulutusmoduuliTyyppi}:ia kertomaa haluttu tyyppi.
 * </p>
 *
 *
 */
@Entity
@Table(name = Koulutusmoduuli.TABLE_NAME)
public class Koulutusmoduuli extends BaseKoulutusmoduuli implements Serializable {

    public static final String TABLE_NAME = "koulutusmoduuli";
    private static final long serialVersionUID = -3359195324699691606L;
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(Koulutusmoduuli.class);
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylamoduuli")
    private Set<KoulutusSisaltyvyys> sisaltyvyysList = new HashSet<KoulutusSisaltyvyys>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "koulutusmoduuli")
    private Set<KoulutusmoduuliToteutus> toteutusList = new HashSet<KoulutusmoduuliToteutus>();
    @Column(name = "organisaatio")
    private String omistajaOrganisaatioOid;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_tutkintonimike", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> tutkintonimikes = new HashSet<KoodistoUri>();
    @Column(name = "ulkoinentunniste")
    private String ulkoinenTunniste;

    @Enumerated(EnumType.STRING)
    @Column(name = "koulutustyyppi")
    private String koulutustyyppi;

    @Column(name = "oppilaitostyyppi", length = 500)
    private String oppilaitostyyppi;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nimi")
    private MonikielinenTeksti nimi;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = TABLE_NAME + "_tekstit", inverseJoinColumns = @JoinColumn(name = "monikielinen_teksti_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "teksti", nullable = false)
    private Map<KomoTeksti, MonikielinenTeksti> tekstit = new HashMap<KomoTeksti, MonikielinenTeksti>();

    @Enumerated(EnumType.STRING)
    @Column(name = "moduulityyppi")
    private KoulutusmoduuliTyyppi moduuliTyyppi;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusala_uri")
    private String koulutusala;
    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "eqf_uri")
    private String eqfLuokitus;
    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "nqf_uri")
    private String nqfLuokitus;
    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusaste_uri")
    private String koulutusAste;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutus_uri")
    private String koulutusKoodi;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutusohjelma_uri")
    private String koulutusohjelmaKoodi;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "tutkinto_uri")
    private String tutkintoOhjelmanNimi;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "opintojen_laajuusarvo_uri")
    private String laajuusArvo;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "opintojen_laajuusyksikko_uri")
    private String laajuusYksikko;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "lukiolinja_uri")
    private String lukiolinja;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutustyyppi_uri")
    private String koulutustyyppiUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "opintoala_uri")
    private String opintoala;

    /**
     * Optional Koodisto uri. See accessors for more info.
     */
    @Column(name = "kandi_koulutus_uri")
    private String kandidaatinKoulutuskoodi;

    /**
     * JPA konstruktori
     */
    public Koulutusmoduuli() {
        super();
    }

    public Koulutusmoduuli(KoulutusmoduuliTyyppi tyyppi) {
        super();
        moduuliTyyppi = tyyppi;
    }

    public Map<KomoTeksti, MonikielinenTeksti> getTekstit() {
        return tekstit;
    }

    public void setTekstit(Map<KomoTeksti, MonikielinenTeksti> tekstit) {
        this.tekstit = tekstit;
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
     * National Qualification Framework. Describes how Finnish qualification
     * system connects with EQF.
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
     * Returns true if given child is a direct or non-direct (unlimited depth)
     * child of this Koulutus. Note that as children are lazily loaded, each
     * level will require one more select.
     *
     * @param child
     * @return
     */
    public boolean hasAsChild(Koulutusmoduuli child) {
        return hasAsChild(child, -1);
    }

    /**
     * Returns true if given child is a direct or non-direct child of this
     * Koulutus, depth begin limited to <code>depth</code>.
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

    public void addSisaltyvyys(KoulutusSisaltyvyys sisaltyvyys) {
        sisaltyvyysList.add(sisaltyvyys);
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
     * Tilastokeskuksen maarittelema koulutus luokitus koodi. Arvona on uri
     * koodistoon joka esittää kyseistä luokitus koodia.
     *
     *
     * @see http://www.stat.fi/meta/luokitukset/koulutus/001-2010/index.html
     * @param koulutusKoodiUri
     */
    public void setKoulutusKoodi(String koulutusKoodiUri) {
        this.koulutusKoodi = koulutusKoodiUri;
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
     * Palautttaa moduulin tyypin joka osaltaa kertoo mikä joukko attribuutteja
     * on päteviä tällä koulutusmoduulilla.
     *
     * @return
     */
    public KoulutusmoduuliTyyppi getModuuliTyyppi() {
        return moduuliTyyppi;
    }

    public void setModuuliTyyppi(KoulutusmoduuliTyyppi moduuliTyyppi) {
        this.moduuliTyyppi = moduuliTyyppi;
    }

    /**
     * Palauttaa koodisto uri:n joka viittaa valittuun koulutusohjelmaan.
     *
     * Esimerkki koulutusohjelmasta: "Ympäristön suunnittelun ja rakentamisen
     * koulutusohjelma (1603)"
     *
     * @return
     */
    public String getKoulutusohjelmaKoodi() {
        return koulutusohjelmaKoodi;
    }

    public void setKoulutusohjelmaKoodi(String koulutusohjelmaKoodi) {
        this.koulutusohjelmaKoodi = koulutusohjelmaKoodi;
    }

    /**
     * Laajuuden arvo. Esim. 30.
     *
     * @return
     */
    public String getLaajuusArvo() {
        return laajuusArvo;
    }

    /**
     * Laajuuden yksikko. Sisalto ilmeisesti koodisto uri mutta esitetty tieto
     * esim. "opintoviikko"
     *
     * @return
     */
    public String getLaajuusYksikko() {
        return laajuusYksikko;
    }

    /**
     * Koulutuksen laajuus. yksikkoUri on Koodisto uri joka kertoo laajuuden
     * yksikon kuten "vuosi", "kuukausi" "opintojakso". Arvo on arvo
     * edellämainitussa yksikössä.
     *
     * @param yksikkoUri
     * @param arvo
     */
    public void setLaajuus(String yksikkoUri, String arvo) {
        laajuusYksikko = yksikkoUri;
        laajuusArvo = arvo;
    }

    /**
     * Tutkintonimike, esim. "filosofian maisteri". Arvo koodisto uri?
     *
     * @return
     */
    public String getTutkintonimike() {
        if (this.tutkintonimikes.size() > 1) {
            throw new RuntimeException("Not allowed error - Too many starting tutkintonimike objects, maybe you are using a wrong method?");
        } else if (tutkintonimikes.isEmpty()) {
            //at least parent komo's tutkintonimike can be null. 
            return null;
        }

        return tutkintonimikes.iterator().next().getKoodiUri();
    }

    public void setTutkintonimike(String tutkintonimike) {
        if (tutkintonimike == null) {
            this.tutkintonimikes.clear();
        } else {
            final KoodistoUri koodistoUri = new KoodistoUri(tutkintonimike);
            EntityUtils.keepSelectedKoodistoUri(this.tutkintonimikes, koodistoUri);
            tutkintonimikes.add(koodistoUri);
        }
    }

    public Set<KoodistoUri> getTutkintonimikes() {
        return tutkintonimikes;
    }

    /**
     * Tutkintonimike Koodisto uri:na.
     *
     * @see #getTutkintonimike()
     * @param koodistoUris
     */
    public void setTutkintonimikes(Set<KoodistoUri> koodistoUris) {
        this.tutkintonimikes = koodistoUris;
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
     * Koulutuksen rakenteen kuvaus tekstina mikali rakenteellista tieto ei ole.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getKoulutuksenRakenne() {
        return tekstit.get(KomoTeksti.KOULUTUKSEN_RAKENNE);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setKoulutuksenRakenne(MonikielinenTeksti koulutuksenRakenne) {
        MonikielinenTeksti.merge(tekstit, KomoTeksti.KOULUTUKSEN_RAKENNE, koulutuksenRakenne);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getJatkoOpintoMahdollisuudet() {
        return tekstit.get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setJatkoOpintoMahdollisuudet(MonikielinenTeksti jatkoOpintoMahdollisuudet) {
        MonikielinenTeksti.merge(tekstit, KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, jatkoOpintoMahdollisuudet);
    }

    /**
     * Kielistetty koulutuksen nimi. Joillain opintoasteilla kuten 2.aste, nimi
     * generoituu joukosta attribuutteja joten sen manuaalinen asettaminen
     * saattaa myöhemmi ylikirjoittautua.
     *
     * @return kielistetyt nimet tai null
     */
    public MonikielinenTeksti getNimi() {
        return nimi;
    }

    /**
     * @see #getNimi()
     */
    public void setNimi(MonikielinenTeksti nimi) {
        this.nimi = MonikielinenTeksti.merge(this.nimi, nimi);
    }

    /**
     * @return the tavoitteet
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getTavoitteet() {
        return tekstit.get(KomoTeksti.TAVOITTEET);
    }

    /**
     * @param tavoitteet the tavoitteet to set
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setTavoitteet(MonikielinenTeksti tavoitteet) {
        MonikielinenTeksti.merge(tekstit, KomoTeksti.TAVOITTEET, tavoitteet);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getPatevyys() {
        return tekstit.get(KomoTeksti.PATEVYYS);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setPatevyys(MonikielinenTeksti tavoitteet) {
        MonikielinenTeksti.merge(tekstit, KomoTeksti.PATEVYYS, tavoitteet);
    }

    /**
     * @return the opintoala
     */
    public String getOpintoala() {
        return opintoala;
    }

    /**
     * @param opintoala the opintoala to set
     */
    public void setOpintoala(String opintoala) {
        this.opintoala = opintoala;
    }

    public String getKoulutustyyppi() {
        return koulutustyyppi;
    }

    public void setKoulutustyyppi(String koulutustyyppi) {
        this.koulutustyyppi = koulutustyyppi;
    }

    public String getLukiolinja() {
        return lukiolinja;
    }

    public void setLukiolinja(String lukiolinja) {
        this.lukiolinja = lukiolinja;
    }

    /**
     * @return the oppilaitostyyppi
     */
    public String getOppilaitostyyppi() {
        return oppilaitostyyppi;
    }

    /**
     * @param oppilaitostyyppi the oppilaitostyyppi to set
     */
    public void setOppilaitostyyppi(String oppilaitostyyppi) {
        this.oppilaitostyyppi = oppilaitostyyppi;
    }

    /**
     * AntiSamy Filtteröidään (vain) kentät joissa tiedetään olevan HTML:ää.
     * Muut kentät esityskerroksen vastuulla!
     */
    @PrePersist
    @PreUpdate
    public void filterHTMLFields() {
        for (MonikielinenTeksti teksti : tekstit.values()) {
            filter(teksti);
        }
    }

    /**
     * @return the kandidaatinKoulutuskoodi
     */
    public String getKandidaatinKoulutuskoodi() {
        return kandidaatinKoulutuskoodi;
    }

    /**
     * @param kandidaatinKoulutuskoodi the kandidaatinKoulutuskoodi to set
     */
    public void setKandidaatinKoulutuskoodi(String kandidaatinKoulutuskoodi) {
        this.kandidaatinKoulutuskoodi = kandidaatinKoulutuskoodi;
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
}
