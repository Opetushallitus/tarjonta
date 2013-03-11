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

import org.junit.Test;

import fi.vm.sade.tarjonta.mock.UserProviderMock;

public class UserContextTest {

    @Test
    public void test() throws Exception {
        
        //"oph user"
        UserProviderMock userProvider = new UserProviderMock();
        userProvider.setUserOrgSet("3 2 1");
        userProvider.setDebugCRUD(true);
        UserContext userContext = new UserContext();
        userContext.userProvider = userProvider;
        userContext.rootOrgOid = "1";
        Assert.assertFalse(userContext.isDoAutoSearch());
        Assert.assertTrue(userContext.isOphUser());

        //non "oph user" with multiple orgs
        userProvider = new UserProviderMock();
        userProvider.setUserOrgSet("2 3 4");
        userContext.userProvider = userProvider;
        Assert.assertFalse(userContext.isDoAutoSearch());
        Assert.assertFalse(userContext.isOphUser());

        //non "oph user" with single org
        userProvider = new UserProviderMock();
        userProvider.setUserOrgSet("2");
        userContext.userProvider = userProvider;
        Assert.assertTrue(userContext.isDoAutoSearch());
        Assert.assertFalse(userContext.isOphUser());
}

}
