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

import fi.vm.sade.tarjonta.dao.LocalisationDAO;
import fi.vm.sade.tarjonta.model.Localisation;
import fi.vm.sade.tarjonta.service.resources.LocalisationResource;
import fi.vm.sade.tarjonta.service.resources.dto.LocalisationRDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST services for localisations.
 *
 * @author mlyly
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class LocalisationResourceImpl implements LocalisationResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocalisationResourceImpl.class);

    @Autowired
    private LocalisationDAO localisationDAO;

    @Override
    public Map<String, LocalisationRDTO> getLocalisations(String requestedLocale, boolean includeAllLanguages) {
        LOG.info("getLocalisations({}, {})", requestedLocale, includeAllLanguages);

        if (requestedLocale == null) {
            requestedLocale = "fi";
        }

        List<Localisation> localisations;

        if (includeAllLanguages) {
            localisations = localisationDAO.findByKeyPrefix("");
        } else {
            localisations = localisationDAO.findByKeyPrefixAndLocale("", requestedLocale);
        }

        Map<String, LocalisationRDTO> result = convertToResult(requestedLocale, includeAllLanguages, localisations);
        return result;
    }

    @Override
    public Map<String, LocalisationRDTO> getLocalisation(String key) {
        LOG.info("getLocalisation({})", key);

        List<Localisation> localisations = localisationDAO.findByKeyPrefix(key);

        Map<String, LocalisationRDTO> result = convertToResult(null, true, localisations);
        return result;
    }

    @Override
    public void updateLocalization(String key, LocalisationRDTO data) {
        LOG.info("updateLocalization({})", key);

        Localisation l = localisationDAO.findByKeyAndLocale(data.getKey(), data.getLocale());
        if (l != null) {
            l.setValue(data.getValue());
            localisationDAO.update(l);
        }
    }

    @Override
    public LocalisationRDTO createLocalization(String key, LocalisationRDTO data) {
        LOG.info("createLocalization({}, {})", key, data);

        Localisation l = new Localisation();
        l.setKey(data.getKey());
        l.setLanguage(data.getKey());
        l.setValue(data.getValue());

        localisationDAO.insert(l);

        return data;
    }

    @Override
    public void deleteLocalization(String key) {
        LOG.info("deleteLocalization({})", key);

        List<Localisation> localisations = localisationDAO.findByKey(key);
        for (Localisation localisation : localisations) {
            localisationDAO.remove(localisation);
        }
    }

    /**
     * Convert list to result set.
     *
     * @param requestedLocale
     * @param includeAllLanguages
     * @param localisations
     * @return
     */
    private Map<String, LocalisationRDTO> convertToResult(String requestedLocale, boolean includeAllLanguages, List<Localisation> localisations) {
        Map<String, LocalisationRDTO> result = new HashMap<String, LocalisationRDTO>();

        if (requestedLocale == null) {
            requestedLocale = "fi";
        }

        for (Localisation localisation : localisations) {
            // Create OR use placeholder for translations
            LocalisationRDTO l = result.get(localisation.getKey());
            if (l == null) {
                // Create placeholder
                l = new LocalisationRDTO(localisation.getKey(), requestedLocale, null);
                result.put(l.getKey(), l);
            }

            // If locale is selected one, then use this localisations value as "value".
            if (requestedLocale.equalsIgnoreCase(localisation.getLanguage())) {
                l.setValue(localisation.getValue());
            }

            // Add all translated languages?
            if (includeAllLanguages) {
                if (l.getValues() == null) {
                    l.setValues(new HashMap<String, String>());
                }
                l.getValues().put(localisation.getLanguage(), localisation.getValue());
            }
        }

        return result;
    }
}
