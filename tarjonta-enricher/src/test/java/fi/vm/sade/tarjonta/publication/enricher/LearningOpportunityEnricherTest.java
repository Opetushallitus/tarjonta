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

    @Before
    public void setUp() throws Exception {

        KoodistoLookupService koodistoService = mock(KoodistoLookupService.class);

        when(koodistoService.lookupKoodi("371101", 2010)).
            thenReturn(new SimpleKoodiValue("371101", "2010", "Nimi", "Name", "Namn"));
        when(koodistoService.lookupKoodi("laajuus1", 0)).
            thenReturn(new SimpleKoodiValue("laajuus1", "123", "Opintopiste", "CreditUnit", "laajuusSV"));

        LearningOpportunityDataEnricherFactory factory = new LearningOpportunityDataEnricherFactory();
        factory.setKoodistoService(koodistoService);

        processor = factory.getObject();
        processor.setInput(new FileInputStream("src/test/resources/simple-enrich-in.xml"));
        processor.setOutput(out = new ByteArrayOutputStream());

        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new PublicationNamespaceContext());

    }

    @After
    public void tearDown() {
        input = null;
    }

    @Test
    public void testProcess() throws Exception {

        processor.process();

        // todo: add e.g. xpath expression to validate that processing has taken place
        // and also we need to validate that XML is still valid
        //
        System.out.println("output:\n" + out.toString());

        // check that the original value is present
        assertXPathEvals("20", "//LearningOpportunityDownloadData/LearningOpportunitySpecification[1]/Credits/Value/text()");


    }

    private void assertXPathEvals(String expected, String expression) throws XPathExpressionException {

        assertEquals(expected, evalXPath(expression));

    }

    private void ensureXPathSource() {

        input = new InputSource(new StringReader(new String(out.toByteArray())));
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
            "src/test/resources/enrich-minimal-in.xml", //"src/test/resources/enrich-minimal-in-with-ns.xml"
        };

        XPath path = XPathFactory.newInstance().newXPath();

        for (String inputFile : inputFiles) {

            for (String[] tmp : data) {

                InputSource is = new InputSource(new FileInputStream(inputFile));

                String expression = tmp[0];
                String expected = tmp[1];
                System.out.println("xpath: " + expression);

                assertEquals("bad result for xpath: " + expression, expected, path.evaluate(expression, is));

                System.out.println("OK: " + expression);

            }



        }

    }

    private String evalXPath(String xPathExpression) throws XPathExpressionException {

        ensureXPathSource();
        return xpath.evaluate(xPathExpression, input);


    }

}

