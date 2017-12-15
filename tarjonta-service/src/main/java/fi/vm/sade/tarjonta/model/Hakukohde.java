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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.*;

import static fi.vm.sade.generic.common.validation.ValidationConstants.WWW_PATTERN;
import static fi.vm.sade.tarjonta.model.XSSUtil.filter;

/**
 *
 */
@Entity
@JsonIgnoreProperties({"koulutusmoduuliToteutuses", "haku", "id", "version", "ryhmaliitokset"})
@Table(name = Hakukohde.TABLE_NAME)
public class Hakukohde extends TarjontaBaseEntity {

    public static final String TABLE_NAME = "hakukohde";
    private static final long serialVersionUID = -3320464257959195992L;
    private static final Date NO_HAKUAIKA_PVM = new Date(0);

    @Column(name = "oid", unique = true, updatable = false, nullable = false)
    private String oid;

    @Column(name = "unique_external_id", nullable = true, insertable = true, updatable = false, unique = true)
    private String uniqueExternalId;

    @ManyToMany(mappedBy = "hakukohdes", cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<KoulutusmoduuliToteutus> koulutusmoduuliToteutuses = new HashSet<KoulutusmoduuliToteutus>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "hakukohde", orphanRemoval = true)
    @OrderBy("id ASC")
    @Sort(type = SortType.NATURAL)
    private SortedSet<Valintakoe> valintakoes = new TreeSet<>();

    /**
     * The koodisto uri of the name of this hakukohde object.
     */
    @Column(name = "hakukohde_nimi")
    private String hakukohdeNimi;

    /**
     * The string containing the human readable name of this hakukohde object.
     * Names in different languages are concatenated to this field. This field
     * is created to enable search.
     */
    @Column(name = "hakukohde_koodisto_nimi")
    private String hakukohdeKoodistoNimi;
    @Column(name = "alin_valinta_pistamaara")
    private Integer alinValintaPistemaara;
    @Column(name = "ylin_valinta_pistemaara")
    private Integer ylinValintaPistemaara;
    @Column(name = "ensikertalaisten_aloituspaikat")
    private Integer ensikertalaistenAloituspaikat;
    @Column(name = "aloituspaikat_lkm", nullable = false)
    private int aloituspaikatLkm;
    @Column(name = "valintojenAloituspaikatLkm", nullable = false)
    private int valintojenAloituspaikatLkm;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "aloituspaikat_teksti_id")
    private MonikielinenTeksti aloituspaikatKuvaus;
    @Column(name = "kaytetaanHaunPaattymisenAikaa", nullable = false)
    private boolean kaytetaanHaunPaattymisenAikaa;
    @Column(name = "kaytetaanJarjestelmanValintapalvelua", nullable = false)
    private boolean kaytetaanJarjestelmanValintapalvelua;
    @Column(name = "kaksoisTutkinto", nullable = false)
    private boolean kaksoisTutkinto = false;
    @Column(name = "edellisenvuodenhakijat")
    private Integer edellisenVuodenHakijat;
    /*@Column(name = "hakukelpoisuusvaatimus")
     @FilterXss
     private String hakukelpoisuusvaatumus;
     */
    /* todo: double check if this is koodisto uri. */
    @Column(name = "tila")
    @Enumerated(EnumType.STRING)
    private TarjontaTila tila;

    @OneToMany(mappedBy = "hakukohde", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Yhteystiedot> yhteystiedot = new HashSet<Yhteystiedot>();

    @Embedded
    private Osoite liitteidenToimitusOsoite;
    @Column(name = "sahkoinenToimitusOsoite")
    private String sahkoinenToimitusOsoite;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "liitteidenToimitusPvm")
    private Date liitteidenToimitusPvm;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Haku haku;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "lisatiedot_teksti_id")
    private MonikielinenTeksti lisatiedot;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<PainotettavaOppiaine> painotettavatOppiaineet = new HashSet<PainotettavaOppiaine>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "hakukohde", orphanRemoval = true)
    @OrderBy("jarjestys")
    private Set<HakukohdeLiite> liites = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_hakukelpoisuusvaatimus", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<String> hakukelpoisuusVaatimukset = new HashSet<String>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_opinto_oikeus", joinColumns = @JoinColumn(name = TABLE_NAME + "_id"))
    @Column(name = "opinto_oikeus_uri")
    private Set<String> opintoOikeudet = new HashSet<String>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "valintaperustekuvaus_teksti_id")
    private MonikielinenTeksti valintaperusteKuvaus;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "hakukelpoisuusvaatimuskuvaus_teksti_id")
    private MonikielinenTeksti hakukelpoisuusVaatimusKuvaus;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "hakukohde_monikielinen_nimi_id")
    private MonikielinenTeksti hakukohdeMonikielinenNimi;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "sorakuvaus_teksti_id")
    private MonikielinenTeksti soraKuvaus;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "hakumenettely_teksti_id")
    private MonikielinenTeksti hakuMenettelyKuvaus;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "peruutusehdot_teksti_id")
    private MonikielinenTeksti peruutusEhdotKuvaus;

    @Column(name = "pohjakoulutusvaatimus_koodi_uri")
    private String pohjakoulutusvaatimusKoodiUri;
    @Column(name = "valintaperustekuvaus_koodi_uri")
    private String valintaperustekuvausKoodiUri; //the koodi uri points to metadata
    @Column(name = "sora_kuvaus_koodi_uri")
    private String soraKuvausKoodiUri; //the koodi uri points to metadata
    @Column(name = "alinHyvaksyttavaKeskiarvo")
    private Double alinHyvaksyttavaKeskiarvo;
    @Column(name = "viimPaivitysPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate = new Date();
    @Column(name = "viimPaivittajaOid")
    private String lastUpdatedByOid;

    @Column(name = "ulkoinentunniste")
    private String ulkoinenTunniste;

    @Pattern(regexp = WWW_PATTERN)
    @Column(name = "hakulomake_url")
    private String hakulomakeUrl;

    @Column(name = "viimIndeksointiPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date viimIndeksointiPvm = null;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private Hakuaika hakuaika;

    @Column(name = "hakuaikaAlkuPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hakuaikaAlkuPvm;

    @Column(name = "hakuaikaLoppuPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hakuaikaLoppuPvm;

    @Column(name = "valintaPerusteKuvausTunniste")
    private Long valintaPerusteKuvausTunniste;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_valintaperuste_kielet", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<String> valintaPerusteKuvausKielet = new HashSet<String>();

    @Column(name = "soraKuvausTunniste")
    private Long soraKuvausTunniste;

    @Column(name = "kela_linja_koodi")
    private String kelaLinjaKoodi;
    @Column(name = "kela_linja_tarkenne")
    private String kelaLinjaTarkenne;

    @Column(name = "ylioppilastutkinto_antaa_hakukelpoisuuden")
    private Boolean ylioppilastutkintoAntaaHakukelpoisuuden;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_sora_kielet", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<String> soraKuvausKielet = new HashSet<String>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "hakukohde_koulutusmoduuli_toteutus_tarjoajatiedot", inverseJoinColumns = @JoinColumn(name = "koulutusmoduuli_toteutus_tarjoajatiedot_id"))
    @MapKeyColumn(name = "koulutusmoduuli_toteutus_oid", nullable = false)
    private Map<String, KoulutusmoduuliToteutusTarjoajatiedot> koulutusmoduuliToteutusTarjoajatiedot = new HashMap<String, KoulutusmoduuliToteutusTarjoajatiedot>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hakukohde", orphanRemoval = true)
    private Set<Ryhmaliitos> ryhmaliitokset = new HashSet<Ryhmaliitos>();

    @Column(name = "haun_kopioinnin_tunniste")
    private String haunKopioinninTunniste;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_pohjakoulutusliite", joinColumns = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> pohjakoulutusliitteet = new HashSet<KoodistoUri>();

    @Column(name = "jos_yo_ei_muita_liitepyyntoja")
    private boolean josYoEiMuitaLiitepyyntoja = false;

    @Column(name = "ohjeet_uudelle_opiskelijalle")
    private String ohjeetUudelleOpiskelijalle;

    @PreRemove
    public void detachOnDelete() {
        for (KoulutusmoduuliToteutus komoto : koulutusmoduuliToteutuses) {
            komoto.getHakukohdes().remove(this);
        }
    }

    public Hakuaika getHakuaika() {
        return hakuaika;
    }

    public void setHakuaika(Hakuaika hakuaika) {
        this.hakuaika = hakuaika;
    }

    public Date getHakuaikaAlkuPvm() {
        return hakuaikaAlkuPvm;
    }

    public void setHakuaikaAlkuPvm(Date hakuaikaAlkuPvm) {
        this.hakuaikaAlkuPvm = getPvmOrNull(hakuaikaAlkuPvm);
    }

    public Date getHakuaikaLoppuPvm() {
        return hakuaikaLoppuPvm;
    }

    public void setHakuaikaLoppuPvm(Date hakuaikaLoppuPvm) {
        this.hakuaikaLoppuPvm = getPvmOrNull(hakuaikaLoppuPvm);
    }

    private Date getPvmOrNull(Date pvm) {
        return (pvm == null || NO_HAKUAIKA_PVM.equals(pvm)) ? null : pvm;
    }

    /**
     * @return the koulutuses
     */
    public Set<KoulutusmoduuliToteutus> getKoulutusmoduuliToteutuses() {
        Set<KoulutusmoduuliToteutus> filteredKomotos = new HashSet<KoulutusmoduuliToteutus>();
        for (KoulutusmoduuliToteutus komoto : koulutusmoduuliToteutuses) {
            if (!komoto.getTila().equals(TarjontaTila.POISTETTU)) {
                filteredKomotos.add(komoto);
            }
        }
        return Collections.unmodifiableSet(filteredKomotos);
    }

    /**
     * @param toteutuses the koulutuses to set
     */
    public void setKoulutusmoduuliToteutuses(Set<KoulutusmoduuliToteutus> toteutuses) {
        this.koulutusmoduuliToteutuses = toteutuses;
    }

    public void addKoulutusmoduuliToteutus(KoulutusmoduuliToteutus toteutus) {
        koulutusmoduuliToteutuses.add(toteutus);
    }

    public void removeKoulutusmoduuliToteutus(KoulutusmoduuliToteutus toteutus) {
        koulutusmoduuliToteutuses.remove(toteutus);
    }

    /**
     * Returns uri to koodisto.
     *
     * @return the nimi
     */
    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    /**
     * Set uri to koodisto that points to hakukohde's name. Example of a name
     * is: "Autoalan Tutkinto, YO" (where Hakukohde could be for one
     * KoulutusmoduuliToteutus that is "Autoalan Tutkinto").
     */
    public void setHakukohdeNimi(String hakukohde) {
        this.hakukohdeNimi = hakukohde;
    }

    /**
     * Lowest quantity of points that qualifies for selection.
     *
     * @return the alinPistemaara
     */
    public Integer getAlinValintaPistemaara() {
        return alinValintaPistemaara;
    }

    /**
     *
     */
    public void setAlinValintaPistemaara(Integer pistemaara) {
        this.alinValintaPistemaara = pistemaara;
    }

    /**
     * @return the aloituspaikatLkm
     */
    public int getAloituspaikatLkm() {
        return aloituspaikatLkm;
    }

    /**
     * @param aloituspaikatLkm the aloituspaikatLkm to set
     */
    public void setAloituspaikatLkm(int aloituspaikatLkm) {
        this.aloituspaikatLkm = aloituspaikatLkm;
    }

    /**
     * @return the valintakoes
     */
    public SortedSet<Valintakoe> getValintakoes() {
        if (valintakoes == null) {
            valintakoes = new TreeSet<>();
        }

        return valintakoes;
    }

    public void addValintakoe(Valintakoe valintakoe) {
        valintakoes.add(valintakoe);
        valintakoe.setHakukohde(this);

    }

    public void addPainotettavaOppiaine(PainotettavaOppiaine painotettavaOppiaine) {
        painotettavatOppiaineet.add(painotettavaOppiaine);
    }

    /**
     * Note: testing this from JUnit tests might give unexpected results.
     *
     * @param valintakoe
     */
    public void removeValintakoe(Valintakoe valintakoe) {
        if (valintakoes.remove(valintakoe)) {
            valintakoe.setHakukohde(null);
        }
    }

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the ylinValintaPistemaara
     */
    public Integer getYlinValintaPistemaara() {
        return ylinValintaPistemaara;
    }

    /**
     * @param ylinValintaPistemaara the ylinValintaPistemaara to set
     */
    public void setYlinValintaPistemaara(Integer ylinValintaPistemaara) {
        this.ylinValintaPistemaara = ylinValintaPistemaara;
    }

    /**
     * @return the haku
     */
    public Haku getHaku() {
        return haku;
    }

    /**
     * @param haku the haku to set
     */
    public void setHaku(Haku haku) {
        this.haku = haku;
    }

    public Set<HakukohdeLiite> getLiites() {
        if (liites == null) {
            liites = new HashSet<>();
        }

        return liites;
    }

    public void addLiite(HakukohdeLiite liite) {
        liite.setHakukohde(this);
        liites.add(liite);
    }

    public void removeLiite(HakukohdeLiite liite) {
        liites.remove(liite);
    }

    /**
     * @return the lisatiedot
     */
    public MonikielinenTeksti getLisatiedot() {
        return lisatiedot;
    }

    /**
     * @param lisatiedot the lisatiedot to set
     */
    public void setLisatiedot(MonikielinenTeksti lisatiedot) {
        this.lisatiedot = lisatiedot;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return the hakukohdeKoodistoNimi
     */
    public String getHakukohdeKoodistoNimi() {
        return hakukohdeKoodistoNimi;
    }

    /**
     * @param hakukohdeKoodistoNimi the hakukohdeKoodistoNimi to set
     */
    public void setHakukohdeKoodistoNimi(String hakukohdeKoodistoNimi) {
        this.hakukohdeKoodistoNimi = hakukohdeKoodistoNimi;
    }

    /**
     * Palauttaa edellisen vuoden hakijoiden lukumäärän tai null jos ei arvoa.
     *
     * @return
     */
    public Integer getEdellisenVuodenHakijat() {
        return edellisenVuodenHakijat;
    }

    public void setEdellisenVuodenHakijat(Integer edellisenVuodenHakijat) {
        this.edellisenVuodenHakijat = edellisenVuodenHakijat;
    }

    public Set<Yhteystiedot> getYhteystiedot() {
        return yhteystiedot;
    }

    public void addYhteystiedot(Yhteystiedot yhteystiedot) {
        yhteystiedot.setHakukohde(this);
        this.yhteystiedot.add(yhteystiedot);
    }

    public Osoite getLiitteidenToimitusOsoite() {
        return liitteidenToimitusOsoite;
    }

    public void setLiitteidenToimitusOsoite(Osoite liitteidenToimitusOsoite) {
        this.liitteidenToimitusOsoite = liitteidenToimitusOsoite;
    }

    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }

    public Date getLiitteidenToimitusPvm() {
        return liitteidenToimitusPvm;
    }

    public void setLiitteidenToimitusPvm(Date liitteidenToimitusPvm) {
        this.liitteidenToimitusPvm = liitteidenToimitusPvm;
    }

    public int getValintojenAloituspaikatLkm() {
        return valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(int valintojenAloituspaikatLkm) {
        this.valintojenAloituspaikatLkm = valintojenAloituspaikatLkm;
    }

    public boolean isKaytetaanHaunPaattymisenAikaa() {
        return kaytetaanHaunPaattymisenAikaa;
    }

    public void setKaytetaanHaunPaattymisenAikaa(boolean kaytetaanHaunPaattymisenAikaa) {
        this.kaytetaanHaunPaattymisenAikaa = kaytetaanHaunPaattymisenAikaa;
    }

    /**
     * @return the valintaperusteKuvaus
     */
    public MonikielinenTeksti getValintaperusteKuvaus() {
        return valintaperusteKuvaus;
    }

    /**
     * @param valintaperusteKuvaus the valintaperusteKuvaus to set
     */
    public void setValintaperusteKuvaus(MonikielinenTeksti valintaperusteKuvaus) {
        this.valintaperusteKuvaus = valintaperusteKuvaus;
    }

    /**
     * @return the valintaperustekuvausKoodiUri
     */
    public String getValintaperustekuvausKoodiUri() {
        return valintaperustekuvausKoodiUri;
    }

    /**
     * @param valintaperustekuvausKoodiUri the valintaperustekuvausKoodiUri to
     *                                     set
     */
    public void setValintaperustekuvausKoodiUri(String valintaperustekuvausKoodiUri) {
        this.valintaperustekuvausKoodiUri = valintaperustekuvausKoodiUri;
    }

    /**
     * @return the soraKuvausKoodiUri
     */
    public String getSoraKuvausKoodiUri() {
        return soraKuvausKoodiUri;
    }

    /**
     * @param soraKuvausKoodiUri the soraKuvausKoodiUri to set
     */
    public void setSoraKuvausKoodiUri(String soraKuvausKoodiUri) {
        this.soraKuvausKoodiUri = soraKuvausKoodiUri;
    }

    public MonikielinenTeksti getSoraKuvaus() {
        return soraKuvaus;
    }

    public void setSoraKuvaus(MonikielinenTeksti soraKuvaus) {
        this.soraKuvaus = soraKuvaus;
    }

    public MonikielinenTeksti getHakuMenettelyKuvaus() {
        return hakuMenettelyKuvaus;
    }

    public void setHakuMenettelyKuvaus(MonikielinenTeksti hakuMenettelyKuvaus) {
        this.hakuMenettelyKuvaus = hakuMenettelyKuvaus;
    }

    public MonikielinenTeksti getPeruutusEhdotKuvaus() {
        return peruutusEhdotKuvaus;
    }

    public void setPeruutusEhdotKuvaus(MonikielinenTeksti peruutusEhdotKuvaus) {
        this.peruutusEhdotKuvaus = peruutusEhdotKuvaus;
    }

    /**
     * @return the alinHyvaksyttavaKeskiarvo
     */
    public Double getAlinHyvaksyttavaKeskiarvo() {
        return alinHyvaksyttavaKeskiarvo;
    }

    /**
     * @param alinHyvaksyttavaKeskiarvo the alinHyvaksyttavaKeskiarvo to set
     */
    public void setAlinHyvaksyttavaKeskiarvo(Double alinHyvaksyttavaKeskiarvo) {
        this.alinHyvaksyttavaKeskiarvo = alinHyvaksyttavaKeskiarvo;
    }

    /**
     * @return the painotettavatOppiaineet
     */
    public Set<PainotettavaOppiaine> getPainotettavatOppiaineet() {
        return painotettavatOppiaineet;
    }

    /**
     * @param painotettavatOppiaineet the painotettavatOppiaineet to set
     */
    public void setPainotettavatOppiaineet(Set<PainotettavaOppiaine> painotettavatOppiaineet) {
        this.painotettavatOppiaineet = painotettavatOppiaineet;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdatedByOid() {
        return lastUpdatedByOid;
    }

    public void setLastUpdatedByOid(String lastUpdatedByOid) {
        this.lastUpdatedByOid = lastUpdatedByOid;
    }

    public Date getViimIndeksointiPvm() {
        return viimIndeksointiPvm;
    }

    public void setViimIndeksointiPvm(Date viimIndeksointiPvm) {
        this.viimIndeksointiPvm = viimIndeksointiPvm;
    }

    public Set<String> getHakukelpoisuusVaatimukset() {
        if (hakukelpoisuusVaatimukset == null) {
            hakukelpoisuusVaatimukset = new HashSet<String>();
        }
        return hakukelpoisuusVaatimukset;
    }

    public void setHakukelpoisuusVaatimukset(Set<String> hakukelpoisuusVaatimukset) {
        this.hakukelpoisuusVaatimukset = hakukelpoisuusVaatimukset;
    }

    public Set<String> getOpintoOikeudet() {
        return opintoOikeudet;
    }

    public void setOpintoOikeudet(Set<String> opintoOikeudet) {
        this.opintoOikeudet = opintoOikeudet;
    }

    public String getPohjakoulutusvaatimusKoodiUri() {
        return pohjakoulutusvaatimusKoodiUri;
    }

    public void setPohjakoulutusvaatimusKoodiUri(String pohjakoulutusvaatimusKoodiUri) {
        this.pohjakoulutusvaatimusKoodiUri = pohjakoulutusvaatimusKoodiUri;
    }

    public boolean isKaytetaanJarjestelmanValintapalvelua() {
        return kaytetaanJarjestelmanValintapalvelua;
    }

    public void setKaytetaanJarjestelmanValintapalvelua(boolean kaytetaanJarjestelmanValintapalvelua) {
        this.kaytetaanJarjestelmanValintapalvelua = kaytetaanJarjestelmanValintapalvelua;
    }

    public MonikielinenTeksti getHakukohdeMonikielinenNimi() {
        return hakukohdeMonikielinenNimi;
    }

    public void setHakukohdeMonikielinenNimi(MonikielinenTeksti hakukohdeMonikielinenNimi) {
        this.hakukohdeMonikielinenNimi = hakukohdeMonikielinenNimi;
    }

    public boolean isKaksoisTutkinto() {
        return kaksoisTutkinto;
    }

    public void setKaksoisTutkinto(boolean kaksoisTutkinto) {
        this.kaksoisTutkinto = kaksoisTutkinto;
    }

    public MonikielinenTeksti getHakukelpoisuusVaatimusKuvaus() {
        return hakukelpoisuusVaatimusKuvaus;
    }

    public void setHakukelpoisuusVaatimusKuvaus(MonikielinenTeksti hakukelpoisuusVaatimusKuvaus) {
        this.hakukelpoisuusVaatimusKuvaus = hakukelpoisuusVaatimusKuvaus;
    }

    public Long getValintaPerusteKuvausTunniste() {
        return valintaPerusteKuvausTunniste;
    }

    public void setValintaPerusteKuvausTunniste(Long valintaPerusteKuvausTunniste) {
        this.valintaPerusteKuvausTunniste = valintaPerusteKuvausTunniste;
    }

    public Long getSoraKuvausTunniste() {
        return soraKuvausTunniste;
    }

    public void setSoraKuvausTunniste(Long soraKuvausTunniste) {
        this.soraKuvausTunniste = soraKuvausTunniste;
    }

    @Deprecated
    public Set<String> getValintaPerusteKuvausKielet() {
        return valintaPerusteKuvausKielet;
    }

    @Deprecated
    public void setValintaPerusteKuvausKielet(Set<String> valintaPerusteKuvausKielet) {
        this.valintaPerusteKuvausKielet = valintaPerusteKuvausKielet;
    }

    @Deprecated
    public Set<String> getSoraKuvausKielet() {
        return soraKuvausKielet;
    }

    @Deprecated
    public void setSoraKuvausKielet(Set<String> soraKuvausKielet) {
        this.soraKuvausKielet = soraKuvausKielet;
    }

    /**
     * AntiSamy Filtteröidään (vain) kentät joissa tiedetään olevan HTML:ää.
     * Muut kentät esityskerroksen vastuulla!
     */
    @PrePersist
    @PreUpdate
    public void filterHTMLFields() {
        filter(getHakukelpoisuusVaatimusKuvaus());
        filter(getValintaperusteKuvaus());
        filter(getSoraKuvaus());
        filter(getLisatiedot());
        filter(getHakuMenettelyKuvaus());
        filter(getPeruutusEhdotKuvaus());
    }

    public String getUlkoinenTunniste() {
        return ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this.ulkoinenTunniste = ulkoinenTunniste;
    }

    public Map<String, KoulutusmoduuliToteutusTarjoajatiedot> getKoulutusmoduuliToteutusTarjoajatiedot() {
        return koulutusmoduuliToteutusTarjoajatiedot;
    }

    public void setKoulutusmoduuliToteutusTarjoajatiedot(Map<String, KoulutusmoduuliToteutusTarjoajatiedot> koulutusmoduuliToteutusTarjoajatiedot) {
        this.koulutusmoduuliToteutusTarjoajatiedot = koulutusmoduuliToteutusTarjoajatiedot;
    }

    public boolean hasTarjoajatiedotForKoulutus(String koulutusOid) {
        return koulutusmoduuliToteutusTarjoajatiedot.get(koulutusOid) != null;
    }

    public KoulutusmoduuliToteutusTarjoajatiedot getTarjoajatiedotForKoulutus(String koulutusOid) {
        return koulutusmoduuliToteutusTarjoajatiedot.get(koulutusOid);
    }

    public void removeTarjoajatiedotForKoulutus(String oid) {
        koulutusmoduuliToteutusTarjoajatiedot.remove(oid);
    }

    public MonikielinenTeksti getAloituspaikatKuvaus() {
        return aloituspaikatKuvaus;
    }

    public void setAloituspaikatKuvaus(MonikielinenTeksti aloituspaikatKuvaus) {
        this.aloituspaikatKuvaus = aloituspaikatKuvaus;
    }

    public boolean hakuaikasSet() {
        return getHakuaikaAlkuPvm() != null && getHakuaikaLoppuPvm() != null;
    }

    public KoulutusmoduuliToteutus getFirstKoulutus() {
        if (getKoulutusmoduuliToteutuses().isEmpty()) {
            return null;
        }
        return getKoulutusmoduuliToteutuses().iterator().next();
    }

    public boolean isPoistettu() {
        return TarjontaTila.POISTETTU.equals(getTila());
    }

    public Set<Ryhmaliitos> getRyhmaliitokset() {
        return ryhmaliitokset;
    }

    public void setRyhmaliitokset(Set<Ryhmaliitos> ryhmaliitokset) {
        this.ryhmaliitokset = ryhmaliitokset;
    }

    public Ryhmaliitos getRyhmaliitosByRyhmaOid(final String ryhmaOid) {
        return Iterables.tryFind(getRyhmaliitokset(), ryhmaliitos ->
                ryhmaliitos.getRyhmaOid().equals(ryhmaOid)
        ).orNull();
    }

    public void addRyhmaliitos(Ryhmaliitos ryhmaliitos) {
        getRyhmaliitokset().add(ryhmaliitos);
    }

    public void removeRyhmaliitos(Ryhmaliitos ryhmaliitos) {
        getRyhmaliitokset().remove(ryhmaliitos);
    }

    public Boolean getYlioppilastutkintoAntaaHakukelpoisuuden() {
        return ylioppilastutkintoAntaaHakukelpoisuuden;
    }

    public void setYlioppilastutkintoAntaaHakukelpoisuuden(Boolean ylioppilastutkintoAntaaHakukelpoisuuden) {
        this.ylioppilastutkintoAntaaHakukelpoisuuden = ylioppilastutkintoAntaaHakukelpoisuuden;
    }

    public String getKelaLinjaKoodi() {
        return kelaLinjaKoodi;
    }

    public void setKelaLinjaKoodi(String kelaLinjaKoodi) {
        this.kelaLinjaKoodi = kelaLinjaKoodi;
    }

    public String getKelaLinjaTarkenne() {
        return kelaLinjaTarkenne;
    }

    public void setKelaLinjaTarkenne(String kelaLinjaTarkenne) {
        this.kelaLinjaTarkenne = kelaLinjaTarkenne;
    }

    public String getHaunKopioinninTunniste() {
        return haunKopioinninTunniste;
    }

    public void setHaunKopioinninTunniste(String haunKopioinninTunniste) {
        this.haunKopioinninTunniste = haunKopioinninTunniste;
    }

    public Integer getEnsikertalaistenAloituspaikat() {
        return ensikertalaistenAloituspaikat;
    }

    public void setEnsikertalaistenAloituspaikat(Integer ensikertalaistenAloituspaikat) {
        this.ensikertalaistenAloituspaikat = ensikertalaistenAloituspaikat;
    }

    public Set<KoodistoUri> getPohjakoulutusliitteet() {
        return pohjakoulutusliitteet;
    }

    public void setPohjakoulutusliitteet(Set<KoodistoUri> pohjakoulutusliitteet) {
        this.pohjakoulutusliitteet = pohjakoulutusliitteet;
    }

    public boolean isJosYoEiMuitaLiitepyyntoja() {
        return josYoEiMuitaLiitepyyntoja;
    }

    public void setJosYoEiMuitaLiitepyyntoja(boolean josYoEiMuitaLiitepyyntoja) {
        this.josYoEiMuitaLiitepyyntoja = josYoEiMuitaLiitepyyntoja;
    }

    public String getHakulomakeUrl() {
        return hakulomakeUrl;
    }

    public void setHakulomakeUrl(String hakulomakeUrl) {
        this.hakulomakeUrl = hakulomakeUrl;
    }

    public String getUniqueExternalId() {
        return uniqueExternalId;
    }

    public void setUniqueExternalId(String uniqueExternalId) {
        this.uniqueExternalId = uniqueExternalId;
    }

    public String getOhjeetUudelleOpiskelijalle() {
        return ohjeetUudelleOpiskelijalle;
    }

    public void setOhjeetUudelleOpiskelijalle(String ohjeetUudelleOpiskelijalle) {
        this.ohjeetUudelleOpiskelijalle = ohjeetUudelleOpiskelijalle;
    }

    public int getUniqueAlkamisVuosi() {
        Integer vuosi = null;
        for (KoulutusmoduuliToteutus koulutus : this.getKoulutusmoduuliToteutuses()) {
            if (koulutus.getTila() != TarjontaTila.POISTETTU) {
                int v = koulutus.getUniqueAlkamisVuosi();
                if (vuosi != null && vuosi != v) {
                    throw new IllegalStateException(String.format(
                            "Hakukohteen %s koulutusten %s alkamisvuodet ovat ristiriitaiset",
                            this.getOid(), this.koulutusOids()
                    ));
                } else {
                    vuosi = v;
                }
            }
        }
        if (vuosi == null) {
            throw new IllegalStateException(String.format(
                    "Hakukohteen %s koulutuksilla %s ei ole alkamisvuotta",
                    this.getOid(), this.koulutusOids()
            ));
        }
        return vuosi;
    }

    public String getUniqueAlkamiskausiUri() {
        String kausiUri = null;
        for (KoulutusmoduuliToteutus koulutus : this.getKoulutusmoduuliToteutuses()) {
            if (koulutus.getTila() != TarjontaTila.POISTETTU) {
                String k = koulutus.getUniqueAlkamiskausiUri();
                if (kausiUri != null && !kausiUri.equals(k)) {
                    throw new IllegalStateException(String.format(
                            "Hakukohteen %s koulutusten %s alkamiskaudet ovat ristiriitaiset",
                            this.getOid(), this.koulutusOids()
                    ));
                } else {
                    kausiUri = k;
                }
            }
        }
        if (kausiUri == null) {
            throw new IllegalStateException(String.format(
                    "Hakukohteen %s koulutuksilla %s ei ole alkamiskautta",
                    this.getOid(), this.koulutusOids()
            ));
        }
        return kausiUri;
    }

    private List<String> koulutusOids() {
        ArrayList<String> koulutusOids = new ArrayList<>();
        for (KoulutusmoduuliToteutus k : this.getKoulutusmoduuliToteutuses()) {
            koulutusOids.add(k.getOid());
        }
        return koulutusOids;
    }
}
