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

import fi.vm.sade.generic.model.BaseEntity;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;

/**
 * KoulutusmoduuliToteutus (LearningOpportunityInstance) tarkentaa
 * Koulutusmoduuli:n tietoja ja antaa moduulille aika seka paikka ulottuvuuden.
 *
 */
@Entity
@Table(name = KoulutusmoduuliToteutus.TABLE_NAME)
public class KoulutusmoduuliToteutus extends BaseKoulutusmoduuli {

    public static final String TABLE_NAME = "koulutusmoduuli_toteutus";
    private static final long serialVersionUID = -1278564574746813425L;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "koulutusmoduuli_id", nullable = false)
    private Koulutusmoduuli koulutusmoduuli;
    @Column(name = "tarjoaja")
    private String tarjoaja;
    /**
     * Example display values 'Nuorten koulutus, Aikuisten koulutus'.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_koulutuslaji", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> koulutuslajis = new HashSet<KoodistoUri>();
    /**
     * todo: can we set this attribute to "required"?
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "koulutuksen_alkamis_pvm")
    private Date koulutuksenAlkamisPvm;
    @Column(name = "suunniteltu_kesto_arvo")
    private String suunniteltuKestoArvo;
    @Column(name = "suunniteltu_kesto_yksikko")
    private String suunniteltuKestoYksikko;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_teema", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> teemas = new HashSet<KoodistoUri>();
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_avainsana", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> avainsanas = new HashSet<KoodistoUri>();
    @Size(min = 1)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_opetuskieli", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetuskielis = new HashSet<KoodistoUri>();
    @Size(min = 1)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_opetusmuoto", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetusmuotos = new HashSet<KoodistoUri>();
    /**
     * If non-null, this "koulutus" comes with a charge. This field defines the
     * amount of the charge. The actual content of this field is yet to be
     * defined.
     */
    private String maksullisuus;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "koulutus_hakukohde", joinColumns =
    @JoinColumn(name = "koulutus_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns =
    @JoinColumn(name = "hakukohde_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Yhteyshenkilo> yhteyshenkilos = new HashSet<Yhteyshenkilo>();
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_linkki", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<WebLinkki> linkkis = new HashSet<WebLinkki>();
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "maksullisuus_teksti_id")
    private MonikielinenTeksti maksullisuusUrl;
    @Column(name = "ulkoinentunniste")
    private String ulkoinenTunniste;
    @Column(name = "koulutusaste")
    private String koulutusaste;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "arviointikriteerit")
    private MonikielinenTeksti arviointikriteerit;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "loppukoevaatimukset")
    private MonikielinenTeksti loppukoeVaatimukset;
    @Column(name = "pohjakoulutusvaatimus")
    private String pohjakoulutusvaatimus;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "painotus")
    private MonikielinenTeksti painotus;

    /*
     * Koulutuksen Lisatiedot  (additional information)
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_ammattinimike", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> ammattinimikes = new HashSet<KoodistoUri>();
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "kuvailevattiedot")
    private MonikielinenTeksti kuvailevatTiedot;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sisalto")
    private MonikielinenTeksti sisalto;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sijoittuminentyoelamaan")
    private MonikielinenTeksti sijoittuminenTyoelamaan;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "kansainvalistyminen")
    private MonikielinenTeksti kansainvalistyminen;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "yhteistyomuidentoimijoidenkanssa")
    private MonikielinenTeksti yhteistyoMuidenToimijoidenKanssa;

    public KoulutusmoduuliToteutus() {
        super();
    }

    /**
     *
     * @param moduuli Koulutusmoduuli jota tämä toteutus tarkentaa
     */
    public KoulutusmoduuliToteutus(Koulutusmoduuli moduuli) {
        setKoulutusmoduuli(moduuli);
    }

    /**
     *
     * @param moduuli
     */
    public final void setKoulutusmoduuli(Koulutusmoduuli moduuli) {
        if (this.koulutusmoduuli != null && !this.koulutusmoduuli.equals(moduuli)) {
            throw new IllegalStateException("trying to change koulutusmoduuli from: "
                    + this.koulutusmoduuli + " to " + moduuli);
        }
        this.koulutusmoduuli = moduuli;
    }

    public Koulutusmoduuli getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    /**
     * Palauttaa oid:n joka viittaa organisaatioon joka tarjoaa tata koulutusta.
     *
     * @return
     */
    public String getTarjoaja() {
        return tarjoaja;
    }

    /**
     *
     */
    public void setTarjoaja(String organisaatioOid) {
        tarjoaja = organisaatioOid;
    }

    /**
     * Koodisto uri
     *
     * @return the koulutusLajiUri
     */
    public Set<KoodistoUri> getKoulutuslajis() {
        return Collections.unmodifiableSet(koulutuslajis);
    }

    /**
     * @param koulutuslajiUri the koulutusLajiUri to set
     */
    public void addKoulutuslaji(String koulutuslajiUri) {
        koulutuslajis.add(new KoodistoUri(koulutuslajiUri));
    }

    public void removeKoulutuslaji(String koulutuslajiUri) {
        koulutuslajis.remove(new KoodistoUri(koulutuslajiUri));
    }

    public void setKoulutuslajis(Collection<String> uris) {
        koulutuslajis.clear();
        for (String uri : uris) {
            koulutuslajis.add(new KoodistoUri(uri));
        }
    }

    /**
     * Replace all koulutuslahis from given values.
     *
     * @param uris
     */
    public void setKoulutuslajis(Set<KoodistoUri> uris) {
        this.koulutuslajis = uris;
    }

    /**
     * The date this KoulutusmoduuliToteutus is scheduled to start.
     *
     * @return the koulutuksenAlkamisPvm
     */
    public Date getKoulutuksenAlkamisPvm() {
        return koulutuksenAlkamisPvm;
    }

    /**
     * @param koulutuksenAlkamisPvm the koulutuksenAlkamisPvm to set
     */
    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        this.koulutuksenAlkamisPvm = koulutuksenAlkamisPvm;
    }

    /**
     *
     * @return the teemaUris
     */
    public Set<KoodistoUri> getTeemas() {
        return Collections.unmodifiableSet(teemas);
    }

    public void removeTeema(KoodistoUri uri) {
        teemas.remove(uri);
    }

    public void addTeema(KoodistoUri uri) {
        teemas.add(uri);
    }

    /**
     *
     * @param teemaUris the teemaUris to set
     */
    public void setTeemas(Set<KoodistoUri> teemaUris) {
        this.teemas = new HashSet<KoodistoUri>(teemaUris);
    }

    public Set<KoodistoUri> getAvainsanas() {
        return Collections.unmodifiableSet(avainsanas);
    }

    public void removeAvainsana(KoodistoUri avainsana) {
        avainsanas.remove(avainsana);
    }

    public void addAvainsana(KoodistoUri avainsana) {
        avainsanas.add(avainsana);
    }

    public void setAvainsanas(Set<KoodistoUri> avainsanas) {
        this.avainsanas = new HashSet<KoodistoUri>(avainsanas);
    }

    /**
     * Returns non-null value if this KoulutusmoduuliToteutus comes with a
     * charge or null if it is free-of-charge.
     *
     * @return the maksullisuus
     */
    public String getMaksullisuus() {
        return maksullisuus;
    }

    /**
     * Set amount of charge or null to make free-of-charge. Empty string will be
     * converted to null.
     *
     * @param maksullisuus the maksullisuus to set
     */
    public void setMaksullisuus(String maksullisuus) {
        this.maksullisuus = StringUtils.isEmpty(maksullisuus) ? null : maksullisuus;
    }

    /**
     * @return the hakukohdes
     */
    public Set<Hakukohde> getHakukohdes() {
        return Collections.unmodifiableSet(hakukohdes);
    }

    public void addHakukohde(Hakukohde hakukohde) {
        hakukohdes.add(hakukohde);
    }

    public void removeHakukohde(Hakukohde hakukohde) {
        hakukohdes.remove(hakukohde);
    }

    /**
     * @return the opetuskielis
     */
    public Set<KoodistoUri> getOpetuskielis() {
        return Collections.unmodifiableSet(opetuskielis);
    }

    /**
     * Replaces all opetuskielis from given values.
     *
     * @param uris
     */
    public void setOpetuskieli(Set<KoodistoUri> uris) {
        this.opetuskielis = uris;
    }

    /**
     * @param opetuskieli the opetuskielis to set
     */
    public void addOpetuskieli(KoodistoUri opetuskieli) {
        opetuskielis.add(opetuskieli);
    }

    public void removeOpetuskieli(KoodistoUri opetuskieli) {
        opetuskielis.remove(opetuskieli);
    }

    public void setAmmattinimikes(Set<KoodistoUri> ammattinimikes) {
        this.ammattinimikes = ammattinimikes;
    }

    public Set<KoodistoUri> getAmmattinimikes() {
        return Collections.unmodifiableSet(ammattinimikes);
    }

    public void addAmmattinimike(KoodistoUri ammattinimike) {
        ammattinimikes.add(ammattinimike);
    }

    public void removeAmmattinimike(KoodistoUri ammattinimike) {
        ammattinimikes.remove(ammattinimike);
    }

    public Set<KoodistoUri> getOpetusmuotos() {
        return Collections.unmodifiableSet(opetusmuotos);
    }

    public void addOpetusmuoto(KoodistoUri opetusmuoto) {
        opetusmuotos.add(opetusmuoto);
    }

    public void removeOpetusmuoto(KoodistoUri opetusmuoto) {
        opetusmuotos.remove(opetusmuoto);
    }

    /**
     * Replaces all opetusmuotos with given values..
     *
     * @param uris
     */
    public void setOpetusmuoto(Set<KoodistoUri> uris) {
        this.opetusmuotos = uris;
    }

    public void addLinkki(WebLinkki linkki) {
        linkkis.add(linkki);;
    }

    public void removeLinkki(WebLinkki linkki) {
        linkkis.remove(linkki);
    }

    public Set<WebLinkki> getLinkkis() {
        return Collections.unmodifiableSet(linkkis);
    }

    /**
     * Replace all linkkis from given values.
     *
     * @param linkkis
     */
    public void setLinkkis(Set<WebLinkki> linkkis) {
        this.linkkis = new HashSet<WebLinkki>(linkkis);
    }

    public Set<Yhteyshenkilo> getYhteyshenkilos() {
        return Collections.unmodifiableSet(yhteyshenkilos);
    }

    public void addYhteyshenkilo(Yhteyshenkilo henkilo) {
        yhteyshenkilos.add(henkilo);
    }

    public void removeYhteyshenkilo(Yhteyshenkilo henkilo) {
        yhteyshenkilos.remove(henkilo);
    }
    
    public void setYhteyshenkilos(Set<Yhteyshenkilo> yhteyshenkilos) {
        this.yhteyshenkilos.clear();
        this.yhteyshenkilos = yhteyshenkilos;
    }

    /**
     * @return the maksullisuusUrl
     */
    public MonikielinenTeksti getMaksullisuusUrl() {
        return maksullisuusUrl;
    }

    /**
     * @param maksullisuusUrl the maksullisuusUrl to set
     */
    public void setMaksullisuusUrl(MonikielinenTeksti maksullisuusUrl) {
        this.maksullisuusUrl = maksullisuusUrl;
    }

    /**
     * Koulutuksen keston arvo. Yksikkö on eri kentässä: {@link #getSuunniteltuKestoYksikko()
     * }
     *
     * @return the suunniteltuKestoArvo
     */
    public String getSuunniteltuKestoArvo() {
        return suunniteltuKestoArvo;
    }

    /**
     * @param suunniteltuKestoArvo the suunniteltuKestoArvo to set
     */
    public void setSuunniteltuKesto(String kestoYksikkoUri, String suunniteltuKestoArvo) {
        this.suunniteltuKestoYksikko = kestoYksikkoUri;
        this.suunniteltuKestoArvo = suunniteltuKestoArvo;
    }

    /**
     * Koodisto uri joka kertoo käytetyt yksiköt kuten "vuosi", "päivä" yms.
     *
     * @return the suunniteltuKestoYksikko
     */
    public String getSuunniteltuKestoYksikko() {
        return suunniteltuKestoYksikko;
    }

    /**
     * Tunniste jolla koulutuksen tarjoaja yksiloi kyseisen koulutuksen.
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
     * Gets the koulutusaste koodisto uri
     *
     * @return the koulutusaste
     */
    public String getKoulutusaste() {
        return koulutusaste;
    }

    /**
     * Sets the koulutusaste koodisto uri
     *
     * @param koulutusaste the koulutusaste to set
     */
    public void setKoulutusaste(String koulutusaste) {
        this.koulutusaste = koulutusaste;
    }

    /**
     * Sanallinen kuvaus arviointikriteereistä.
     *
     * @return
     */
    public MonikielinenTeksti getArviointikriteerit() {
        return arviointikriteerit;
    }

    public void setArviointikriteerit(MonikielinenTeksti arviointikriteerit) {
        this.arviointikriteerit = arviointikriteerit;
    }

    /**
     * Sanallinen kuvaus koulutuksen loppukokeoista/opinnäytteistä.
     *
     * @return
     */
    public MonikielinenTeksti getLoppukoeVaatimukset() {
        return loppukoeVaatimukset;
    }

    public void setLoppukoeVaatimukset(MonikielinenTeksti loppukoeVaatimukset) {
        this.loppukoeVaatimukset = loppukoeVaatimukset;
    }

    /**
     * Koulutukselle määritelty vaatimus, jonka mukaan hakijalla (1) on oltava
     * tietty pohjakoulutus voidakseen tulla valituksi kyseiseen koulutukseen.
     * Pohjakoulutusvaatimuksen täyttäminen on yksi hakukelpoisuuden
     * edellytyksistä. Koulutuksen järjestäjät ja korkeakoulut voivat valita
     * hakijoita (1) opiskelijoiksi myös ilman pohjakoulutusvaatimusta, ks.
     * joustava valinta.
     *
     * Arvo on koodisto uri.
     *
     * @return
     */
    public String getPohjakoulutusvaatimus() {
        return pohjakoulutusvaatimus;
    }

    /**
     * @see #getPohjakoulutusvaatimus()
     * @param pohjakoulutusvaatimus
     */
    public void setPohjakoulutusvaatimus(String pohjakoulutusvaatimus) {
        this.pohjakoulutusvaatimus = pohjakoulutusvaatimus;
    }

    /**
     * Sanallinen kuvaus koulutuksesta.
     *
     * @return
     */
    public MonikielinenTeksti getKuvailevatTiedot() {
        return kuvailevatTiedot;
    }

    public void setKuvailevatTiedot(MonikielinenTeksti kuvailevatTiedot) {
        this.kuvailevatTiedot = kuvailevatTiedot;
    }

    /**
     * Sanallinen kuvaus koulutuksen sisöllöstä.
     *
     * @return
     */
    public MonikielinenTeksti getSisalto() {
        return sisalto;
    }

    public void setSisalto(MonikielinenTeksti sisalto) {
        this.sisalto = sisalto;
    }

    /**
     * Sanallinen kuvaus sijoittumisesta työelämään.
     *
     * @return
     */
    public MonikielinenTeksti getSijoittuminenTyoelamaan() {
        return sijoittuminenTyoelamaan;
    }

    public void setSijoittuminenTyoelamaan(MonikielinenTeksti sijoittuminenTyoelamaan) {
        this.sijoittuminenTyoelamaan = sijoittuminenTyoelamaan;
    }

    /**
     * Sanallinen kuvaus kansainvälistymisestä.
     *
     * @return
     */
    public MonikielinenTeksti getKansainvalistyminen() {
        return kansainvalistyminen;
    }

    public void setKansainvalistyminen(MonikielinenTeksti kansainvalistyminen) {
        this.kansainvalistyminen = kansainvalistyminen;
    }

    /**
     * Sanallinen kuvaus yhteistyöstä muiden toimijoiden kanssa.
     *
     * @return
     */
    public MonikielinenTeksti getYhteistyoMuidenToimijoidenKanssa() {
        return yhteistyoMuidenToimijoidenKanssa;
    }

    public void setYhteistyoMuidenToimijoidenKanssa(MonikielinenTeksti yhteistyoMuidenToimijoidenKanssa) {
        this.yhteistyoMuidenToimijoidenKanssa = yhteistyoMuidenToimijoidenKanssa;
    }

    /**
     * @return the painotus
     */
    public MonikielinenTeksti getPainotus() {
        return painotus;
    }

    /**
     * @param painotus the painotus to set
     */
    public void setPainotus(MonikielinenTeksti painotus) {
        if (this.painotus != null && this.painotus.getTekstis() == null) {
            this.painotus.getTekstis().clear();
        }
        this.painotus = painotus;
    }
}