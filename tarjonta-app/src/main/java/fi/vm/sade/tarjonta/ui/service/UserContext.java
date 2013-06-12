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
package fi.vm.sade.tarjonta.ui.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import java.io.Serializable;

/**
 * Contains information about user context. This object is bound to session.
 */
@Service
@Scope(org.springframework.web.context.WebApplicationContext.SCOPE_SESSION)
public class UserContext implements InitializingBean, Serializable {

    private transient static final Logger log = LoggerFactory.getLogger(UserContext.class);
    String rootOrgOid;
    private boolean isUseRestriction;
    private Set<String> userOrganisations;

    private Set<String> parseUserOrganisations(Authentication authentication) {
        if (authentication == null) {
            log.warn("user is not authenticated!");
            return Sets.newHashSet();
        }
        Set<String> orgs = Sets.newHashSet();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().contains(
                    TarjontaPermissionServiceImpl.TARJONTA)) {
                String authorityString = authority.getAuthority();
                String orgOid = authorityString.substring(authorityString
                        .lastIndexOf("_") + 1);
                if (orgOid.startsWith("1")) {
                    // XXX this is hack, use userservice?
                    orgs.add(orgOid);
                }
            }

        }
        return orgs;
    }

    /**
     * Return "first" org oid for user or null if no orgs available for user.
     *
     * @return
     */
    public String getFirstOrganisaatio() {
        return userOrganisations.size() > 0 ? userOrganisations.iterator().next() : null;
    }

    @Autowired
    public UserContext(@Value("${root.organisaatio.oid}") String rootOrgOid) {
        this.rootOrgOid = rootOrgOid;
        log.debug("Constructing new user context");
        userOrganisations = parseUserOrganisations(SecurityContextHolder.getContext().getAuthentication());
    }

    //Sami USE THIS!!!
    public String getUserOid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Should organisaatio search be executed automatically. Yes if user is not
     * oph user is member of max 1 orgs.
     *
     * @return
     */
    public boolean isDoAutoSearch() {
        final boolean is = !isOphUser() && userOrganisations.size() >= 1;
        log.info("is Autosearch:{}", is);
        return is;
    }

    /**
     * Checks if user is member of "oph".
     *
     * @return
     */
    public boolean isOphUser() {
        final boolean isOphUser = userOrganisations.contains(rootOrgOid);
        log.debug("isOphUser: {}", isOphUser);
        return isOphUser;
    }

    public boolean isUseRestriction() {
        return isUseRestriction;
    }

    public void setUseRestriction(boolean useRestriction) {
        this.isUseRestriction = useRestriction;
    }

    /**
     * Convenience method for getting user organisations.
     *
     * @return
     */
    public Set<String> getUserOrganisations() {
        final Set<String> userOrgs = ImmutableSet.copyOf(userOrganisations);
        log.debug("userOrgs: {}", userOrgs.toString());
        return userOrgs;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //use restriction only if not oph user and user has organisations
        isUseRestriction = !isOphUser() && getUserOrganisations().size() >= 1;
        log.debug("useRestriction: {}", isUseRestriction);
    }
}
