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
package fi.vm.sade.tarjonta.publication.enricher.it;

import fi.vm.sade.tarjonta.publication.enricher.koodisto.KoodistoCodeValueEnricher;
import fi.vm.sade.tarjonta.publication.enricher.XMLStreamEnricher;
import java.io.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Setup like: depth 10, elements per level 1000 and text rows 25 results in output file of 28MB that
 * takes some 2.6sec to process on a basic laptop machine. Tested with 1GB+ file size, does not cause
 * any memory problems but takes rather long time.
 * </p>
 *
 * <p>
 * To generate test file of about 5 megabytes use:
 * <pre>
 * mvn test -Dtest=LargeFileProcessTest -DmaxDepth=50 -DelementsPerLevel=100 -DtextRows=15
 * </pre>
 * </p>
 *
 *
 * @author Jukka Raanamo
 */
public class LargeFileProcessTest {

    private static final Logger log = LoggerFactory.getLogger("TEST");

    private static File testFileIn;

    private File testFileOut;

    private static int maxDepth;

    private static int elementsPerLevel;

    private static int textRows;

    private static String TEXT;

    @Before
    public void setUpTest() throws Exception {

        testFileOut = File.createTempFile("largeFileProcessOut", ".xml");
    }

    @After
    public void tearDownTest() throws Exception {

        delete(testFileOut);

    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        textRows = Integer.parseInt(System.getProperty("textRows", "10"));
        elementsPerLevel = Integer.parseInt(System.getProperty("elementsPerLevel", "50"));
        maxDepth = Integer.parseInt(System.getProperty("maxDepth", "20"));

        final String line = "On todistettu, että lukijaa häiritsee sivun ulkoasu lukiessaan sivua. "
            + "Lorem Ipsumin käytön tarkoitus on";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textRows; i++) {
            sb.append(line).append("\n");
        }
        TEXT = sb.toString();

        generateTestData();
    }

    @AfterClass
    public static void tearDownClass() {

        delete(testFileIn);

    }

    @Test
    public void testProcessLargeFile() throws Exception {

        // no handlers
        XMLStreamEnricher enricher = new XMLStreamEnricher();

        enricher.setInput(new FileInputStream(testFileIn));
        enricher.setOutput(new FileOutputStream(testFileOut));

        long startTime = System.currentTimeMillis();

        enricher.process();

        long processTime = (System.currentTimeMillis() - startTime);

        log.debug("xml processing took " +  processTime + "ms");
        log.debug("text rows: " + textRows + ", maxDepth: " + maxDepth + ", elementsPerLevel: " + elementsPerLevel);

    }

    private static void generateTestData() throws IOException, XMLStreamException {

        delete(testFileIn);

        testFileIn = File.createTempFile("largeFileProcessIn", ".xml");

        OutputStream out = new FileOutputStream(testFileIn);
        XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(new OutputStreamWriter(out, "UTF-8"));

        writeTestFile(writer);

        writer.close();

        log.debug("wrote test file to: " + testFileIn
            + " of size: " + (testFileIn.length() / (1024 * 1024)) + " megabytes");

    }

    private static void writeTestFile(XMLStreamWriter writer) throws XMLStreamException {

        writer.writeStartDocument();
        writeElements(writer, 0);
        writer.writeEndDocument();

    }

    private static void writeElements(XMLStreamWriter writer, int depth) throws XMLStreamException {

        writer.writeStartElement("container");

        if (depth < maxDepth) {
            writeElements(writer, ++depth);
        }

        for (int i = 0; i < elementsPerLevel; i++) {
            writer.writeStartElement("item" + i);
            writer.writeCharacters(TEXT);
            writer.writeEndElement();
        }

        writer.writeEndElement();
        writer.flush();

    }

    private static void delete(File file) {

        if (file != null && file.exists()) {
            file.delete();
        }

    }

}

