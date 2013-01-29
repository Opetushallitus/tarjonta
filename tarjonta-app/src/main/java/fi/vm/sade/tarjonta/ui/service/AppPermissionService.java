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

import java.util.List;

import fi.vm.sade.generic.service.PermissionService;

/**
 *
 * @author Jani Wilén
 */
public interface AppPermissionService extends PermissionService {

    public static final String SERVICE = "APP_TARJONTA";
    public static final String ROLE_CRUD = SERVICE + "_CRUD";
    public static final String ROLE_RU = SERVICE + "_READ_UPDATE";
    public static final String ROLE_R = SERVICE + "_READ";
    
    public List<String> getUserOrganisationOids(); 
}
