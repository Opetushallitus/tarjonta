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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.namespace.QName;
import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.NamespaceContext;

import fi.vm.sade.tarjonta.publication.types.*;
import fi.vm.sade.tarjonta.publication.types.CodeValueType.Code;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Classification;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Description;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.*;

/**
 * Stream based writer. Writes data using JAXB classes generated from Tarjonta "publication" schema.
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityJAXBWriter extends PublicationCollector.EventHandlerSuppport {

    public static final String DEFAULT_ROOT_ELEMENT_NAME = "LearningOpportunityDownloadData";

    public static final String NAMESPACE = "http://publication.tarjonta.sade.vm.fi/types";

    private XMLStreamWriter xmlWriter;

    private Marshaller marshaller;

    private boolean partialDocument = false;

    private final ObjectFactory objectFactory;

    private static DatatypeFactory datatypeFactory;

    private String rootElementName = DEFAULT_ROOT_ELEMENT_NAME;

    private static final Logger log = LoggerFactory.getLogger(LearningOpportunityJAXBWriter.class);

    public LearningOpportunityJAXBWriter() throws JAXBException {

        final String packageName = LearningOpportunityDownloadDataType.class.getPackage().getName();
        final JAXBContext context = JAXBContext.newInstance(packageName, LearningOpportunityDownloadDataType.class.getClassLoader());

        objectFactory = new ObjectFactory();
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            // there's nothing that can be done
            throw new IllegalStateException("configuring DatatypeFactory failed", e);
        }

        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

    }

    /**
     * By passing a true value, this writer will not write start and end of
     * document to stream.
     *
     * todo: it seems this does not work with XMLStreamWriter
     *
     * @param partial
     */
    public void setPartialDocument(boolean partial) {

        this.partialDocument = partial;

    }

    /**
     * XML output.
     *
     * @param out
     * @throws XMLStreamException
     */
    public void setOutput(Writer out) throws XMLStreamException {

        xmlWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(out);

    }

    /**
     * XML output.
     *
     * @param out
     * @throws XMLStreamException
     */
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

        log.debug("marshalling Koulutusmoduuli by oid: " + moduuli.getOid());

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
        specification.setDegreeTitle(createExtendedString(moduuli.getTutkintonimike()));

        // LearningOpportunitySpecification/Classification
        Classification classification = new Classification();
        specification.setClassification(classification);

        addKoulutusluokitus(moduuli, classification);
        addKoulutusala(moduuli, classification);
        addKoulutusaste(moduuli, classification);
        addOpintoala(moduuli, classification);
        addEqf(moduuli, classification);
        addNqf(moduuli, classification);


        // LearningOpportunitySpecification/Description
        Description description = new Description();
        specification.setDescription(description);

        // LearningOpportunitySpecification/Description/StructureDiagram

        marshal(LearningOpportunitySpecificationType.class, specification);

    }

    @Override
    public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception {

        log.debug("marshalling KoulutusmoduuliToteutus by oid: " + toteutus.getOid());

        LearningOpportunityInstanceType instance = new LearningOpportunityInstanceType();

        // LearningOpportunityInstance#id
        instance.setId(toteutus.getOid());

        // LearningOpportunityInstance/Identifier
        if (toteutus.getUlkoinenTunniste() != null) {
            instance.setIdentifier(toteutus.getUlkoinenTunniste());
        }

        // LearningOpportunityInstance/SpecificationRef
        LearningOpportunitySpecificationRefType losRef = new LearningOpportunitySpecificationRefType();
        losRef.setRef(toteutus.getKoulutusmoduuli().getOid());
        instance.setSpecificationRef(losRef);

        // LearningOpportunityInstance/OrganizationRef
        OrganizationRefType orgRef = new OrganizationRefType();
        orgRef.setOidRef(toteutus.getTarjoaja());
        instance.setOrganizationRef(orgRef);

        // LearningOpportunityInstance/ProfessionLabels
        addAmmattinimikkeet(toteutus, instance);

        // LearningOpportunityInstance/Keywords
        addAvainsanat(toteutus, instance);

        // LearningOpportunityInstance/LanguagesOfInsruction
        addOpetuskielet(toteutus, instance);

        // LearningOpportunityInstance/FormOfEducation
        addKoulutuslajit(toteutus, instance);

        // LearningOpportunityInstance/FormOfTeaching
        addOpetusmuodot(toteutus, instance);

        // LearningOpportunityInstance/StartDate
        instance.setStartDate((toteutus.getKoulutuksenAlkamisPvm()));

        // LearningOpportunityInstance/AcademicYear
        instance.setAcademicYear(formatAcademicYear(toteutus.getKoulutuksenAlkamisPvm()));

        // LearningOpportunityInstance/Duration
        instance.setDuration(formatDuration(toteutus.getSuunniteltuKestoYksikko(), toteutus.getSuunniteltuKestoArvo()));

        marshal(LearningOpportunityInstanceType.class, instance);

    }

    /**
     * Marshals data fragment into underlying stream.
     * @param <T> type of element to write
     * @param elementClass
     * @param data fragment to write
     * @throws JAXBException
     * @throws XMLStreamException
     */
    private <T> void marshal(Class<T> elementClass, T data) throws JAXBException, XMLStreamException {

        // class name with out the Type postfix
        String name = elementClass.getSimpleName();
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

    private void addOpetusmuodot(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getOpetusmuotos());
        target.setFormsOfTeaching(collection);

    }

    private void addKoulutuslajit(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getKoulutuslajis());
        target.setFormOfEducation(collection);

    }

    private void addOpetuskielet(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        CodeValueCollectionType collection = toCodeValueCollection(toteutus.getOpetuskielis());
        target.setLanguagesOfInstruction(collection);

    }

    /**
     * Helper method that converts Koodisto uri's to codes. If input set is null or empty, null is returned.
     *
     * @param uris
     * @return
     */
    private static CodeValueCollectionType toCodeValueCollection(Set<KoodistoUri> uris) {

        if (uris == null || uris.isEmpty()) {
            return null;
        }

        CodeValueCollectionType collection = new CodeValueCollectionType();
        collection.setScheme(CodeSchemeType.KOODISTO);
        List<CodeValueCollectionType.Code> codes = collection.getCode();
        for (KoodistoUri uri : uris) {
            CodeValueCollectionType.Code code = new CodeValueCollectionType.Code();
            code.setValue(uri.getKoodiUri());
            codes.add(code);
        }
        return collection;

    }

    private void addAmmattinimikkeet(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        final Set<KoodistoUri> uris = toteutus.getAmmattinimikes();
        if (uris != null && !uris.isEmpty()) {

            ProfessionCollectionType professions = new ProfessionCollectionType();
            List<CodeValueType> list = professions.getProfession();

            for (KoodistoUri uri : uris) {
                list.add(createCodeValue(CodeSchemeType.KOODISTO, uri.getKoodiUri()));
            }

            target.setProfessionLabels(professions);

        }

    }

    private void addAvainsanat(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        final Set<KoodistoUri> uris = toteutus.getAvainsanas();
        if (uris != null && !uris.isEmpty()) {

            LearningOpportunityInstanceType.Keywords keywords = new LearningOpportunityInstanceType.Keywords();
            List<CodeValueType> list = keywords.getKeyword();

            for (KoodistoUri uri : uris) {
                list.add(createCodeValue(CodeSchemeType.KOODISTO, uri.getKoodiUri()));
            }

            target.setKeywords(keywords);

        }

    }

    private void addKoulutusala(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getKoulutusala();
        if (uri != null) {
            target.setEducationDomain(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }

    }

    private void addKoulutusluokitus(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getKoulutusKoodi();
        if (uri != null) {
            target.setEducationClassification(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }

    }

    private void addKoulutusaste(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getKoulutusAste();
        if (uri != null) {
            target.setEducationDegree(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }

    }

    private void addOpintoala(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getKoulutusala();
        if (uri != null) {
            target.setStudyDomain(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }

    }

    private void addEqf(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getEqfLuokitus();
        if (uri != null) {
            target.setEqfClassification(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }

    }

    private void addNqf(Koulutusmoduuli koulutusmoduuli, Classification target) {

        final String uri = koulutusmoduuli.getNqfLuokitus();
        if (uri != null) {
            target.setNqfClassification(createCodeValue(CodeSchemeType.KOODISTO, uri));
        }

    }

    private static String formatAcademicYear(Date date) {

        return new SimpleDateFormat("yyyy").format(date);

    }

    /**
     * Converts "tarjonta duration" into XML Duration. This logic assumes that we
     * are only dealing with whole years. Use of koodisto uris makes of no sense.
     *
     * @param unitsUri not handled
     * @param value years as number expected
     * @return
     */
    private static Duration formatDuration(String unitsUri, String value) {

        // fix me: disabled for now since values are any text
        if (1 < 2) {
            return null;
        }

        // likely to throw exception if values are something like "3-4"
        int numYears = Integer.parseInt(value);
        int numMonths = 0;

        return datatypeFactory.newDurationYearMonth(true, numYears, numMonths);

    }

    private static CodeValueType createCodeValue(CodeSchemeType scheme, String codeValue) {

        Code code = new Code();
        code.setScheme(scheme);
        code.setValue(codeValue);

        CodeValueType codeValueType = new CodeValueType();
        codeValueType.setCode(code);

        return codeValueType;

    }

}

