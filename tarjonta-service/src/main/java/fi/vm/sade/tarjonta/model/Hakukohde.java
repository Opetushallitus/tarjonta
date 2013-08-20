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
import fi.vm.sade.security.xssfilter.FilterXss;
import fi.vm.sade.security.xssfilter.XssFilterListener;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.Collections;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity
@Table(name = "hakukohde")
@EntityListeners(XssFilterListener.class)
public class Hakukohde extends BaseEntity {

    private static final long serialVersionUID = -3320464257959195992L;
    @Column(name = "oid", unique=true)
    private String oid;
    @ManyToMany(mappedBy = "hakukohdes", cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.LAZY)
    private Set<KoulutusmoduuliToteutus> koulutusmoduuliToteutuses = new HashSet<KoulutusmoduuliToteutus>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "hakukohde_id")
    private Set<Valintakoe> valintakoes = new HashSet<Valintakoe>();
    /**
     * The koodisto uri of the name of this hakukohde object.
     */
    @NotNull
    @Column(name = "hakukohde_nimi", nullable = false)
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
    @Column(name = "aloituspaikat_lkm")
    private Integer aloituspaikatLkm;
    private Integer valintojenAloituspaikatLkm;
    private boolean kaytetaanHaunPaattymisenAikaa;
    @Column(name = "edellisenvuodenhakijat")
    private Integer edellisenVuodenHakijat;
    @Column(name = "hakukelpoisuusvaatimus")
    @FilterXss
    private String hakukelpoisuusvaatumus;

    /* todo: double check if this is koodisto uri. */
    @Column(name = "tila")
    @Enumerated(EnumType.STRING)
    private TarjontaTila tila;
    @Embedded
    private Osoite liitteidenToimitusOsoite;
    private String sahkoinenToimitusOsoite;
    @Temporal(TemporalType.TIMESTAMP)
    private Date liitteidenToimitusPvm;
    @ManyToOne
    @NotNull
    private Haku haku;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "lisatiedot_teksti_id")
    private MonikielinenTeksti lisatiedot;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PainotettavaOppiaine> painotettavatOppiaineet = new HashSet<PainotettavaOppiaine>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "hakukohde")
    private Set<HakukohdeLiite> liites = new HashSet<HakukohdeLiite>();
    @Deprecated
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "valintaperustekuvaus_teksti_id")
    private MonikielinenTeksti valintaperusteKuvaus;
    @Column(name = "valintaperustekuvaus_koodi_uri")
    private String valintaperustekuvausKoodiUri; //the koodi uri points to metadata
    @Column(name = "sora_kuvaus_koodi_uri")
    private String soraKuvausKoodiUri; //the koodi uri points to metadata
    @Column(name ="alinHyvaksyttavaKeskiarvo")
    private Double alinHyvaksyttavaKeskiarvo;
    @Column(name="viimPaivitysPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate = new Date();
    @Column(name="viimPaivittajaOid")
    private String lastUpdatedByOid;

    @Column(name="viimIndeksointiPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date viimIndeksointiPvm = null;

    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    private Hakuaika hakuaika;
    
    @Column(name="hakuaikaAlkuPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hakuaikaAlkuPvm;
    
    @Column(name="hakuaikaLoppuPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hakuaikaLoppuPvm;
    
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
    public Integer getAloituspaikatLkm() {
        return aloituspaikatLkm;
    }

    /**
     * @param aloituspaikatLkm the aloituspaikatLkm to set
     */
    public void setAloituspaikatLkm(Integer aloituspaikatLkm) {
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

    }

    /**
     * Note: testing this from JUnit tests might give unexpected results.
     *
     * @param valintakoe
     */
    public void removeValintakoe(Valintakoe valintakoe) {
        valintakoes.remove(valintakoe);
    }

    public String getHakukelpoisuusvaatimus() {
        return hakukelpoisuusvaatumus;
    }

    public void setHakukelpoisuusvaatimus(String hakukelpoisuusvaatimus) {
        this.hakukelpoisuusvaatumus = hakukelpoisuusvaatimus;
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
        liite.setHakukohde(null);
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

    public Integer getValintojenAloituspaikatLkm() {
        return valintojenAloituspaikatLkm;
    }

    public void setValintojenAloituspaikatLkm(Integer valintojenAloituspaikatLkm) {
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
    @Deprecated
    public MonikielinenTeksti getValintaperusteKuvaus() {
        return valintaperusteKuvaus;
    }

    /**
     * @param valintaperusteKuvaus the valintaperusteKuvaus to set
     */
    @Deprecated
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

}
