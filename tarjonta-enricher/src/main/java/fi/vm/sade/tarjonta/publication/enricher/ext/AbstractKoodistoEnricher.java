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

import fi.vm.sade.tarjonta.publication.enricher.ElementEnricher;
import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoLookupService.KoodiValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
public abstract class AbstractKoodistoEnricher extends ElementEnricher {

    protected KoodistoLookupService koodistoService;
    protected boolean failOnKoodiError = true;
    private static final Logger log = LoggerFactory.getLogger(AbstractKoodistoEnricher.class);

    /**
     * If true, and looking up koodi fails with exception, the exception is
     * allowed to propagate upwards.
     *
     * @param failOnKoodiError
     */
    public void setFailOnKoodiError(boolean failOnKoodiError) {
        this.failOnKoodiError = failOnKoodiError;
    }

    public void setKoodistoService(KoodistoLookupService koodistoService) {
        this.koodistoService = koodistoService;
    }

    public KoodistoLookupService getKoodistoService() {
        return koodistoService;
    }

    /**
     * Helper method that invokes KoodistoLookupService and catches any errors
     * if {@link #failOnKoodiError} is set to false.
     */
    @Override
    public KoodiValue lookupKoodi(final String koodiUri, final Integer koodiVersion) {
        log.debug("looking up koodi koodiUri: '{}', koodiVersion: '{}'", koodiUri, koodiVersion);
        if (failOnKoodiError) {
            return koodistoService.lookupKoodi(koodiUri, koodiVersion);
        } else {
            try {
                return koodistoService.lookupKoodi(koodiUri, koodiVersion);
            } catch (Exception e) {
                log.error("looking up koodi failed (ignoring), koodiUri: " + koodiUri + ", koodiVersion: " + koodiVersion, e);
                return null;
            }
        }
    }
}
