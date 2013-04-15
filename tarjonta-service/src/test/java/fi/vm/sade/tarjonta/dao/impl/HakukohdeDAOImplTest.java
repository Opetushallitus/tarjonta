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
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.Valintakoe;
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
public class HakukohdeDAOImplTest {
    
    private static final int VALINTAKOE_COUNT_FOR_OID1 = 3;
    private static final String OID1 = "oid_1";
    private static final String OID2 = "oid_2";
    private static final String OID3 = "oid_3";
    private static final String HUMAN_READABLE_NAME_1 = "human_readable_name";
    private static final String KOODISTO_URI_1 = "koodisto_uri";
    private Hakukohde kohde1;
    private Hakukohde kohde2;
    private Hakukohde kohde3;
    private Valintakoe koe1;
    private Valintakoe koe2;
    private Valintakoe koe3;
    private Valintakoe koe4;
    private Haku haku;
    @Autowired(required = true)
    private HakukohdeDAOImpl instance;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;
    
    public HakukohdeDAOImplTest() {
    }
    
    @After
    public void cleanUp() {
        remove(koe1);
        remove(koe2);
        remove(koe3);
        remove(koe4);
        
        remove(kohde1);
        remove(kohde2);
        remove(kohde3);
        
        remove(haku);
    }
    
    @Before
    public void setUp() {
        em = instance.getEntityManager();
        Koulutusmoduuli komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        persist(komo);
        
        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus("123455");
        komoto.setKoulutusmoduuli(komo);
        komo.addKoulutusmoduuliToteutus(komoto);
        
        persist(komoto);
        
        haku = fixtures.createHaku();
        persist(haku);
        /*
         * CREATE OBJECTS
         */
        
        kohde1 = fixtures.createHakukohde();
        kohde1.setHakukohdeKoodistoNimi(HUMAN_READABLE_NAME_1);
        kohde1.setHakukohdeNimi(KOODISTO_URI_1);
        kohde1.setOid(OID1); //three exams

        kohde2 = fixtures.createHakukohde();
        kohde2.setOid(OID2); //one exam

        kohde3 = fixtures.createHakukohde();
        kohde3.setOid(OID3); //no exams

        koe1 = fixtures.createValintakoe();
        koe2 = fixtures.createValintakoe();
        koe3 = fixtures.createValintakoe();
        koe4 = fixtures.createValintakoe();

        /* 
         * DATA MAPPING 
         */
        kohde1.addKoulutusmoduuliToteutus(komoto);
        komoto.addHakukohde(kohde1);
        
        kohde1.setHaku(haku);
        kohde2.setHaku(haku);
        kohde3.setHaku(haku);
        
        haku.addHakukohde(kohde1);
        haku.addHakukohde(kohde2);
        haku.addHakukohde(kohde3);

        /* 
         * PERSIST ALL OBJECTS 
         */

        //hakukohde1 <- koe1, koe2, koe3
        kohde1.addValintakoe(koe1);
        kohde1.addValintakoe(koe2);
        kohde1.addValintakoe(koe3);

        //hakukohde2 <- koe4
        kohde2.addValintakoe(koe4);
        
        persist(kohde1);
        persist(kohde2);
        persist(kohde3);
        
        check(3, kohde1);
        check(1, kohde2);
        check(0, kohde3);
        
        
        
    }
    
    @Test
    public void testFindHakukohdeByOid() {
        Hakukohde hakukohde = instance.findHakukohdeByOid(OID1);
        assertNotNull(hakukohde);
        assertEquals(OID1, hakukohde.getOid());
        assertEquals(VALINTAKOE_COUNT_FOR_OID1, hakukohde.getValintakoes().size());
    }
    
    @Test
    public void testFindValintakoeByHakukohdeOid1() {
        final List result = instance.findValintakoeByHakukohdeOid(OID1);
        assertEquals(VALINTAKOE_COUNT_FOR_OID1, result.size());
    }
    
    @Test
    public void testFindValintakoeByHakukohdeOid2() {
        final List result = instance.findValintakoeByHakukohdeOid(OID2);
        assertEquals(1, result.size());
        
    }
    
    @Test
    public void testFindValintakoeByHakukohdeOid3() {
        final List result = instance.findValintakoeByHakukohdeOid(OID3);
        assertEquals(0, result.size());
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

    /*
     *
     * Private helpper methods
     * 
     */
    private void check(int items, Hakukohde kohde) {
        Hakukohde k = em.find(Hakukohde.class, kohde.getId());
        em.detach(k);
        assertEquals(items, k.getValintakoes().size());
    }
    
    private void remove(Object o) {
        if (o instanceof Hakukohde) {
            em.remove(em.find(Hakukohde.class, ((Hakukohde) o).getId()));
        } else if (o instanceof Valintakoe) {
            em.remove(em.find(Valintakoe.class, ((Valintakoe) o).getId()));
        } else if (o instanceof Haku) {
            em.remove(em.find(Haku.class, ((Haku) o).getId()));
        }
    }
    
    private void persist(Object o) {
        em.persist(o);
        em.flush();
        em.detach(o);

        //a quick check
        if (o instanceof Haku) {
            Haku haku = (Haku) o;
            assertNotNull(em.find(Haku.class, haku.getId()));
        } else if (o instanceof Hakukohde) {
            Hakukohde h = (Hakukohde) o;
            assertNotNull(em.find(Hakukohde.class, h.getId()));
        } else if (o instanceof Valintakoe) {
            Valintakoe v = (Valintakoe) o;
            Valintakoe find = em.find(Valintakoe.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else if (o instanceof KoulutusmoduuliToteutus) {
            KoulutusmoduuliToteutus v = (KoulutusmoduuliToteutus) o;
            KoulutusmoduuliToteutus find = em.find(KoulutusmoduuliToteutus.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else if (o instanceof Koulutusmoduuli) {
            Koulutusmoduuli v = (Koulutusmoduuli) o;
            Koulutusmoduuli find = em.find(Koulutusmoduuli.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else {
            fail("Found an unknown object type : " + o.toString());
        }
    }
}