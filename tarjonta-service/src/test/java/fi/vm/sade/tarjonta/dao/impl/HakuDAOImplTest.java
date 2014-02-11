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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.After;
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
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

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
public class HakuDAOImplTest extends TestData {

    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;
    @Autowired(required = true)
    private HakuDAOImpl dao;

    public HakuDAOImplTest() {
    }

    public void setUp() {
        em = dao.getEntityManager();
        super.initializeData(em, fixtures);
    }

    public void cleanUp() {
        super.clean();
    }

    /**
     * Test of findByOid method, of class HakuDAOImpl.
     */
    @Test
    public void testFindByOid() {
        setUp();
        Haku result = dao.findByOid(HAKU_OID1);
        assertEquals(haku1, result);
        assertEquals(3, result.getHakukohdes().size());

        result = dao.findByOid("none");
        assertEquals(null, result);
        cleanUp();
    }

    @Test
    public void testFindHakukohdeHakus() {
        setUp();
        assertEquals(2, dao.findAll().size());

        //TODO:If I have understood this correctly, it should output 3 items, not 6? 
        List<Haku> findHakukohdeHakus = dao.findHakukohdeHakus(haku1);
        assertEquals(3, findHakukohdeHakus.size());

        findHakukohdeHakus = dao.findHakukohdeHakus(haku2);
        assertEquals(0, findHakukohdeHakus.size());
        cleanUp();
    }
    
    @Test
    public void testFindHakuByCriteria(){
        super.clean();
        final String OID1="1.2.3";
        final String OID2="2.2.3";
        
        Haku h1 = new Haku();
        h1.setOid(OID1);
        h1.setKoulutuksenAlkamiskausiUri("k");
        h1.setKoulutuksenAlkamisVuosi(2014);
        h1.setTila(TarjontaTila.VALMIS);
        h1.setHakutyyppiUri("hakutyyppi1");
        h1.setHakukausiUri("k");
        h1.setHakutapaUri("hakutapa1");
        h1.setKohdejoukkoUri("kohdejoukko1");
        h1.setHakukausiVuosi(2014);
        dao.insert(h1);
        
        Haku h2 = new Haku();
        h2.setOid(OID2);
        h2.setKoulutuksenAlkamiskausiUri("s");
        h2.setKoulutuksenAlkamisVuosi(2015);
        h2.setTila(TarjontaTila.KOPIOITU);
        h2.setHakutyyppiUri("hakutyyppi2");
        h2.setHakukausiUri("s");
        h2.setHakutapaUri("hakutapa2");
        h2.setKohdejoukkoUri("kohdejoukko2");
        h2.setHakukausiVuosi(2015);
        dao.insert(h2);

        int count =0;
        int index=0;
        List<HakuSearchCriteria> criteria;
        List<String> result = dao.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        Assert.assertEquals(2, result.size());
        
        //countilla
        count =1;
        result = dao.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        Assert.assertEquals(1, result.size());

        //indexillä
        count=0;
        index=1;
        result = dao.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        Assert.assertEquals(1, result.size());
        
        count=0;
        index=0;
        
        //hakukauden vuodella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUVUOSI, 2014).build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));


        //hakukauden kaudella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUKAUSI, "k").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));


        //koulutuksen alkamisvuodella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOULUTUKSEN_ALKAMISVUOSI, 2014).build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));


        //koulutuksen alkamiskaudella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOULUTUKSEN_ALKAMISKAUSI, "k").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //hakutapa
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUTAPA, "hakutapa1").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //hakutyyppi
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUTYYPPI, "hakutyyppi1").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //kohdejoukko
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOHDEJOUKKO, "kohdejoukko1").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //tila
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.TILA, TarjontaTila.VALMIS).build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

    }

    private void assertResult(String oid, List<String> result) {
        assertEquals(1, result.size());
        assertEquals(oid, result.get(0));
    }
    
    
    
}