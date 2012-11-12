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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.commons.io.output.NullWriter;
import com.meggison.sax.XMLWriter;

import static fi.vm.sade.tarjonta.publication.enricher.ElementEnricher.*;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Component that takes an XML stream as input and writes the
 * same stream to output enriching the content with registered handlers.
 *
 * @author Jukka Raanamo
 */
public class XMLStreamEnricher {

    private InputStream in;

    private OutputStream out;

    private Map<String, ElementEnricher> handlerMap;

    public XMLStreamEnricher() {

        handlerMap = new HashMap<String, ElementEnricher>();

    }

    public void setOutput(OutputStream out) {
        this.out = out;
    }

    public void setInput(InputStream in) {
        this.in = in;
    }

    /**
     * Register non-built-in handler to handle elements by given name.
     *
     * @param elementName
     * @param handler
     */
    public void registerHandler(String elementName, ElementEnricher handler) {
        handlerMap.put(elementName, handler);
    }

    /**
     * Remove all handler, including built-in.
     */
    public void clearHandlers() {
        handlerMap.clear();
    }

    /**
     * Consumes input stream in its entirety by passing it to registered
     * enrichers and writing results to output stream.
     *
     * @throws SAXException
     * @throws IOException
     */
    public void process() throws SAXException, IOException {

        final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        final XMLWriter xmlWriter = new XMLWriter(xmlReader, new NullWriter());
        final XMLProcessor processor = new XMLProcessor(xmlWriter);

        processor.setContentHandler(new XMLWriter(new OutputStreamWriter(out)));
        processor.parse(new InputSource(in));

    }

    /**
     * Interrupts SAX events by allowing registered handlers to inject richer data. Original
     * XML is always written to output stream.
     */
    public class XMLProcessor extends XMLFilterImpl {

        private ElementEnricher handler;

        public XMLProcessor(XMLWriter writer) {
            super(writer);
        }

        /**
         * Looks up handler for the element by it's name. If handler has been registered, delegates call to handler
         * allowing it to inject custom data.
         *
         * todo: need to add mechanism where handler indicates if it has already written the content or if
         * default writing should take place or be skipped.
         */
        @Override
        public void startElement(String uri, String localName, String qname, Attributes attrs) throws SAXException {

            if (handler == null) {
                handler = getHandlerByTag(localName);
            }

            if (handler != null) {

                int result = handler.startElement(localName, attrs);

                if ((result & MASK_WRITE) == MASK_WRITE) {
                    super.startElement(uri, localName, qname, attrs);
                }

                if ((result & MASK_EXIT) == MASK_EXIT) {
                    handler.parent = null;
                    handler = null;
                }

            } else {

                // no handler, copy to output
                super.startElement(uri, localName, qname, attrs);

            }

        }

        @Override
        public void endElement(String uri, String localName, String qname) throws SAXException {

            if (handler != null) {

                int result = handler.endElement(localName);

                if ((result & MASK_WRITE) == MASK_WRITE) {
                    super.endElement(uri, localName, qname);
                }

                if ((result & MASK_EXIT) == MASK_EXIT) {
                    handler.parent = null;
                    handler.mappedElementName = null;
                    handler = null;
                }

            } else {

                // no handler, copy to output
                super.endElement(uri, localName, qname);

            }


        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {

            if (handler != null) {

                if ((handler.characters(chars, start, length) & MASK_WRITE) == MASK_WRITE) {
                    super.characters(chars, start, length);
                }

            } else {

                super.characters(chars, start, length);

            }

        }

        private ElementEnricher getHandlerByTag(String tagName) {

            ElementEnricher enricher = handlerMap.get(tagName);
            if (enricher != null) {
                enricher.reset();
                enricher.mappedElementName = tagName;
                enricher.parent = this;
            }
            return enricher;

        }

        /**
         * Write a start element directly to stream.
         *
         * @param name
         * @param attrs
         * @throws SAXException
         */
        protected void writeStartElement(String name, Attributes attrs) throws SAXException {
            super.startElement(EMPTY_STRING, name, null, attrs);
        }

        /**
         * Writes element start with attributes.
         *
         * @param name
         * @param keyValuePairs an even number of attributes: key, value, ... key, value
         * @throws SAXException
         */
        protected void writeStartElement(String name, String... keyValuePairs) throws SAXException {

            AttributesImpl attributes = new AttributesImpl();
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                attributes.addAttribute(EMPTY_STRING, keyValuePairs[i], null, null, keyValuePairs[i + 1]);
            }
            writeStartElement(name, attributes);

        }

        /**
         * Write an end element directly to stream.
         *
         * @param name
         * @throws SAXException
         */
        protected void writeEndElement(String name) throws SAXException {
            super.endElement("", name, null);
        }

        /**
         * Write characters directly to stream.
         *
         * @param chars
         * @throws SAXException
         */
        protected void writeCharacters(String chars) throws SAXException {
            super.characters(chars.toCharArray(), 0, chars.length());
        }

    }


}

