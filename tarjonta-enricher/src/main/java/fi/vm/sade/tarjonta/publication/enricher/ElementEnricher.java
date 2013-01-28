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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Extend this class to provide custom XML injection. Register to enricher using
 * {@link #registerHandler(java.lang.String, fi.vm.sade.tarjonta.publication.enricher.LearningOpportunityDataEnricher.ElementHandler) }
 */
public abstract class ElementEnricher {

    static final int MASK_WRITE = 1;

    static final int MASK_SKIP = 2;

    static final int MASK_CONTINUE = 4;

    static final int MASK_EXIT = 8;

    /**
     * Tells calling processor to skip writing current event to output stream
     * and continue using current enricher.
     */
    public static final int SKIP_AND_CONTINUE = MASK_SKIP | MASK_CONTINUE;

    /**
     * Tells calling processor to skip writing current event to output stream
     * and stop using current enricher.
     */
    public static final int SKIP_AND_EXIT = MASK_SKIP | MASK_EXIT;

    /**
     * Tells calling processor to write current event to output stream
     * and continue using current enricher.
     */
    public static final int WRITE_AND_CONTINUE = MASK_WRITE | MASK_CONTINUE;

    /**
     * Tells calling processor to write current event to output stream
     * and stop using current enricher.
     */
    public static final int WRITE_AND_EXIT = MASK_WRITE | MASK_EXIT;

    public static final String EMPTY_STRING = "";

    /**
     * Parent processor, exposes methods to write custom content directly to stream.
     */
    protected XMLStreamEnricher.XMLProcessor parent;

    /**
     * Name of the XML element that this handled was mapped with.
     */
    protected String mappedElementName;

    private StringBuilder sb = new StringBuilder();

    /**
     * Returns string from given array.
     *
     * @param characters
     * @param start
     * @param length
     * @return
     */
    protected String charsToString(char[] characters, int start, int length) {

        sb.setLength(0);
        return sb.append(characters, start, length).toString();

    }

    /**
     * Overwrite to handle start of element. Default implementation just returns {@link #WRITE_AND_CONTINUE}
     *
     * @param localName name of the parsed element
     * @param attributes element's attributes if any
     * @return flag indicating how to process current element and how to continue processing
     * @throws SAXException
     */
    public int startElement(String localName, Attributes attributes) throws SAXException {
        return WRITE_AND_CONTINUE;
    }

    /**
     * Overwrite to handle end of element. Default implementation returns flag that will write current end element
     * as-is to stream but will stop calling this handler if current element has the same name as the one that started
     * this handler.
     *
     * TODO: compare depth and not element name.
     *
     * @param localName
     * @return
     * @throws SAXException
     */
    public int endElement(String localName) throws SAXException {

        if (mappedElementName.equals(localName)) {
            return WRITE_AND_EXIT;
        } else {
            return WRITE_AND_CONTINUE;
        }

    }

    /**
     * Overwrite to handle text characters from XML. Default implementation returns flag that
     * will cause characters to be written to underlying stream as-is and this handler will
     * keep on receiving events.
     *
     * @param characters
     * @param start
     * @param length
     * @return
     * @throws SAXException
     */
    public int characters(char[] characters, int start, int length) throws SAXException {

        return WRITE_AND_CONTINUE;

    }

    /**
     * Invoked to reset internal state so that instances can be recycled.
     */
    public abstract void reset();
    
    
    public abstract Attributes getAttributes();

}

