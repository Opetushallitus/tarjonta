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
package fi.vm.sade.tarjonta.publication.enricher.koodisto;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements KoodistoLookupService (used by Koodisto element enrichers) by directly looking
 * up code values from Koodisto webservice.
 *
 * @author Jukka Raanamo
 */
public class KoodistoLookupWebServiceImpl implements KoodistoLookupService {

    @Autowired
    private KoodiService koodiService;

    public void setKoodiService(KoodiService service) {
        koodiService = service;
    }

    @Override
    public KoodiValue lookupKoodi(String uri, Integer version) {

        List<KoodiType> koodis = koodiService.searchKoodis(buildCriteria(uri, version));

        if (koodis.isEmpty()) {
            return null;
        } else if (koodis.size() > 1) {
            throw new IllegalStateException("multiple koodis returned for uri: " + uri
                + ", version: " + version);
        }

        KoodiType koodi = koodis.get(0);

        if (koodi.getTila() != TilaType.HYVAKSYTTY) {
            throw new IllegalStateException("koodi has invalid state, uri: " + uri
                + ", version: " + version
                + ", state: " + koodi.getTila());
        }

        return new KoodiValueImpl(koodi);

    }

    private SearchKoodisCriteriaType buildCriteria(String uri, Integer version) {

        return (version != null
            ? KoodiServiceSearchCriteriaBuilder.koodiByUriAndVersion(uri, version)
            : KoodiServiceSearchCriteriaBuilder.latestValidAcceptedKoodiByUri(uri));

    }

    public static class KoodiValueImpl implements KoodiValue {

        private KoodiType koodi;

        public KoodiValueImpl(KoodiType koodi) {
            this.koodi = koodi;
        }

        @Override
        public String getMetaName(String lang) {
            return findName(lang);
        }

        @Override
        public String getValue() {
            return koodi.getKoodiArvo();
        }

        @Override
        public String getUri() {
            return koodi.getKoodiUri();
        }

        private String findName(String lang) {
            for (KoodiMetadataType md : koodi.getMetadata()) {
                if (md.getKieli().name().equalsIgnoreCase(lang)) {
                    return md.getNimi();
                }
            }
            return null;
        }

    }


}

