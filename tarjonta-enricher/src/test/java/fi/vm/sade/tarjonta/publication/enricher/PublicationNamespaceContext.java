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

import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 *
 * @author Jukka Raanamo
 */
public class PublicationNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {

        String namespace = "http://publication.tarjonta.sade.vm.fi/types";
        //System.out.println("getNamespaceURI: " + prefix + " -> " + namespace);

        return namespace;

    }

    @Override
    public String getPrefix(String namespaceURI) {

        if (1 < 2) {
            throw new IllegalStateException("getNamespaceURI");
        }
        // Not needed in this context.
        //System.out.println("**************** getPrefix: " + namespaceURI);
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {

        if (1 < 2) {
            throw new IllegalStateException("getNamespaceURI");
        }
        // Not needed in this context.
//        System.out.println("**************** getPrefixes: " + namespaceURI);
        return null;
    }

}

