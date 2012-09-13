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

import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker;
import fi.vm.sade.generic.model.BaseEntity;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Common base class for all entities on both the specification and instance side of learning objects. The CEN MLO model names this as "Learning Opportunity
 * Object".
 *
 */
@Entity
@Table(name = Koulutus.TABLE_NAME)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "koulutus_tyyppi", length = 6)
public abstract class Koulutus extends BaseEntity {

    /**
     * Name of the OID column to be used in queries.
     */
    public static final String OID_COLUMN_NAME = "oid";

    static final String TABLE_NAME = "koulutus";

    /**
     * OID that can be assigned once and once only. NOTE: for some reason this does not work - could it have something to do with inheritance??
     */
    @Column(name = OID_COLUMN_NAME, nullable = false, insertable = true, updatable = false)
    private String oid;

    /**
     * 
     */
    @Column(name = "tila")
    private String tila;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Column(name = "nimi")
    private String nimi;

    /**
     * Hierarchy of Koulutus objects that further break down this Koulutus into smaller parts.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private Set<KoulutusSisaltyvyys> children = new HashSet<KoulutusSisaltyvyys>();

    /**
     * Always call super when overriding constructor.
     */
    public Koulutus() {
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
     * Creates a parent-child relationship to given Koulutus.
     *
     * @param child
     * @param optional
     * @return
     * @throws CyclicReferenceException
     */
    public boolean addChild(final Koulutus child, final boolean optional) throws KoulutusTreeException {

        if (child == null) {
            return false;
        }

        if (child == this) {
            // bad use of API, throw to catch bug
            throw new KoulutusTreeException("you cannot add *this* as a child");
        }

        final KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(this, child, optional);
        if (!addChildRelationship(sisaltyvyys)) {
            return false;
        }

        return true;
    }

    /**
     * Removes given Koulutusmoduuli from the "includes" list.
     *
     * @param koulutusmoduuli
     * @return true if moduuli was removed, otherwise false.
     */
    public boolean removeChild(Koulutus child) {

        final KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(this, child, true);
        return children.remove(sisaltyvyys);

    }

    /**
     * Helper method that, before adding,  checks if relationship already exists.
     *
     * @param sisaltyvyys
     * @return
     */
    private boolean addChildRelationship(KoulutusSisaltyvyys sisaltyvyys) {

        if (children.contains(sisaltyvyys)) {
            return false;
        }

        children.add(sisaltyvyys);
        return true;

    }

    /**
     * Returns immutable set of child Koulutus -objects.
     *
     * @return
     */
    public Set<KoulutusSisaltyvyys> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Returns true if given child is a direct or non-direct (unlimited depth) child of this Koulutus. Note that as children are lazily loaded, each level will
     * require one more select.
     *
     * @param child
     * @return
     */
    public boolean hasAsChild(Koulutus child) {
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
    public boolean hasAsChild(Koulutus child, int depth) {

        KoulutusTreeWalker.EqualsMatcher matcher = new KoulutusTreeWalker.EqualsMatcher(child);
        new KoulutusTreeWalker(depth, matcher).walk(this);

        return matcher.isFound();

    }

    /**
     * Constants to be used as discriminator values for *concrete* classes derived from this class.
     */
    interface KoulutusTyyppit {

        String TUTKINNON_OSA = "M10001";

        String TUTKINTO_OHJELMA = "M10002";

        String TUTKINTO_OHJELMA_TOTEUTUS = "T10002";
    }


}

