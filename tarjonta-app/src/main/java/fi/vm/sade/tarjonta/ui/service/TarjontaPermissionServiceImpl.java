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

import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.generic.ui.portlet.security.User;
import fi.vm.sade.tarjonta.ui.TarjontaApplication;
import fi.vm.sade.tarjonta.ui.TarjontaWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class TarjontaPermissionServiceImpl extends AbstractPermissionService implements TarjontaPermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWebApplication.class);
    @Value("${debug.enable:}")
    private Boolean debugEnabled;
    @Value("${debug.role.crud:}")
    private Boolean debugCRUD;
    @Value("${debug.role.ru:}")
    private Boolean debugRU;

    @Override
    protected User getUser() {
        if (TarjontaApplication.getInstance() != null) {

            if (TarjontaApplication.getInstance().getUser() != null) {
                LOG.info("USER OID : " + TarjontaApplication.getInstance().getUser().getOid());
            }
            return TarjontaApplication.getInstance().getUser();
        }

        throw new RuntimeException("Access denied - no Liferay user found.");
    }

    protected boolean userIsMemberOfOrganisation(final String organisaatioOid) {
        return getUser().getOrganisationsHierarchy().contains(organisaatioOid);
    }

    @Override
    protected String getReadRole() {
        return ROLE_R;
    }

    @Override
    protected String getReadUpdateRole() {
        return ROLE_RU;
    }

    @Override
    protected String getCreateReadUpdateDeleteRole() {
        return ROLE_CRUD;
    }

    @Override
    public boolean userCanRead() {
        return getUser().isUserInRole(getReadRole()) || userCanReadAndUpdate() || userCanCreateReadUpdateAndDelete();
    }

    @Override
    public boolean userCanReadAndUpdate() {

        if (isOverideModeEnabled(debugRU)) {
            return overideRoleByProperty(debugRU, "RU");
        }

        LOG.info("RU : " + super.userCanReadAndUpdate());

        return super.userCanReadAndUpdate();
    }

    @Override
    public boolean userCanCreateReadUpdateAndDelete() {

        if (isOverideModeEnabled(debugRU)) {
            return overideRoleByProperty(debugCRUD, "CRUD");
        }

        LOG.info("CRUD : " + super.userCanCreateReadUpdateAndDelete());

        return super.userCanCreateReadUpdateAndDelete();
    }

    private boolean isOverideModeEnabled(final Boolean role) {
        return debugEnabled != null && debugEnabled && role != null;
    }

    private boolean overideRoleByProperty(final Boolean role, String roleName) {
        LOG.warn("DEBUG MODE ENABLED - APPLICATION DO NOT USE PORTAL USER ROLES!");
        LOG.debug(roleName + " : " + role);
        return role;
    }
}
