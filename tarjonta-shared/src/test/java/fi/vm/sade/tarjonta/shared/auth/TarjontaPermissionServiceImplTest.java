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
package fi.vm.sade.tarjonta.shared.auth;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

import fi.vm.sade.security.OidProvider;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl.TPermissionService;

public class TarjontaPermissionServiceImplTest {

    public String rootOrgOid ="1";

    public static final String userOid = "nimi";
    public static final String userOrgOid = "1.2.2004.2";
    public static final String otherOrgOid = "1.2.2005.2";

    private TarjontaPermissionServiceImpl permissionService;

    @Before
    public void before() throws Exception{
        permissionService = new TarjontaPermissionServiceImpl();
        permissionService.rootOrgOid=rootOrgOid;
        OidProvider oidProvider = Mockito.mock(OidProvider.class);
        Mockito.stub(oidProvider.getSelfAndParentOids(otherOrgOid)).toReturn(
                Lists.newArrayList(rootOrgOid, otherOrgOid));
        Mockito.stub(oidProvider.getSelfAndParentOids(userOrgOid)).toReturn(
                Lists.newArrayList(rootOrgOid, userOrgOid));
        Mockito.stub(oidProvider.getSelfAndParentOids(rootOrgOid)).toReturn(
                Lists.newArrayList(rootOrgOid));
        OrganisationHierarchyAuthorizer authorizer = new OrganisationHierarchyAuthorizer(oidProvider);
        permissionService.wrapped= new TPermissionService();
        permissionService.wrapped.setAuthorizer(authorizer);

        permissionService.afterPropertiesSet();
    }
    @Test
    public void test() throws Exception {
        OrganisaatioContext c1 = OrganisaatioContext.getContext(userOrgOid); //in user hierarchy
        OrganisaatioContext c2 = OrganisaatioContext.getContext(otherOrgOid); //outside user hierarchy

        //user with no permissions
        setCurrentUser(userOid, Lists.newArrayList(Collections.EMPTY_LIST));

        Assert.assertFalse(permissionService.userCanPublishKoulutus(c1));
        Assert.assertFalse(permissionService.userCanCancelKoulutusPublish(c1));
        Assert.assertFalse(permissionService.userCanCreateHakukohde(c1));
        Assert.assertFalse(permissionService.userCanUpdateHakukohde(c1));
        Assert.assertFalse(permissionService.userCanDeleteHakukohde(c1));
        Assert.assertFalse(permissionService.userCanCreateKoulutus(c1));
        Assert.assertFalse(permissionService.userCanUpdateKoulutus(c1));
        Assert.assertFalse(permissionService.userCanDeleteKoulutus(c1));

        //non oph user with ru
        setCurrentUser(userOid, Lists.newArrayList(getAuthority(
                permissionService.wrapped.ROLE_RU, userOrgOid)));

        Assert.assertFalse(permissionService.userCanPublishKoulutus(c1));
        Assert.assertFalse(permissionService.userCanCancelKoulutusPublish(c1));
        Assert.assertFalse(permissionService.userCanCreateHakukohde(c1));
        Assert.assertTrue(permissionService.userCanUpdateHakukohde(c1));
        Assert.assertFalse(permissionService.userCanUpdateHakukohde(c2));
        Assert.assertFalse(permissionService.userCanDeleteHakukohde(c1));
        Assert.assertFalse(permissionService.userCanCreateKoulutus(c1));
        Assert.assertTrue(permissionService.userCanUpdateKoulutus(c1));
        Assert.assertFalse(permissionService.userCanUpdateKoulutus(c2));
        Assert.assertFalse(permissionService.userCanDeleteKoulutus(c1));

        //non oph user with crud
        setCurrentUser(userOid, Lists.newArrayList(getAuthority(
                permissionService.wrapped.ROLE_CRUD, userOrgOid)));

        Assert.assertTrue(permissionService.userCanPublishKoulutus(c1));
        Assert.assertTrue(permissionService.userCanCancelKoulutusPublish(c1));
        Assert.assertTrue(permissionService.userCanCreateHakukohde(c1));
        Assert.assertTrue(permissionService.userCanUpdateHakukohde(c1));
        Assert.assertTrue(permissionService.userCanDeleteHakukohde(c1));
        Assert.assertTrue(permissionService.userCanCreateKoulutus(c1));
        Assert.assertTrue(permissionService.userCanUpdateKoulutus(c1));
        Assert.assertTrue(permissionService.userCanDeleteKoulutus(c1));
        Assert.assertFalse(permissionService.userCanCreateHaku());
        Assert.assertFalse(permissionService.userCanUpdateHaku());
        Assert.assertFalse(permissionService.userCanDeleteHaku());
        Assert.assertFalse(permissionService.userCanPublishHaku());
        Assert.assertFalse(permissionService.userCanCancelHakuPublish());

        //oph crud
        setCurrentUser(userOid, Lists.newArrayList(getAuthority(
                permissionService.wrapped.ROLE_CRUD, rootOrgOid)));

        Assert.assertTrue(permissionService.userCanCreateKoulutus(c1));
        Assert.assertTrue(permissionService.userCanUpdateKoulutus(c1));
        Assert.assertTrue(permissionService.userCanDeleteKoulutus(c1));
        Assert.assertTrue(permissionService.userCanPublishKoulutus(c1));
        Assert.assertTrue(permissionService.userCanCancelKoulutusPublish(c1));
        Assert.assertTrue(permissionService.userCanCreateHakukohde(c1));
        Assert.assertTrue(permissionService.userCanUpdateHakukohde(c1));
        Assert.assertTrue(permissionService.userCanDeleteHakukohde(c1));
        Assert.assertTrue(permissionService.userCanCreateHaku());
        Assert.assertTrue(permissionService.userCanUpdateHaku());
        Assert.assertTrue(permissionService.userCanDeleteHaku());
        Assert.assertTrue(permissionService.userCanPublishHaku());
        Assert.assertTrue(permissionService.userCanCancelHakuPublish());
    }
    
    List<GrantedAuthority> getAuthority(String appPermission, String oid) {
        GrantedAuthority orgAuthority = new SimpleGrantedAuthority(String.format("%s", appPermission));
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(String.format("%s_%s", appPermission, oid));
        return Lists.newArrayList(orgAuthority, roleAuthority);
    }
    
    static void setCurrentUser(final String oid, final List<GrantedAuthority> grantedAuthorities) {
        
        Authentication auth = new TestingAuthenticationToken(oid, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
}