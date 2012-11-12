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
package fi.vm.sade.tarjonta.publication.enricher.factory;

import fi.vm.sade.tarjonta.publication.enricher.KoodistoCodeValueEnricher;
import fi.vm.sade.tarjonta.publication.enricher.KoodistoLookupService;
import fi.vm.sade.tarjonta.publication.enricher.XMLStreamEnricher;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Factory class that creates XMLStreamEnricher which has been populated
 * with XML enrichers which know how to enrich Tarjonta export
 * schema based XML (LearningOpportunityData).
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityDataEnricherFactory implements FactoryBean<XMLStreamEnricher> {

    @Autowired(required = true)
    private KoodistoLookupService koodistoService;

    /**
     * Element names that should be handled by KoodistoCodeValueEnricher.
     */
    private static final String[] KOODISTO_CODE_VALUE_TAGS = {
        "EducationClassification",
        "EducationDomain",
        "EducationDegree",
        "StudyDomain",
        "EqfClassification",
        "NqfClassification",
        "Credits"
    };

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Class<?> getObjectType() {
        return XMLStreamEnricher.class;
    }

    /**
     * Returns new XMLStreamEnricher prepared with enrichers that know how to handle
     * LearningOpportunityData based XML documents.
     *
     * @return
     */
    public XMLStreamEnricher getObject() {

        XMLStreamEnricher processor = new XMLStreamEnricher();

        KoodistoCodeValueEnricher codeValueEnricher = new KoodistoCodeValueEnricher();
        codeValueEnricher.setKoodistoService(koodistoService);

        for (String tag : KOODISTO_CODE_VALUE_TAGS) {
            processor.registerHandler(tag, codeValueEnricher);
        }

        return processor;

    }

    /**
     * Set Koodisto service that will be passed to enrichers using Koodisto data.
     *
     * @param koodistoService
     */
    public void setKoodistoService(KoodistoLookupService koodistoService) {
        this.koodistoService = koodistoService;
    }

}

