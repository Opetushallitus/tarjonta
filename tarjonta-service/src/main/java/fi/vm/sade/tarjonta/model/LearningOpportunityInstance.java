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
 * A single occurrence of a learning opportunity. Unlike a Learning Opportunity Specification, 
 * a Learning Opportunity Instance is not abstract, may be bound to particular dates or locations, 
 * and may be applied for or participated in by learners.
 * 
 * @see http://mjukis.blogg.skolverket.se/files/2008/10/mlo-ad-v5.pdf
 * @author Jukka Raanamo
 */
@MappedSuperclass
public abstract class LearningOpportunityInstance extends Koulutus {

    private static final long serialVersionUID = 7347253354901040237L;
}

