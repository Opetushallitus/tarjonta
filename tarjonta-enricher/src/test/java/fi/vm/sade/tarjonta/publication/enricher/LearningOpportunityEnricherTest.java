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
package fi.vm.sade.tarjonta.publication.enricher;

import fi.vm.sade.tarjonta.publication.enricher.KoodistoLookupService.KoodiValue;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import fi.vm.sade.tarjonta.publication.enricher.KoodistoLookupService.SimpleKoodiValue;
import fi.vm.sade.tarjonta.publication.enricher.factory.LearningOpportunityDataEnricherFactory;
import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.xml.sax.InputSource;

/**
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityEnricherTest {

    private XMLStreamEnricher processor;

    private ByteArrayOutputStream out;

    private XPath xpath;

    private InputSource input;

    private static boolean sPrintXML;

    @BeforeClass
    public static void setUpClass() {
        sPrintXML = Boolean.parseBoolean(System.getProperty("printXML", "false"));
    }

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

    }

    @After
    public void tearDown() {
        input = null;

        if (sPrintXML) {
            System.out.println("output:\n" + out.toString());
        }

    }

    @Test
    public void testEnrichCredits() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Credits";
        assertCodeValue(basePath, "laajuus1", "laajuus");

    }

    @Test
    public void testEnrichQualification() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Qualification";
        assertCodeValue(basePath, "lahihoitaja", "tutkintonimike");

    }

    @Test
    public void testEnrichEducationClassification() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Classification/EducationClassification";
        assertCodeValue(basePath, "371101", "koulutusluokitus");


    }

    @Test
    public void testEnrichEducationDomain() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Classification/EducationDomain";
        assertCodeValue(basePath, "uri:koulutusala", "koulutusala");

    }

    @Test
    public void testEnrichEducationDegree() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Classification/EducationDegree";
        assertCodeValue(basePath, "uri:koulutusaste", "koulutusaste");

    }

    @Test
    public void testEnrichOpintoala() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Classification/StudyDomain";
        assertCodeValue(basePath, "uri:opintoala", "opintoala");

    }

    @Test
    public void testEnrichEqf() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Classification/EqfClassification";
        assertCodeValue(basePath, "uri:eqf", "eqf");

    }

    @Test
    public void testEnrichNqf() throws Exception {

        final String basePath = "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Classification/NqfClassification";
        assertCodeValue(basePath, "uri:nqf", "nqf");

    }

    /**
     * Asserts codes value and labels in three languages.
     *
     * @param baseXPath
     * @param expectedValue
     * @param expectedBaseLabel
     * @throws Exception
     */
    private void assertCodeValue(String baseXPath, String expectedValue, String expectedBaseLabel) throws Exception {

        assertXPathEvals(expectedValue, baseXPath + "/Code/text()");
        assertXPathEvals(expectedBaseLabel + "-fi", baseXPath + "/Label[@lang='fi']/text()");
        assertXPathEvals(expectedBaseLabel + "-en", baseXPath + "/Label[@lang='en']/text()");
        assertXPathEvals(expectedBaseLabel + "-sv", baseXPath + "/Label[@lang='sv']/text()");

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

    private void assertXPathEvals(String expected, String expression) throws XPathExpressionException {

        assertEquals(expected, evalXPath(expression));

    }

    private void ensureXPathSource() {

        input = new InputSource(new StringReader(new String(out.toByteArray())));
    }

    private String evalXPath(String xPathExpression) throws XPathExpressionException {

        ensureXPathSource();
        return xpath.evaluate(xPathExpression, input);

    }

    /**
     * Creates KoodistoLookupService that's been prepared to return values for uri's found in the test file.
     */
    private KoodistoLookupService prepareKoodistoLookupMockService() {

        KoodistoLookupService service = mock(KoodistoLookupService.class);

        when(service.lookupKoodi("371101", 2010)).thenReturn(createSimpleKoodiValue("koulutusluokitus"));
        when(service.lookupKoodi("uri:koulutusala", 2002)).thenReturn(createSimpleKoodiValue("koulutusala"));
        when(service.lookupKoodi("laajuus1", 0)).thenReturn(createSimpleKoodiValue("laajuus"));
        when(service.lookupKoodi("lahihoitaja", 0)).thenReturn(createSimpleKoodiValue("tutkintonimike"));
        when(service.lookupKoodi("uri:koulutusaste", 2002)).thenReturn(createSimpleKoodiValue("koulutusaste"));
        when(service.lookupKoodi("uri:opintoala", 2002)).thenReturn(createSimpleKoodiValue("opintoala"));
        when(service.lookupKoodi("uri:eqf", 0)).thenReturn(createSimpleKoodiValue("eqf"));
        when(service.lookupKoodi("uri:nqf", 0)).thenReturn(createSimpleKoodiValue("nqf"));

        return service;

    }

    private KoodiValue createSimpleKoodiValue(String baseName) {

        return new SimpleKoodiValue(baseName + "-uri", baseName + "-value", baseName + "-fi", baseName + "-en", baseName + "-sv");

    }

}

