/*
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
package fi.vm.sade.tarjonta.rest;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.Reader;
import java.io.StringReader;

@RunWith(JUnit4.class)
public class SaxParserPresenceTest {
    private boolean tagParsed;

    @Test
    public void testXmlInitialisation() throws Exception {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        xmlReader.setContentHandler(new DefaultHandler() {
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if ("foo".equals(localName)) {
                    tagParsed = true;
                }
                super.endElement(uri, localName, qName);
            }
        });
        xmlReader.parse(new InputSource() {
            @Override
            public Reader getCharacterStream() {
                return new StringReader("<foo>Hello</foo>");
            }
        });
        Assert.assertTrue("Document does not seem to be parsed?", tagParsed);
    }
}

