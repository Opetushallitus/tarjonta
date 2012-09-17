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

    @ManyToMany(mappedBy = "hakukohdes")
    private Set<KoulutusmoduuliToteutus> koulutuses = new HashSet<KoulutusmoduuliToteutus>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "hakukohde_id")
    private Set<Valintakoe> valintakoes = new HashSet<Valintakoe>();

    @NotNull
    @Column(name = "hakukohde_nimi", nullable = false)
    private String hakukohdeNimi;

    @Column(name = "alin_valinta_pistamaara")
    private int alinValintaPistemaara;

    @Column(name = "ylin_valinta_pistemaara")
    private int ylinValintaPistemaara;

    @Column(name = "aloituspaikat_lkm")
    private int aloituspaikatLkm;

    @Column(name = "hakukelpoisuusvaatimus")
    private String hakukelpoisuusvaatimus;

    /* todo: double check if this is koodisto uri. */
    @Column(name = "tila")
    private String tila;

    @ManyToOne
    private Haku haku;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "valintaperustekuvaus_teksti_id")
    private MonikielinenTeksti valintaperusteKuvaus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lisatiedot_teksti_id")
    private MonikielinenTeksti lisatiedot;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "hakukohde")
    private Set<HakukohdeLiite> liites;

    /**
     * @return the koulutuses
     */
    public Set<KoulutusmoduuliToteutus> getKoulutuses() {
        return koulutuses;
    }

    /**
     * @param koulutuses the koulutuses to set
     */
    public void setKoulutuses(Set<KoulutusmoduuliToteutus> koulutuses) {
        this.koulutuses = koulutuses;
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
    public int getAlinValintaPistemaara() {
        return alinValintaPistemaara;
    }

    /**
     * @param alinPistemaara the alinPistemaara to set
     */
    public void setAlinValintaPistemaara(int pistemaara) {
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
        return hakukelpoisuusvaatimus;
    }

    public void setHakukelpoisuusvaatimus(String hakukelpoisuusvaatimus) {
        this.hakukelpoisuusvaatimus = hakukelpoisuusvaatimus;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
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
    public int getYlinValintaPistemaara() {
        return ylinValintaPistemaara;
    }

    /**
     * @param ylinValintaPistemaara the ylinValintaPistemaara to set
     */
    public void setYlinValintaPistemaara(int ylinValintaPistemaara) {
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

}

