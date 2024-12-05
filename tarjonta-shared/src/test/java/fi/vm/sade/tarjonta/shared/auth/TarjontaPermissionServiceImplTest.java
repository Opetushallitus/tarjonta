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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.common.collect.Lists;
import fi.vm.sade.security.OidProvider;
import fi.vm.sade.security.OrganisationHierarchyAuthorizer;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl.HakujenHallintaPermissionService;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl.TPermissionService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class TarjontaPermissionServiceImplTest {

  public static final String rootOrgOid = "1";

  public static final String userOid = "nimi";
  public static final String userOrgOid = "1.2.2004.2";
  public static final String otherOrgOid = "1.2.2005.2";

  private static TarjontaPermissionServiceImpl permissionService;

  @BeforeAll
  public static void before() throws Exception {
    permissionService = new TarjontaPermissionServiceImpl();
    permissionService.rootOrgOid = rootOrgOid;
    OidProvider oidProvider = mock(OidProvider.class);
    when(oidProvider.getSelfAndParentOids(otherOrgOid))
        .thenReturn(Lists.newArrayList(rootOrgOid, otherOrgOid));
    when(oidProvider.getSelfAndParentOids(userOrgOid))
        .thenReturn(Lists.newArrayList(rootOrgOid, userOrgOid));
    when(oidProvider.getSelfAndParentOids(rootOrgOid)).thenReturn(Lists.newArrayList(rootOrgOid));
    OrganisationHierarchyAuthorizer authorizer = new OrganisationHierarchyAuthorizer(oidProvider);
    permissionService.wrapped = new TPermissionService();
    permissionService.wrapped.setAuthorizer(authorizer);

    permissionService.hakujenHallintaPermissionServiceWrapped =
        new HakujenHallintaPermissionService();
    permissionService.hakujenHallintaPermissionServiceWrapped.setAuthorizer(authorizer);

    permissionService.afterPropertiesSet();
  }

  @Test
  public void test() throws Exception {
    OrganisaatioContext c1 = OrganisaatioContext.getContext(userOrgOid); // in user hierarchy
    OrganisaatioContext c2 = OrganisaatioContext.getContext(otherOrgOid); // outside user hierarchy

    // user with no permissions
    setCurrentUser(userOid, Lists.newArrayList(Collections.EMPTY_LIST));

    assertFalse(permissionService.userCanPublishKoulutus(c1));
    assertFalse(permissionService.userCanCreateHakukohde(c1));
    assertFalse(permissionService.userCanUpdateHakukohde(c1));
    assertFalse(permissionService.userCanDeleteHakukohde(c1));
    assertFalse(permissionService.userCanCreateKoulutus(c1));
    assertFalse(permissionService.userCanUpdateKoulutus(c1));
    assertFalse(permissionService.userCanDeleteKoulutus(c1));

    // non oph user with ru
    setCurrentUser(
        userOid,
        Lists.newArrayList(
            getAuthority(
                permissionService.wrapped.ROLE_RU,
                userOrgOid,
                permissionService.hakujenHallintaPermissionServiceWrapped.ROLE_R,
                userOrgOid)));

    assertFalse(permissionService.userCanPublishKoulutus(c1));
    assertFalse(permissionService.userCanCreateHakukohde(c1));
    assertTrue(permissionService.userCanUpdateHakukohde(c1));
    assertFalse(permissionService.userCanUpdateHakukohde(c2));
    assertFalse(permissionService.userCanDeleteHakukohde(c1));
    assertFalse(permissionService.userCanCreateKoulutus(c1));
    assertTrue(permissionService.userCanUpdateKoulutus(c1));
    assertFalse(permissionService.userCanUpdateKoulutus(c2));
    assertFalse(permissionService.userCanDeleteKoulutus(c1));

    assertFalse(permissionService.userCanCreateHakuWithOrgs(userOrgOid));
    assertFalse(permissionService.userCanUpdateHakuWithOrgs(userOrgOid));
    assertFalse(permissionService.userCanDeleteHakuWithOrgs(userOrgOid));

    // non oph user with crud
    List<GrantedAuthority> authorities =
        Lists.newArrayList(getAuthority(permissionService.wrapped.ROLE_CRUD, userOrgOid));
    authorities.addAll(
        getAuthority(
            permissionService.hakujenHallintaPermissionServiceWrapped.ROLE_CRUD, userOrgOid));
    setCurrentUser(userOid, authorities);

    assertTrue(permissionService.userCanPublishKoulutus(c1));
    assertTrue(permissionService.userCanCreateHakukohde(c1));
    assertTrue(permissionService.userCanUpdateHakukohde(c1));
    assertFalse(permissionService.userCanUpdateHakukohde(c1, true));

    assertTrue(permissionService.userCanDeleteHakukohde(c1));
    assertTrue(permissionService.userCanCreateKoulutus(c1));
    assertTrue(permissionService.userCanUpdateKoulutus(c1));
    assertTrue(permissionService.userCanDeleteKoulutus(c1));
    assertTrue(permissionService.userCanCreateHaku());
    assertTrue(permissionService.userCanUpdateHaku("NONE"));
    assertTrue(permissionService.userCanDeleteHaku("NONE"));
    assertFalse(permissionService.userCanPublishHaku("NONE"));
    assertFalse(permissionService.userCanCancelHakuPublish("NONE"));

    assertTrue(permissionService.userCanCreateHakuWithOrgs(userOrgOid));
    assertTrue(permissionService.userCanUpdateHakuWithOrgs(userOrgOid));
    assertTrue(permissionService.userCanDeleteHakuWithOrgs(userOrgOid));

    // oph crud
    setCurrentUser(
        userOid,
        Lists.newArrayList(
            getAuthority(
                permissionService.wrapped.ROLE_CRUD,
                rootOrgOid,
                permissionService.hakujenHallintaPermissionServiceWrapped.ROLE_CRUD,
                rootOrgOid)));

    assertTrue(permissionService.userCanCreateKoulutus(c1));
    assertTrue(permissionService.userCanUpdateKoulutus(c1));
    assertTrue(permissionService.userCanDeleteKoulutus(c1));
    assertTrue(permissionService.userCanPublishKoulutus(c1));
    assertTrue(permissionService.userCanCreateHakukohde(c1));
    assertTrue(permissionService.userCanUpdateHakukohde(c1));
    assertTrue(permissionService.userCanUpdateHakukohde(c1, true));
    assertTrue(permissionService.userCanDeleteHakukohde(c1));
    assertTrue(permissionService.userCanCreateHaku());
    assertTrue(permissionService.userCanUpdateHaku("NONE"));
    assertTrue(permissionService.userCanDeleteHaku("NONE"));
    assertTrue(permissionService.userCanPublishHaku("NONE"));
    assertTrue(permissionService.userCanCancelHakuPublish("NONE"));

    assertTrue(permissionService.userCanCreateHakuWithOrgs(userOrgOid));
    assertTrue(permissionService.userCanUpdateHakuWithOrgs(userOrgOid));
    assertTrue(permissionService.userCanDeleteHakuWithOrgs(userOrgOid));
  }

  List<GrantedAuthority> getAuthority(String... data) {
    List<GrantedAuthority> authorities = Lists.newArrayList();
    for (int i = 0; i < data.length / 2; i++) {
      authorities.add(new SimpleGrantedAuthority(String.format("%s", data[i * 2])));
      authorities.add(
          new SimpleGrantedAuthority(String.format("%s_%s", data[i * 2], data[i * 2 + 1])));
    }
    return authorities;
  }

  static void setCurrentUser(final String oid, final List<GrantedAuthority> grantedAuthorities) {

    Authentication auth = new TestingAuthenticationToken(oid, null, grantedAuthorities);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
