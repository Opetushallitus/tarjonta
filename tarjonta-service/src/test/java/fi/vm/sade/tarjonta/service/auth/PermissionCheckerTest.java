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
package fi.vm.sade.tarjonta.service.auth;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

public class PermissionCheckerTest {

    @Test
    public void test() {
        PermissionChecker checker = new PermissionChecker();
        TarjontaPermissionServiceImpl permissionService = Mockito
                .mock(TarjontaPermissionServiceImpl.class);
        Mockito.stub(
                permissionService.userCanCopyKoulutusAsNew(Mockito
                        .any(OrganisaatioContext.class))).toReturn(true);
        Whitebox.setInternalState(checker, "permissionService",
                permissionService);
        checker.checkCopyKoulutus(Lists.newArrayList("123", "124"));
        Mockito.verify(permissionService, Mockito.times(2))
                .userCanCopyKoulutusAsNew(
                        Mockito.any(OrganisaatioContext.class));
    }

}
