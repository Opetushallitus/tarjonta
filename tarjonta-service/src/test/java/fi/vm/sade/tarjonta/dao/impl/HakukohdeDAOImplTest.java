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
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@Transactional
public class HakukohdeDAOImplTest extends TestData {

    @Autowired
    private HakukohdeDAOImpl instance;

    @Autowired
    private TarjontaFixtures fixtures;

    public HakukohdeDAOImplTest() {
        super();
    }

    @After
    public void cleanUp() {
        super.clean();
    }

    @Before
    public void setUp() {
        cleanDb();
        EntityManager em = instance.getEntityManager();
        super.initializeData(em, fixtures);
    }

    private void cleanDb() {
        for (Hakukohde hakukohde : instance.findAll()) {
            instance.remove(hakukohde);
        }
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
        final List<Valintakoe> result = instance.findValintakoeByHakukohdeOid(HAKUKOHDE_OID1);
        assertEquals(VALINTAKOE_COUNT_FOR_OID1, result.size());
    }

    @Test
    public void testFindValintakoeByHakukohdeOid2() {
        final List<Valintakoe> result = instance.findValintakoeByHakukohdeOid(HAKUKOHDE_OID2);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindValintakoeByHakukohdeOid3() {
        final List<Valintakoe> result = instance.findValintakoeByHakukohdeOid(HAKUKOHDE_OID3);
        assertEquals(0, result.size());
    }


    @Test
    public void testFindOidsBy() {
        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 0, null, null,true);
            assertEquals(4, result.size());
        }
        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.LUONNOS, 100, 0, null, null,true);
            assertEquals(0, result.size());
        }


        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 2, 0, null, null,true);
            assertEquals(2, result.size());
        }
        {
            // TILA
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 1, null, null,true);
            assertEquals(3, result.size());
        }

        Date d = new Date();
        {
            // TILA + date before
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 0, d, null,true);
            assertEquals(4, result.size());
        }
        {
            // TILA + date after
            List<String> result = instance.findOIDsBy(TarjontaTila.VALMIS, 100, 0, null, d,true);
            assertEquals(0, result.size());
        }


    }

    @Test
    public void testFindByHakuOid() {
        {
            List<String> result = instance.findByHakuOid(HAKU_OID1, null, 100, 0, null, null);
            assertEquals(1, result.size());
        }
        {
            List<String> result = instance.findByHakuOid(HAKU_OID2, null, 100, 0, null, null);
            assertEquals(0, result.size(), 0);
        }
    }

    @Test
    public void testFindYlioppilastutkintoAntaaHakukelpoisuuden() {
        // Kun haun asetus = false, mutta yhdellä hakukohteella = true
        List<String> hakukohdeOids = instance.findHakukohteetWithYlioppilastutkintoAntaaHakukelpoisuuden(haku1.getId(), false);
        assertEquals(hakukohdeOids.size(), 1);
        assertEquals(hakukohdeOids.iterator().next(), HAKUKOHDE_OID1);

        // Kun haun asetus = true, mutta yhdellä hakukohteella = false
        hakukohdeOids = instance.findHakukohteetWithYlioppilastutkintoAntaaHakukelpoisuuden(haku1.getId(), true);
        assertEquals(hakukohdeOids.size(), 3);
    }
    
    @Test
    public void testXSSFiltering(){
        //TODO test more fields...
        
        Hakukohde hk = fixtures.createHakukohde();
        Haku haku = fixtures.createPersistedHaku();
        hk.setHaku(haku);
        hk.setOid("xss-1");
        MonikielinenTeksti teksti = new MonikielinenTeksti("fi","pure teksti", "en", "joku & merkki");
        
        hk.setHakukelpoisuusVaatimusKuvaus(teksti);
        instance.insert(hk);
        
        hk = instance.findBy("oid", "xss-1").get(0);
        Map<String, String> values = hk.getHakukelpoisuusVaatimusKuvaus().asMap();
        Assert.assertEquals("pure teksti", values.get("fi"));
        Assert.assertEquals("joku &amp; merkki", values.get("en"));
    }


    @Test
    public void testFindHakukohdeWithPohjakoulutusvaatimus(){
        Hakukohde hakukohde = instance.findHakukohdeByOid(HAKUKOHDE_OID4);
        assertNotNull(hakukohde);
        assertEquals(HAKUKOHDE_OID4, hakukohde.getOid());
        assertEquals(HAKU_OID1, hakukohde.getHaku().getOid());
        assertEquals("pohjakoulutusvaatimus_pk", hakukohde.getPohjakoulutusvaatimusKoodiUri());
    }
}
