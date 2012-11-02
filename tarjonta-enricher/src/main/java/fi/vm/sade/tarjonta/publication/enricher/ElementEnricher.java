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
    XMLStreamEnricher.XMLProcessor parent;

    /**
     * Name of the XML element that this handled was mapped with.
     */
    String mappedElementName;

    private StringBuilder sb = new StringBuilder();

    /**
     * Returns string from given array.
     *
     * @param characters
     * @param start
     * @param length
     * @return
     */
    protected String text(char[] characters, int start, int length) {

        sb.setLength(0);
        return sb.append(characters, start, length).toString();

    }

    /**
     *
     * @param localName
     * @param attributes
     * @return
     * @throws SAXException
     */
    public abstract int startElement(String localName, Attributes attributes) throws SAXException;

    public abstract int endElement(String localName) throws SAXException;

    public abstract int characters(char[] characters, int start, int length) throws SAXException;

}

