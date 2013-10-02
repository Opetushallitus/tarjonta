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
import java.util.ArrayList;
import java.util.List;
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
@Transactional(readOnly = false)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class LocalisationResourceImpl implements LocalisationResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocalisationResourceImpl.class);
    @Autowired
    private LocalisationDAO localisationDAO;

    @Override
    public List<LocalisationRDTO> getLocalisations() {
        LOG.info("getLocalisations()");

        List<LocalisationRDTO> result = new ArrayList<LocalisationRDTO>();

        List<Localisation> localisations = localisationDAO.findAll();
        for (Localisation l : localisations) {
            result.add(convertToRDTO(l));
        }

        return result;
    }

    @Override
    public List<LocalisationRDTO> getLocalisationsByLocale(String locale) {
        LOG.info("getLocalisationsByLocale({})", locale);

        List<LocalisationRDTO> result = new ArrayList<LocalisationRDTO>();

        List<Localisation> localisations = localisationDAO.findBy("language", locale);
        for (Localisation l : localisations) {
            result.add(convertToRDTO(l));
        }

        return result;
    }

    @Override
    public LocalisationRDTO getLocalisationByLocaleAndKey(String locale, String key) {
        LOG.info("getLocalisationByLocaleAndKey({}, {})", locale, key);

        LocalisationRDTO result = null;

        Localisation l = localisationDAO.findByKeyAndLocale(key, locale);
        if (l != null) {
            result = convertToRDTO(l);
        }

        return result;
    }

    @Override
    public LocalisationRDTO updateLocalisationByLocaleAndKey(String locale, String key, LocalisationRDTO data) {
        LOG.info("updateLocalisationByLocaleAndKey({}, {})", locale, key);

        Localisation l = localisationDAO.findByKeyAndLocale(key, locale);
        if (l == null) {
            l = new Localisation();
            l.setKey(data.getKey());
            l.setLanguage(locale);
        }

        l.setValue(data.getValue());

        if (l.getId() != null) {
            localisationDAO.update(l);
        } else {
            localisationDAO.insert(l);
        }

        return convertToRDTO(l);
    }

    @Override
    public LocalisationRDTO createLocalisationByLocaleAndKey(String locale, String key, LocalisationRDTO data) {
        LOG.info("createLocalisationByLocaleAndKey({}, {})", locale, key);
        return updateLocalisationByLocaleAndKey(locale, key, data);
    }

    @Override
    public LocalisationRDTO deleteLocalisationByLocaleAndKey(String locale, String key) {
        LOG.info("deleteLocalisationByLocaleAndKey({}, {})", locale, key);

        LocalisationRDTO result = null;

        Localisation l = localisationDAO.findByKeyAndLocale(key, locale);
        if (l != null) {
            localisationDAO.remove(l);
            result = convertToRDTO(l);
        }

        return result;
    }

    private LocalisationRDTO convertToRDTO(Localisation l) {
        if (l == null) {
            return null;
        }

        LocalisationRDTO result = new LocalisationRDTO(l.getKey(), l.getLanguage(), l.getValue());
        return result;
    }
}
