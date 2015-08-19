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
import fi.vm.sade.generic.service.exception.NotAuthorizedException;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionException;
import fi.vm.sade.tarjonta.service.impl.aspects.KoulutusPermissionService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.tasks.KoulutusPermissionSynchronizer;
import fi.vm.sade.tarjonta.shared.amkouteDTO.AmkouteOrgDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusPermissionTest {

    static final String ORG_OID = "org1";

    @Autowired
    KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    KoulutusPermissionSynchronizer koulutusPermissionSynchronizer;

    OrganisaatioService organisaatioServiceMock;

    @Autowired
    KoulutusPermissionService koulutusPermissionService;

    @Before
    public void setUp() {
        organisaatioServiceMock = createMock(OrganisaatioService.class);
        Whitebox.setInternalState(koulutusPermissionService, "organisaatioService", organisaatioServiceMock);
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
                new TypeReference<List<AmkouteOrgDTO>>() {
                }
        );
        assertEquals(5, orgs.size());

        koulutusPermissionSynchronizer.updatePermissionsToDb(orgs);

        // Update should have removed old permission
        List<KoulutusPermission> deletedPermission = koulutusPermissionDAO.find(Lists.newArrayList("org1"), "koulutus", "koulutus_1", null, null);
        assertEquals(0, deletedPermission.size());

        // And inserted all permissions from JSON
        List<KoulutusPermission> permissions = koulutusPermissionDAO.getAll();
        assertEquals(107, permissions.size());

        permissions = koulutusPermissionDAO.find(Lists.newArrayList("1.2.246.562.10.354067406510"), "koulutus", "koulutus_381113", null, null);
        assertEquals(1, permissions.size());
    }

    @Test(expected = KoulutusPermissionException.class)
    public void testThatIsNotAllowedWhenKuntaIsNotPermitted() {
        expectOrganization();
        KoulutusV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto.getOrganisaatio().setOid(ORG_OID);
        koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(dto);
    }

    @Test(expected = KoulutusPermissionException.class)
    public void testThatIsNotAllowedWhenKoulutusIsNotPermitted() {
        insertKunta();
        expectOrganization();
        KoulutusV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto.getOrganisaatio().setOid(ORG_OID);
        dto.setKoulutuskoodi(createCode("koulutus_1"));
        koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(dto);
    }

    @Test(expected = KoulutusPermissionException.class)
    public void testThatIsNotAllowedWhenLanguageIsNotPermitted() {
        insertKunta();

        KoulutusPermission kieliFiPermission = new KoulutusPermission(ORG_OID, "kieli", "kieli_fi", null, null);
        koulutusPermissionDAO.insert(kieliFiPermission);
        KoulutusPermission kieliSvPermission = new KoulutusPermission(ORG_OID, "kieli", "kieli_sv", null, null);
        koulutusPermissionDAO.insert(kieliSvPermission);
        KoulutusPermission permission = new KoulutusPermission(ORG_OID, "koulutus", "koulutus_1", null, null);
        koulutusPermissionDAO.insert(permission);

        expectOrganization();
        KoulutusV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto.getOrganisaatio().setOid(ORG_OID);
        dto.setKoulutuskoodi(createCode("koulutus_1"));
        dto.setOpetuskielis(getOpetuskielis(Lists.newArrayList("kieli_fi", "kieli_en")));

        koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(dto);
    }

    @Test(expected = KoulutusPermissionException.class)
    public void testThatIsNotAllowedWhenOsaamisalaIsNotPermitted() {
        insertKunta();

        KoulutusPermission koulutusPermission = new KoulutusPermission(ORG_OID, "koulutus", "koulutus_1", null, null);
        koulutusPermissionDAO.insert(koulutusPermission);
        KoulutusPermission kieliPermission = new KoulutusPermission(ORG_OID, "kieli", "kieli_fi", null, null);
        koulutusPermissionDAO.insert(kieliPermission);

        expectOrganization();
        KoulutusV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto.getOrganisaatio().setOid(ORG_OID);
        dto.setKoulutuskoodi(createCode("koulutus_1"));
        dto.setOpetuskielis(getOpetuskielis(Lists.newArrayList("kieli_fi")));
        dto.setKoulutusohjelma(createOsaamisalaCode("osaamisala_1"));

        koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(dto);
    }

    @Test
    public void testThatIsAllowedToOrganize() {
        insertKunta();

        KoulutusPermission koulutusPermission = new KoulutusPermission(ORG_OID, "koulutus", "koulutus_1", null, null);
        koulutusPermissionDAO.insert(koulutusPermission);
        KoulutusPermission kieliFiPermission = new KoulutusPermission(ORG_OID, "kieli", "kieli_fi", null, null);
        koulutusPermissionDAO.insert(kieliFiPermission);
        KoulutusPermission kieliSvPermission = new KoulutusPermission(ORG_OID, "kieli", "kieli_sv", null, null);
        koulutusPermissionDAO.insert(kieliSvPermission);
        KoulutusPermission osaamisalaPermission = new KoulutusPermission(ORG_OID, "osaamisala", "osaamisala_1", null, null);
        koulutusPermissionDAO.insert(osaamisalaPermission);

        expectOrganization();
        KoulutusV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto.getOrganisaatio().setOid(ORG_OID);
        dto.setKoulutuskoodi(createCode("koulutus_1"));
        dto.setOpetuskielis(getOpetuskielis(Lists.newArrayList("kieli_fi", "kieli_sv")));
        dto.setKoulutusohjelma(createOsaamisalaCode("osaamisala_1"));

        koulutusPermissionService.checkThatOrganizationIsAllowedToOrganizeEducation(dto);
    }

    private KoodiV1RDTO createCode(String uri) {
        KoodiV1RDTO koodi = new KoodiV1RDTO();
        koodi.setUri(uri);
        return koodi;
    }

    private NimiV1RDTO createOsaamisalaCode(String uri) {
        NimiV1RDTO code = new NimiV1RDTO();
        code.setUri(uri);
        return code;
    }

    private void expectOrganization() {
        OrganisaatioDTO orgDto = new OrganisaatioDTO();
        orgDto.setOid(ORG_OID);
        orgDto.setParentOidPath("orgRoot|orgChild1|orgChild2|orgChild3");
        orgDto.setKotipaikka("kunta_1");

        expect(organisaatioServiceMock.findByOid(ORG_OID)).andReturn(orgDto);
        replay(organisaatioServiceMock);
    }

    private void insertKunta() {
        KoulutusPermission permission = new KoulutusPermission(ORG_OID, "kunta", "kunta_1", null, null);
        koulutusPermissionDAO.insert(permission);
    }

    private KoodiUrisV1RDTO getOpetuskielis(List<String> opetuskielet) {
        KoodiUrisV1RDTO uris = new KoodiUrisV1RDTO();
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (String opetuskieli : opetuskielet) {
            map.put(opetuskieli, 1);
        }

        uris.setUris(map);

        return uris;
    }

}
