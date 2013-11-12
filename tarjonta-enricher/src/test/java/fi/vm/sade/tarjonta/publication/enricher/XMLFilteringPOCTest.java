package fi.vm.sade.tarjonta.publication.enricher;

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


import com.meggison.sax.XMLWriter;
import java.io.*;
import org.apache.commons.io.output.NullWriter;
import org.junit.Test;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A POC to test enriching streamed XML content.
 *
 * @author Jukka Raanamo
 */
public class XMLFilteringPOCTest {

    private static final String INPUT_FILE = "src/test/resources/filter-test.xml";

    @Test
    public void testWriteFilteringWithSAX() throws Exception {

        // destination of modified output
        StringWriter out = new StringWriter();

        XMLReader reader = XMLReaderFactory.createXMLReader();

        // ignore original output with null writer - does not feel right
        XMLWriter writer = new XMLWriter(reader, new NullWriter());
        XMLEnricher filter = new XMLEnricher(writer);

        filter.setContentHandler(new XMLWriter(out));
        filter.parse(new InputSource(INPUT_FILE));

        //System.out.println("output: \n" + out.toString());

    }

    /**
     *  Simple enricher that conditionally injects elements and attributes.
     */
    private static class XMLEnricher extends XMLFilterImpl {

        public XMLEnricher(XMLReader reader) {
            super(reader);
        }

        private boolean hasLength;

        @Override
        public void startElement(String uri, String localName, String qname, Attributes attrs) throws SAXException {

            Attributes attributes = attrs;

            if ("length".equals(localName)) {
                hasLength = true;
            } else if ("snake".equals(localName)) {
                hasLength = false;
                if (attrs.getIndex("color") == -1) {
                    // no color, add
                    AttributesImpl newAttributes = new AttributesImpl(attrs);
                    newAttributes.addAttribute("", "color", null, null, "red");
                    attributes = newAttributes;
                }
            }

            super.startElement(uri, localName, qname, attributes);

        }

        @Override
        public void endElement(String uri, String localName, String qname) throws SAXException {

            // add new content
            if ("snake".equals(localName) && !hasLength) {
                super.startElement("", "length", null, new AttributesImpl());
                super.characters("15".toCharArray(), 0, 2);
                super.endElement("", "length", null);
            }

            super.endElement(uri, localName, qname);

        }

    }


}

