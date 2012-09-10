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

import javax.persistence.MappedSuperclass;

/**
 * An abstract description of a learning opportunity, consisting of information that will be 
 * consistent across multiple instances of the learning opportunity.
 * 
 * @see http://mjukis.blogg.skolverket.se/files/2008/10/mlo-ad-v5.pdf
 */
@MappedSuperclass
public abstract class LearningOpportunitySpecification extends Koulutus {
    
}

