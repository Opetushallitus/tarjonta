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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker;
import fi.vm.sade.generic.model.BaseEntity;

/**
 * Common base class for all entities on both the specification and instance side of learning objects. 
 * The CEN MLO model names this as "Learning Opportunity Object".
 * 
 * <p>
 * Note about version management: multiple versions of single LOO is currently not implemented due to the fact that 
 * LOO's version life cycle is rather lightly documented: https://confluence.csc.fi/pages/viewpage.action?pageId=8688089.
 * </p>
 *
 */
@Entity
@Table(name = LearningOpportunityObject.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "koulutus_tyyppi", length = 6)
public abstract class LearningOpportunityObject extends BaseEntity implements Comparable<LearningOpportunityObject> {

    public static final String TABLE_NAME = "koulutus";

    public static final String OID_COLUMN_NAME = "oid";

    private static final long serialVersionUID = -8023508784857174305L;

    @Column(name = OID_COLUMN_NAME, nullable = false, insertable = true, updatable = false)
    private String oid;

    @Column(name = "tila")
    private String tila;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Column(name = "nimi")
    private String nimi;

    @Column(name = "learning_opportunity_type")
    private String learningOpportunityType;

    /**
     * Used for versioning Koulutus -objects. Do not mix this version field in BaseEntity used by Hibernate.
     */
    private int koulutusVersio;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true)
    private Set<KoulutusSisaltyvyys> children = new HashSet<KoulutusSisaltyvyys>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child", orphanRemoval = true)
    private Set<KoulutusSisaltyvyys> parents = new HashSet<KoulutusSisaltyvyys>();

    /**
     * Make sure you call super when overriding constructor.
     */
    public LearningOpportunityObject() {
        // can we even assign this value if it comes from koodisto??
        this.tila = KoodistoContract.TarjontaTilat.SUUNNITTELUSSA;
    }

    @PreUpdate
    protected void beforeUpdate() {
        updated = new Date();
    }

    @PrePersist
    protected void beforePersist() {
        updated = new Date();
    }

    /**
     * OID of this Koulutus. On database level, this does not uniquely identify Koulutus. For that version needs to be specified.
     *
     * @return the koulutusOid
     */
    public String getOid() {
        return oid;
    }

    /**
     * Assing the OID. Once this entity is persisted - OID cannot be changed.
     *
     * @param koulutusOid the koulutusOid to set
     */
    public void setOid(String koulutusOid) {
        this.oid = koulutusOid;
    }

    /**
     * Returns timestamp when this Koulutusmoduuli was updated or null if has never been persisted.
     *
     * @return
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * Returns uri to koodisto representing current state of this Koulutus.
     *
     * @return
     */
    public String getTila() {
        return tila;
    }

    /**
     * Set uri to koodisto representing the current state of this Koulutus.
     * 
     * @param tila
     */
    public void setTila(String tila) {
        // todo: since states come from koodisto, can we do any state lifecycle validation??
        this.tila = tila;
    }

    /**
     * Returns "static" name for this Koulutus. The actual content may be calculated dynamically based on other
     * properties. This 
     * 
     * @return the nimi
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * Set the display name to be used with this Koulutus. Note that in some cases this value may be recalculated 
     * dynamically based on other properties. 
     * 
     * @param nimi the nimi to set
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    /**
     * Returns immutable set of child relations.
     *
     * @return
     */
    public Set<KoulutusSisaltyvyys> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Returns immutable set of parent relations.
     * 
     * @return
     */
    public Set<KoulutusSisaltyvyys> getParents() {
        return Collections.unmodifiableSet(parents);
    }

    /**
     * Returns true if given child is a direct or non-direct (unlimited depth) child of this Koulutus. 
     * Note that as children are lazily loaded, each level will require one more select.
     *
     * @param child
     * @return
     */
    public boolean hasAsChild(LearningOpportunityObject child) {
        return hasAsChild(child, -1);
    }

    /**
     * Returns true if given child is a direct or non-direct child of this Koulutus, depth begin limited to
     * <code>depth</code>.
     *
     * @param child child to match
     * @param depth maximum depth to use while searching
     * @return
     */
    public boolean hasAsChild(LearningOpportunityObject child, int depth) {

        KoulutusTreeWalker.EqualsMatcher matcher = new KoulutusTreeWalker.EqualsMatcher(child);
        new KoulutusTreeWalker(depth, matcher).walk(this);

        return matcher.isFound();

    }

    /**
     * Convenience method that instead of returning set of KoulutusSisaltyvyys, 
     * returns only the children from those elements.
     * 
     * @return
     */
    public Set<LearningOpportunityObject> getChildNodes() {

        Set<LearningOpportunityObject> nodes = new HashSet<LearningOpportunityObject>();
        for (KoulutusSisaltyvyys s : children) {
            nodes.add(s.getChild());
        }
        return nodes;

    }

    /**
     * Simple comparison by Koulutus name.
     * 
     * @param koulutus
     * @return
     */
    @Override
    public int compareTo(LearningOpportunityObject loo) {
        if (nimi == null) {
            return (loo.getNimi() == null ? 0 : 1);
        } else {
            return nimi.compareTo(loo.getNimi());
        }
    }

    /**
     * @return the learningOpportunityType
     */
    public String getLearningOpportunityType() {
        return learningOpportunityType;
    }

    /**
     * @param learningOpportunityType the learningOpportunityType to set
     */
    public void setLearningOpportunityType(String learningOpportunityType) {
        this.learningOpportunityType = learningOpportunityType;
    }

    /**
     * Constants to be used as discriminator values for *concrete* classes derived from this class.
     */
    interface KoulutusTyyppit {

        String TUTKINNON_OSA = "M10001";

        String TUTKINNON_OSA_TOTEUTUS = "T10001";

        String TUTKINTO_OHJELMA = "M10002";

        String TUTKINTO_OHJELMA_TOTEUTUS = "T10002";
    }


}

