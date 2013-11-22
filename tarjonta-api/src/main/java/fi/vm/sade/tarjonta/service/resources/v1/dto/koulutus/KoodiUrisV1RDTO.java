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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
public class KoodiUrisV1RDTO extends MetaV1RDTO {

    private static final long serialVersionUID = 1L;
    private Map<String, Integer> uris;

    public KoodiUrisV1RDTO() {
    }

    /**
     * @return the uris
     */
    public Map<String, Integer> getUris() {
        if (uris == null) {
            uris = new HashMap<String, Integer>();
        }
        return uris;
    }

    /**
     * @param uris the uris to set
     */
    public void seUris(Map<String, Integer> uris) {
        this.uris = uris;
    }
}
