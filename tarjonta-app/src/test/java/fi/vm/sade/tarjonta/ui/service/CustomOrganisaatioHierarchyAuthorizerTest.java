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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.security.OidProvider;
import fi.vm.sade.tarjonta.mock.UserProviderMock;

public class CustomOrganisaatioHierarchyAuthorizerTest {

    private static final String ORG_OID = "1";

    @Test
    public void test() {

        OidProvider op = EasyMock.createMock(OidProvider.class);
        expect(op.getSelfAndParentOids(ORG_OID)).andReturn(Lists.newArrayList(ORG_OID));
        replay(op);

        CustomOrganisaatioHierarchyAuthorizer a = new CustomOrganisaatioHierarchyAuthorizer(op);

        UserProviderMock mockUserProvider = new UserProviderMock();
        mockUserProvider.setUserOrgSet(ORG_OID);
        mockUserProvider.setDebugCRUD(true);

        UserProvider userProvider = EasyMock.createMock(UserProvider.class);

        a.userProvider = userProvider;
        expect(userProvider.getUser()).andReturn(mockUserProvider.getUser());
        replay(userProvider);

        a.checkAccess(null, ORG_OID, new String[]{"APP_TARJONTA_CRUD"});
        EasyMock.verify(userProvider);
    }

}
