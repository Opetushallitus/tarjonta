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
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class TarjontaPermissionServiceImpl extends AbstractPermissionService implements TarjontaPermissionService {

    @Override
    protected User getUser() {
        if (TarjontaApplication.getInstance() != null) {
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
}
