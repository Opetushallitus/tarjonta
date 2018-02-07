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
package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper class for easy access to security context data.
 *
 * @author jani
 */
@Component
public class ContextDataServiceImpl implements ContextDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ContextDataServiceImpl.class);

    /**
     * Get user's user-service OID.
     *
     * @return user OID
     */
    @Override
    public String getCurrentUserOid() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getName() == null) {
            return "NA";
        }

        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Get user's preferred language code. Default or fallback value is 'FI'.
     *
     * @return language code
     */
    @Override
    public String getCurrentUserLang() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        } else {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final Object principal = authentication.getPrincipal();

            if (principal != null && principal instanceof SadeUserDetailsWrapper) {
                SadeUserDetailsWrapper sadeUser = (SadeUserDetailsWrapper) principal;
                LOG.info("User SadeUserDetailsWrapper : {}, user oid : {}", sadeUser, sadeUser.getUsername());

                if (sadeUser.getLang() != null && !sadeUser.getLang().isEmpty()) {
                    return sadeUser.getLang(); //return an user lang code
                }
            }

            LOG.info("Not user lang found.");
            return "FI";
        }
    }
}
