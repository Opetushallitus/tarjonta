/*
 * Copyright (c) 2012-2014 The Finnish Board of Education - Opetushallitus
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
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static fi.vm.sade.generic.common.validation.ValidationConstants.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Antti Salonen
 */
@Entity
@Table(name = Haku.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(name = "UK_haku_01", columnNames = {"oid"})
})
public class Haku extends TarjontaBaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "haku";
    public static final String HAUN_ALKAMIS_PVM = "haunAlkamisPvm";
    public static final String HAUN_LOPPUMIS_PVM = "haunLoppumisPvm";

    @NotNull
    @Column(unique=true)
    private String oid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nimi_teksti_id")
    private MonikielinenTeksti nimi;

    @NotNull
    @Column(name = "hakutyyppi")
    private String hakutyyppiUri;

    /**
     * kevat tai syksy
     */
    @NotNull
    @Column(name = "hakukausi")
    private String hakukausiUri;

    @NotNull
    @Column(name = "hakukausi_vuosi")
    private Integer hakukausiVuosi;

    @NotNull
    @Column(name = "koulutuksen_alkamiskausi")
    private String koulutuksenAlkamiskausiUri;

    @NotNull
    @Column(name = "koulutuksen_alkamisvuosi")
    private Integer koulutuksenAlkamisVuosi;

    /**
     * Haulla on jo OID. Mihin tunnistetta käytetään ja mikä on sen formaatti?
     */
    @Column(name = "haun_tunniste")
    private String haunTunniste;

    /**
     * yliopistojen / ammattikorkeitten / peruskoulujen jne..
     * esm. ammatillinen koulutus
     */
    @NotNull
    @Column(name = "kohdejoukko")
    private String kohdejoukkoUri;

    /**
     * yhteishaku yms.
     */
    @NotNull
    @Column(name = "hakutapa")
    private String hakutapaUri;

    @Column(name = "sijoittelu")
    private boolean sijoittelu;

    @Pattern(regexp = WWW_PATTERN)
    @Column(name = "hakulomake_url")
    private String hakulomakeUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TarjontaTila tila;

    @OneToMany(mappedBy = "haku", fetch = FetchType.LAZY)
    private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "haku", fetch = FetchType.EAGER)
    private Set<Hakuaika> hakuaikas = new HashSet<Hakuaika>();

    @Column(name="viimPaivitysPvm")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate = new Date();
    @Column(name="viimPaivittajaOid")
    private String lastUpdatedByOid;

    /**
     * KoulutusInformation service uses this to "know" how many hakukohdes can be added to "muistilista" (application list?)
     */
    @Column(name = "max_hakukohdes")
    private int maxHakukohdes = 0;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimiFi() {
        return getNimi("fi");
    }

    public void setNimiFi(String nimiFi) {
        setNimi("fi", nimiFi);
    }

    public String getNimiSv() {
        return getNimi("sv");
    }

    public void setNimiSv(String nimiSv) {
        setNimi("sv", nimiSv);
    }

    public String getNimiEn() {
        return getNimi("en");
    }

    public void setNimiEn(String nimiEn) {
        setNimi("en", nimiEn);
    }

    /**
     * Koulutukseen hakeutumisen tyypin tieto. Esim. varsinainen haku, täydennys- tai lisähaku.
     * Arvo on viittaus koodistoon.
     *
     * @return
     */
    public String getHakutyyppiUri() {
        return hakutyyppiUri;
    }

    /**
     *
     *
     * @param hakutyyppi uri to koodisto
     */
    public void setHakutyyppiUri(String hakutyyppi) {
        this.hakutyyppiUri = hakutyyppi;
    }

    /**
     * Returns uri to Koodisto.
     *
     * @return
     */
    public String getHakukausiUri() {
        return hakukausiUri;
    }

    public void setHakukausiUri(String hakukausi) {
        this.hakukausiUri = hakukausi;
    }

    public String getKoulutuksenAlkamiskausiUri() {
        return koulutuksenAlkamiskausiUri;
    }

    public void setKoulutuksenAlkamiskausiUri(String koodistoUri) {
        this.koulutuksenAlkamiskausiUri = koodistoUri;
    }

    public String getKohdejoukkoUri() {
        return kohdejoukkoUri;
    }

    /**
     * Uri to koodisto. Sample value behind uri could be: "Ammatillinen koulutus". This attribute is mandatory.
     *
     * @param koodistoUri
     */
    public void setKohdejoukkoUri(String koodistoUri) {
        this.kohdejoukkoUri = koodistoUri;
    }

    public String getHakutapaUri() {
        return hakutapaUri;
    }

    /**
     * Uri to koodisto. Sample value behind uri could be: "Yhteishaku". This attribute is mandatory.
     *
     * @param hakutapa
     */
    public void setHakutapaUri(String hakutapa) {
        this.hakutapaUri = hakutapa;
    }

    /**
     * Returns true if "sijoittelu" is to be used.
     *
     * @return
     */
    public boolean isSijoittelu() {
        return sijoittelu;
    }

    public void setSijoittelu(boolean sijoittelu) {
        this.sijoittelu = sijoittelu;
    }

    public String getHakulomakeUrl() {
        return hakulomakeUrl;
    }

    public void setHakulomakeUrl(String hakulomakeUrl) {
        this.hakulomakeUrl = hakulomakeUrl;
    }

    public MonikielinenTeksti getNimi() {
        return nimi;
    }

    public void setNimi(MonikielinenTeksti newNimi) {
    	nimi = MonikielinenTeksti.merge(nimi, newNimi);
    }

    private String getNimi(String kieliKoodi) {
        return (nimi != null ? nimi.getTekstiForKieliKoodi(kieliKoodi) : null);
    }

    private void setNimi(String kieliKoodi, String teksti) {
        if (nimi == null) {
            nimi = new MonikielinenTeksti();
        }
        nimi.setTekstiKaannos(kieliKoodi, teksti);
    }

    public Set<Hakukohde> getHakukohdes() {
        return Collections.unmodifiableSet(hakukohdes);
    }

    public void addHakukohde(Hakukohde hakukohde) {
        hakukohdes.add(hakukohde);
    }

    public void removeHakukohde(Hakukohde hakukohde) {
        hakukohdes.remove(hakukohde);
    }

    public Set<Hakuaika> getHakuaikas() {
        if (hakuaikas == null) {
            hakuaikas = new HashSet<Hakuaika>();
        }
        return hakuaikas;
        // return Collections.unmodifiableSet(hakuaikas);
    }

    public void addHakuaika(Hakuaika hakuaika) {
        hakuaika.setHaku(this);
        hakuaikas.add(hakuaika);
    }

    public void removeHakuaika(Hakuaika hakuaika) {
        if (hakuaikas.remove(hakuaika)) {
            hakuaika.setHaku(null);
        }
    }

    /**
     * Returns current state. Value is a Koodisto uri.
     *
     * @return the tila
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * Set state of this Haku. Value is a Koodisto uri.
     *
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    /**
     * @return the hakukausiVuosi
     */
    public Integer getHakukausiVuosi() {
        return hakukausiVuosi;
    }

    /**
     * @param hakukausiVuosi the hakukausiVuosi to set
     */
    public void setHakukausiVuosi(Integer hakukausiVuosi) {
        this.hakukausiVuosi = hakukausiVuosi;
    }

    /**
     * @return the koulutuksenAlkamisVuosi
     */
    public Integer getKoulutuksenAlkamisVuosi() {
        return koulutuksenAlkamisVuosi;
    }

    /**
     * @param koulutuksenAlkamisVuosi the koulutuksenAlkamisVuosi to set
     */
    public void setKoulutuksenAlkamisVuosi(Integer koulutuksenAlkamisVuosi) {
        this.koulutuksenAlkamisVuosi = koulutuksenAlkamisVuosi;
    }

    /**
     * @return the haunTunniste
     */
    public String getHaunTunniste() {
        return haunTunniste;
    }

    /**
     * @param haunTunniste the haunTunniste to set
     */
    public void setHaunTunniste(String haunTunniste) {
        this.haunTunniste = haunTunniste;
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

    public int getMaxHakukohdes() {
        return maxHakukohdes;
    }

    public void setMaxHakukohdes(int maxHakukohdes) {
        this.maxHakukohdes = maxHakukohdes;
    }
}

