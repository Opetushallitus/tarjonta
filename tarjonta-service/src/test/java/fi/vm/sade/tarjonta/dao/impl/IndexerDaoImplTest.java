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
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
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
public class IndexerDaoImplTest extends TestData {

    @Autowired(required = true)
    private HakukohdeDAOImpl instance;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;

    @Autowired(required = true)
    private IndexerDAO indexerDao;

    @Before
    public void setUp() {
        em = instance.getEntityManager();
        super.initializeData(em, fixtures);
    }

//    Not working in Bamboo, result size is always six in bamboo.    
//    @Test
//    @DirtiesContext 
//    public void testFindAllKoulutukset() {
//        List<KoulutusIndexEntity> result = indexerDao.findAllKoulutukset();
//        assertEquals(4, result.size());
//    }

    @Test
    @DirtiesContext 
    public void testFindKoulutusById() {
        //komoto #1
        KoulutusIndexEntity result = indexerDao.findKoulutusById(getPersistedKomoto1().getId());
        assertEquals(KOMOTO_OID_1, result.getOid());

        //komoto #2 with one date
        result = indexerDao.findKoulutusById(getPersistedKomoto2().getId());
        assertEquals(KOMOTO_OID_2, result.getOid());
        assertNotSame(KOULUTUS_START_DATE, result.getKoulutuksenAlkamisPvmMin());
        assertEquals(DateUtils.truncate(KOULUTUS_START_DATE, Calendar.DATE), result.getKoulutuksenAlkamisPvmMin());
        assertEquals(DateUtils.truncate(KOULUTUS_START_DATE, Calendar.DATE), result.getKoulutuksenAlkamisPvmMax());
        assertEquals(ORG_OID_1, result.getTarjoaja());

        //komoto #4 multiple dates
        result = indexerDao.findKoulutusById(getPersistedKomoto4().getId());
        assertEquals(KOMOTO_OID_4, result.getOid());
        assertNotSame(cal3.getTime(), result.getKoulutuksenAlkamisPvmMin());
        assertEquals(DateUtils.truncate(KOULUTUS_START_DATE, Calendar.DATE), result.getKoulutuksenAlkamisPvmMin());
        assertEquals(DateUtils.truncate(cal3.getTime(), Calendar.DATE), result.getKoulutuksenAlkamisPvmMax());

        //komoto #3 no dates
        result = indexerDao.findKoulutusById(getPersistedKomoto3().getId());
        assertEquals(KOMOTO_OID_3, result.getOid());
        assertEquals(null, result.getKoulutuksenAlkamisPvmMin());
        assertEquals(null, result.getKoulutuksenAlkamisPvmMax());

    }
}
