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
import java.util.*;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.commons.io.output.NullWriter;
import com.meggison.sax.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fi.vm.sade.tarjonta.publication.enricher.ElementEnricher.*;
import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService.KoodiValue;
import fi.vm.sade.tarjonta.publication.utils.SaxUtils;
import fi.vm.sade.tarjonta.publication.utils.StringUtils;
import fi.vm.sade.tarjonta.publication.utils.VersionedUri;

/**
 * <p>Since it has been chosen that several data values, used by Tarjonta
 * Service, are actually centrally hosted in Koodisto Service, and only
 * referenced from Tarjonta Service - the Tarjonta Service is unable to render
 * complete XML of its own data but a secondary process is required to enrich
 * this data with values from Koodisto. This class takes care of that process by
 * reading the stream and invoking registered handlers to perform the actual
 * enriching work.</p>
 *
 * <p> For usage examples, see unit tests e.g. XMLStreamEnricherTest </p>
 *
 * @author Jukka Raanamo
 */
public class XMLStreamEnricher {

    private static final String TAG_ATTRIBUTE_LANG = "lang";
    private InputStream in;
    private OutputStream out;
    private Map<String, ElementEnricher> byTagNameHandlers;
    private Map<Pattern, ElementEnricher> byRegexHandlers;
    private Deque<String> tagStack = new LinkedList<String>();
    private static final Logger log = LoggerFactory.getLogger(XMLStreamEnricher.class);

    public XMLStreamEnricher() {
        byTagNameHandlers = new HashMap<String, ElementEnricher>();
    }

    public void setOutput(OutputStream out) {
        this.out = out;
    }

    public void setInput(InputStream in) {
        this.in = in;
    }

    /**
     * Register handler to handle elements by tag name.
     *
     * @param elementName
     * @param handler
     */
    public void registerTagNameHandler(String elementName, ElementEnricher handler) {
        if (byTagNameHandlers == null) {
            byTagNameHandlers = new HashMap<String, ElementEnricher>();
        }
        byTagNameHandlers.put(elementName, handler);
    }

    /**
     * Register handler to handle elements by path regular expression. Path is
     * the current element stack e.g /root/child1/child2
     *
     * @param regex
     * @param handler
     */
    public void registerRegexHandler(String regex, ElementEnricher handler) {
        if (byRegexHandlers == null) {
            byRegexHandlers = new HashMap<Pattern, ElementEnricher>();
        }
        byRegexHandlers.put(Pattern.compile(regex), handler);
    }

    /**
     * Remove all handler, including built-in.
     */
    public void clearHandlers() {
        if (byRegexHandlers != null) {
            byTagNameHandlers.clear();
        }
        if (byRegexHandlers != null) {
            byRegexHandlers.clear();
        }
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
        xmlReader.setFeature("http://xml.org/sax/features/namespaces",  true);
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        xmlReader.setFeature("http://xml.org/sax/features/validation", true);

        xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
        xmlReader.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", true);

        final XMLProcessor processor = new XMLProcessor(xmlWriter);

        processor.setContentHandler(new XMLWriter(new OutputStreamWriter(out)));
        processor.parse(new InputSource(in));

    }

    private void pushTag(String tagName) {
        tagStack.addFirst(tagName);
    }

    private void popTag(String tagName) {
        tagStack.removeFirst();
    }

    /**
     * Returns path of element names from current element to root element.
     *
     * @return
     */
    private String makePath() {

        return StringUtils.join(tagStack, "/");

    }

    /**
     * Interrupts SAX events by allowing registered handlers to inject richer
     * data. Original XML is always written to output stream.
     */
    public class XMLProcessor extends XMLFilterImpl {

        private ElementEnricher handler;

        public XMLProcessor(XMLWriter writer) {
            super(writer);
        }

        /**
         * Looks up handler for the element by it's name. If handler has been
         * registered, delegates call to handler allowing it to inject custom
         * data.
         */
        @Override
        public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException {
            pushTag(localName);

            if (handler == null) {
                handler = getHandler(localName);
            }

            if (handler != null) {
                if (attributes != null && attributes.getLength() > 0) {
                    // replace koodi language attributes                   
                    attributes = copyAndFilterLangAttributes(handler, attributes);
                }

                int result = handler.startElement(uri, localName, attributes);

                if ((result & MASK_WRITE) == MASK_WRITE) {
                    super.startElement(uri, localName, qname, handler.getAttributes() != null ? handler.getAttributes() : attributes);
                }

                if ((result & MASK_EXIT) == MASK_EXIT) {
                    handler.parent = null;
                    handler = null;
                }

            } else {

                // no handler, copy to output
                super.startElement(uri, localName, qname, attributes);

            }

        }

        @Override
        public void endElement(String uri, String localName, String qname) throws SAXException {
            if (handler != null) {

                int result = handler.endElement(uri, localName);

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

            popTag(localName);

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

        /**
         * Returns initialized ElementHandler if one is configured for current
         * tagName.
         */
        private ElementEnricher getHandler(String tagName) {

            ElementEnricher enricher = getHandlerByTagName(tagName);
            if (enricher == null) {
                enricher = getHandlerByPathRegex();
            }

            if (enricher != null) {
                enricher.reset();
                enricher.mappedElementName = tagName;
                enricher.parent = this;
            }

            return enricher;
        }

        /**
         * Returns ElementEnricher that's been mapped by tag name if any.
         */
        private ElementEnricher getHandlerByTagName(String tagName) {

            return (byTagNameHandlers != null ? byTagNameHandlers.get(tagName) : null);

        }

        /**
         * Returns the first ElementEnricher that's been mapped by a regular
         * expression if any.
         */
        private ElementEnricher getHandlerByPathRegex() {

            if (byRegexHandlers == null) {
                return null;
            }

            // try by path expression
            final String tagPath = makePath();

            for (Map.Entry<Pattern, ElementEnricher> e : byRegexHandlers.entrySet()) {

                if ((e.getKey().matcher(tagPath).matches())) {
                    return e.getValue();
                }

            }

            return null;

        }

        /**
         * Write a start element directly to stream.
         *
         * @param name
         * @param attrs
         * @throws SAXException
         */
        public void writeStartElement(String uri, String name, Attributes attrs) throws SAXException {
            super.startElement(uri, name, name, attrs);
        }

        /**
         * Writes element start with attributes.
         *
         * @param name
         * @param keyValuePairs an even number of attributes: key, value, ...
         * key, value
         * @throws SAXException
         */
        public void writeStartElement(String uri, String name, String... keyValuePairs) throws SAXException {

            AttributesImpl attributes = new AttributesImpl();
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                SaxUtils.addAttribute(attributes, uri, keyValuePairs[i], keyValuePairs[i + 1]);
            }
            super.startElement(uri, name, name, attributes);
        }

        /**
         * Write an end element directly to stream.
         *
         * @param name
         * @throws SAXException
         */
        public void writeEndElement(String uri, String name) throws SAXException {
            super.endElement(uri, name, name);
        }

        /**
         * Write characters directly to stream.
         *
         * @param chars
         * @throws SAXException
         */
        public void writeCharacters(String chars) throws SAXException {
            if (chars == null) {
                log.error("Data was null, the problem might be related to element : '{}'.", this.handler.mappedElementName);
                return;
            }

            super.characters(chars.toCharArray(), 0, chars.length());
        }
    }

    /**
     * Outputs language code in lowercase.
     *
     * @param handler
     * @param str
     * @param localName
     * @return
     */
    private String getLanguageCode(final ElementEnricher handler, final String str) {
        //is language code koodisto uri or real language code?
        if (str == null || str.length() == 2) {
            //TODO: a better language code check.
            return str;
        } else {
            //not a language code
            VersionedUri koodiUriVersion = VersionedUri.parse(str);

            final String koodiUri = koodiUriVersion.getUri();
            final Integer KoodiVersion = koodiUriVersion.getVersio();

            log.debug("Search language code by Koodisto uri {}#{}", koodiUri, KoodiVersion);
            final KoodiValue KoodiLangCode = handler.lookupKoodi(koodiUri, KoodiVersion);
            if (KoodiLangCode != null && KoodiLangCode.getValue() != null) {
                //get a real language code
                return KoodiLangCode.getValue().toLowerCase();
            } else {
                log.warn("Language code not found by uri: '{}'", str);
            }
        }

        return null;
    }

    private Attributes copyAndFilterLangAttributes(final ElementEnricher handler, final Attributes attributes) {
        AttributesImpl copy = new AttributesImpl();

        for (int i = 0; i < attributes.getLength(); i++) {
            final String qName = attributes.getQName(i);
            if (TAG_ATTRIBUTE_LANG.equals(qName)) {
                final String languageCode = getLanguageCode(handler, attributes.getValue(i));
                if (languageCode != null) {
                    copy.addAttribute(attributes.getURI(i), TAG_ATTRIBUTE_LANG, TAG_ATTRIBUTE_LANG, attributes.getType(i), languageCode);
                }
            } else if (attributes.getValue(i) != null) {
                copy.addAttribute(attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
            }
        }

        return copy;
    }
}
