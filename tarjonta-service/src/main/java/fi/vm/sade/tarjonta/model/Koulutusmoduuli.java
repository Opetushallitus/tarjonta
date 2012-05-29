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
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;
import java.util.Collections;
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
@Table(name = Koulutusmoduuli.TABLE_NAME)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Koulutusmoduuli extends LearningOpportunitySpecification {

    public static final String TABLE_NAME = "koulutusmoduuli";

    private static Logger log = LoggerFactory.getLogger(Koulutusmoduuli.class);

    private KoulutusmoduuliTyyppi tyyppi;

    private KoulutusmoduuliTila tila;

    private KoulutusmoduuliPerustiedot perustiedot;

    /**
     *
     */
    private String oid;

    /**
     * Set of Koulutusmoduuli where this Koulutusmoduuli is in a role of child.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "child", orphanRemoval = true)
    private Set<KoulutusmoduuliSisaltyvyys> parents = new HashSet<KoulutusmoduuliSisaltyvyys>();

    /**
     * Set of Koulutusmoduuli for which this Koulutusmoduulis is in a role of parent.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private Set<KoulutusmoduuliSisaltyvyys> children = new HashSet<KoulutusmoduuliSisaltyvyys>();

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
    public Koulutusmoduuli(KoulutusmoduuliTyyppi tyyppi) {
        this();
        this.tyyppi = tyyppi;
        this.tila = KoulutusmoduuliTila.SUUNNITELUSSA;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * Returns the state (tila) this Koulutusmoduuli is in.
     *
     * @return
     */
    public KoulutusmoduuliTila getTila() {
        return tila;
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
     * Returns all those Koulutusmoduuli this Koulutusmoduuli is include into. The returned list is immutable.
     *
     * @return
     */
    public Set<KoulutusmoduuliSisaltyvyys> getParents() {
        return Collections.unmodifiableSet(parents);
    }

    /**
     * Adds this Koulutusmoduuli as a child of given parent. These two lines have the same effect:
     *
     * <pre>
     * child.addParent(parent);
     * parent.addChild(child);
     * </pre>
     *
     * The given parent cannot be a child of this Koulutusmoduuli.
     *
     * @param parent
     * @return
     * @throws CyclicDependencyException if given parent is a child of this Koulutusmoduuli.
     */
    public boolean addParent(Koulutusmoduuli parent, boolean optional) throws CyclicReferenceException {

        if (parent == null) {
            return false;
        }

        final KoulutusmoduuliSisaltyvyys sisaltyvyys = new KoulutusmoduuliSisaltyvyys(parent, this, optional);

        addParent(sisaltyvyys);
        parent.addChildRelationship(sisaltyvyys);

        return true;

    }

    /**
     * Attach this as the child of given
     *
     * @param sisaltyvyys
     * @return
     * @throws CyclicReferenceException
     */
    private boolean addParent(KoulutusmoduuliSisaltyvyys sisaltyvyys) throws CyclicReferenceException {

        // TODO: can you add some Koulutusmoduuli twice under one parent with different optionality??
        if (parents.contains(sisaltyvyys)) {
            return false;
        }
        if (hasAsChild(sisaltyvyys.getParent())) {
            throw new CyclicReferenceException("cannot add as parent since already exists as child: " + sisaltyvyys.getParent());
        }

        parents.add(sisaltyvyys);
        return true;

    }

    /**
     * Adds koulutusmoduuli which is included into this Koulutusmoduuli.
     *
     * @param child
     * @param optional
     * @return
     * @throws CyclicReferenceException
     */
    public boolean addChild(Koulutusmoduuli child, boolean optional) throws CyclicReferenceException {

        if (child == null) {
            return false;
        }

        final KoulutusmoduuliSisaltyvyys sisaltyvyys = new KoulutusmoduuliSisaltyvyys(this, child, optional);
        if (!addChildRelationship(sisaltyvyys)) {
            return false;
        }

        child.addParent(sisaltyvyys);

        return true;
    }

    private boolean addChildRelationship(KoulutusmoduuliSisaltyvyys sisaltyvyys) {

        if (children.contains(sisaltyvyys)) {
            return false;
        }

        children.add(sisaltyvyys);
        return true;

    }

    public boolean removeParent(Koulutusmoduuli parent) {

        KoulutusmoduuliSisaltyvyys sisaltyvyys = new KoulutusmoduuliSisaltyvyys(parent, this, true);
        boolean removed = parents.remove(sisaltyvyys);
        if (removed) {
            parent.removeChild(this);
        }
        return removed;

    }

    /**
     * Removes given Koulutusmoduuli from the "includes" list.
     *
     * @param koulutusmoduuli
     * @return true if moduuli was removed, otherwise false.
     */
    public boolean removeChild(Koulutusmoduuli child) {

        KoulutusmoduuliSisaltyvyys sisaltyvyys = new KoulutusmoduuliSisaltyvyys(this, child, true);
        boolean removed = children.remove(sisaltyvyys);
        if (removed) {
            child.removeParent(this);
        }
        return removed;

    }
    
  
    /**
     * Returns all those Koulutusmoduuli that this Koulutusmoduuli includes. The returned list is immutable.
     *
     * @return
     */
    public Set<KoulutusmoduuliSisaltyvyys> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public boolean hasAsChild(Koulutusmoduuli child) {
        return hasAsChild(child, -1);
    }

    public boolean hasAsChild(Koulutusmoduuli child, int depth) {

        KoulutusmoduuliTreeWalker.NodeEqualsTester tester = new KoulutusmoduuliTreeWalker.NodeEqualsTester(child);
        KoulutusmoduuliTreeWalker.createWalker(depth, tester).walkDown(this);

        return tester.isFound();

    }

}

