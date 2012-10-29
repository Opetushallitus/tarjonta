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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.tarjonta.model.*;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.namespace.QName;

import fi.vm.sade.tarjonta.publication.types.*;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Classification;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityDataWriter extends PublicationCollector.EventHandlerSuppport {

    public static final String DEFAULT_ROOT_ELEMENT_NAME = "LearningOpportunityDownloadData";

    public static final String NAMESPACE = "http://publication.tarjonta.sade.vm.fi/types";

    private XMLStreamWriter xmlWriter;

    private Marshaller marshaller;

    private boolean partialDocument = false;

    private final ObjectFactory objectFactory;

    private String rootElementName = DEFAULT_ROOT_ELEMENT_NAME;

    private static final Logger log = LoggerFactory.getLogger(LearningOpportunityDataWriter.class);

    public LearningOpportunityDataWriter() throws Exception {

        final String packageName = LearningOpportunityDownloadDataType.class.getPackage().getName();
        final JAXBContext context = JAXBContext.newInstance(packageName, LearningOpportunityDownloadDataType.class.getClassLoader());
        objectFactory = new ObjectFactory();

        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

    }

    /**
     * Setting this value to true allows other elements to be written before and after
     * content written here, else this writer will produce a complete XML document.
     *
     * @param partial
     */
    public void setPartialDocument(boolean partial) {

        this.partialDocument = partial;

    }

    public void setOutput(Writer out) throws XMLStreamException {

        xmlWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(out);

    }

    public void setOutput(OutputStream out) throws XMLStreamException {

        setOutput(new OutputStreamWriter(out));

    }

    @Override
    public void onCollectStart() throws Exception {

        if (!partialDocument) {
            xmlWriter.writeStartDocument();
        }

        xmlWriter.writeStartElement("", rootElementName);
        xmlWriter.writeAttribute("xmlns", NAMESPACE);

    }

    @Override
    public void onCollectEnd() throws Exception {

        xmlWriter.writeEndElement();
        if (!partialDocument) {
            xmlWriter.writeEndDocument();
        }

    }

    @Override
    public void onCollect(Haku haku) throws Exception {

        final ApplicationSystemType applicationSystem = objectFactory.createApplicationSystemType();

        marshal(ApplicationSystemType.class, applicationSystem);

    }

    @Override
    public void onCollect(Hakukohde hakukohde) throws Exception {

        ApplicationOptionType applicationOption = objectFactory.createApplicationOptionType();

        ApplicationOptionType.Identifier identifier = new ApplicationOptionType.Identifier();
        identifier.setValue(hakukohde.getOid());
        applicationOption.setIdentifier(identifier);

        ApplicationSystemRefType applicationSystemRef = new ApplicationSystemRefType();
        applicationSystemRef.setOidRef(hakukohde.getHaku().getOid());
        applicationOption.setApplicationSystemRef(applicationSystemRef);

        marshal(ApplicationOptionType.class, applicationOption);

    }

    @Override
    public void onCollect(Koulutusmoduuli moduuli) throws Exception {

        LearningOpportunitySpecificationType specification = objectFactory.createLearningOpportunitySpecificationType();

        if (moduuli.getModuuliTyyppi() != KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA) {
            throw new Exception("KoulutusmoduuliTyyppi not supported: " + moduuli.getModuuliTyyppi());
        }

        // LearningOpportunitySpecification/type
        specification.setType(LearningOpportunityTypeType.DEGREE_PROGRAMME);

        // LearningOpportunitySpecification/id
        specification.setId(moduuli.getOid());

        // LearningOpportunitySpecification/Name
        // todo: how is the name formulated?

        if (moduuli.getUlkoinenTunniste() != null) {
            specification.setIdentifier(moduuli.getUlkoinenTunniste());
        }

        // LearningOpportunitySpecification/OrganizationRef
        OrganizationRefType organizationRef = new OrganizationRefType();
        organizationRef.setOidRef(moduuli.getOmistajaOrganisaatioOid());
        specification.setOrganizationRef(organizationRef);

        // LearningOpportunitySpecification/OfferedBy
        specification.setOfferedBy(null);

        // LearningOpportunitySpecification/Credits
        if (moduuli.getLaajuusArvo() != null) {
            CreditsType credits = new CreditsType();
            credits.setUnit(moduuli.getLaajuusYksikko());
            credits.setValue(moduuli.getLaajuusArvo());
            specification.setCredits(credits);
        }

        // LearningOpportunitySpecification/Qualification
        if (moduuli.getTutkintonimike() != null) {
            QualificationType qualification = new QualificationType();
            qualification.setCode(moduuli.getTutkintonimike());
            specification.setQualification(qualification);
        }

        // LearningOpportunitySpecification/DegreeTitle
        specification.setDegreeTitle(createExtendedString(moduuli.getNimi()));

        // LearningOpportunitySpecification/Classification
        Classification classification = new Classification();
        specification.setClassification(classification);

        // LearningOpportunitySpecification/Classification (koulutuskoodi)
        classification.getClassificationCode().add(createClassification(
            LearningClassificationCodeSchemeType.HTTP_OPH_FI,
            null,
            moduuli.getKoulutusKoodi()));

        // LearningOpportunitySpecification/Classification (koulutusala)
        classification.getClassificationCode().add(createClassification(
            LearningClassificationCodeSchemeType.HTTP_OPH_FI,
            LearningClassificationCategoryType.EDUCATION_DOMAIN,
            moduuli.getKoulutusala()));

        // LearningOpportunitySpecification/Classification (koulutusaste)
        addKoulutusaste(moduuli, classification);

        // LearningOpportunitySpecification/Classification (opintoala)
        addOpintoala(moduuli, classification);

        // LearningOpportunitySpecification/Classification (eqf)
        addEqf(moduuli, classification);

        marshal(LearningOpportunitySpecificationType.class, specification);

    }

    @Override
    public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception {

        LearningOpportunityInstanceType instance = new LearningOpportunityInstanceType();

        marshal(LearningOpportunityInstanceType.class, instance);

    }

    private <T> void marshal(Class<T> elementClass, T data) throws JAXBException, XMLStreamException {

        String name = elementClass.getSimpleName();
        // class name with out the Type postfix
        name = name.substring(0, name.length() - 4);

        marshal(name, elementClass, data);

    }

    private void marshal(String elementName, Class elementClass, Object data) throws JAXBException, XMLStreamException {

        QName qname = new QName("", elementName);
        JAXBElement e = new JAXBElement(qname, elementClass, data);
        marshaller.marshal(e, xmlWriter);

        xmlWriter.flush();

    }

    private static ExtendedStringType createExtendedString(String valueFI) {

        ExtendedStringType s = new ExtendedStringType();
        s.setLang("FI");
        s.setValue(valueFI);
        return s;

    }

    /**
     * Helper method to create classification code.
     *
     * @param scheme
     * @param category
     * @param value
     * @return
     */
    private static LearningClassificationCodeType createClassification(LearningClassificationCodeSchemeType scheme,
        LearningClassificationCategoryType category,
        String value) {

        LearningClassificationCodeType classification = new LearningClassificationCodeType();
        classification.setScheme(scheme);
        if (category != null) {
            classification.setCategory(category);
        }
        classification.setValue(value);

        return classification;

    }

    private void addKoulutusaste(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getKoulutusAste();
        if (uri != null) {
            target.getClassificationCode().add(createClassification(
                LearningClassificationCodeSchemeType.HTTP_OPH_FI,
                LearningClassificationCategoryType.EDUCATION_DEGREE,
                uri));
        }

    }

    private void addOpintoala(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getKoulutusala();
        if (uri != null) {
            target.getClassificationCode().add(createClassification(
                LearningClassificationCodeSchemeType.HTTP_OPH_FI,
                LearningClassificationCategoryType.STYDY_DOMAIN,
                uri));
        }

    }

    private void addEqf(Koulutusmoduuli koulutusmoduuli, Classification target) {

        // TODO: jatka tasta

        final String uri = koulutusmoduuli.getEqfLuokitus();
        if (uri != null) {
            target.getClassificationCode().add(createClassification(
                LearningClassificationCodeSchemeType.HTTP_OPH_FI,
                null,
                uri));
        }

    }

    private static class NoNamespaceContext implements NamespaceContext {

        @Override
        public String getNamespaceURI(String string) {
            return "";
        }

        @Override
        public String getPrefix(String string) {
            return "";
        }

        @Override
        public Iterator getPrefixes(String string) {
            return null;
        }

    }


    private static class PublicationNamespaceContext implements NamespaceContext {

        @Override
        public String getNamespaceURI(String string) {
            System.out.println("getNamespaceURI: " + string);
            return "";
        }

        @Override
        public String getPrefix(String string) {
            System.out.println("getPrefix: " + string);
            return "";
        }

        @Override
        public Iterator getPrefixes(String string) {
            System.out.println("getPrefixes: " + string);
            return null;
        }

    }


}

