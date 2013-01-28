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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashSet;
import java.util.Set;
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

    private static final Logger log = LoggerFactory.getLogger(KoodistoCodeValueCollectionEnricher.class);
    private KoodiValue koodistoKoodi;

    @Override
    public int startElement(String localName, Attributes attributes) throws SAXException {
        if (TAG_CODE.equals(localName)) {
            koodiUri = koodiUri(attributes);
            final KoodiValue koodistoKoodi = getKoodistoKoodi(localName);

            if (koodistoKoodi != null) {
                AttributesImpl atts = new AttributesImpl();
                for (int i = 0; i < attributes.getLength(); i++) {
                    atts.addAttribute("", attributes.getQName(i), null, null, attributes.getValue(i));
                }

                atts.addAttribute("", "value", null, null, koodistoKoodi.getValue());
                setAttributes(atts);
            } else {
                setAttributes(attributes);
            }
        } else {
            return startElementHandler(mappedElementName, localName, attributes);
        }

        return WRITE_AND_CONTINUE;
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
