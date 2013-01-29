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
package fi.vm.sade.tarjonta.ui.view;

import fi.vm.sade.generic.service.PermissionService;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.service.AppPermissionService;


/**
 *
 * @author jani
 */
public interface ICommonResource {

    public boolean isSaveButtonEnabled(final String oid, final SisaltoTyyppi sisalto, final TarjontaTila... requiredState);

    public void showNotification(final UserNotification msg);

    public void showMainDefaultView();

    public AppPermissionService getPermission();

    public void changeStateToCancelled(final String oid, final SisaltoTyyppi sisalto);

    public void changeStateToPublished(final String oid, final SisaltoTyyppi sisalto);
}
