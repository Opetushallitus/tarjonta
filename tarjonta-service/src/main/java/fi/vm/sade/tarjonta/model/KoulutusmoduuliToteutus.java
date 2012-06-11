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

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
@Entity
@Table(name = KoulutusmoduuliToteutus.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class KoulutusmoduuliToteutus extends LearningOpportunitySpecification {

    private static final long serialVersionUID = -1278564574746813425L;

    public static final String TABLE_NAME = "koulutusmoduuli_toteutus";

    private static Logger log = LoggerFactory.getLogger(KoulutusmoduuliToteutus.class);

    private String nimi;

    /**
     * todo: make this embedded?
     */
    @OneToOne(cascade = CascadeType.ALL)
    private KoulutusmoduuliPerustiedot perustiedot;

    @Enumerated(EnumType.STRING)
    private KoulutusmoduuliTila tila;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<KoulutusmoduuliToteutusTarjoaja> tarjoajat = new HashSet<KoulutusmoduuliToteutusTarjoaja>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(name = TABLE_NAME + "_tarjoaja")
    private Koulutusmoduuli koulutusmoduuli;

    /**
     * Koodisto Uri. Example display values 'Nuorten koulutus, Aikuisten koulutus'.
     *
     * <p>Note 11.06.2012: as per today, when looking at the "model" you'll find this attribute from Koulutusmoduuli. This is a mistake, the model is just not
     * updated.</p>
     *
     */
    private String koulutusLajiUri;

    /**
     * <p>Note 11.06.2012: this attribute has been added via wire frame and at least as per today, does not exists in the model</p>
     */
    @Temporal(TemporalType.DATE)
    private Date koulutuksenAlkamisPvm;

    /**
     * OID for this entity.
     */
    private String oid;

    /**
     * Constructor for JPA
     */
    protected KoulutusmoduuliToteutus() {
        super();
    }

    /**
     *
     * @param moduuli
     */
    public KoulutusmoduuliToteutus(Koulutusmoduuli moduuli) {
        this();
        this.koulutusmoduuli = moduuli;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimi() {
        return nimi;
    }

    /**
     * @param nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    /**
     *
     * @return
     */
    public KoulutusmoduuliTila getTila() {
        return tila;
    }

    public void setTila(KoulutusmoduuliTila tila) {
        this.tila = tila;
    }

    /**
     * Returns perustiedot, may be null.
     *
     * @return
     */
    public KoulutusmoduuliPerustiedot getPerustiedot() {
        return perustiedot;
    }

    /**
     *
     * @param perustiedot
     */
    public void setPerustiedot(KoulutusmoduuliPerustiedot perustiedot) {
        this.perustiedot = perustiedot;
    }

    public Koulutusmoduuli getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    public void setKoulutusmoduuli(Koulutusmoduuli moduuli) {
        koulutusmoduuli = moduuli;
    }

    /**
     * Returns immutable set of currently added tarjoajat.
     *
     * @return
     */
    public Set<KoulutusmoduuliToteutusTarjoaja> getTarjoajat() {
        return Collections.unmodifiableSet(tarjoajat);
    }

    /**
     * Adds a new KoulutusmooduuliToteutusTarjoaja
     *
     * @param organisaatioOID a non null organisaatio OID.
     * @return true if this tarjoaja was not already added
     */
    public boolean addTarjoaja(String organisaatioOID) {
        final KoulutusmoduuliToteutusTarjoaja t = new KoulutusmoduuliToteutusTarjoaja(organisaatioOID);
        if (!tarjoajat.contains(t)) {
            tarjoajat.add(t);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes tarjoaja with given OID.
     *
     * @param organisaatioOID
     * @return true if given OID was previously added and is now removed
     */
    public boolean removeTarjoaja(String organisaatioOID) {
        KoulutusmoduuliToteutusTarjoaja t = new KoulutusmoduuliToteutusTarjoaja(organisaatioOID);
        return tarjoajat.remove(t);
    }

    /**
     * @return the koulutusLajiUri
     */
    public String getKoulutusLajiUri() {
        return koulutusLajiUri;
    }

    /**
     * @param koulutusLajiUri the koulutusLajiUri to set
     */
    public void setKoulutusLajiUri(String koulutusLajiUri) {
        this.koulutusLajiUri = koulutusLajiUri;
    }

    /**
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

}

