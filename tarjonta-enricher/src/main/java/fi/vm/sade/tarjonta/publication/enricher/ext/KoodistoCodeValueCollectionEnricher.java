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
package fi.vm.sade.tarjonta.publication.enricher.ext;

import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService.KoodiValue;
import fi.vm.sade.tarjonta.publication.utils.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Handles collections of Koodisto code -elements that all share the same
 * scheme.
 *
 * @author Jukka Raanamo
 */
public class KoodistoCodeValueCollectionEnricher extends KoodistoCodeValueEnricher {

    private static final String TAG_ATTRIBUTE_VALUE = "value";
    private static final Logger log = LoggerFactory.getLogger(KoodistoCodeValueCollectionEnricher.class);
    private KoodiValue koodistoKoodi;

    @Override
    public int startElement(String localName, Attributes attributes) throws SAXException {
        if (TAG_CODE.equals(localName)) {
            int startElementHandler = startElementHandler(TAG_CODE, localName, attributes);
            koodistoKoodi = getKoodistoKoodi(localName);

            //add a 'value'-attribute to tag
            if (koodistoKoodi != null && koodistoKoodi.getValue() != null) {
                AttributesImpl copy = SaxUtils.copyAttributes(attributes);
                SaxUtils.addAttribute(copy, TAG_ATTRIBUTE_VALUE, koodistoKoodi.getValue());
                setAttributes(copy);
            }
            return startElementHandler;
        } else {
            //get a scheme 'Koodisto' form parent and store it for the next element loop.
            if (scheme == null) {
                scheme = attributes.getValue(EMPTY_STRING, ATTRIBUTE_SCHEME);
            }
            return startElementHandler(mappedElementName, localName, attributes);
        }
    }

    @Override
    public int endElement(String localName) throws SAXException {
        if (TAG_CODE.equals(localName)) {
            this.maybeWriteLabels(localName, koodistoKoodi);
        } else if (mappedElementName.equals(localName)) {
            reset();
            return WRITE_AND_EXIT;
        }

        return WRITE_AND_CONTINUE;
    }
}
