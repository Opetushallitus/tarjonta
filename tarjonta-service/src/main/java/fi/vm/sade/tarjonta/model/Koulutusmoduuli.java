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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import static fi.vm.sade.tarjonta.model.XSSUtil.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "koulutustyyppi")
    private fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum koulutustyyppiEnum;

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

    @Column(name = "koulutuksen_tunniste_oid")
    private String koulutuksenTunnisteOid;

    /*
     * Tutke 2 muutoksen myötä ammatillisen tutkinnon rakenne voi olla kahta eri tyyppiä
     * 1. Tutkinto -> tutkinto-ohjelma -> koulutusmoduulitoteutus
     * 2. Tutkinto -> koulutusmoduulitoteutus
     * Jotta KI:n indeksointi ei menisi sekaisin, myös tyyppiä 2 olevat koulutusmoduulitoteutukset saavat
     * linkin tutkinto-ohjelmaan, jolloin KI:n tietokantarakennetta ei tarvitse päivitää, sillä tarjonnan rajapinta
     * palauttaa edelleen myös tutkinto-ohjelman tiedot. Rakenne saa siis muodon:
     * Tutkinto -> tutkinto-ohjelma (pseudo) -> koulutusmoduulitoteutus
     * Tämä toteutus ei oikeasti kuuluisi lainkaan tarjontaan, mutta KI:n indeksointilogiikkaa ei ehditä päivittää.
     */
    @Column(name = "is_pseudo")
    private boolean isPseudo;

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
     * <p/>
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
     * @return
     */
    public Set<KoulutusmoduuliToteutus> getKoulutusmoduuliToteutusList() {
        return Collections.unmodifiableSet(toteutusList);
    }

    /**
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
     * @return
     */
    public Set<KoulutusSisaltyvyys> getSisaltyvyysList() {
        return Collections.unmodifiableSet(sisaltyvyysList);
    }

    public void addSisaltyvyys(KoulutusSisaltyvyys sisaltyvyys) {
        sisaltyvyysList.add(sisaltyvyys);
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


    public ModuulityyppiEnum getKoulutustyyppiEnum() {
        return koulutustyyppiEnum;
    }

    public void setKoulutustyyppiEnum(ModuulityyppiEnum koulutustyyppiEnum) {
        this.koulutustyyppiEnum = koulutustyyppiEnum;
    }

    /**
     * Tutkintonimike, esim. "filosofian maisteri". Arvo koodisto uri?
     *
     * @return
     */
    public String getTutkintonimikeUri() {
        return BaseKoulutusmoduuli.getTutkintonimikeUri(tutkintonimikes);
    }

    public void setTutkintonimikeUri(String tutkintonimikeUri) {
        if (tutkintonimikeUri == null) {
            this.tutkintonimikes.clear();
        } else {
            final KoodistoUri koodistoUri = new KoodistoUri(tutkintonimikeUri);
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
     * @param koodistoUris
     * @see #getTutkintonimikeUri()
     */
    public void setTutkintonimikes(Set<KoodistoUri> koodistoUris) {
        this.tutkintonimikes = koodistoUris;
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
    public void filterHTMLFields() {
        for (MonikielinenTeksti teksti : tekstit.values()) {
            filter(teksti);
        }
    }

    @PrePersist
    public void beforeKomoInsert() {
        if (this.getKoulutuksenTunnisteOid() == null) {
            this.setKoulutuksenTunnisteOid(this.getOid());
        }
        filterHTMLFields();
    }

    @PreUpdate
    public void beforeKomoUpdate() {
        filterHTMLFields();
    }

    public boolean isPseudo() {
        return isPseudo;
    }

    public void setPseudo(boolean isPseudo) {
        this.isPseudo = isPseudo;
    }

    public String getKoulutuksenTunnisteOid() {
        return koulutuksenTunnisteOid;
    }

    public void setKoulutuksenTunnisteOid(String koulutuksenTunnisteOid) {
        this.koulutuksenTunnisteOid = koulutuksenTunnisteOid;
    }
}
