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
package fi.vm.sade.tarjonta.mock;

import fi.vm.sade.generic.service.PermissionService;
import fi.vm.sade.generic.ui.portlet.security.AccessRight;
import fi.vm.sade.generic.ui.portlet.security.User;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

/**
 * This is a mock implementation of AbstractPermissionService.
 *
 * @author Jani Wilén
 */
public class PermissionServiceMock implements PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionServiceMock.class);
    private static String ROOT_OID;
    public static final String ANY_ROLE = OrganisationHierarchyAuthorizer.ANY_ROLE;
    protected String ROLE_CRUD;
    protected String ROLE_RU;
    protected String ROLE_R;
    @Value("${root.organisaatio.oid:NOT_SET}")
    private String rootOrgOid;
    @Value("${debug.role.crud:}")
    private Boolean debugCRUD;
    @Value("${debug.role.ru:}")
    private Boolean debugRU;
    @Autowired(required = false)
    private OrganisationHierarchyAuthorizer authorizer;

    public PermissionServiceMock() {
        ROLE_CRUD = "APP_MOCK_CRUD";
        ROLE_RU = "APP_MOCK_READ_UPDATE";
        ROLE_R = "APP_MOCK_READ";
    }

    protected String getReadRole() {
        return ROLE_R;
    }

    protected String getReadUpdateRole() {
        return ROLE_RU;
    }

    protected String getCreateReadUpdateDeleteRole() {
        return ROLE_CRUD;
    }

    @Override
    public boolean userCanRead() {
        return getUser().isUserInRole(getReadRole()) || userCanReadAndUpdate() || userCanCreateReadUpdateAndDelete();
    }

    @Override
    public boolean userCanReadAndUpdate() {
        return getUser().isUserInRole(getReadUpdateRole()) || userCanCreateReadUpdateAndDelete();
    }

    @Override
    public boolean userCanCreateReadUpdateAndDelete() {
        return getUser().isUserInRole(getCreateReadUpdateDeleteRole());
    }

    protected User getUser() {
        return user;
    }

    protected boolean userIsMemberOfOrganisation(final String organisaatioOid) {
        //return getUser().getOrganisationsHierarchy().contains(organisaatioOid);
        return checkAccess(organisaatioOid, ANY_ROLE);
    }

    public boolean checkAccess(String targetOrganisaatioOid, String... roles) {
        if (authorizer == null) {
            throw new NullPointerException(this.getClass().getSimpleName() + ".authorizer -property is not wired, do it with spring or manuyally");
        }
        try {
            authorizer.checkAccess(getUser().getAuthentication(), targetOrganisaatioOid, roles);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getRootOrgOid() {
        if (rootOrgOid == null) {
            throw new RuntimeException("rootOrgId is null!");
        }
        return rootOrgOid;
    }

    public boolean isOPHUser() {
        return checkAccess(getRootOrgOid(), ANY_ROLE);
    }

    public void setAuthorizer(OrganisationHierarchyAuthorizer authorizer) {
        this.authorizer = authorizer;
    }
    /*
     * The mocked Liferay portal user.
     */
    private final User user = new User() {
        @Override
        public boolean isUserInRole(String role) {
            boolean allow;
            if (ROLE_CRUD.equals(role)) {
                allow = debugCRUD;
            } else if (ROLE_RU.equals(role)) {
                allow = debugRU;
            } else {
                allow = true;
            }

            log.info("MOCK USER ROLE - required role for user : {},  accpted : {} ", role, allow);
            return allow;
        }

        @Override
        public String getTicket() {
            return "4907489q95qgrfr8c0vmbdt8cs";
        }

        @Override
        public List<AccessRight> getRawAccessRights() {
            return null;
        }

        @Override
        public Set<String> getOrganisationsHierarchy() {
            Set<String> set = new HashSet<String>();
            set.add("99999");
            return set;
        }

        @Override
        public Set<String> getOrganisations() {
            Set<String> oids = new HashSet<String>();
            oids.add(rootOrgOid);
            return oids;
        }

        @Override
        public String getOid() {
            return "1.2.246.562.24.00000000001";
        }

        @Override
        public Locale getLang() {
            return new Locale("FI_fi");
        }

        @Override
        public Authentication getAuthentication() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
}