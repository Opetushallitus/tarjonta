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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity
@Table(name = "hakukohde")
public class Hakukohde extends BaseEntity {

    private static final long serialVersionUID = -3320464257959195992L;

    @Column(name = "oid")
    private String oid;

    @ManyToMany(mappedBy = "hakukohdes")
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
     * The string containing the human readable name of this
     * hakukohde object. Names in different languages are
     * concatenated to this field. This field is created
     * to enable search.
     */
    @Column(name = "hakukohde_koodisto_nimi")
    private String hakukohdeKoodistoNimi;

    @Column(name = "alin_valinta_pistamaara")
    private Integer alinValintaPistemaara;

    @Column(name = "ylin_valinta_pistemaara")
    private Integer ylinValintaPistemaara;

    @Column(name = "aloituspaikat_lkm")
    private Integer aloituspaikatLkm;

    @Column(name = "edellisenvuodenhakijat")
    private Integer edellisenVuodenHakijat;

    @Column(name = "hakukelpoisuusvaatimus")
    private String hakukelpoisuusvaatumus;

    /* todo: double check if this is koodisto uri. */
    @Column(name = "tila")
    @Enumerated(EnumType.STRING)
    private TarjontaTila tila;

    @ManyToOne
    @NotNull
    private Haku haku;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "valintaperustekuvaus_teksti_id")
    private MonikielinenTeksti valintaperusteKuvaus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lisatiedot_teksti_id")
    private MonikielinenTeksti lisatiedot;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "hakukohde")
    private Set<HakukohdeLiite> liites = new HashSet<HakukohdeLiite>();

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

    /**
     * Returns uri to koodisto.
     *
     * @return the nimi
     */
    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    /**
     * Set uri to koodisto that points to hakukohde's name. Example of a name is: "Autoalan Tutkinto, YO" (where Hakukohde could be for one
     * KoulutusmoduuliToteutus that is "Autoalan Tutkinto").
     *
     * @param uri the nimi to set
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
     * @param alinPistemaara the alinPistemaara to set
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
        return Collections.unmodifiableSet(valintakoes);
    }

    /**
     * @param valintakoes the valintakoes to set
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

    public MonikielinenTeksti getValintaperusteKuvaus() {
        return valintaperusteKuvaus;
    }

    public void setValintaperusteKuvaus(MonikielinenTeksti valintaperusteKuvaus) {
        this.valintaperusteKuvaus = valintaperusteKuvaus;
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
        return Collections.unmodifiableSet(liites);
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

}

