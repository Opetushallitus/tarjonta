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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

/**
 * A single occurrence of a learning opportunity. Unlike a Learning Opportunity Specification, 
 * a Learning Opportunity Instance is not abstract, may be bound to particular dates or locations, 
 * and may be applied for or participated in by learners.
 * 
 * @see http://mjukis.blogg.skolverket.se/files/2008/10/mlo-ad-v5.pdf
 */
@Entity
public abstract class LearningOpportunityInstance extends LearningOpportunityObject {

    private static final long serialVersionUID = 7347253354901040237L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(name = TABLE_NAME + "_koulutusmoduuli")
    private LearningOpportunitySpecification specification;

    protected LearningOpportunityInstance() {
        super();
    }

    public LearningOpportunityInstance(LearningOpportunitySpecification specification) {
        this();
        this.specification = specification;
    }

    /**
     * Returns the LOS this LOI specifies.
     *
     * @return
     */
    public LearningOpportunitySpecification getLearningOpportunitySpecification() {
        return specification;
    }

    public final void setLearningOpportunitySpecification(LearningOpportunitySpecification newSpecification) {

        if (this.specification == newSpecification) {
            return;
        }

        // use "updatable=false"?
        if (this.specification != null && newSpecification != null) {
            if (!this.specification.equals(newSpecification)) {
                throw new IllegalArgumentException("reference to Koulutusmoduuli cannot be chagned, was: "
                    + specification + ", update attempted to: " + newSpecification);
            }
        }

        this.specification = newSpecification;
        if (this.specification != null) {
            this.specification.addLearningOpportunityInstance(this);
        }

    }

}

