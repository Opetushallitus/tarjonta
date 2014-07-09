/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.dao.impl;

import com.google.common.base.Preconditions;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.MassakopiointiDAOTest;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.copy.EntityToJsonHelper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MassakopiointiTest extends TestData {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MassakopiointiTest.class);

    @Autowired(required = true)
    private HakukohdeDAOImpl instance;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;

    @Before
    public void setUp() {
        em = instance.getEntityManager();
        super.initializeData(em, fixtures);
    }

    @Test
    public void testKoulutusConversions() {
        String json = null;
        try {
            json = EntityToJsonHelper.convertToJson(getPersistedKomoto1());
        } catch (Exception ex) {
            fail("conversion error from entity to json : " + ex.getMessage());
        }
        assertNotNull("KoulutusmoduuliToteutus - not nullable", json);
        assertTrue("conversion to json failed", json.length() > 0 && !json.equals("null"));
        KoulutusmoduuliToteutus komoto = null;
        try {
            komoto = (KoulutusmoduuliToteutus) EntityToJsonHelper.convertToEntity(json, KoulutusmoduuliToteutus.class);
        } catch (Exception ex) {
            fail("conversion error from json to entity : " + ex.getMessage());
        }
        assertNotNull(komoto);
        assertEquals(null, komoto.getId());
        assertEquals(KOMOTO_OID_1, komoto.getOid());
    }

    @Test
    public void testHakukohdeConversions() throws IOException {
        String json = null;
        try {
            json = EntityToJsonHelper.convertToJson(kohde1);
        } catch (Exception ex) {
            fail("conversion error from entity to json : " + ex.getMessage());
        }

        assertNotNull("Hakukohde - not nullable", json);
        assertTrue("conversion to json failed", json.length() > 0 && !json.equals("null"));

        Hakukohde hakukohde = null;
        try {
            hakukohde = (Hakukohde) EntityToJsonHelper.convertToEntity(json, Hakukohde.class);
        } catch (Exception ex) {
            fail("conversion error from json to entity : " + ex.getMessage());
        }
        assertNotNull(hakukohde);
        assertEquals(null, hakukohde.getId());
        assertEquals(HAKUKOHDE_OID1, hakukohde.getOid());
    }
}
