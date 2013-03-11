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
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.ui.portlet.security.User;

/**
 * Contains information about user context. This object is bound to session.
 */
@Service
@Scope(org.springframework.web.context.WebApplicationContext.SCOPE_SESSION)
public class UserContext implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(UserContext.class);

    @Autowired(required = true)
    UserProvider userProvider;

    @Value("${root.organisaatio.oid:}")
    String rootOrgOid;

    private boolean isUseRestriction;

    /**
     * Return "first" org oid for user or null if no orgs available for user.
     * 
     * @return
     */
    public String getFirstOrganisaatio() {
        return getUser().getOrganisations().size() > 0 ? getUser().getOrganisations().iterator().next() : null;
    }

    public UserContext() {
        log.debug("Constructing new user context");
    }

    private User getUser() {
        final User user = userProvider.getUser();
        log.debug("user:" + user);
        return user;
    }

    /**
     * Should organisaatio search be executed automatically. Yes if user is not
     * oph user is member of max 1 orgs.
     * 
     * @return
     */
    public boolean isDoAutoSearch() {
        final boolean is = !isOphUser() && getUser().getOrganisations().size() == 1;
        log.info("is Autosearch:{}",is);
        return is;
    }
    
    /**
     * Checks if user is member of "oph".
     * 
     * @return
     */
    public boolean isOphUser() {
        final boolean isOphUser = getUser().getOrganisations().contains(rootOrgOid);
        log.debug("isOphUser: {}", isOphUser);
        return isOphUser;
    }
    
    public boolean isUseRestriction(){
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
        final Set<String> userOrgs = getUser().getOrganisations();
        log.debug("userOrgs: {}", userOrgs.toString() );
        return userOrgs;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(rootOrgOid, "rootOrgOid is not set.");
        Preconditions.checkNotNull(userProvider, "userProvider is not set.");
        //use restriction only if not oph user and user has single organisation
        isUseRestriction = !isOphUser() && getUserOrganisations().size()==1;
        log.debug("userProvider: {}, useRestriction: {}", userProvider.getClass().getName(), isUseRestriction);
    }

}
