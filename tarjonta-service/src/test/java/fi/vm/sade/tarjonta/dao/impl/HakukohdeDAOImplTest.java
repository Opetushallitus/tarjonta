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
package fi.vm.sade.tarjonta.dao.impl;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import static fi.vm.sade.tarjonta.dao.impl.TestData.HAKU_OID1;
import static fi.vm.sade.tarjonta.dao.impl.TestData.HAKUKOHDE_OID1;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

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
public class HakukohdeDAOImplTest extends TestData {

    @Autowired(required = true)
    private HakukohdeDAOImpl instance;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;

    public HakukohdeDAOImplTest() {
        super();
    }

    @After
    public void cleanUp() {
        super.clean();
    }

    @Before
    public void setUp() {
        em = instance.getEntityManager();
        super.initializeData(em, fixtures);
    }

    @Test
    public void testFindHakukohdeByOid() {
        Hakukohde hakukohde = instance.findHakukohdeByOid(HAKUKOHDE_OID1);
        assertNotNull(hakukohde);
        assertEquals(HAKUKOHDE_OID1, hakukohde.getOid());
        assertEquals(HAKU_OID1, hakukohde.getHaku().getOid());
        assertEquals(VALINTAKOE_COUNT_FOR_OID1, hakukohde.getValintakoes().size());
    }

    @Test
    public void testFindValintakoeByHakukohdeOid1() {
        final List result = instance.findValintakoeByHakukohdeOid(HAKUKOHDE_OID1);
        assertEquals(VALINTAKOE_COUNT_FOR_OID1, result.size());
    }

    @Test
    public void testFindValintakoeByHakukohdeOid2() {
        final List result = instance.findValintakoeByHakukohdeOid(HAKUKOHDE_OID2);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindValintakoeByHakukohdeOid3() {
        final List result = instance.findValintakoeByHakukohdeOid(HAKUKOHDE_OID3);
        assertEquals(0, result.size());
    }


    @Test
    public void testFindOidsBy() {
        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 0, null, null,true);
            assertEquals(result.size(), 3);
        }
        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.LUONNOS, 100, 0, null, null,true);
            assertEquals(result.size(), 0);
        }


        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 2, 0, null, null,true);
            assertEquals(result.size(), 2);
        }
        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 1, null, null,true);
            assertEquals(result.size(), 2);
        }

        Date d = new Date();
        {
            // TILA + date before
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 0, d, null,true);
            assertEquals(result.size(), 3);
        }
        {
            // TILA + date after
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 0, null, d,true);
            assertEquals(result.size(), 0);
        }


    }



    @Test
    public void testFindByHakuOid() {
        {
            List<String> result = instance.findByHakuOid(HAKU_OID1, null, 100, 0, null, null);
            assertEquals(result.size(), 3);
        }
        {
            List<String> result = instance.findByHakuOid(HAKU_OID2, null, 100, 0, null, null);
            assertEquals(result.size(), 0);
        }
    }


//    @Test
//    public void testHaeHakukohteetJaKoulutukset() {
//        HaeHakukohteetKyselyTyyppi tyyppi = new HaeHakukohteetKyselyTyyppi();
//        //tyyppi.setNimiKoodiUri(KOODISTO_URI_1);
//
//        tyyppi.setNimi(HUMAN_READABLE_NAME_1);
//
//        final List result = instance.haeHakukohteetJaKoulutukset(tyyppi);
//        assertEquals(1, result.size());
//    }
}