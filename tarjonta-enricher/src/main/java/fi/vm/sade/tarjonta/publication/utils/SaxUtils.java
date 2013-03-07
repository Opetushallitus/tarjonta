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
package fi.vm.sade.tarjonta.publication.utils;

import fi.vm.sade.tarjonta.publication.enricher.ElementEnricher;
import static fi.vm.sade.tarjonta.publication.enricher.ElementEnricher.EMPTY_STRING;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author Jani Wilén
 */
public class SaxUtils {
    
    private static final String ATTRIBUTE_TYPE_CDATA = "CDATA";
    
    public static AttributesImpl copyAttributes(final Attributes attributes) {
        AttributesImpl copy = new AttributesImpl();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getValue(i) != null) {
                
                
                
                copy.addAttribute(attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
            }
        }
        
        return copy;
    }
    
    public static void addAttribute(AttributesImpl attributes, final String uri,  final String key, final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value object cannot be null.");
        }
        
        attributes.addAttribute(uri, key, "xsi" + key, ATTRIBUTE_TYPE_CDATA, value);
    }
}
