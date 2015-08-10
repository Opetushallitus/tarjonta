/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.service.tasks.KoulutusPermissionSynchronizer;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOrgDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusPermissionTest {

    @Autowired
    KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    KoulutusPermissionSynchronizer koulutusPermissionSynchronizer;

    @Before
    public void setUp() {
        koulutusPermissionDAO.removeAll();
    }

    @Test
    public void testThatPermissionsAreFoundInDb() {
        KoulutusPermission permission = new KoulutusPermission("org1", "koulutus", "koulutus_1", null, null);
        koulutusPermissionDAO.insert(permission);

        List<KoulutusPermission> matchingPermissions = koulutusPermissionDAO.find(Lists.newArrayList("org1"), "koulutus", "koulutus_1", null, null);
        assertEquals(1, matchingPermissions.size());

        KoulutusPermission firstMatch = matchingPermissions.iterator().next();
        assertEquals("org1", firstMatch.getOrgOid());
        assertEquals("koulutus", firstMatch.getKoodisto());
        assertEquals("koulutus_1", firstMatch.getKoodiUri());
        assertNull(firstMatch.getAlkuPvm());
        assertNull(firstMatch.getLoppuPvm());

        matchingPermissions = koulutusPermissionDAO.find(Lists.newArrayList("org1"), "koulutus", "koulutus_2", null, null);
        assertEquals(0, matchingPermissions.size());
    }

    @Test
    public void testThatPermissionsAreUpdated() throws IOException {
        // This permission should be removed after update
        KoulutusPermission permission = new KoulutusPermission("org1", "koulutus", "koulutus_1", null, null);
        koulutusPermissionDAO.insert(permission);
        List<KoulutusPermission> beforeUpdatePermission = koulutusPermissionDAO.find(Lists.newArrayList("org1"), "koulutus", "koulutus_1", null, null);
        assertEquals(1, beforeUpdatePermission.size());

        ObjectMapper objectMapper = new ObjectMapper();
        List<AmkouteOrgDTO> orgs = objectMapper.readValue(
                new File("src/test/java/fi/vm/sade/tarjonta/service/impl/resources/v1/amkouteTestData.json"),
                new TypeReference<List<AmkouteOrgDTO>>() {}
        );
        assertEquals(5, orgs.size());

        koulutusPermissionSynchronizer.updatePermissionsToDb(orgs);

        // Update should have removed old permission
        List<KoulutusPermission> deletedPermission = koulutusPermissionDAO.find(Lists.newArrayList("org1"), "koulutus", "koulutus_1", null, null);
        assertEquals(0, deletedPermission.size());

        // And inserted all permissions from JSON
        List<KoulutusPermission> permissions = koulutusPermissionDAO.getAll();
        assertEquals(78, permissions.size());
    }

}
