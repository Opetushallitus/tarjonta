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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;


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

    /**
     * Display name, most likely generated from other properties.
     */
    @Column
    private String nimi;

    /**
     * Most koodisto related values are stored into KoulutusmoduuliPerustiedot.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private KoulutusmoduuliPerustiedot perustiedot;

    /**
     * <p>Note 20.6.2012: the same state enumeration is now being used as it is for Koulutusmoduuli - verify whether the same state diagram 
     * applies from Seppo</p>
     */
    @Enumerated(EnumType.STRING)
    private KoulutusmoduuliTila tila;

    /**
     * OID references to those Organisaatio that offers this toteutus.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_tarjoaja", joinColumns =
    @JoinColumn(name = "koulutusmoduuli_toteutus_id"))    
    private Set<Oid> tarjoajat = new HashSet<Oid>();

    /**
     * The Koulutusmoduuli this "implementation" implements and/or completes.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(name = TABLE_NAME + "_koulutusmoduuli")
    private Koulutusmoduuli koulutusmoduuli;

    /**
     * Koodisto Uri. Example display values 'Nuorten koulutus, Aikuisten koulutus'.
     *
     * <p>Note 11.06.2012: as per today, when looking at the "model" you'll find this attribute from Koulutusmoduuli. 
     * This is a mistake, the model is just not updated.</p>
     *
     */
    private String koulutusLajiUri;

    /**
     * <p>Note 11.06.2012: this attribute has been added via wire frame and at least as per today, does not exists in the model</p>
     */
    @Temporal(TemporalType.DATE)
    private Date koulutuksenAlkamisPvm;

    /**
     * Koodisto Uri. Example display value '7+2 vuotta'. This is different from the current (15.6.2012) 
     * wireframe, but the wireframe is wrong.
     */
    private String suunniteltuKestoUri;

    /**
     * Set of Koodisto uris, one for each "teema" (theme) provided.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_teema", joinColumns =
    @JoinColumn(name = "koulutusmoduuli_toteutus_id"))
    private Set<KoodistoKoodiUri> teemaUris;

    /**
     * If defined, this "koulutus" comes with a charge. This field defines the amount of the charge. The actual content of this 
     * field is yet to be defined.
     */
    private String maksullisuus;

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
     * @param moduuli Koulutusmoduuli this KoulutusumoduuliToteutus "implements".
     */
    public KoulutusmoduuliToteutus(Koulutusmoduuli moduuli) {
        this();
        this.koulutusmoduuli = moduuli;
    }

    /**
     * Returns OID for this KoulutusmoduuliToteutus
     *
     * @return
     */
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     *
     * @return
     */
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

    /**
     * Returns the Koulutusmoduuli this KoulutusmoduuliToteutus "implements".
     *
     * @return
     */
    public Koulutusmoduuli getKoulutusmoduuli() {
        return koulutusmoduuli;
    }

    public void setKoulutusmoduuli(Koulutusmoduuli moduuli) {
        koulutusmoduuli = moduuli;
    }

    /**
     * Returns immutable set of OID references to Organisaatio that offer this KoulutusmoduuliToteutus.
     *
     * @return
     */
    public Set<String> getTarjoajat() {
        Set<String> copy = new HashSet<String>(tarjoajat.size());
        for (Oid tarjoaja : tarjoajat) {
            copy.add(tarjoaja.getOid());
        }
        return copy;        
    }

    /**
     * Adds a new KoulutusmooduuliToteutus tarjoaja
     *
     * @param organisaatioOID a non null organisaatio OID.
     * @return true if this tarjoaja was not already added
     */
    public boolean addTarjoaja(String organisaatioOID) {
        final Oid tarjoaja = new Oid(organisaatioOID);
        if (!tarjoajat.contains(tarjoaja)) {
            tarjoajat.add(tarjoaja);
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
        final Oid tarjoaja = new Oid(organisaatioOID);
        return tarjoajat.remove(tarjoaja);
    }

    /**
     * Koodisto uri
     *
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
     * @return the suunniteltuKestoUri
     */
    public String getSuunniteltuKestoUri() {
        return suunniteltuKestoUri;
    }

    /**
     *
     * @param suunniteltuKestoUri the suunniteltuKestoUri to set
     */
    public void setSuunniteltuKestoUri(String suunniteltuKestoUri) {
        this.suunniteltuKestoUri = suunniteltuKestoUri;
    }

    /**
     *
     * @return the teemaUris
     */
    public Set<KoodistoKoodiUri> getTeemaUris() {
        return teemaUris;
    }

    /**
     *
     * @param teemaUris the teemaUris to set
     */
    public void setTeemaUris(Set<KoodistoKoodiUri> teemaUris) {
        this.teemaUris = teemaUris;
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

}

