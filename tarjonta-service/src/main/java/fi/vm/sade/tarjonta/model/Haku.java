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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

import static fi.vm.sade.generic.common.validation.ValidationConstants.*;
import java.util.Collections;
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
public class Haku extends BaseEntity {

    private static Logger log = LoggerFactory.getLogger(Haku.class);

    public static final String TABLE_NAME = "haku";

    public static final String HAUN_ALKAMIS_PVM = "haunAlkamisPvm";

    public static final String HAUN_LOPPUMIS_PVM = "haunLoppumisPvm";

    @NotNull
    private String oid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nimi_teksti_id")
    private MonikielinenTeksti nimi;

    // todo: i might move this and the next date parameter to a collection that might be required anyways
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "haun_alkamis_pvm")
    private Date haunAlkamisPvm;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "haun_loppumis_pvm")
    private Date haunLoppumisPvm;

    @NotNull
    @Column(name = "hakutyyppi")
    private String hakutyyppiUri;

    @NotNull
    @Column(name = "hakukausi")
    private String hakukausiUri;

    @NotNull
    @Column(name = "koulutuksen_alkamiskausi")
    private String koulutuksenAlkamiskausiUri;

    @NotNull
    @Column(name = "kohdejoukko")
    private String kohdejoukkoUri;

    @NotNull
    @Column(name = "hakutapa")
    private String hakutapaUri;

    @Column(name = "sijoittelu")
    private boolean sijoittelu;

    @Pattern(regexp = WWW_PATTERN)
    @Column(name = "hakulomake_url")
    private String hakulomakeUrl;

    @NotNull
    private String tila;

    @OneToMany
    private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "haku")
    private Set<Hakuaika> hakuaikas = new HashSet<Hakuaika>();

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

    public Date getHaunAlkamisPvm() {
        return haunAlkamisPvm;
    }

    public void setHaunAlkamisPvm(Date haunAlkamisPvm) {
        this.haunAlkamisPvm = haunAlkamisPvm;
    }

    public Date getHaunLoppumisPvm() {
        return haunLoppumisPvm;
    }

    public void setHaunLoppumisPvm(Date haunLoppumisPvm) {
        this.haunLoppumisPvm = haunLoppumisPvm;
    }

    /**
     * Returns uri to Koodisto.
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

    public void setNimi(MonikielinenTeksti nimi) {
        this.nimi = nimi;
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
        return Collections.unmodifiableSet(hakuaikas);
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
    public String getTila() {
        return tila;
    }

    /**
     * Set state of this Haku. Value is a Koodisto uri.
     * 
     * @param tila the tila to set
     */
    public void setTila(String tila) {
        this.tila = tila;
    }

}

