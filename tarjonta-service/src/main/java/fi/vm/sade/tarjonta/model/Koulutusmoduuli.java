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

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;

/**
 * An abstract base class for different types of Koulutusmoduuli's. This class adds OPH specified features to LOS.
 */
@Entity
public abstract class Koulutusmoduuli extends LearningOpportunitySpecification implements Serializable {

    private static final long serialVersionUID = -3359195324699691606L;

    private static Logger log = LoggerFactory.getLogger(Koulutusmoduuli.class);

    /*
     * This type could be derived from the actual derived class but we want this information to persist in database.
     */
    @Enumerated(EnumType.STRING)
    private KoulutusmoduuliTyyppi tyyppi;

    /**
     * TODO: double check how and where this is used. There are multiple names for different types of Koulutusmoduuli - make sure they have sensible purpose.
     */
    private String nimi;

    /**
     * The "owner" of this koulutusmoduuli.
     */
    @Column(name = "owner_oid")
    private String ownerOrganisaatioOid;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutus_ala")
    private String koulutusAla;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "eqf_luokitus")
    private String eqfLuokitus;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "nqf_luokitus")
    private String nqfLuokitus;

    /**
     * Koodisto uri. See accessors for more info.
     */
    @Column(name = "koulutus_aste")
    private String koulutusAste;

    /**
     * Constructor for JPA
     */
    protected Koulutusmoduuli() {
        super();
    }

    /**
     *
     * @param tyyppi
     */
    protected Koulutusmoduuli(KoulutusmoduuliTyyppi tyyppi) {
        this();
        this.tyyppi = tyyppi;
    }

    /**
     *
     *
     * @return
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * todo: can you set value directly or was it calculated from some other properties?
     *
     * @param nimi
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    /**
     * @return the organisaatioOid
     */
    public String getOwnerOrganisaatioOid() {
        return ownerOrganisaatioOid;
    }

    /**
     * @param organisaatioOid the organisaatioOid to set
     */
    public void setOwnerOrganisaatioOid(String organisaatioOid) {
        ownerOrganisaatioOid = organisaatioOid;
    }

    /**
     * When entered by user this is typically a numeric value. The actual data is stored in Koodisto and this methods returns its uri.
     *
     * @return uri to koodisto
     */
    public String getKoulutusAla() {
        return koulutusAla;
    }

    /**
     * @param koulutusAla the koulutusAla to set
     */
    public void setKoulutusAla(String koulutusAla) {
        this.koulutusAla = koulutusAla;
    }

    /**
     * EU classification
     *
     * @see https://confluence.csc.fi/display/oppija/EQF-luokitus
     * @see http://en.wikipedia.org/wiki/European_Qualifications_Framework
     *
     * @return the eqfLuokitus
     */
    public String getEqfLuokitus() {
        return eqfLuokitus;
    }

    /**
     * @param eqfLuokitus the eqfLuokitus to set
     */
    public void setEqfLuokitus(String eqfLuokitus) {
        this.eqfLuokitus = eqfLuokitus;
    }

    /**
     * National Qualification Framework. Describes how Finnish qualification system connects with EQF.
     *
     * @see http://www.oph.fi/mobility/qualifications_frameworks
     *
     * @return the nqfLuokitus
     */
    public String getNqfLuokitus() {
        return nqfLuokitus;
    }

    /**
     * @param nqfLuokitus the nqfLuokitus to set
     */
    public void setNqfLuokitus(String nqfLuokitus) {
        this.nqfLuokitus = nqfLuokitus;
    }

    /**
     * Sample content of the actual data:
     * <pre>
     * 0	Esiaste
     * 1	Alempi perusaste
     * 2	Ylempi perusaste
     * 3	Keskiaste
     * 5	Alin korkea-aste
     * 6	Alempi korkeakouluaste
     * 7	Ylempi korkeakouluaste
     * 8	Tutkijakoulutusaste
     * 9	Koulutusaste tuntematon
     * </pre>
     *
     * @see http://www.stat.fi/meta/luokitukset/koulutusaste/versio.html
     *
     * @return uri to koodisto
     */
    public String getKoulutusAste() {
        return koulutusAste;
    }

    /**
     * @param koulutusAste the koulutusAste to set
     */
    public void setKoulutusAste(String koulutusAste) {
        this.koulutusAste = koulutusAste;
    }

}

