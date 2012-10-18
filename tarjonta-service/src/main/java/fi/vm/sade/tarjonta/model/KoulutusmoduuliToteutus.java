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
 * KoulutusmoduuliToteutus (LearningOpportunityInstance) tarkentaa Koulutusmoduuli:n tietoja ja antaa
 * moduulille aika seka paikka ulottuvuuden.
 *
 */
@Entity
@Table(name = KoulutusmoduuliToteutus.TABLE_NAME)
public class KoulutusmoduuliToteutus extends BaseKoulutusmoduuli {

    public static final String TABLE_NAME = "koulutusmoduuli_toteutus";

    private static final long serialVersionUID = -1278564574746813425L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(name = TABLE_NAME + "_koulutusmoduuli")
    private Koulutusmoduuli koulutusmoduuli;

    @Column(name = "tarjoaja")
    private String tarjoaja;

    /**
     * Example display values 'Nuorten koulutus, Aikuisten koulutus'.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_koulutuslaji", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> koulutuslajiList;

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

    @Size(min = 1)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_opetuskieli", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetuskielis = new HashSet<KoodistoUri>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_ammattinimike", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> ammattinimikes = new HashSet<KoodistoUri>();

    @Size(min = 1)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_opetusmuoto", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetusmuotos = new HashSet<KoodistoUri>();

    /**
     * If non-null, this "koulutus" comes with a charge. This field defines the amount of the charge.
     * The actual content of this field is yet to be defined.
     */
    private String maksullisuus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "koulutus_hakukohde", joinColumns =
    @JoinColumn(name = "koulutus_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns =
    @JoinColumn(name = "hakukohde_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Yhteyshenkilo> yhteyshenkilos = new HashSet<Yhteyshenkilo>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "opetussuunnitelma_teskti_id")
    private MonikielinenTeksti opetussuunnitelmaUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "oppilaitos_teksti_id")
    private MonikielinenTeksti oppilaitosUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sosiaalinenmedia_teksti_id")
    private MonikielinenTeksti sosiaalinenMediaUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "maksullisuus_teksti_id")
    private MonikielinenTeksti maksullisuusUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stipendimahdollisuus_teksti_id")
    private MonikielinenTeksti stipendimahdollisuusUrl;

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
    public Set<KoodistoUri> getKoulutuslajiList() {
        return Collections.unmodifiableSet(koulutuslajiList);
    }

    /**
     * @param koulutuslajiUri the koulutusLajiUri to set
     */
    public void addKoulutuslaji(String koulutuslajiUri) {
        koulutuslajiList.add(new KoodistoUri(koulutuslajiUri));
    }

    public void removeKoulutuslaji(String koulutuslajiUri) {
        koulutuslajiList.remove(new KoodistoUri(koulutuslajiUri));
    }

    public void setKoulutuslajiList(Collection<String> uris) {
        koulutuslajiList.clear();
        for (String uri : uris) {
            koulutuslajiList.add(new KoodistoUri(uri));
        }
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
        this.teemas = teemaUris;
    }

    /**
     * Returns non-null value if this KoulutusmoduuliToteutus comes with a charge or null if it is free-of-charge.
     *
     * @return the maksullisuus
     */
    public String getMaksullisuus() {
        return maksullisuus;
    }

    /**
     * Set amount of charge or null to make free-of-charge. Empty string will be converted to null.
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
     * @param opetuskielis the opetuskielis to set
     */
    public void addOpetuskieli(KoodistoUri opetuskieli) {
        opetuskielis.add(opetuskieli);
    }

    public void removeOpetuskieli(KoodistoUri opetuskieli) {
        opetuskielis.remove(opetuskieli);
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

    public Set<Yhteyshenkilo> getYhteyshenkilos() {
        return Collections.unmodifiableSet(yhteyshenkilos);
    }

    public void addYhteyshenkilo(Yhteyshenkilo henkilo) {
        yhteyshenkilos.add(henkilo);
    }

    public void removeYhteyshenkilo(Yhteyshenkilo henkilo) {
        yhteyshenkilos.remove(henkilo);
    }

    public MonikielinenTeksti getOppilaitosUrl() {
        return oppilaitosUrl;
    }

    public void setOppilaitosUrl(MonikielinenTeksti oppilaitosUrl) {
        this.oppilaitosUrl = oppilaitosUrl;
    }

    public MonikielinenTeksti getOpetussuunnitelmaUrl() {
        return opetussuunnitelmaUrl;
    }

    public void setOpetussuunnitelmaUrl(MonikielinenTeksti opetussuunnitelmaUrl) {
        this.opetussuunnitelmaUrl = opetussuunnitelmaUrl;
    }

    /**
     * @return the sosiaalinenMediaUrl
     */
    public MonikielinenTeksti getSosiaalinenMediaUrl() {
        return sosiaalinenMediaUrl;
    }

    /**
     * @param sosiaalinenMediaUrl the sosiaalinenMediaUrl to set
     */
    public void setSosiaalinenMediaUrl(MonikielinenTeksti sosiaalinenMediaUrl) {
        this.sosiaalinenMediaUrl = sosiaalinenMediaUrl;
    }

    /**
     * @return the stipendimahdollisuusUrl
     */
    public MonikielinenTeksti getStipendimahdollisuusUrl() {
        return stipendimahdollisuusUrl;
    }

    /**
     * @param stipendimahdollisuusUrl the stipendimahdollisuusUrl to set
     */
    public void setStipendimahdollisuusUrl(MonikielinenTeksti stipendimahdollisuusUrl) {
        this.stipendimahdollisuusUrl = stipendimahdollisuusUrl;
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
     * Koulutuksen keston arvo. Yksikkö on eri kentässä: {@link #getSuunniteltuKestoYksikko() }
     *
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
     * Koodisto uri joka kertoo käytetyt yksiköt kuten "vuosi", "päivä" yms.
     *
     * @return the suunniteltuKestoYksikko
     */
    public String getSuunniteltuKestoYksikko() {
        return suunniteltuKestoYksikko;
    }

    /**
     * @param suunniteltuKestoYksikko the suunniteltuKestoYksikko to set
     */
    public void setSuunniteltuKestoYksikko(String suunniteltuKestoYksikko) {
        this.suunniteltuKestoYksikko = suunniteltuKestoYksikko;
    }

}

