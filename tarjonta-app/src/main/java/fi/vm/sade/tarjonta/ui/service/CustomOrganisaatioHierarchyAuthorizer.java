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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.security.OidProvider;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.tarjonta.mock.UserProviderMock;

/**
 * Authorizer that uses {@link UserProvider} to get the current user. When
 * developing the permissions one normally uses UserProviderMock to provide a
 * user with hand crafted permissions, see {@link UserProviderMock}.
 */
public class CustomOrganisaatioHierarchyAuthorizer extends OrganisationHierarchyAuthorizer implements InitializingBean {

    @Autowired(required = true)
    UserProvider userProvider;

    public CustomOrganisaatioHierarchyAuthorizer() {
        super();
    }

    public CustomOrganisaatioHierarchyAuthorizer(OidProvider op) {
        super(op);
    }

    @Override
    public void checkAccess(Authentication currentUser, String targetOrganisationOid, String... roles)
            throws NotAuthorizedException {
        super.checkAccess(userProvider.getUser().getAuthentication(), targetOrganisationOid, roles);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(userProvider, "userProvider not set!");
    }
}
