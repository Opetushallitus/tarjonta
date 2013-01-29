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
import fi.vm.sade.generic.service.PermissionService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Jani Wilén
 */
public abstract class AbstractAppPermissionServiceImpl extends AbstractPermissionService implements AppPermissionService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAppPermissionServiceImpl.class);
    @Value("${debug.enable:}")
    private Boolean debugEnabled;
    @Value("${debug.role.crud:}")
    private Boolean debugCRUD;
    @Value("${debug.role.ru:}")
    private Boolean debugRU;

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

        LOG.debug("RU : " + super.userCanReadAndUpdate());

        return super.userCanReadAndUpdate();
    }

    @Override
    public boolean userCanCreateReadUpdateAndDelete() {

        if (isOverideModeEnabled(debugRU)) {
            return overideRoleByProperty(debugCRUD, "CRUD");
        }

        LOG.debug("CRUD : " + super.userCanCreateReadUpdateAndDelete());

        return super.userCanCreateReadUpdateAndDelete();
    }

    //Gets user organisation oids
    @Override
    public List<String> getUserOrganisationOids() {
        List<String> organisations = new ArrayList<String>();
        organisations.addAll(this.getUser().getOrganisations());
        return organisations;
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
