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

import static fi.vm.sade.tarjonta.model.XSSUtil.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.AutowireHelper;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.shared.types.OpintopolkuAlkamiskausi;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * KoulutusmoduuliToteutus (LearningOpportunityInstance) tarkentaa
 * Koulutusmoduuli:n tietoja ja antaa moduulille aika seka paikka ulottuvuuden.
 */
@Entity
@JsonIgnoreProperties({"koulutusmoduuli", "hakukohdes", "id", "version", "koulutuslajis"})
@Table(name = KoulutusmoduuliToteutus.TABLE_NAME)
@Component
public class KoulutusmoduuliToteutus extends BaseKoulutusmoduuli {

    public static final String UNIQUE_EXTERNAL_ID_COLUMN_NAME = "uniqueExternalId";


    @Autowired
    @Transient
    KoulutusmoduuliDAO koulutusmoduuliDAO;

    public static final String TABLE_NAME = "koulutusmoduuli_toteutus";
    private static final long serialVersionUID = -1278564574746813425L;

    @Column(name = "toteutustyyppi")
    @Enumerated(EnumType.STRING)
    private ToteutustyyppiEnum toteutustyyppi;

    @Column(name = "unique_external_id", nullable = true, insertable = true, updatable = false, unique = true)
    private String uniqueExternalId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "koulutusmoduuli_id", nullable = false)
    private Koulutusmoduuli koulutusmoduuli;

    //    //Valmentava ja kuntouttava koulutus käyttää tätä nimeä
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nimi")
    private MonikielinenTeksti nimi;

    /**
     * Example display values 'Nuorten koulutus, Aikuisten koulutus'.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_koulutuslaji", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> koulutuslajis = new HashSet<KoodistoUri>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = TABLE_NAME + "_sisaltyvat_koulutuskoodit",
            joinColumns = @JoinColumn(name = TABLE_NAME + "_id")
    )
    private Set<KoodistoUri> sisaltyvatKoulutuskoodit = new HashSet<KoodistoUri>();

    @Deprecated
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_teema", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> teemas = new HashSet<KoodistoUri>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_aihe", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> aihees = new HashSet<KoodistoUri>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_avainsana", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> avainsanas = new HashSet<KoodistoUri>();
    //@Size(min = 1) REMOVED RESTRICTION BECAUSE NOT APPLICABLE FOR TUTKINTO KOMOTOS
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_opetuskieli", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetuskielis = new HashSet<KoodistoUri>();
    //@Size(min = 1) REMOVED RESTRICTION BECAUSE NOT APPLICABLE FOR TUTKINTO KOMOTOS
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_opetusmuoto", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetusmuotos = new HashSet<KoodistoUri>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_opetusaika", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetusAikas = new HashSet<KoodistoUri>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = TABLE_NAME + "_oppiaineet",
            joinColumns = @JoinColumn(name = TABLE_NAME + "_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "oppiaine_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME)
    )
    private Set<Oppiaine> oppiaineet = new HashSet<Oppiaine>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_opetuspaikka", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetusPaikkas = new HashSet<KoodistoUri>();
    private Boolean maksullisuus = false;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "koulutus_hakukohde", joinColumns
            = @JoinColumn(name = "koulutus_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns
            = @JoinColumn(name = "hakukohde_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Yhteyshenkilo> yhteyshenkilos = new HashSet<Yhteyshenkilo>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_linkki", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<WebLinkki> linkkis = new HashSet<WebLinkki>();

    /*
     * Koulutuksen Lisatiedot  (additional information)
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_ammattinimike", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> ammattinimikes = new HashSet<KoodistoUri>();

    //Lukiospesifeja kenttia
    @MapKey(name = "key")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String, Kielivalikoima> tarjotutKielet = new HashMap<String, Kielivalikoima>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_lukiodiplomi", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> lukiodiplomit = new HashSet<KoodistoUri>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_pohjakoulutusvaatimus", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    @Column(name = "kk_pohjakoulutusvaatimus")
    private Set<KoodistoUri> kkPohjakoulutusvaatimus = new HashSet<KoodistoUri>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = TABLE_NAME + "_tekstit", inverseJoinColumns = @JoinColumn(name = "monikielinen_teksti_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "teksti", nullable = false)
    private Map<KomotoTeksti, MonikielinenTeksti> tekstit = new HashMap<KomotoTeksti, MonikielinenTeksti>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = TABLE_NAME + "_kuvat", inverseJoinColumns = @JoinColumn(name = "binary_data_id"))
    @MapKeyColumn(name = "kieli_uri", nullable = false)
    private Map<String, BinaryData> kuvat = new HashMap<String, BinaryData>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_alkamispvm",
            joinColumns = @JoinColumn(name = TABLE_NAME + "_id"))
    @Column(name = "alkamispvm")
    private Set<Date> koulutuksenAlkamisPvms = new HashSet<Date>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_tutkintonimike", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> tutkintonimikes = new HashSet<KoodistoUri>();

    @OneToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "koulutusmoduuli_toteutus_children_id", nullable = true)
    private KoulutusmoduuliToteutus valmistavaKoulutus;

    @Column(name = "tarjoaja")
    private String tarjoaja;

    @Column(name = "avoimen_yliopiston_koulutus")
    private Boolean isAvoimenYliopistonKoulutus = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "koulutusmoduuli_toteutus_id", nullable = false)
    private Set<KoulutusOwner> owners = new HashSet<KoulutusOwner>();

    @Column(name = "viimPaivittajaOid")
    private String lastUpdatedByOid;

    @Column(name = "viimIndeksointiPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date viimIndeksointiPvm = null;

    @Column(name = "hinta")
    private BigDecimal hinta;

    @Column(name = "hinta_string")
    private String hintaString;

    @Column(name = "jarjesteja")
    private String jarjesteja;

    /**
     * Populoidaan aina.
     */
    @Column(name = "alkamisvuosi")
    private Integer alkamisVuosi;

    /**
     * Koodisto uri. See accessors for more info. Populoidaan aina.
     */
    @Column(name = "alkamiskausi_uri")
    private String alkamiskausiUri;

    /**
     * Real input value from user, not koodisto uri.
     */
    @Column(name = "opintojen_laajuusarvo")
    private String opintojenLaajuusarvo;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "pohjakoulutusvaatimus_uri")
    private String pohjakoulutusvaatimusUri;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "suunniteltukesto_yksikko_uri")
    private String suunniteltukestoYksikkoUri;

    /**
     * String value, not koodisto uri.
     */
    @Column(name = "suunniteltukesto_arvo")
    private String suunniteltukestoArvo;

    @Column(name = "koulutuksenloppumispvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date koulutuksenLoppumisPvm = null;

    /**
     * Se koulutus, josta tämä koulutus on järjestetty
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "tarjoajan_koulutus_id", nullable = true)
    private KoulutusmoduuliToteutus tarjoajanKoulutus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_koulutusryhma",
            joinColumns = @JoinColumn(name = TABLE_NAME + "_id"))
    @Column(name = "koulutusryhma_oid")
    private Set<String> koulutusRyhmaOids = new HashSet<String>();

    @Column(name = "oppiaine")
    private String oppiaine;

    @Column(name = "opettaja")
    private String opettaja;

    @Column(name = "opinnontyyppi_uri")
    private String opinnonTyyppiUri;

    @Column(name = "haun_kopioinnin_tunniste")
    private String haunKopioinninTunniste;

    @Column(name = "hakijalle_naytettava_tunniste")
    private String hakijalleNaytettavaTunniste;

    @Column(name = "opintopolku_alkamiskausi")
    @Enumerated(EnumType.STRING)
    private OpintopolkuAlkamiskausi.KaudetEnum opintopolkuAlkamiskausi;

    @Column(name = "koulutuksenlaajuus_uri")
    private String koulutuksenlaajuusUri;

    public String getOpintojenLaajuusArvo() {
        return opintojenLaajuusarvo;
    }

    public void setOpintojenLaajuusArvo(String opintojenLaajuusArvo) {
        this.opintojenLaajuusarvo = opintojenLaajuusArvo;
    }

    public Map<KomotoTeksti, MonikielinenTeksti> getTekstit() {
        return tekstit;
    }

    public void setTekstit(Map<KomotoTeksti, MonikielinenTeksti> tekstit) {
        this.tekstit = tekstit;
    }

    public Date getViimIndeksointiPvm() {
        return viimIndeksointiPvm;
    }

    public void setViimIndeksointiPvm(Date viimIndeksointiPvm) {
        this.viimIndeksointiPvm = viimIndeksointiPvm;
    }

    public KoulutusmoduuliToteutus() {
        super();
    }

    /**
     * @param moduuli Koulutusmoduuli jota tämä toteutus tarkentaa
     */
    public KoulutusmoduuliToteutus(Koulutusmoduuli moduuli) {
        setKoulutusmoduuli(moduuli);
    }

    /**
     * @param moduuli
     */
    public final void setKoulutusmoduuli(Koulutusmoduuli moduuli) {
        if (this.koulutusmoduuli != null && !this.koulutusmoduuli.equals(moduuli)) {
            throw new IllegalStateException("trying to change koulutusmoduuli from: "
                    + this.koulutusmoduuli + " to " + moduuli);
        }
        this.koulutusmoduuli = moduuli;
    }

    public final void overrideKoulutusmoduuli(Koulutusmoduuli komo) {
        this.koulutusmoduuli = komo;
    }

    public Koulutusmoduuli getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    /**
     * Koodisto uri
     *
     * @return the koulutusLajiUri
     */
    public Set<KoodistoUri> getKoulutuslajis() {
        if (koulutuslajis == null) {
            koulutuslajis = new HashSet<KoodistoUri>();
        }
        return koulutuslajis;
    }

    /**
     * This is for the json serializer (massakopiointi)
     */
    @JsonProperty
    public Set<KoodistoUri> getKoulutuslajit() {
        if (koulutuslajis == null) {
            koulutuslajis = new HashSet<KoodistoUri>();
        }

        return koulutuslajis;
    }

    /**
     * This is for the json serializer
     */
    public void setKoulutuslajit(Set<KoodistoUri> uris) {
        koulutuslajis.clear();
        koulutuslajis.addAll(uris);
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
     * @return the teemaUris
     * @deprecated not used!!
     */
    @Deprecated
    public Set<KoodistoUri> getTeemas() {
        return Collections.unmodifiableSet(teemas);
    }

    @Deprecated
    public void removeTeema(KoodistoUri uri) {
        teemas.remove(uri);
    }

    @Deprecated
    public void addTeema(KoodistoUri uri) {
        teemas.add(uri);
    }

    /**
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
    public Boolean getMaksullisuus() {
        return maksullisuus;
    }

    /**
     * Set amount of charge or null to make free-of-charge. Empty string will be
     * converted to null.
     *
     * @param maksullisuus the maksullisuus to set
     */
    public void setMaksullisuus(Boolean maksullisuus) {
        if (maksullisuus == null) {
            maksullisuus = false;
        }
        this.maksullisuus = maksullisuus;
    }

    /**
     * @return the hakukohdes
     */
    public Set<Hakukohde> getHakukohdes() {
        return hakukohdes;
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
        return opetuskielis;
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
        return opetusmuotos;
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
        linkkis.add(linkki);
    }

    public void removeLinkki(WebLinkki linkki) {
        linkkis.remove(linkki);
    }

    public Set<WebLinkki> getLinkkis() {
        return linkkis;
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

    public Map<String, Kielivalikoima> getTarjotutKielet() {
        return Collections.unmodifiableMap(tarjotutKielet);
    }

    public Kielivalikoima getKieliValikoima(String key) {
        Kielivalikoima ret = tarjotutKielet.get(key);
        if (ret == null) {
            ret = new Kielivalikoima();
            ret.setKey(key);
            tarjotutKielet.put(key, ret);
        }
        return ret;
    }

    public void setKieliValikoima(String key, Collection<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            getKieliValikoima(key).setKielet(codes);
        } else {
            // Map.remove ei toimi tässä (hibernaten "ominaisuus", pitäisi kutsua entitymanagerin removea jotta
            // poistuisi varmasti), siksi get ja set..
            Kielivalikoima kv = tarjotutKielet.get(key);
            if (kv != null) {
                kv.setKielet(new ArrayList<String>());
            }
        }
    }

    public Boolean getIsAvoimenYliopistonKoulutus() {
        return isAvoimenYliopistonKoulutus;
    }

    public void setIsAvoimenYliopistonKoulutus(Boolean isAvoimenYliopistonKoulutus) {
        this.isAvoimenYliopistonKoulutus = isAvoimenYliopistonKoulutus;
    }

    /**
     * @return the maksullisuusUrl
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getMaksullisuusUrl() {
        return tekstit.get(KomotoTeksti.MAKSULLISUUS);
    }

    /**
     * @param maksullisuusUrl the maksullisuusUrl to set
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setMaksullisuusUrl(MonikielinenTeksti maksullisuusUrl) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.MAKSULLISUUS, maksullisuusUrl);
    }

    /**
     * Koulutuksen keston arvo. Yksikkö on eri kentässä: {@link #getSuunniteltukestoYksikkoUri()
     * }
     *
     * @return the suunniteltuKestoArvo
     */
    public String getSuunniteltukestoArvo() {
        return suunniteltukestoArvo;
    }

    /**
     * @param suunniteltukestoArvo the suunniteltuKestoArvo to set
     */
    public void setSuunniteltuKesto(String suunniteltukestoYksikkoUri, String suunniteltukestoArvo) {
        this.suunniteltukestoYksikkoUri = suunniteltukestoYksikkoUri;
        this.suunniteltukestoArvo = suunniteltukestoArvo;
    }

    /**
     * Koodisto uri joka kertoo käytetyt yksiköt kuten "vuosi", "päivä" yms.
     *
     * @return the suunniteltuKestoYksikko
     */
    public String getSuunniteltukestoYksikkoUri() {
        return suunniteltukestoYksikkoUri;
    }

    /**
     * Sanallinen kuvaus arviointikriteereistä.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getArviointikriteerit() {
        return tekstit.get(KomotoTeksti.ARVIOINTIKRITEERIT);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setArviointikriteerit(MonikielinenTeksti arviointikriteerit) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.ARVIOINTIKRITEERIT, arviointikriteerit);
    }

    /**
     * Sanallinen kuvaus koulutuksen loppukokeoista/opinnäytteistä.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getLoppukoeVaatimukset() {
        return tekstit.get(KomotoTeksti.LOPPUKOEVAATIMUKSET);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setLoppukoeVaatimukset(MonikielinenTeksti loppukoeVaatimukset) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.LOPPUKOEVAATIMUKSET, loppukoeVaatimukset);
    }

    /**
     * Koulutukselle määritelty vaatimus, jonka mukaan hakijalla (1) on oltava
     * tietty pohjakoulutus voidakseen tulla valituksi kyseiseen koulutukseen.
     * Pohjakoulutusvaatimuksen täyttäminen on yksi hakukelpoisuuden
     * edellytyksistä. Koulutuksen järjestäjät ja korkeakoulut voivat valita
     * hakijoita (1) opiskelijoiksi myös ilman pohjakoulutusvaatimusta, ks.
     * joustava valinta.
     * <p/>
     * Arvo on koodisto uri.
     *
     * @return
     */
    public String getPohjakoulutusvaatimusUri() {
        return pohjakoulutusvaatimusUri;
    }

    /**
     * @param pohjakoulutusvaatimusUri
     * @see #getPohjakoulutusvaatimusUri()
     */
    public void setPohjakoulutusvaatimusUri(String pohjakoulutusvaatimusUri) {
        this.pohjakoulutusvaatimusUri = pohjakoulutusvaatimusUri;
    }

    /**
     * Sanallinen kuvaus koulutuksesta.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getKuvailevatTiedot() {
        return tekstit.get(KomotoTeksti.KUVAILEVAT_TIEDOT);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setKuvailevatTiedot(MonikielinenTeksti kuvailevatTiedot) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.KUVAILEVAT_TIEDOT, kuvailevatTiedot);
    }

    /**
     * Sanallinen kuvaus koulutuksen sisöllöstä.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getSisalto() {
        return tekstit.get(KomotoTeksti.SISALTO);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setSisalto(MonikielinenTeksti sisalto) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.SISALTO, sisalto);
    }

    /**
     * Sanallinen kuvaus sijoittumisesta työelämään.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getSijoittuminenTyoelamaan() {
        return tekstit.get(KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setSijoittuminenTyoelamaan(MonikielinenTeksti sijoittuminenTyoelamaan) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.SIJOITTUMINEN_TYOELAMAAN, sijoittuminenTyoelamaan);
    }

    /**
     * Sanallinen kuvaus kansainvälistymisestä.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getKansainvalistyminen() {
        return tekstit.get(KomotoTeksti.KANSAINVALISTYMINEN);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setKansainvalistyminen(MonikielinenTeksti kansainvalistyminen) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.KANSAINVALISTYMINEN, kansainvalistyminen);
    }

    /**
     * Sanallinen kuvaus yhteistyöstä muiden toimijoiden kanssa.
     *
     * @return
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getYhteistyoMuidenToimijoidenKanssa() {
        return tekstit.get(KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setYhteistyoMuidenToimijoidenKanssa(MonikielinenTeksti yhteistyoMuidenToimijoidenKanssa) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.YHTEISTYO_MUIDEN_TOIMIJOIDEN_KANSSA, yhteistyoMuidenToimijoidenKanssa);
    }

    /**
     * @return the painotus
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getPainotus() {
        return tekstit.get(KomotoTeksti.PAINOTUS);
    }

    /**
     * @param painotus the painotus to set
     */
    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setPainotus(MonikielinenTeksti painotus) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.PAINOTUS, painotus);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getKoulutusohjelmanValinta() {
        return tekstit.get(KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setKoulutusohjelmanValinta(MonikielinenTeksti koulutusohjelmanValinta) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.KOULUTUSOHJELMAN_VALINTA, koulutusohjelmanValinta);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getLisatietoaOpetuskielista() {
        return tekstit.get(KomotoTeksti.LISATIETOA_OPETUSKIELISTA);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setLisatietoaOpetuskielista(MonikielinenTeksti tavoitteet) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.LISATIETOA_OPETUSKIELISTA, tavoitteet);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public MonikielinenTeksti getTutkimuksenPainopisteet() {
        return tekstit.get(KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET);
    }

    @Deprecated // TODO näitä kenttiä olisi parempi käsitellä suoraan mappina
    public void setTutkimuksenPainopisteet(MonikielinenTeksti tavoitteet) {
        MonikielinenTeksti.merge(tekstit, KomotoTeksti.TUTKIMUKSEN_PAINOPISTEET, tavoitteet);
    }

    public void setLukiodiplomit(Set<KoodistoUri> lukiodiplomit) {
        this.lukiodiplomit.clear();
        this.lukiodiplomit = lukiodiplomit;
    }

    public Set<KoodistoUri> getLukiodiplomit() {
        return lukiodiplomit;
    }

    public void addLukiodiplomi(KoodistoUri lukiodiplomi) {
        lukiodiplomit.add(lukiodiplomi);
    }

    public void removeLukiodiplomi(KoodistoUri lukiodiplomi) {
        lukiodiplomit.remove(lukiodiplomi);
    }

    public String getLastUpdatedByOid() {
        return lastUpdatedByOid;
    }

    public void setLastUpdatedByOid(String lastUpdatedByOid) {
        this.lastUpdatedByOid = lastUpdatedByOid;
    }

    /**
     * @return the kkPohjakoulutusvaatimus
     */
    public Set<KoodistoUri> getKkPohjakoulutusvaatimus() {
        return kkPohjakoulutusvaatimus;
    }

    /**
     * @param kkPohjakoulutusvaatimus the kkPohjakoulutusvaatimus to set
     */
    public void setKkPohjakoulutusvaatimus(Set<KoodistoUri> kkPohjakoulutusvaatimus) {
        this.kkPohjakoulutusvaatimus = kkPohjakoulutusvaatimus;
    }

    /**
     * @return the hinta
     */
    public BigDecimal getHinta() {
        return hinta;
    }

    /**
     * @param hinta the hinta to set
     */
    public void setHinta(BigDecimal hinta) {
        this.hinta = hinta;
    }

    public String getHintaString() {
        return hintaString;
    }

    public void setHintaString(String hintaString) {
        this.hintaString = hintaString;
    }

    public String getAlkamiskausiUri() {
        return alkamiskausiUri;
    }

    public void setAlkamiskausiUri(String alkamiskausiUri) {
        this.alkamiskausiUri = alkamiskausiUri;
    }

    public Integer getAlkamisVuosi() {
        return alkamisVuosi;
    }

    public void setAlkamisVuosi(Integer alkamisVuosi) {
        this.alkamisVuosi = alkamisVuosi;
    }

    /**
     * @return the kuvat
     */
    public Map<String, BinaryData> getKuvat() {
        return kuvat;
    }

    /**
     * @param kuvat the kuvat to set
     */
    public void setKuvat(Map<String, BinaryData> kuvat) {
        this.kuvat = kuvat;
    }

    public boolean isKuva(String kielikoodi) {
        return kuvat.containsKey(kielikoodi);
    }

    public final void setKuvaByUri(String kielikoodi, BinaryData binaryData) {
        kuvat.put(kielikoodi, binaryData);
    }

    public Set<KoodistoUri> getAihees() {
        return aihees;
    }

    public void setAihees(Set<KoodistoUri> aihees) {
        this.aihees = aihees;
    }

    public Set<KoodistoUri> getOpetusAikas() {
        return opetusAikas;
    }

    public void setOpetusAikas(Set<KoodistoUri> opetusAikas) {
        this.opetusAikas = opetusAikas;
    }

    public Set<KoodistoUri> getOpetusPaikkas() {
        return opetusPaikkas;
    }

    public void setOpetusPaikkas(Set<KoodistoUri> opetusPaikkas) {
        this.opetusPaikkas = opetusPaikkas;
    }

    /**
     * Korkeakouluilla voi olla monta aloituspaivanmaaraa.
     *
     * @return the koulutuksenAlkamisPvms
     */
    public Set<Date> getKoulutuksenAlkamisPvms() {
        return koulutuksenAlkamisPvms;
    }

    /**
     * Korkeakouluilla voi olla monta aloituspaivanmaaraa.
     *
     * @param koulutuksenAlkamisPvms the koulutuksenAlkamisPvms to set
     */
    public void setKoulutuksenAlkamisPvms(Set<Date> koulutuksenAlkamisPvms) {
        this.koulutuksenAlkamisPvms = koulutuksenAlkamisPvms;
    }

    public void addKoulutuksenAlkamisPvms(Date koulutuksenAlkamisPvm) {
        Preconditions.checkNotNull(koulutuksenAlkamisPvm, "koulutuksenAlkamisPvm date object cannot be null.");
        final Date dateWithoutMinutes = DateUtils.truncate(koulutuksenAlkamisPvm, Calendar.DATE);

        if (!this.koulutuksenAlkamisPvms.contains(dateWithoutMinutes)) {
            this.koulutuksenAlkamisPvms.add(dateWithoutMinutes);
        }
    }

    public void clearKoulutuksenAlkamisPvms() {
        this.koulutuksenAlkamisPvms.clear();
    }

    /**
     * Amattillisella- ja lukiokoulutuksella on vain yksi aloituspvm.
     *
     * @param koulutuksenAlkamisPvm
     */
    public void setKoulutuksenAlkamisPvm(Date koulutuksenAlkamisPvm) {
        Preconditions.checkNotNull(koulutuksenAlkamisPvm, "koulutuksenAlkamisPvm date object cannot be null.");
        EntityUtils.keepSelectedDates(this.koulutuksenAlkamisPvms, koulutuksenAlkamisPvm);
        this.koulutuksenAlkamisPvms.add(DateUtils.truncate(koulutuksenAlkamisPvm, Calendar.DATE));
    }

    public Date getKoulutuksenAlkamisPvm() {
        if (this.koulutuksenAlkamisPvms.size() > 1) {
            throw new RuntimeException("Not allowed error - Too many starting dates, maybe you are using a wrong method?");
        } else if (koulutuksenAlkamisPvms.isEmpty()) {
            //at least parent komoto's date can be null.
            return null;
        }

        return this.koulutuksenAlkamisPvms.iterator().next();
    }

    public int getUniqueAlkamisVuosi() {
        Integer vuosi = this.getAlkamisVuosi();
        for (Date pvm : this.getKoulutuksenAlkamisPvms()) {
            int v = IndexDataUtils.parseYearInt(pvm);
            if (vuosi != null && vuosi != v) {
                throw new IllegalStateException(String.format(
                        "Koulutuksen %s alkamisvuodet ovat ristiriitaiset", this.getOid()
                ));
            } else {
                vuosi = v;
            }
        }
        if (vuosi == null) {
            throw new IllegalStateException(String.format(
                    "Koulutuksella %s ei ole alkamisvuotta", this.getOid()
            ));
        }
        return vuosi;
    }

    public String getUniqueAlkamiskausiUri() {
        String kausiUri = this.getAlkamiskausiUri();
        for (Date pvm : this.getKoulutuksenAlkamisPvms()) {
            String k = IndexDataUtils.parseKausiKoodi(pvm);
            if (kausiUri != null && !kausiUri.equals(k)) {
                throw new IllegalStateException(String.format(
                        "Koulutuksen %s alkamiskaudet ovat ristiriitaiset", this.getOid()
                ));
            } else {
                kausiUri = k;
            }
        }
        if (kausiUri == null) {
            throw new IllegalStateException(String.format(
                    "Koulutuksella %s ei ole alkamiskautta", this.getOid()
            ));
        }
        return kausiUri;
    }

    public Date getMinAlkamisPvm() {
        if (koulutuksenAlkamisPvms.isEmpty()) {
            return null;
        }
        return Ordering.<Date>natural().min(koulutuksenAlkamisPvms);
    }

    public Date getMaxAlkamisPvm() {
        if (koulutuksenAlkamisPvms.isEmpty()) {
            return null;
        }
        return Ordering.<Date>natural().max(koulutuksenAlkamisPvms);
    }

    public MonikielinenTeksti getNimi() {
        return nimi;
    }

    public void setNimi(MonikielinenTeksti nimi) {
        this.nimi = nimi;
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
    public void beforeKomotoInsert() {
        filterHTMLFields();
        fixTelmaKoulutus();
    }

    @PreUpdate
    public void beforeKomotoUpdate() {
        filterHTMLFields();
    }

    /**
     * Valmentava ja kuntouttava muuttui TELMA-koulutukseksi. Kopioinnin yhteydessä:
     * - poista käsin syötettävä linja
     * - poista koulutusohjelma
     * - aseta komoksi ylätason komo (koska ei ole enää koulutuoshjelma-komoa)
     */
    private void fixTelmaKoulutus() {
        if (ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS.equals(this.toteutustyyppi)) {
            AutowireHelper.autowire(this, this.koulutusmoduuliDAO);
            this.setNimi(null);
            this.setKoulutusohjelmaUri(null);
            Koulutusmoduuli parentKomo = koulutusmoduuliDAO.findParentKomo(this.getKoulutusmoduuli());
            if (parentKomo != null) {
                this.overrideKoulutusmoduuli(parentKomo);
            }
        }
    }

    /**
     * @return the tutkintonimikes
     */
    public Set<KoodistoUri> getTutkintonimikes() {
        return tutkintonimikes;
    }

    /**
     * @param tutkintonimikes the tutkintonimikes to set
     */
    public void setTutkintonimikes(Set<KoodistoUri> tutkintonimikes) {
        this.tutkintonimikes = tutkintonimikes;
    }

    public String getTutkintonimikeUri() {
        return getTutkintonimikeUri(tutkintonimikes);
    }

    public void setTutkintonimikeUri(String tutkintonimike) {
        if (tutkintonimike == null) {
            this.tutkintonimikes.clear();
        } else {
            final KoodistoUri koodistoUri = new KoodistoUri(tutkintonimike);
            EntityUtils.keepSelectedKoodistoUri(this.tutkintonimikes, koodistoUri);
            tutkintonimikes.add(koodistoUri);
        }
    }

    /**
     * Tarjonta education identification enum, based on OPH's koulutustyyppi
     * code uri.
     *
     * @return the toteutustyyppi
     */
    public ToteutustyyppiEnum getToteutustyyppi() {
        return toteutustyyppi;
    }

    /**
     * Tarjonta education identification enum, based on OPH's koulutustyyppi
     * code uri.
     *
     * @param tyyppi the toteutustyyppi to set
     */
    public void setToteutustyyppi(ToteutustyyppiEnum tyyppi) {
        this.toteutustyyppi = tyyppi;
    }

    /**
     * @return the nayttotutkintoValmistavaKoulutus
     */
    public KoulutusmoduuliToteutus getValmistavaKoulutus() {
        return valmistavaKoulutus;
    }

    /**
     * @param valmistavaKoulutus the nayttotutkintoValmistavaKoulutus to set
     */
    public void setValmistavaKoulutus(KoulutusmoduuliToteutus valmistavaKoulutus) {
        this.valmistavaKoulutus = valmistavaKoulutus;
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
        this.tarjoaja = organisaatioOid;
    }

    /**
     * @return the jarjesteja OID
     */
    public String getJarjesteja() {
        return jarjesteja;
    }

    /**
     * @param jarjesteja OID the jarjesteja to set
     */
    public void setJarjesteja(String jarjesteja) {
        this.jarjesteja = jarjesteja;
    }

    /**
     * Manage different kind of relations to this object.
     *
     * @return
     */
    public Set<KoulutusOwner> getOwners() {
        return owners;
    }

    public void setOwners(Set<KoulutusOwner> owners) {
        this.owners = owners;
    }

    public Set<String> getOwnerOids() {
        Set<String> oids = new HashSet<String>();
        for (KoulutusOwner owner : owners) {
            oids.add(owner.getOwnerOid());
        }
        return oids;
    }

    public Set<String> getTarjoajaOids() {
        Set<String> oids = new HashSet<String>();
        for (KoulutusOwner owner : owners) {
            if (KoulutusOwner.TARJOAJA.equals(owner.getOwnerType())) {
                oids.add(owner.getOwnerOid());
            }
        }

        if (oids.isEmpty()) {
            oids.add(getTarjoaja());
        }

        return oids;
    }

    public Set<String> getJarjestajaOids() {
        Set<String> oids = new HashSet<String>();
        for (KoulutusOwner owner : owners) {
            if (KoulutusOwner.JARJESTAJA.equals(owner.getOwnerType())) {
                oids.add(owner.getOwnerOid());
            }
        }
        return oids;
    }

    public List<String> getKoulutuslajiKoodiUris() {
        List<String> koodiUris = new ArrayList<String>();
        for (KoodistoUri koulutuslaji : koulutuslajis) {
            koodiUris.add(koulutuslaji.getKoodiUri());
        }
        return koodiUris;
    }

    /**
     * Koulutuksen loppumispvm, tutkintoonjohtamattomille koulutuksille.
     * @return
     */
    public Date getKoulutuksenLoppumisPvm() {
        return koulutuksenLoppumisPvm;
    }

    public void setKoulutuksenLoppumisPvm(Date koulutuksenLoppumisPvm) {
        this.koulutuksenLoppumisPvm = koulutuksenLoppumisPvm;
    }

    public String getOpinnonTyyppiUri() {
        return opinnonTyyppiUri;
    }

    public void setOpinnonTyyppiUri(String opinnonTyyppiUri) {
        this.opinnonTyyppiUri = opinnonTyyppiUri;
    }

    public KoulutusmoduuliToteutus getTarjoajanKoulutus() {
        return tarjoajanKoulutus;
    }

    public void setTarjoajanKoulutus(KoulutusmoduuliToteutus tarjoajanKoulutus) {
        this.tarjoajanKoulutus = tarjoajanKoulutus;
    }

    public String getOppiaine() {
        return oppiaine;
    }

    public void setOppiaine(String oppiaine) {
        this.oppiaine = oppiaine;
    }

    public String getOpettaja() {
        return opettaja;
    }

    public void setOpettaja(String opettaja) {
        this.opettaja = opettaja;
    }

    public Set<String> getKoulutusRyhmaOids() {
        return koulutusRyhmaOids;
    }

    public void setKoulutusRyhmaOids(Set<String> koulutusRyhmaOids) {
        this.koulutusRyhmaOids = koulutusRyhmaOids;
    }



    /**
     * Syksyn 2015 koulutukset ja myöhemmät käyttävät osaamisala-koodistoa vanhan
     * koulutusohjelma-koodiston sijasta
     * @return boolean
     */
    public boolean isSyksy2015OrLater() {
        String kausi = getKoodiUriWithoutVersion(this.getAlkamiskausiUri());

        boolean isSyksy2015 = this.getAlkamisVuosi() == 2015 && kausi.equals("kausi_s");
        boolean isLater = this.getAlkamisVuosi() > 2015;

        return isSyksy2015 || isLater;
    }

    public String getKoodiUriWithoutVersion(String koodiUri) {
        if (koodiUri == null) {
            return null;
        }

        int hashPos = koodiUri.indexOf('#');

        if (hashPos == -1) {
            return koodiUri;
        }

        return koodiUri.substring(0, hashPos);
    }

    public Set<Oppiaine> getOppiaineet() {
        return oppiaineet;
    }

    public void setOppiaineet(Set<Oppiaine> oppiaineet) {
        this.oppiaineet = oppiaineet;
    }

    public String getHaunKopioinninTunniste() {
        return haunKopioinninTunniste;
    }

    public void setHaunKopioinninTunniste(String haunKopioinninTunniste) {
        this.haunKopioinninTunniste = haunKopioinninTunniste;
    }

    public OpintopolkuAlkamiskausi.KaudetEnum getOpintopolkuAlkamiskausi() {
        return opintopolkuAlkamiskausi;
    }

    public void setOpintopolkuAlkamiskausi(OpintopolkuAlkamiskausi.KaudetEnum opintopolkuAlkamiskausi) {
        this.opintopolkuAlkamiskausi = opintopolkuAlkamiskausi;
    }

    public Set<KoodistoUri> getSisaltyvatKoulutuskoodit() {
        return sisaltyvatKoulutuskoodit;
    }

    public void setSisaltyvatKoulutuskoodit(Set<KoodistoUri> sisaltyvatKoulutuskoodit) {
        this.sisaltyvatKoulutuskoodit = sisaltyvatKoulutuskoodit;
    }

    public String getHakijalleNaytettavaTunniste() {
        return hakijalleNaytettavaTunniste;
    }

    public void setHakijalleNaytettavaTunniste(String hakijalleNaytettavaTunniste) {
        this.hakijalleNaytettavaTunniste = hakijalleNaytettavaTunniste;
    }

    public String getUniqueExternalId() {
        return uniqueExternalId;
    }

    public void setUniqueExternalId(String uniqueExternalId) {
        this.uniqueExternalId = uniqueExternalId;
    }

    /**
     * @return the koulutuksenlaajuusUri
     */
    public String getKoulutuksenlaajuusUri() {
        return koulutuksenlaajuusUri;
    }


    /**
     * @param koulutuksenlaajuusUri the koulutuksenlaajuusUri to set
     */
    public void setKoulutuksenlaajuusUri(String koulutuksenlaajuusUri) {
        this.koulutuksenlaajuusUri = koulutuksenlaajuusUri;
    }

    public boolean alkaaEnnenReformia() {
        if ((getKoulutuksenAlkamisPvms() == null || getKoulutuksenAlkamisPvms().isEmpty())
                &&
                (getAlkamisVuosi() == null || getAlkamisVuosi() >= 2018)) {
            return false;
        }

        Date beginningOfJanuary2018 = Date.from(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("EET")).toInstant());
        return getKoulutuksenAlkamisPvms().stream().allMatch(a -> a.before(beginningOfJanuary2018));
    }

}
