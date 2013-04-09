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

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
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
public class KoulutusmoduuliDAOImplTest {

    private static final String LUKIOLINJA_URI = "uri_lukiolinja";
    private static final String KOULUTUSOHJELMA_URI = "uri_koulutusohjelma";
    @Autowired(required = true)
    private KoulutusmoduuliDAOImpl instance;
    private Koulutusmoduuli komo1, komo2, komo3;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;

    @After
    public void cleanUp() {
        remove(komo1);
        remove(komo2);
        remove(komo3);
    }

    @Before
    public void setUp() {
        em = instance.getEntityManager();
        komo1 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo1.setOppilaitostyyppi("uri_1#uri_2#uri_170#");
        persist(komo1);

        komo2 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo2.setOppilaitostyyppi("uri_a#");
        komo2.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        persist(komo2);

        komo3 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo3.setOppilaitostyyppi("uri_fuu#");
        komo3.setLukiolinja(LUKIOLINJA_URI);
        persist(komo3);
    }

    /**
     * Test of search method, of class KoulutusmoduuliDAOImpl.
     */
    @Test
    public void testSearchByOppilaitostyyppi() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.getOppilaitostyyppis().add("uri_170");
        List result = instance.search(criteria);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchWithEmptyCriteria() {
        List result = instance.search(new KoulutusmoduuliDAO.SearchCriteria());
        assertEquals(3, result.size());
    }

    @Test
    public void testSearchByLukiolinja() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setLukiolinjaKoodiUri(LUKIOLINJA_URI);
        List result = instance.search(criteria);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByKoulutusohjelma() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        List result = instance.search(criteria);
        assertEquals(1, result.size());
    }


    /*
     *
     * Private helpper methods
     * 
     */
    private void persist(Object o) {
        em.persist(o);
        em.flush();
        em.detach(o);

        //a quick check
        if (o instanceof Koulutusmoduuli) {
            Koulutusmoduuli v = (Koulutusmoduuli) o;
            Koulutusmoduuli find = em.find(Koulutusmoduuli.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else {
            fail("Found an unknown object type : " + o.toString());
        }
    }

    private void remove(Object o) {
        if (o instanceof Koulutusmoduuli) {
            em.remove(em.find(Koulutusmoduuli.class, ((Koulutusmoduuli) o).getId()));
        }
    }
}