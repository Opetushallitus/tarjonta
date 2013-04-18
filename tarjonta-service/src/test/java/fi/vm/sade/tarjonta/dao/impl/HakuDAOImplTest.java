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
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.SearchCriteriaType;
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
public class HakuDAOImplTest extends TestData {

    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;
    @Autowired(required = true)
    private HakuDAOImpl instance;

    public HakuDAOImplTest() {
    }

    @Before
    public void setUp() {
        em = instance.getEntityManager();
        super.initializeData(em, fixtures);
    }

    @After
    public void cleanUp() {
        super.clean();
    }

    /**
     * Test of findByOid method, of class HakuDAOImpl.
     */
    @Test
    public void testFindByOid() {
        Haku result = instance.findByOid(HAKU_OID1);
        assertEquals(haku1, result);
        assertEquals(3, result.getHakukohdes().size());

        result = instance.findByOid("none");
        assertEquals(null, result);
    }

    @Test
    public void testFindHakukohdeHakus() {
        assertEquals(2, instance.findAll().size());

        //TODO:If I have understood this correctly, it should output 3 items, not 6? 
        List<Haku> findHakukohdeHakus = instance.findHakukohdeHakus(haku1);
        assertEquals(3, findHakukohdeHakus.size());

        findHakukohdeHakus = instance.findHakukohdeHakus(haku2);
        assertEquals(0, findHakukohdeHakus.size());
    }
}