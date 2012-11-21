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
import java.util.List;
import java.util.Set;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

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

import fi.vm.sade.tarjonta.publication.types.*;
import fi.vm.sade.tarjonta.publication.types.CodeValueType.Code;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Classification;
import fi.vm.sade.tarjonta.publication.types.LearningOpportunitySpecificationType.Description;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.model.Koulutustarjoaja;
import fi.vm.sade.tarjonta.publication.types.AttachmentCollectionType.Attachment;
import fi.vm.sade.tarjonta.publication.types.SelectionCriterionsType.EntranceExaminations.Examination;
import fi.vm.sade.tarjonta.publication.types.WebLinkCollectionType.Link;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang.StringUtils;

/**
 * Implements {@link PublicationCollector.EventHandler} by writing all encountered
 * published entities as "Pubication XML". Data is written to stream as the events occur.
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityJAXBWriter extends PublicationCollector.EventHandlerSuppport {

    public static final String UTF_8 = "UTF-8";

    public static final String DEFAULT_ROOT_ELEMENT_NAME = "LearningOpportunityDownloadData";

    public static final String NAMESPACE = "http://publication.tarjonta.sade.vm.fi/types";

    private XMLStreamWriter xmlWriter;

    private Marshaller marshaller;

    private boolean partialDocument = false;

    private final ObjectFactory objectFactory;

    private static DatatypeFactory datatypeFactory;

    /**
     * Root element gets written manually.
     */
    private String rootElementName = DEFAULT_ROOT_ELEMENT_NAME;

    /**
     * When using ID and IDREF -types with JAXB, the actual object being referenced needs to be put into
     * the "ref" field. This is problematic since we need to obtain instance to an element that we'd
     * otherwise had forgotten already. This makes this writer statefull which is no good. Custom IDResolver
     * was attempted to work around this issue but Marshaller does not seem to accept one. One way is to
     * stop using ID and IDREF types, another is to use more low level writing.
     */
    private Map<String, Object> idRefMap = new HashMap<String, Object>();

    private static final Logger log = LoggerFactory.getLogger(LearningOpportunityJAXBWriter.class);

    /**
     * Constructs new writer and initializes JAXBContext. Cannot be reused.
     *
     * @throws JAXBException
     */
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

        // hmm... not supported
        //marshaller.setProperty("com.sun.xml.bind.IDResolver", new CustomIDResolver());


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

        try {
            setOutput(new OutputStreamWriter(out, UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(UTF_8 + " must be supported");
        }

    }

    @Override
    public void onCollectStart() throws Exception {

        if (!partialDocument) {
            xmlWriter.writeStartDocument(UTF_8, "1.0");
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
        applicationSystem.setId(haku.getOid());

        // ApplicationSystem/Name
        copyTexts(haku.getNimi(), applicationSystem.getName());

        // ApplicationSystem/ApplicationSeason
        addHakukausi(haku, applicationSystem);

        // ApplicationSystem/ApplicationMethod
        addHakutapa(haku, applicationSystem);

        // ApplicationSystem/Type
        addHakutyyppi(haku, applicationSystem);

        // ApplicationSystem/ApplicationYear
        addHakuvuosi(haku, applicationSystem);

        // ApplicationSystem/Identifier
        addHaunTunniste(haku, applicationSystem);

        // ApplicationSystem/EducationStartSeason
        addKoulutuksenAlkamiskausi(haku, applicationSystem);

        // ApplicationSystem/EducationStartYear
        addKoulutuksenAlkamisvuosi(haku, applicationSystem);

        // ApplicationSystem/TargetGroup
        addKohdejoukko(haku, applicationSystem);

        marshal(ApplicationSystemType.class, applicationSystem);

        log.debug("marshalled Haku, oid: " + haku.getOid());

    }

    @Override
    public void onCollect(Hakukohde hakukohde) throws Exception {

        ApplicationOptionType applicationOption = objectFactory.createApplicationOptionType();

        // ApplicationOption/Identifier
        ApplicationOptionType.Identifier identifier = new ApplicationOptionType.Identifier();
        identifier.setValue(hakukohde.getOid());
        applicationOption.setIdentifier(identifier);

        // ApplicationOption/Title - insert koodisto uri only
        addHakukohdeNimi(hakukohde, applicationOption);

        // ApplicationOption/ApplicationSystemRef
        ApplicationSystemRefType applicationSystemRef = new ApplicationSystemRefType();
        applicationSystemRef.setOidRef(hakukohde.getHaku().getOid());
        applicationOption.setApplicationSystemRef(applicationSystemRef);

        // ApplicationOption/EligibilityRequirements
        addHakukelpoisuusvaatimus(hakukohde, applicationOption);

        // ApplicationOption/SelectionCriterions
        addValintaperusteet(hakukohde, applicationOption);

        // ApplicationOption/LearningOpportunities
        addKoulutukset(hakukohde, applicationOption);

        copyTexts(hakukohde.getLisatiedot(), applicationOption.getDescription());

        marshal(ApplicationOptionType.class, applicationOption);

        log.debug("marshalled Hakukohde, oid: " + hakukohde.getOid());

    }

    @Override
    public void onCollect(Koulutusmoduuli moduuli) throws Exception {

        LearningOpportunitySpecificationType specification = objectFactory.createLearningOpportunitySpecificationType();

        if (moduuli.getModuuliTyyppi() != KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA) {
            throw new Exception("KoulutusmoduuliTyyppi not supported: " + moduuli.getModuuliTyyppi());
        }

        // LearningOpportunitySpecification/Name
        addNimi(moduuli, specification);

        // LearningOpportunitySpecification/type
        specification.setType(LearningOpportunityTypeType.DEGREE_PROGRAMME);

        // LearningOpportunitySpecification/id
        specification.setId(putID(moduuli.getOid(), specification));

        // LearningOpportunitySpecification/Identifier
        addUlkoinenTunniste(moduuli, specification);

        // LearningOpportunitySpecification/OrganizationRef
        addOrganisaatioRef(moduuli, specification);

        // LearningOpportunitySpecification/OfferedBy
        specification.setOfferedBy(null);

        // LearningOpportunitySpecification/Credits
        addLaajuus(moduuli, specification);

        // LearningOpportunitySpecification/Qualification
        addTutkintonimike(moduuli, specification);

        // LearningOpportunitySpecification/DegreeTitle
        specification.setDegreeTitle(createExtendedString(moduuli.getTutkintoOhjelmanNimi()));

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
        addKoulutuksenRakenne(moduuli, description);

        // LearningOpportunitySpecification/Description/AccessToFurtherStudies
        addJatkoOpintoMahdollisuudet(moduuli, description);

        marshal(LearningOpportunitySpecificationType.class, specification);

        log.debug("marshalledKoulutusmoduuli, oid: " + moduuli.getOid());

    }

    @Override
    public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception {

        log.debug("processing KoulutusmoduuliToteutus, oid: " + toteutus.getOid());

        LearningOpportunityInstanceType instance = new LearningOpportunityInstanceType();

        // LearningOpportunityInstance#id
        instance.setId(putID(toteutus.getOid(), instance));

        // LearningOpportunityInstance/Identifier
        addUlkoinenTunniste(toteutus, instance);

        // LearningOpportunityInstance/SpecificationRef
        addSpecificationRef(toteutus, instance);

        // LearningOpportunityInstance/OrganizationRef
        addOrganisaatioRef(toteutus, instance);

        // LearningOpportunityInstance/Prerequisite
        addPohjakoulutusvaatimus(toteutus, instance);

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
        instance.setStartDate(toteutus.getKoulutuksenAlkamisPvm());

        // LearningOpportunityInstance/AcademicYear
        instance.setAcademicYear(formatAcademicYear(toteutus.getKoulutuksenAlkamisPvm()));

        // LearningOpportunityInstance/Duration
        addKesto(toteutus, instance);

        // LearningOpportunityInstance/Assessments
        addArviointikriteerit(toteutus, instance);

        // LearningOpportunityInstance/FinalExamination
        addLoppukoeVaatimukset(toteutus, instance);

        // LearningOpportunityInstance/CostOfEducation
        addMaksullisuus(toteutus, instance);

        // LearningOpportunityInstance/WebLinks
        addLinkit(toteutus, instance);

        marshal(LearningOpportunityInstanceType.class, instance);

        log.debug("marshalled KoulutusmoduuliToteutus, oid: " + toteutus.getOid());

    }

    @Override
    public void onCollect(Koulutustarjoaja tarjoaja) throws Exception {

        LearningOpportunityProviderType provider = new LearningOpportunityProviderType();

        OrganizationRefType organizationRef = new OrganizationRefType();
        organizationRef.setOidRef(tarjoaja.getOrganisaatioOid());
        provider.setOrganizationRef(organizationRef);

        marshal(LearningOpportunityProviderType.class, provider);

    }

    private void addHakutapa(Haku source, ApplicationSystemType target) {

        target.setApplicationMethod(createCodeValue(CodeSchemeType.KOODISTO, source.getHakutapaUri()));

    }

    private void addHakukausi(Haku source, ApplicationSystemType target) {

        target.setApplicationSeason(createCodeValue(CodeSchemeType.KOODISTO, source.getHakukausiUri()));

    }

    private void addHakutyyppi(Haku source, ApplicationSystemType target) {

        target.setApplicationType(createCodeValue(CodeSchemeType.KOODISTO, source.getHakutyyppiUri()));

    }

    private void addHakuvuosi(Haku source, ApplicationSystemType target) {

        target.setApplicationYear(formatYear(source.getHakukausiVuosi()));

    }

    private void addKoulutuksenAlkamiskausi(Haku source, ApplicationSystemType target) {

        target.setEducationStartSeason(createCodeValue(CodeSchemeType.KOODISTO, source.getKoulutuksenAlkamiskausiUri()));

    }

    private void addKoulutuksenAlkamisvuosi(Haku source, ApplicationSystemType target) {

        target.setEducationStartYear(formatYear(source.getKoulutuksenAlkamisVuosi()));

    }

    private void addHaunTunniste(Haku source, ApplicationSystemType target) {

        target.setIdentifier(source.getHaunTunniste());

    }

    private void addKohdejoukko(Haku source, ApplicationSystemType target) {

        target.setTargetGroup(createCodeValue(CodeSchemeType.KOODISTO, source.getKohdejoukkoUri()));

    }

    private void addNimi(Koulutusmoduuli from, LearningOpportunitySpecificationType to) {

        copyTexts(from.getNimi(), to.getName());

    }

    private void addUlkoinenTunniste(Koulutusmoduuli from, LearningOpportunitySpecificationType to) {

        if (from.getUlkoinenTunniste() != null) {
            to.setIdentifier(from.getUlkoinenTunniste());
        }

    }

    private void addUlkoinenTunniste(KoulutusmoduuliToteutus from, LearningOpportunityInstanceType to) {

        if (from.getUlkoinenTunniste() != null) {
            to.setIdentifier(from.getUlkoinenTunniste());
        }

    }

    private void addSpecificationRef(KoulutusmoduuliToteutus from, LearningOpportunityInstanceType to) {

        LearningOpportunitySpecificationRefType losRef = new LearningOpportunitySpecificationRefType();
        losRef.setRef(getIDREF(from.getKoulutusmoduuli().getOid()));
        to.setSpecificationRef(losRef);

    }

    private void addOrganisaatioRef(Koulutusmoduuli from, LearningOpportunitySpecificationType to) {

        OrganizationRefType organizationRef = new OrganizationRefType();
        organizationRef.setOidRef(from.getOmistajaOrganisaatioOid());
        to.setOrganizationRef(organizationRef);

    }

    private void addOrganisaatioRef(KoulutusmoduuliToteutus from, LearningOpportunityInstanceType to) {

        OrganizationRefType orgRef = new OrganizationRefType();
        orgRef.setOidRef(from.getTarjoaja());
        to.setOrganizationRef(orgRef);

    }

    private void addHakukohdeNimi(Hakukohde source, ApplicationOptionType target) {

        target.setTitle(createCodeValue(CodeSchemeType.KOODISTO, source.getHakukohdeNimi()));

    }

    private void addTutkintonimike(Koulutusmoduuli source, LearningOpportunitySpecificationType target) {

        final String nimike = source.getTutkintonimike();
        if (nimike != null) {
            target.setQualification(createCodeValue(CodeSchemeType.KOODISTO, nimike));
        }

    }

    private void addKesto(KoulutusmoduuliToteutus source, LearningOpportunityInstanceType target) {

        final String units = source.getSuunniteltuKestoYksikko();
        final String value = source.getSuunniteltuKestoArvo();

        if (units != null && value != null) {

            EducationDurationType duration = new EducationDurationType();

            duration.setUnits(createCodeValue(CodeSchemeType.KOODISTO, units));
            duration.setValue(value);

            target.setDuration(duration);

        }

    }

    private void addPohjakoulutusvaatimus(KoulutusmoduuliToteutus source, LearningOpportunityInstanceType target) {

        target.setPrerequisite(createCodeValue(CodeSchemeType.KOODISTO, source.getPohjakoulutusvaatimus()));

    }

    private void addKoulutukset(Hakukohde hakukohde, ApplicationOptionType target) {

        ApplicationOptionType.LearningOpportunities refContainer = new ApplicationOptionType.LearningOpportunities();
        List<LearningOpportunityInstanceRefType> refList = refContainer.getInstanceRef();

        Set<KoulutusmoduuliToteutus> koulutukset = hakukohde.getKoulutusmoduuliToteutuses();
        for (KoulutusmoduuliToteutus koulutus : koulutukset) {

            LearningOpportunityInstanceRefType ref = new LearningOpportunityInstanceRefType();
            ref.setRef(getIDREF(koulutus.getOid()));
            refList.add(ref);

        }

        target.setLearningOpportunities(refContainer);

    }

    private void addLaajuus(Koulutusmoduuli source, LearningOpportunitySpecificationType target) {

        if (source.getLaajuusArvo() != null) {

            CreditsType credits = new CreditsType();

            Code code = new Code();
            code.setScheme(CodeSchemeType.KOODISTO);
            code.setValue(source.getLaajuusYksikko());
            credits.setCode(code);

            credits.setValue(source.getLaajuusArvo());
            target.setCredits(credits);

        }


    }

    private void addValintaperusteet(Hakukohde source, ApplicationOptionType target) {

        SelectionCriterionsType criterions = new SelectionCriterionsType();
        target.setSelectionCriterions(criterions);

        if (source.getAloituspaikatLkm() != null) {
            criterions.setStartingQuota(BigInteger.valueOf(source.getAloituspaikatLkm()));
        }
        if (source.getAlinValintaPistemaara() != null) {
            criterions.setLastYearMinScore(BigInteger.valueOf(source.getAlinValintaPistemaara()));
        }
        if (source.getYlinValintaPistemaara() != null) {
            criterions.setLastYearMaxScore(BigInteger.valueOf(source.getYlinValintaPistemaara()));
        }
        if (source.getEdellisenVuodenHakijat() != null) {
            criterions.setLastYearTotalApplicants(BigInteger.valueOf(source.getEdellisenVuodenHakijat()));
        }

        copyTexts(source.getValintaperusteKuvaus(), criterions.getDescription());

        addValintakokeet(source, criterions);

        addLiitteet(source, criterions);

    }

    private void addValintakokeet(Hakukohde source, SelectionCriterionsType target) {

        Set<Valintakoe> valintakoes = source.getValintakoes();
        if (valintakoes == null || valintakoes.isEmpty()) {
            return;
        }

        SelectionCriterionsType.EntranceExaminations exams = new SelectionCriterionsType.EntranceExaminations();

        copyTexts(source.getValintaperusteKuvaus(), exams.getDescription());

        for (Valintakoe sourceExamination : valintakoes) {

            Examination targetExamination = new Examination();
            addValintakoe(sourceExamination, targetExamination);
            exams.getExamination().add(targetExamination);

        }

        target.setEntranceExaminations(exams);

    }

    private void addLiitteet(Hakukohde source, SelectionCriterionsType target) {

        Set<HakukohdeLiite> liitteet = source.getLiites();
        if (liitteet == null || liitteet.isEmpty()) {
            return;
        }

        AttachmentCollectionType attachmentContainer = new AttachmentCollectionType();
        List<Attachment> attachments = attachmentContainer.getAttachment();

        for (HakukohdeLiite liite : liitteet) {

            Attachment attachment = new Attachment();

            attachment.setType(createCodeValue(CodeSchemeType.KOODISTO, liite.getLiitetyyppi()));

            LocalizedTextType description = new LocalizedTextType();
            copyTexts(liite.getKuvaus(), description.getText());
            attachment.setDescription(description);

            Attachment.Return returnSpec = new Attachment.Return();
            returnSpec.setDueDate(liite.getErapaiva());

            Attachment.Return.To to = new Attachment.Return.To();

            if (liite.getSahkoinenToimitusosoite() != null) {
                // could be anyUri???
                to.setEmailAddress(liite.getSahkoinenToimitusosoite());
            }

            // todo: postal address

            returnSpec.setTo(to);
            attachment.setReturn(returnSpec);

            attachments.add(attachment);

        }

        target.setAttachments(attachmentContainer);

    }

    private void addValintakoe(Valintakoe source, Examination target) {

        // ApplicationOption/.../Examination/ExaminationType
        target.setExaminationType(createCodeValue(CodeSchemeType.KOODISTO, source.getTyyppiUri()));

        // ApplicationOption/.../Examination/Description
        copyTexts(source.getKuvaus(), target.getDescription());

        Set<ValintakoeAjankohta> ajankohtas = source.getAjankohtas();
        if (ajankohtas != null && !ajankohtas.isEmpty()) {

            List<ExaminationEventType> events = target.getExaminationEvent();

            for (ValintakoeAjankohta ajankohta : ajankohtas) {

                // ApplicationOption/.../Examination/ExaminationEvent
                ExaminationEventType event = new ExaminationEventType();

                event.setStart(ajankohta.getAlkamisaika());
                event.setEnd(ajankohta.getPaattymisaika());

                Set<ValintakoeOsoite> osoites = ajankohta.getOsoites();
                if (osoites != null && !osoites.isEmpty()) {

                    ExaminationEventType.Locations locations = new ExaminationEventType.Locations();
                    event.setLocations(locations);

                    for (ValintakoeOsoite valintakoeOsoite : osoites) {

                        Osoite osoite = valintakoeOsoite.getOsoite();

                        // ApplicationOption/.../Examination/ExaminationEvent/Locations/Location
                        ExaminationLocationType location = new ExaminationLocationType();
                        location.getAddressLine().add(osoite.getOsoiterivi1());
                        if (!StringUtils.isEmpty(osoite.getOsoiterivi2())) {
                            location.getAddressLine().add(osoite.getOsoiterivi2());
                        }
                        location.setCity(osoite.getPostitoimipaikka());
                        location.setPostalCode(osoite.getPostinumero());
                        locations.getLocation().add(location);

                    }

                }

                events.add(event);

            }

        }


    }

    private void addHakukelpoisuusvaatimus(Hakukohde source, ApplicationOptionType target) {

        // todo: is this koodisto uri??
        final String hakukelpoisuus = source.getHakukelpoisuusvaatimus();

        if (hakukelpoisuus != null) {

            EligibilityRequirementsType eligibilityRequirements = new EligibilityRequirementsType();
            target.setEligibilityRequirements(eligibilityRequirements);

            ExtendedStringType targetString = new ExtendedStringType();
            targetString.setValue(hakukelpoisuus);

            eligibilityRequirements.getDescription().add(targetString);

        }

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
        s.setLang("fi");
        s.setValue(valueFI);
        return s;

    }

    private void addLinkit(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        Set<WebLinkki> sourceLinkkis = toteutus.getLinkkis();
        if (sourceLinkkis != null && !sourceLinkkis.isEmpty()) {

            WebLinkCollectionType linkContainer = new WebLinkCollectionType();
            target.setWebLinks(linkContainer);

            List<Link> targetLinks = linkContainer.getLink();

            for (WebLinkki sourceLink : sourceLinkkis) {
                Link targetLink = new Link();
                targetLink.setType(sourceLink.getTyyppi());
                targetLink.setUri(sourceLink.getUrl());
                targetLinks.add(targetLink);
            }

        }

    }

    private void addMaksullisuus(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {
        // TODO maksullisuus on koodisto url ...
    }

    private void addLoppukoeVaatimukset(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        MonikielinenTeksti teksti = toteutus.getLoppukoeVaatimukset();
        if (teksti != null && !teksti.getTekstis().isEmpty()) {
            FinalExaminationCollectionType examinations = new FinalExaminationCollectionType();
            target.setFinalExamination(examinations);

            copyTexts(teksti, examinations.getDescription());
        }

    }

    private void addArviointikriteerit(KoulutusmoduuliToteutus toteutus, LearningOpportunityInstanceType target) {

        MonikielinenTeksti tekstis = toteutus.getArviointikriteerit();

        if (tekstis != null && !tekstis.getTekstis().isEmpty()) {
            AssessmentCollectionType assessments = new AssessmentCollectionType();
            copyTexts(tekstis, assessments.getAssessment());
        }

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

    private void addKoulutuksenRakenne(Koulutusmoduuli moduuli, Description target) {

        copyTexts(moduuli.getKoulutuksenRakenne(), target.getStructureDiagram());

    }

    private void addJatkoOpintoMahdollisuudet(Koulutusmoduuli moduuli, Description target) {

        copyTexts(moduuli.getJatkoOpintoMahdollisuudet(), target.getAccessToFurtherStudies());

    }

    /**
     * Helper method that copies all translated texts from the source format to target format.
     * Null source object is silently ignored.
     *
     * @param source
     * @param target
     */
    private void copyTexts(MonikielinenTeksti source, List<ExtendedStringType> target) {

        if (source != null) {
            for (TekstiKaannos teksti : source.getTekstis()) {
                ExtendedStringType targetText = new ExtendedStringType();
                targetText.setLang(teksti.getKieliKoodi());
                targetText.setValue(teksti.getArvo());
                target.add(targetText);
            }
        }

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

    private static String formatYear(Integer year) {

        if (year == null) {
            return null;
        }

        return year.toString();
    }

    /**
     * Formats year with four digits.
     *
     * @param date
     * @return
     */
    private static String formatAcademicYear(Date date) {

        return new SimpleDateFormat("yyyy").format(date);

    }

    /**
     * Creates CodeValueType by setting it's scheme and code's value. If value is null, null is returned.
     *
     * @param scheme
     * @param codeValue
     * @return
     */
    private static CodeValueType createCodeValue(CodeSchemeType scheme, String codeValue) {

        if (codeValue == null) {
            return null;
        }

        Code code = new Code();
        code.setScheme(scheme);
        code.setValue(codeValue);

        CodeValueType codeValueType = new CodeValueType();
        codeValueType.setCode(code);

        return codeValueType;

    }

    /**
     * Stores element that is used as a target of IDREF later. Returns the input id for method chaining.
     *
     * @param idTarget
     * @param id
     * @return
     */
    private String putID(String id, Object idTarget) {

        idRefMap.put(id, idTarget);
        return id;

    }

    /**
     * Returns element that has been stored before using {@link #putID(java.lang.Object, java.lang.String) }.
     *
     * @exception IllegalArgumentException if not such id is found
     * @param key
     * @return
     */
    private Object getIDREF(String key) {

        Object value = idRefMap.get(key);
        if (value == null) {
            throw new IllegalStateException("no such ID " + key);
        }
        return value;
    }

}

