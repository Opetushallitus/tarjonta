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

import fi.vm.sade.tarjonta.publication.enricher.koodisto.KoodistoCodeValueCollectionEnricher;
import fi.vm.sade.tarjonta.publication.enricher.koodisto.KoodistoCodeValueEnricher;
import fi.vm.sade.tarjonta.publication.enricher.koodisto.KoodistoLookupService;
import fi.vm.sade.tarjonta.publication.enricher.XMLStreamEnricher;
import fi.vm.sade.tarjonta.publication.enricher.organisaatio.KoulutustarjoajaEnricher;
import fi.vm.sade.tarjonta.publication.enricher.organisaatio.KoulutustarjoajaLookupService;
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

    @Autowired(required = true)
    private KoulutustarjoajaLookupService tarjoajaService;

    private boolean failOnKoodistoError = false;

    /**
     * Element names that should be handled by KoodistoCodeValueEnricher. Note that these elements should have
     * a unique name.
     */
    private static final String[] KOODISTO_CODE_VALUE_TAGS = {
        "EducationClassification",
        "EducationDomain",
        "EducationDegree",
        "StudyDomain",
        "EqfClassification",
        "NqfClassification",
        "Credits",
        "Qualification",
        "Prerequisite",
        "Profession",
        "Keyword",
        "Units",
        "ExaminationType",
        "ApplicationType",
        "ApplicationMethod",
        "ApplicationSeason",
        "EducationStartSeason",
        "TargetGroup"
    };

    /**
     * Path expressions that should be handled by KoodistoCodeValueEnricher.
     */
    private static final String[] KOODISTO_CODE_VALUE_REGEX = {
        ".*/ApplicationOption/Title",
        ".*/Attachment/Type"
    };

    /**
     * Element names that should be handled by KoodistoCodeValueCollectionEnricher. Note that
     * these elements should have a unique name.
     */
    private static final String[] KOODISTO_CODE_VALUE_COLLECTION_TAGS = {
        "LanguagesOfInstruction",
        "FormOfEducation",
        "FormsOfTeaching"
    };

    /**
     * Element names that should be handled by KoulutustarjoajaEnricher.
     */
    private static final String[] KOULUTUSTARJOAJA_TAGS = {
        "LearningOpportunityProvider"
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
    @Override
    public XMLStreamEnricher getObject() {

        final XMLStreamEnricher processor = new XMLStreamEnricher();

        final KoodistoCodeValueEnricher codeValueEnricher = new KoodistoCodeValueEnricher();
        codeValueEnricher.setKoodistoService(koodistoService);
        codeValueEnricher.setFailOnKoodiError(failOnKoodistoError);

        for (String tag : KOODISTO_CODE_VALUE_TAGS) {
            processor.registerTagNameHandler(tag, codeValueEnricher);
        }

        for (String regex : KOODISTO_CODE_VALUE_REGEX) {
            processor.registerRegexHandler(regex, codeValueEnricher);
        }

        final KoodistoCodeValueCollectionEnricher codeValueCollectionEnricher = new KoodistoCodeValueCollectionEnricher();
        codeValueCollectionEnricher.setKoodistoService(koodistoService);
        codeValueCollectionEnricher.setFailOnKoodiError(failOnKoodistoError);

        for (String tag : KOODISTO_CODE_VALUE_COLLECTION_TAGS) {
            processor.registerTagNameHandler(tag, codeValueCollectionEnricher);
        }

        final KoulutustarjoajaEnricher tarjoajaEnricher = new KoulutustarjoajaEnricher();
        tarjoajaEnricher.setTarjoajaService(tarjoajaService);
        for (String tag : KOULUTUSTARJOAJA_TAGS) {
            processor.registerTagNameHandler(tag, tarjoajaEnricher);
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

    /**
     * Set Tarjoaja service that will be passed to enrichers using Koulutustarjoaja data.
     *
     * @param tarjoajaService
     */
    public void setTarjoajaService(KoulutustarjoajaLookupService tarjoajaService) {
        this.tarjoajaService = tarjoajaService;
    }

    /**
     * This flag is passed on to koodisto enrichers.
     *
     * @param failOnKoodistoError
     *
     * @see KoodistoCodeValueEnricher#setFailOnKoodiError(boolean)
     * @see KoodistoCodeValueCollectionEnricher#setFailOnKoodiError(boolean)
     */
    public void setFailOnKoodistoError(boolean failOnKoodistoError) {
        this.failOnKoodistoError = failOnKoodistoError;
    }

}

