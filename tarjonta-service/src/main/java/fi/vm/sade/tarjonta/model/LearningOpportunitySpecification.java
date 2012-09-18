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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * An abstract description of a learning opportunity, consisting of information that will be 
 * consistent across multiple instances of the learning opportunity.
 * 
 * @see http://mjukis.blogg.skolverket.se/files/2008/10/mlo-ad-v5.pdf
 */
@Entity
public abstract class LearningOpportunitySpecification extends LearningOpportunityObject {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "specification")
    private Set<LearningOpportunityInstance> instances = new HashSet<LearningOpportunityInstance>();

    public Set<LearningOpportunityInstance> getLearningOpportunityInstances() {

        return Collections.unmodifiableSet(instances);

    }

    public boolean addLearningOpportunityInstance(LearningOpportunityInstance instance) {
        if (!instances.contains(instance)) {
            instances.add(instance);
            instance.setLearningOpportunitySpecification(this);
            return true;
        }
        return false;
    }

    public boolean removeLearningOppotunityInstance(LearningOpportunityInstance instance) {
        if (instances.remove(instance)) {
            instance.setLearningOpportunitySpecification(this);
            return true;
        }
        return false;
    }

}

