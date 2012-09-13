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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Jukka Raanamo
 */
@Entity
public abstract class KoulutusmoduuliToteutus extends LearningOpportunityInstance {

    private static final long serialVersionUID = -1278564574746813425L;

    /**
     * Providers of this learning opportunity.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_tarjoaja", joinColumns =
    @JoinColumn(name = "koulutusmoduuli_toteutus_id"))
    private Set<Oid> tarjoajat = new HashSet<Oid>();

    /**
     * Realized Koulutusmoduuli.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(name = TABLE_NAME + "_koulutusmoduuli")
    private Koulutusmoduuli koulutusmoduuli;

    /**
     * Koodisto Uri. Example display values 'Nuorten koulutus, Aikuisten koulutus'.
     */
    @Column(name = "koulutus_laji")
    private String koulutusLaji;

    /**
     * todo: can we set this attribute to "required"?
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "koulutuksen_alkamis_pvm")
    private Date koulutuksenAlkamisPvm;

    /**
     * Koodisto Uri. Example display value '7+2 vuotta'. This is different from the current (15.6.2012) wireframe, but the wireframe is wrong.
     */
    @Column(name = "suunniteltu_kesto")
    private String suunniteltuKesto;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_teema", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> teemas = new HashSet<KoodistoUri>();

    @Size(min = 1)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_opetuskieli", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetuskielis = new HashSet<KoodistoUri>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_ammattinimike", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> ammattinimikes = new HashSet<KoodistoUri>();

    @Size(min = 1)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = TABLE_NAME + "_opetusmuoto", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_id"))
    private Set<KoodistoUri> opetusmuotos = new HashSet<KoodistoUri>();

    /**
     * If non-null, this "koulutus" comes with a charge. This field defines the amount of the charge. The actual content of this field is yet to be defined.
     */
    private String maksullisuus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "koulutus_hakukohde", joinColumns =
    @JoinColumn(name = "koulutus_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns =
    @JoinColumn(name = "hakukohde_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<Hakukohde> hakukohdes = new HashSet<Hakukohde>();

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
    public String getKoulutusLaji() {
        return koulutusLaji;
    }

    /**
     * @param koulutusLajiUri the koulutusLajiUri to set
     */
    public void setKoulutusLaji(String koulutusLajiUri) {
        this.koulutusLaji = koulutusLajiUri;
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
    public String getSuunniteltuKesto() {
        return suunniteltuKesto;
    }

    /**
     *
     * @param suunniteltuKestoUri the suunniteltuKestoUri to set
     */
    public void setSuunniteltuKesto(String suunniteltuKestoUri) {
        this.suunniteltuKesto = suunniteltuKestoUri;
    }

    /**
     *
     * @return the teemaUris
     */
    public Set<KoodistoUri> getTeemas() {
        return Collections.unmodifiableSet(teemas);
    }

    public void removeTeema(KoodistoUri uri) {
        teemas.remove(uri);
    }

    public void addTeema(KoodistoUri uri) {
        teemas.add(uri);
    }

    /**
     *
     * @param teemaUris the teemaUris to set
     */
    public void setTeemas(Set<KoodistoUri> teemaUris) {
        this.teemas = teemaUris;
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

    /**
     * @return the hakukohdes
     */
    public Set<Hakukohde> getHakukohdes() {
        return Collections.unmodifiableSet(hakukohdes);
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
        return Collections.unmodifiableSet(opetuskielis);
    }

    /**
     * @param opetuskielis the opetuskielis to set
     */
    public void addOpetuskieli(KoodistoUri opetuskieli) {
        opetuskielis.add(opetuskieli);
    }

    public void removeOpetuskieli(KoodistoUri opetuskieli) {
        opetuskielis.remove(opetuskieli);
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
        return Collections.unmodifiableSet(opetusmuotos);
    }

    public void addOpetusmuoto(KoodistoUri opetusmuoto) {
        opetusmuotos.add(opetusmuoto);
    }

    public void removeOpetusmuoto(KoodistoUri opetusmuoto) {
        opetusmuotos.remove(opetusmuoto);
    }

}

