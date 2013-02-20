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
package fi.vm.sade.tarjonta.publication.enricher.mock;

import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService;
import java.util.Map;

/**
 *
 * @author Jukka Raanamo
 */
public class KoodistoLookupServiceMock implements KoodistoLookupService {

    @Override
    public KoodiValue lookupKoodi(String uri, Integer version) {
        return new SimpleKoodiValue(uri, "123", "Nimi", "Name", "Namn");
    }

    @Override
    public KoodiValue searchKoodiRelation(String uri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getCachedKoodistoLanguageCodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLanguageCodeByKoodiUri(String koodiUri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

