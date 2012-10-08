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
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * LearningOpportunityStructure defines the sub modules that are directly under the
 * container module and the semantics that regulate how those child modules may or
 * need to be used.
 *
 */
@Entity
@Table(name = KoulutusRakenne.TABLE_NAME)
public class KoulutusRakenne extends BaseEntity implements Serializable {

    static final String TABLE_NAME = "koulutus_rakenne";

    private static final long serialVersionUID = 7833956682160881671L;

    private static final String PARENT_COLUMN_NAME = "parent_id";

    private Integer minValue;

    private Integer maxValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SelectorType selector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PARENT_COLUMN_NAME)
    private LearningOpportunityObject parent;

    @ManyToMany
    @JoinTable(name = TABLE_NAME + "_koulutus", joinColumns =
    @JoinColumn(name = TABLE_NAME + "_" + ID_COLUMN_NAME, referencedColumnName = BaseEntity.ID_COLUMN_NAME), inverseJoinColumns =
    @JoinColumn(name = LearningOpportunityObject.TABLE_NAME + "_" + LearningOpportunityObject.ID_COLUMN_NAME, referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    private Set<LearningOpportunityObject> children = new HashSet<LearningOpportunityObject>();

    public KoulutusRakenne() {
    }

    public KoulutusRakenne(LearningOpportunityObject parent, LearningOpportunityObject child, SelectorType selector) {
        this.selector = selector;
        this.parent = parent;
        addChild(child);
    }

    public void setParent(LearningOpportunityObject parent) {
        this.parent = parent;
    }

    public LearningOpportunityObject getParent() {
        return parent;
    }

    public Integer getMax() {
        return maxValue;
    }

    public Integer getMin() {
        return minValue;
    }

    public void setMax(Integer max) {
        this.maxValue = max;
    }

    public void setMin(Integer min) {
        this.minValue = min;
    }

    public SelectorType getSelector() {
        return selector;
    }

    public void setSelector(SelectorType selector) {
        this.selector = selector;
    }

    public Set<LearningOpportunityObject> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public void removeChild(LearningOpportunityObject child) {
        children.remove(child);
    }

    public final void addChild(LearningOpportunityObject child) {
        children.add(child);
    }

    public enum SelectorType {

        /**
         * All sub modules are mandatory.
         */
        ALL_OFF,
        /**
         * One of the sub modules must be selected.
         */
        ONE_OFF,
        /**
         * A number between {@link #min} to {@link #max} must be selected.
         */
        NUMBER_OF,
        /**
         * An arbitrary number of modules need to be selected.
         */
        SOME_OFF,
        /**
         * Modules that will sum between {@link #min} and {@link #max} credits must be selected.
         */
        CREDITS,
        /**
         * Modules that will sum between {@link #min} and {@link #max} course units must be selected.
         */
        COURSE_UNITS
    }


}

