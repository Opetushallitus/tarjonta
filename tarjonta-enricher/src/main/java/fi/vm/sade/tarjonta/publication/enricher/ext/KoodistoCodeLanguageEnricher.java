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

import static fi.vm.sade.tarjonta.publication.enricher.ElementEnricher.WRITE_AND_CONTINUE;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handles elements that are of type Description: This is a dummy class without
 * any implementation. Look more info at XMLStreamEnricher.
 *
 * @author Jani Wilén
 */
public class KoodistoCodeLanguageEnricher extends KoodistoCodeValueEnricher {

    @Override
    public int startElement(String localName, Attributes attributes) {
        return WRITE_AND_CONTINUE;
    }

    @Override
    public int endElement(String localName) throws SAXException {
        return WRITE_AND_EXIT;
    }
}
