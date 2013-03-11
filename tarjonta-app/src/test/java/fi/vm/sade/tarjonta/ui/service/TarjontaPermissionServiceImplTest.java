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

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.generic.ui.feature.UserFeature;
import fi.vm.sade.generic.ui.portlet.security.UserMock;
import fi.vm.sade.security.OidProvider;
import fi.vm.sade.tarjonta.mock.UserProviderMock;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionServiceImpl.TPermissionService;

public class TarjontaPermissionServiceImplTest {

    private static final String ORG2 = "o2";
    private static final String ORG1 = "o1";

    @Test
    public void test() throws Exception {
        UserFeature.set(new UserMock()); //need to set this or authorizer gets confused
        OrganisaatioContext c1 = OrganisaatioContext.getContext(ORG1);
        OrganisaatioContext c2 = OrganisaatioContext.getContext(ORG2);

        TarjontaPermissionServiceImpl permissionService = new TarjontaPermissionServiceImpl();
        OidProvider oidProvider = EasyMock.createMock(OidProvider.class);
        EasyMock.expect(oidProvider.getSelfAndParentOids(ORG1)).andReturn(Lists.newArrayList(ORG1, "root")).anyTimes();
        EasyMock.expect(oidProvider.getSelfAndParentOids(ORG2)).andReturn(Lists.newArrayList(ORG2, "root")).anyTimes();
        EasyMock.replay(oidProvider);
        
        TPermissionService wrapped = new TPermissionService();
        CustomOrganisaatioHierarchyAuthorizer authorizer = new CustomOrganisaatioHierarchyAuthorizer(oidProvider);

        //user with no permissions
        UserProviderMock userProviderMock = getUserProvider(false, false, false, ORG1);
        
        authorizer.userProvider = userProviderMock;
        authorizer.afterPropertiesSet();
        wrapped.setAuthorizer(authorizer);
        permissionService.wrapped = wrapped;
        permissionService.afterPropertiesSet();

        Assert.assertFalse(permissionService.userCanPublishKoulutus(c1));
        Assert.assertFalse(permissionService.userCanCancelPublish(c1));
        Assert.assertFalse(permissionService.userCanCreateHakukohde(c1));
        Assert.assertFalse(permissionService.userCanUpdateHakukohde(c1));
        Assert.assertFalse(permissionService.userCanDeleteHakukohde(c1));
        Assert.assertFalse(permissionService.userCanCreateKoulutus(c1));
        Assert.assertFalse(permissionService.userCanUpdateKoulutus(c1));
        Assert.assertFalse(permissionService.userCanDeleteKoulutus(c1));

        userProviderMock = getUserProvider(false, true, false, ORG1);
        authorizer.userProvider = userProviderMock;

        Assert.assertFalse(permissionService.userCanPublishKoulutus(c1));
        Assert.assertFalse(permissionService.userCanCancelPublish(c1));
        Assert.assertFalse(permissionService.userCanCreateHakukohde(c1));
        Assert.assertTrue(permissionService.userCanUpdateHakukohde(c1));
        Assert.assertFalse(permissionService.userCanUpdateHakukohde(c2));
        Assert.assertFalse(permissionService.userCanDeleteHakukohde(c1));
        Assert.assertFalse(permissionService.userCanCreateKoulutus(c1));
        Assert.assertTrue(permissionService.userCanUpdateKoulutus(c1));
        Assert.assertFalse(permissionService.userCanUpdateKoulutus(c2));
        Assert.assertFalse(permissionService.userCanDeleteKoulutus(c1));
    }

    private UserProviderMock getUserProvider(boolean read, boolean ru, boolean crud, String org) {
        UserProviderMock userProvidderMock = new UserProviderMock();
        userProvidderMock.setDebugCRUD(crud);
        userProvidderMock.setDebugR(read);
        userProvidderMock.setDebugRU(ru);
        userProvidderMock.setUserOrgSet(org);
        return userProvidderMock;
    }

}
