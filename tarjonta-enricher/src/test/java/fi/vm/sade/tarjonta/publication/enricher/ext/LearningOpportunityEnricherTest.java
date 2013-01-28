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
package fi.vm.sade.tarjonta.publication.enricher.ext;

import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioKuvailevatTiedotTyyppi;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.StringReader;

import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import fi.vm.sade.tarjoaja.service.types.MetatietoArvoTyyppi;
import fi.vm.sade.tarjoaja.service.types.MetatietoAvainTyyppi;
import fi.vm.sade.tarjoaja.service.types.MetatietoTyyppi;

import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService.SimpleKoodiValue;
import fi.vm.sade.tarjonta.publication.enricher.factory.LearningOpportunityDataEnricherFactory;
import fi.vm.sade.tarjonta.publication.enricher.PublicationNamespaceContext;
import fi.vm.sade.tarjonta.publication.enricher.XMLStreamEnricher;
import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService.KoodiValue;
import fi.vm.sade.tarjonta.publication.enricher.organisaatio.KoulutustarjoajaLookupService;
import fi.vm.sade.tarjonta.publication.types.DescriptionType;

import fi.vm.sade.tarjonta.util.SystemUtils;

/**
 * Test to verify that "raw" Tarjonta XML is correctly enriched.
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityEnricherTest {

    private static final Integer CODE_VERSION = 1;
    private static final String DOWNLOAD_DATA_PATH = "//LearningOpportunityDownloadData";
    private static final String SPECIFICATION_PATH = DOWNLOAD_DATA_PATH + "/LearningOpportunitySpecification[1]";
    private static final String CLASSIFICATION_PATH = SPECIFICATION_PATH + "/Classification";
    private static final String INSTANCE_PATH = DOWNLOAD_DATA_PATH + "/LearningOpportunityInstance[1]";
    private static final String APPLICATION_OPTION_PATH = DOWNLOAD_DATA_PATH + "/ApplicationOption[1]";
    private static final String APPLICATION_SYSTEM_PATH = DOWNLOAD_DATA_PATH + "/ApplicationSystem[1]";
    private XMLStreamEnricher processor;
    private ByteArrayOutputStream out;
    private XPath xpath;
    private InputSource input;

    @Before
    public void setUp() throws Exception {

        KoodistoLookupService koodistoService = prepareKoodistoLookupMockService();

        LearningOpportunityDataEnricherFactory factory = new LearningOpportunityDataEnricherFactory();
        factory.setKoodistoService(koodistoService);

        processor = factory.getObject();
        processor.setInput(new FileInputStream("src/test/resources/simple-enrich-in.xml"));
        processor.setOutput(out = new ByteArrayOutputStream());
        processor.process();

        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new PublicationNamespaceContext());

        System.out.print(out.toString());

    }

    @After
    public void tearDown() {
        input = null;
        SystemUtils.printOutIf(out.toString(), "printXML");
    }

    @Test
    public void testEnrichCredits() throws Exception {



        final String basePath = SPECIFICATION_PATH + "/Credits";
        assertCodeValue(basePath, "laajuus", "laajuus");

    }

    @Test
    public void testEnrichQualification() throws Exception {

        final String basePath = SPECIFICATION_PATH + "/Qualification";
        assertCodeValue(basePath, "tutkintonimike", "tutkintonimike");

    }

    @Test
    public void testEnrichEducationClassification() throws Exception {

        final String basePath = CLASSIFICATION_PATH + "/EducationClassification";
        assertCodeValue(basePath, "371101", "koulutusluokitus");
    }

    @Test
    public void testEnrichEducationDomain() throws Exception {

        final String basePath = CLASSIFICATION_PATH + "/EducationDomain";
        assertCodeValue(basePath, "uri:koulutusala", "koulutusala");

    }

    @Test
    public void testEnrichEducationDegree() throws Exception {

        final String basePath = CLASSIFICATION_PATH + "/EducationDegree";
        assertCodeValue(basePath, "uri:koulutusaste", "koulutusaste");

    }

    @Test
    public void testEnrichOpintoala() throws Exception {

        final String basePath = CLASSIFICATION_PATH + "/StudyDomain";
        assertCodeValue(basePath, "uri:opintoala", "opintoala");

    }

    @Test
    public void testEnrichEqf() throws Exception {

        final String basePath = CLASSIFICATION_PATH + "/EqfClassification";
        assertCodeValue(basePath, "uri:eqf", "eqf");

    }

    @Test
    public void testEnrichNqf() throws Exception {

        final String basePath = CLASSIFICATION_PATH + "/NqfClassification";
        assertCodeValue(basePath, "uri:nqf", "nqf");

    }

    @Test
    public void testEnrichPrerequisite() throws Exception {

        final String basePath = INSTANCE_PATH + "/Prerequisite";
        assertCodeValue(basePath, "uri:pohjakoulutusvaatimus", "pohjakoulutusvaatimus");

    }

    @Test
    public void testEnrichProfessionLabels() throws Exception {

        final String basePath = INSTANCE_PATH + "/ProfessionLabels/Profession";
        assertCodeValue(basePath + "[1]", "uri:ammattinimike/lahihoitaja", "ammattinimike");
        assertCodeValue(basePath + "[2]", "uri:ammattinimike/perushoitaja", "ammattinimike");
        assertCodeValue(basePath + "[3]", "uri:ammattinimike/ensihoitaja", "ammattinimike");

    }

    @Test
    public void testEnrichKeywords() throws Exception {

        final String basePath = INSTANCE_PATH + "/Keywords";
        assertCodeValue(basePath + "/Keyword[1]", "uri:asiasana/lahihoitaja", "asiasana");
        assertCodeValue(basePath + "/Keyword[2]", "uri:asiasana/hoivaala", "asiasana");

    }

    @Test
    public void testEnrichLanguagesOfInstruction() throws Exception {

        final String basePath = INSTANCE_PATH + "/LanguagesOfInstruction";
        assertCodeValueCollection(basePath + "/Code[1]", "kieli-fi-value", "kieli");
        assertCodeValueCollection(basePath + "/Code[2]", "kieli-en-value", "kieli");

    }

    @Test
    public void testEnrichFormOfEducation() throws Exception {

        final String basePath = INSTANCE_PATH + "/FormOfEducation";
        assertCodeValueCollection(basePath + "/Code[1]", "koulutuslaji-value", "koulutuslaji");

    }

    @Test
    public void testEnrichFormOfTeaching() throws Exception {

        final String basePath = INSTANCE_PATH + "/FormsOfTeaching";
        assertCodeValueCollection(basePath + "/Code[1]", "opetusmuoto-value", "opetusmuoto");

    }

    @Test
    public void testEnrichDurationUnits() throws Exception {

        final String basePath = INSTANCE_PATH + "/Duration";
        assertCodeValue(basePath + "/Units", "uri:kesto/vuotta", "kesto");

        // check that the actual duration value is still there
        assertXPathEvals("unexpected duration value", "40", basePath + "/Value/text()");

    }

    @Test
    public void testEnrichApplicationOptionTitle() throws Exception {

        final String basePath = APPLICATION_OPTION_PATH + "/Title";
        assertCodeValue(basePath, "uri:hakukohde/876", "hakukohde");

    }

    @Test
    public void testEnrichExaminationType() throws Exception {

        final String basePath = APPLICATION_OPTION_PATH + "/SelectionCriterions/EntranceExaminations/Examination/ExaminationType";
        assertCodeValue(basePath, "uri:valintakoetyyppi/123", "valintakoe");

    }

    @Test
    public void testEnrichAttachmentType() throws Exception {

        final String basePath = APPLICATION_OPTION_PATH + "/SelectionCriterions/Attachments/Attachment/Type";
        assertCodeValue(basePath, "uri:liitetyyppi/12345", "liitetyyppi");

    }

    @Test
    public void testEnrichApplicationType() throws Exception {

        final String basePath = APPLICATION_SYSTEM_PATH + "/ApplicationType";
        assertCodeValue(basePath, "uri:hakutyyppi/varsinaishaku", "hakutyyppi");

    }

    @Test
    public void testEnrichApplicationMethod() throws Exception {

        final String basePath = APPLICATION_SYSTEM_PATH + "/ApplicationMethod";
        assertCodeValue(basePath, "uri:hakutapa/yhteishaku", "hakutapa");

    }

    @Test
    public void testEnrichApplicationSeason() throws Exception {

        final String basePath = APPLICATION_SYSTEM_PATH + "/ApplicationSeason";
        assertCodeValue(basePath, "uri:kausi/kevat", "kausi");

    }

    @Test
    public void testEnrichEducationStartSeason() throws Exception {

        final String basePath = APPLICATION_SYSTEM_PATH + "/EducationStartSeason";
        assertCodeValue(basePath, "uri:kausi/syksy", "kausi");

    }

    @Test
    public void testEnrichTargetGroup() throws Exception {

        final String basePath = APPLICATION_SYSTEM_PATH + "/TargetGroup";
        assertCodeValue(basePath, "uri:kohdejoukko/peruskoulut", "kohdejoukko");

    }

    /**
     * Asserts codes value and labels in three languages (base element type
     * CodeValueCollectionType)
     *
     * @param baseXPath
     * @param expectedValue
     * @param expectedBaseLabel
     * @throws Exception
     */
    private void assertCodeValueCollection(String baseXPath, String expectedValue, String expectedBaseLabel) throws Exception {

        assertXPathEvals("unexpected Code value ->" + baseXPath, expectedValue, baseXPath + "/@value");
        assertXPathEvals("unexpected Label (fi) ->" + baseXPath, expectedBaseLabel + "-fi", baseXPath + "/Label[@lang='fi']/text()");
        assertXPathEvals("unexpected Label (en) ->" + baseXPath, expectedBaseLabel + "-en", baseXPath + "/Label[@lang='en']/text()");
        assertXPathEvals("unexpected Label (sv) ->" + baseXPath, expectedBaseLabel + "-sv", baseXPath + "/Label[@lang='sv']/text()");

    }

    /**
     * Asserts codes value and labels in three languages (base element type
     * CodeValueType).
     *
     * @param baseXPath
     * @param expectedValue
     * @param expectedBaseLabel
     * @throws Exception
     */
    private void assertCodeValue(String baseXPath, String expectedValue, String expectedBaseLabel) throws Exception {

        assertXPathEvals("unexpected Code value -> " + baseXPath, expectedBaseLabel + "-value", baseXPath + "/Code/text()");
        assertXPathEvals("unexpected Label (fi) -> " + baseXPath, expectedBaseLabel + "-fi", baseXPath + "/Label[@lang='fi']/text()");
        assertXPathEvals("unexpected Label (en) -> " + baseXPath, expectedBaseLabel + "-en", baseXPath + "/Label[@lang='en']/text()");
        assertXPathEvals("unexpected Label (sv) -> " + baseXPath, expectedBaseLabel + "-sv", baseXPath + "/Label[@lang='sv']/text()");

    }

    /**
     * Proof-of-concept test to validate that XPath return what we expect.
     *
     * @throws Exception
     */
    @Test
    public void testXPathPOC() throws Exception {

        String educationClassification = "//LearningOpportunityDownloadData"
                + "/LearningOpportunitySpecification"
                + "/Classification"
                + "/EducationClassification";

        String[][] data = {
            {educationClassification + "/Code/text()", "371101"},
            {educationClassification + "/Label[@lang='fi']", "label_fi"}
        };

        String[] inputFiles = {
            "src/test/resources/enrich-minimal-in.xml",};

        XPath path = XPathFactory.newInstance().newXPath();

        for (String inputFile : inputFiles) {

            for (String[] tmp : data) {

                InputSource is = new InputSource(new FileInputStream(inputFile));

                String expression = tmp[0];
                String expected = tmp[1];

                assertEquals("bad result for xpath: " + expression, expected, path.evaluate(expression, is));

            }

        }

    }

    private void assertXPathEvals(String msg, String expected, String expression) throws XPathExpressionException {
        assertEquals(msg, expected, evalXPath(expression));
    }

    private void ensureXPathSource() {

        input = new InputSource(new StringReader(new String(out.toByteArray())));
    }

    private String evalXPath(String xPathExpression) throws XPathExpressionException {

        ensureXPathSource();
        return xpath.evaluate(xPathExpression, input);

    }

    /**
     * Creates KoodistoLookupService that's been prepared to return values for
     * uri's found in the test file.
     */
    private KoodistoLookupService prepareKoodistoLookupMockService() {

        KoodistoLookupService service = mock(KoodistoLookupService.class);

        when(service.lookupKoodi("371101", 2010)).thenReturn(createSimpleKoodiValue("koulutusluokitus"));
        when(service.lookupKoodi("uri:koulutusala", CODE_VERSION)).thenReturn(createSimpleKoodiValue("koulutusala"));
        when(service.lookupKoodi("laajuus1", CODE_VERSION)).thenReturn(createSimpleKoodiValue("laajuus"));
        when(service.lookupKoodi("uri tutkintonimike", CODE_VERSION)).thenReturn(createSimpleKoodiValue("tutkintonimike"));
        when(service.lookupKoodi("uri:koulutusaste", CODE_VERSION)).thenReturn(createSimpleKoodiValue("koulutusaste"));
        when(service.lookupKoodi("uri:opintoala", CODE_VERSION)).thenReturn(createSimpleKoodiValue("opintoala"));
        when(service.lookupKoodi("uri:eqf", CODE_VERSION)).thenReturn(createSimpleKoodiValue("eqf"));
        when(service.lookupKoodi("uri:nqf", CODE_VERSION)).thenReturn(createSimpleKoodiValue("nqf"));
        when(service.lookupKoodi("uri:pohjakoulutusvaatimus", CODE_VERSION)).thenReturn(createSimpleKoodiValue("pohjakoulutusvaatimus"));
        when(service.lookupKoodi("uri:ammattinimike/lahihoitaja", CODE_VERSION)).thenReturn(createSimpleKoodiValue("ammattinimike"));
        when(service.lookupKoodi("uri:ammattinimike/perushoitaja", CODE_VERSION)).thenReturn(createSimpleKoodiValue("ammattinimike"));
        when(service.lookupKoodi("uri:ammattinimike/ensihoitaja", CODE_VERSION)).thenReturn(createSimpleKoodiValue("ammattinimike"));
        when(service.lookupKoodi("uri:asiasana/lahihoitaja", CODE_VERSION)).thenReturn(createSimpleKoodiValue("asiasana"));
        when(service.lookupKoodi("uri:asiasana/hoivaala", CODE_VERSION)).thenReturn(createSimpleKoodiValue("asiasana"));
        when(service.lookupKoodi("uri:kieli/fi", CODE_VERSION)).thenReturn(createSimpleKoodiValue("kieli-fi"));
        when(service.lookupKoodi("uri:kieli/en", CODE_VERSION)).thenReturn(createSimpleKoodiValue("kieli-en"));
        when(service.lookupKoodi("uri:koulutuslaji/nuorten", CODE_VERSION)).thenReturn(createSimpleKoodiValue("koulutuslaji"));
        when(service.lookupKoodi("uri:opetusmuoto/lahiopetus", CODE_VERSION)).thenReturn(createSimpleKoodiValue("opetusmuoto"));
        when(service.lookupKoodi("uri:kesto/vuotta", CODE_VERSION)).thenReturn(createSimpleKoodiValue("kesto"));
        when(service.lookupKoodi("uri:hakukohde/876", CODE_VERSION)).thenReturn(createSimpleKoodiValue("hakukohde"));
        when(service.lookupKoodi("uri:valintakoetyyppi/123", CODE_VERSION)).thenReturn(createSimpleKoodiValue("valintakoe"));
        when(service.lookupKoodi("uri:liitetyyppi/12345", CODE_VERSION)).thenReturn(createSimpleKoodiValue("liitetyyppi"));
        when(service.lookupKoodi("uri:hakutyyppi/varsinaishaku", CODE_VERSION)).thenReturn(createSimpleKoodiValue("hakutyyppi"));
        when(service.lookupKoodi("uri:hakutapa/yhteishaku", CODE_VERSION)).thenReturn(createSimpleKoodiValue("hakutapa"));
        when(service.lookupKoodi("uri:kausi/kevat", CODE_VERSION)).thenReturn(createSimpleKoodiValue("kausi"));
        when(service.lookupKoodi("uri:kausi/syksy", CODE_VERSION)).thenReturn(createSimpleKoodiValue("kausi"));
        when(service.lookupKoodi("uri:kohdejoukko/peruskoulut", CODE_VERSION)).thenReturn(createSimpleKoodiValue("kohdejoukko"));

        return service;

    }

    private KoulutustarjoajaLookupService prepareTarjoajaLookupMockService() throws Exception {

        KoulutustarjoajaLookupService service = mock(KoulutustarjoajaLookupService.class);

        when(service.lookupKoulutustarjoajaByOrganisaatioOid("1.2.3.4.5")).thenReturn(createTarjoaja());

        return service;

    }

    private OrganisaatioDTO createTarjoaja() {

        OrganisaatioDTO tarjoaja = new OrganisaatioDTO();
        MonikielinenTekstiTyyppi type = new MonikielinenTekstiTyyppi();
        type.getTeksti().add(createTarjoajaNimi("fi", "nimi-fi"));
        type.getTeksti().add(createTarjoajaNimi("en", "nimi-en"));
        type.getTeksti().add(createTarjoajaNimi("sv", "nimi-sv"));
        OrganisaatioKuvailevatTiedotTyyppi kuvailevatTiedot = tarjoaja.getKuvailevatTiedot();
//        kuvailevatTiedot.getVapaatKuvaukset().add(createMetatieto(MetatietoAvainTyyppi.ESTEETTOMYYS_PALVELUT));
//        kuvailevatTiedot.getVapaatKuvaukset().add(createMetatieto(MetatietoAvainTyyppi.KUSTANNUKSET));
//        kuvailevatTiedot.getVapaatKuvaukset().add(createMetatieto(MetatietoAvainTyyppi.OPPIMISYMPARISTOT));
//        kuvailevatTiedot.getVapaatKuvaukset().add(createMetatieto(MetatietoAvainTyyppi.RUOKAILU));
//        kuvailevatTiedot.getVapaatKuvaukset().add(createMetatieto(MetatietoAvainTyyppi.TERVEYDENHUOLTO));

        return tarjoaja;

    }

    private MonikielinenTekstiTyyppi.Teksti createTarjoajaNimi(String lang, String value) {

        MonikielinenTekstiTyyppi.Teksti nimi = new MonikielinenTekstiTyyppi.Teksti();
        nimi.setKieliKoodi(lang);
        nimi.setValue(value);

        return nimi;

    }

    private MetatietoTyyppi createMetatieto(MetatietoAvainTyyppi tyyppi) {

        MetatietoTyyppi metaField = new MetatietoTyyppi();
        metaField.setAvain(tyyppi);
        metaField.getArvos().add(createMetaArvo("fi", "arvo-fi"));
        metaField.getArvos().add(createMetaArvo("sv", "arvo-sv"));
        metaField.getArvos().add(createMetaArvo("en", "arvo-en"));
        return metaField;

    }

    private MetatietoArvoTyyppi createMetaArvo(String lang, String value) {

        MetatietoArvoTyyppi metaFieldValue = new MetatietoArvoTyyppi();
        metaFieldValue.setKieliKoodi(lang);
        metaFieldValue.setArvo(value);
        return metaFieldValue;

    }

    private KoodiValue createSimpleKoodiValue(String baseName) {

        return new SimpleKoodiValue(baseName + "-uri", baseName + "-value", baseName + "-fi", baseName + "-en", baseName + "-sv");

    }
}
