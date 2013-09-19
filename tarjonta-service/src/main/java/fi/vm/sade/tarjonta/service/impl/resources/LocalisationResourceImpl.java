/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.service.resources.LocalisationResource;
import fi.vm.sade.tarjonta.service.resources.dto.LocalisationRDTO;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mlyly
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class LocalisationResourceImpl implements LocalisationResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocalisationResourceImpl.class);

    @Override
    public Map<String, LocalisationRDTO> getLocalisations(String requestedLocale, boolean includeAllLanguages) {
        LOG.info("getLocalisations({}, {})", requestedLocale, includeAllLanguages);
        Map<String, LocalisationRDTO> result = new HashMap<String, LocalisationRDTO>();

        if (requestedLocale == null) {
            requestedLocale = "fi";
        }

        Map<String, String> allLangsMap = null;
        if (includeAllLanguages) {
            allLangsMap = new HashMap<String, String>();
            allLangsMap.put("fi", "Tää");
            allLangsMap.put("en", "is");
            allLangsMap.put("sv", "testen");
        }

        {
            LocalisationRDTO t = new LocalisationRDTO("this", requestedLocale, "Tämä tosiaankin");
            t.setValues(allLangsMap);
            result.put(t.getKey(), t);
        }
        {
            LocalisationRDTO t = new LocalisationRDTO("is", requestedLocale, "ollakko (vai eikö olla)");
            t.setValues(allLangsMap);
            result.put(t.getKey(), t);
        }
        {
            LocalisationRDTO t = new LocalisationRDTO("a", requestedLocale, "jonkinlainen");
            t.setValues(allLangsMap);
            result.put(t.getKey(), t);
        }
        {
            LocalisationRDTO t = new LocalisationRDTO("test", requestedLocale, "testi tms. kokeilu");
            t.setValues(allLangsMap);
            result.put(t.getKey(), t);
        }

        return result;
    }

    @Override
    public LocalisationRDTO getLocalisation(String key) {
        LOG.info("getLocalisation({})", key);
        return new LocalisationRDTO();
    }

    @Override
    public void updateLocalization(String key, LocalisationRDTO data) {
        LOG.info("updateLocalization({})", key);
    }

    @Override
    public LocalisationRDTO createLocalization(String key, LocalisationRDTO data) {
        LOG.info("createLocalization({})", key);
        return data;
    }

    @Override
    public void deleteLocalization(String key) {
        LOG.info("deleteLocalization({})", key);
    }
}
