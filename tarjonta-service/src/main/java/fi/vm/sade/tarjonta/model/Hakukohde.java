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

import java.util.*;

import javax.persistence.*;

import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 */
@Entity
@JsonIgnoreProperties({"koulutusmoduuliToteutuses", "haku", "id", "version","organisaatioRyhmaOids"})
@Table(name = Hakukohde.TABLE_NAME)
public class Hakukohde extends TarjontaBaseEntity {

    public static final String TABLE_NAME = "hakukohde";
    private static final long serialVersionUID = -3320464257959195992L;

    @Column(name = "oid", unique = true, updatable = false)
    private String oid;

    @ManyToMany(mappedBy = "hakukohdes", cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<KoulutusmoduuliToteutus> koulutusmoduuliToteutuses = new HashSet<KoulutusmoduuliToteutus>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "hakukohde", orphanRemoval = true)
    private Set<Valintakoe> valintakoes = new HashSet<Valintakoe>();

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
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PainotettavaOppiaine> painotettavatOppiaineet = new HashSet<PainotettavaOppiaine>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "hakukohde", orphanRemoval = true)
    private Set<HakukohdeLiite> liites = new HashSet<HakukohdeLiite>();
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_hakukelpoisuusvaatimus", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<String> hakukelpoisuusVaatimukset = new HashSet<String>();
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

    @Column(name = "viimIndeksointiPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date viimIndeksointiPvm = null;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_sora_kielet", joinColumns
            = @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<String> soraKuvausKielet = new HashSet<String>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(name = "hakukohde_koulutusmoduuli_toteutus_tarjoajatiedot", inverseJoinColumns = @JoinColumn(name = "koulutusmoduuli_toteutus_tarjoajatiedot_id"))
    @MapKeyColumn(name = "koulutusmoduuli_toteutus_oid", nullable = false)
    private Map<String, KoulutusmoduuliToteutusTarjoajatiedot> koulutusmoduuliToteutusTarjoajatiedot = new HashMap<String, KoulutusmoduuliToteutusTarjoajatiedot>();

    /**
     * KJOH-810 Hakukohteen ryhmän valinta
     */
    private String organisaatioRyhmaOids;

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
        this.hakuaikaAlkuPvm = hakuaikaAlkuPvm;
    }

    public Date getHakuaikaLoppuPvm() {
        return hakuaikaLoppuPvm;
    }

    public void setHakuaikaLoppuPvm(Date hakuaikaLoppuPvm) {
        this.hakuaikaLoppuPvm = hakuaikaLoppuPvm;
    }

    /**
     * @return the koulutuses
     */
    public Set<KoulutusmoduuliToteutus> getKoulutusmoduuliToteutuses() {
        return Collections.unmodifiableSet(koulutusmoduuliToteutuses);
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
     *
     *
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
    public Set<Valintakoe> getValintakoes() {
        if (valintakoes == null) {
            valintakoes = new HashSet<Valintakoe>();
        }

        return valintakoes;

    }

    /**
     *
     */
    public void addValintakoe(Valintakoe valintakoe) {
        valintakoes.add(valintakoe);
        valintakoe.setHakukohde(this);

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
    /*
     public String getHakukelpoisuusvaatimus() {
     return hakukelpoisuusvaatumus;
     }

     public void setHakukelpoisuusvaatimus(String hakukelpoisuusvaatimus) {
     this.hakukelpoisuusvaatumus = hakukelpoisuusvaatimus;
     } */

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
            liites = new HashSet<HakukohdeLiite>();
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
     * set
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
    }

    public String getUlkoinenTunniste() {
        return ulkoinenTunniste;
    }

    public void setUlkoinenTunniste(String ulkoinenTunniste) {
        this.ulkoinenTunniste = ulkoinenTunniste;
    }

    @JsonProperty
    public String getOrganisaatioRyhmat() {
        return organisaatioRyhmaOids;
    }

    public void setOrganisaatioRyhmat(String oids) {
        organisaatioRyhmaOids = oids;
    }

    
    public String[] getOrganisaatioRyhmaOids() {
        if (organisaatioRyhmaOids == null || organisaatioRyhmaOids.isEmpty()) {
            return new String[0];
        }
        return organisaatioRyhmaOids.split(",");
    }

    public void setOrganisaatioRyhmaOids(String[] organisationOids) {
        if (organisationOids == null || organisationOids.length == 0) {
            this.organisaatioRyhmaOids = null;
        } else {
            this.organisaatioRyhmaOids = StringUtils.join(organisationOids, ",");
        }
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
}
