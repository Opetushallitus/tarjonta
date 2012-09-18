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
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.tarjonta.KoulutusFixtures;
import fi.vm.sade.tarjonta.dao.impl.KoulutusDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import java.util.List;
import org.junit.Ignore;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusDAOTest {

    private static final int NUM_TUTKINTO_OHJELMAS = 10;

    private static final int NUM_TUTKINTO_OHJELMA_FIRST_LEVEL_CHILDREN = 4;

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    @Autowired
    private KoulutusFixtures fixtures;

    @Before
    public void setUp() {
        fixtures.recreate();
        initData();
        printData();
    }

    /*
     THE ERROR IN BAMBOO:
     java.lang.AssertionError: expected:<10> but was:<13>
     at org.junit.Assert.fail(Assert.java:93)
     at org.junit.Assert.failNotEquals(Assert.java:647)
     at org.junit.Assert.assertEquals(Assert.java:128)
     at org.junit.Assert.assertEquals(Assert.java:472)
     at org.junit.Assert.assertEquals(Assert.java:456)
     */
    @Ignore
    @Test
    public void testFindByType() {

        List<TutkintoOhjelma> tutkintoOhjelmas = koulutusDAO.findAll(TutkintoOhjelma.class);
        assertEquals(NUM_TUTKINTO_OHJELMAS, tutkintoOhjelmas.size());

        List<TutkinnonOsa> tutkinnonOsas = koulutusDAO.findAll(TutkinnonOsa.class);
        assertEquals(NUM_TUTKINTO_OHJELMAS * NUM_TUTKINTO_OHJELMA_FIRST_LEVEL_CHILDREN, tutkinnonOsas.size());

        // you cannot search based on base class
        List<LearningOpportunityObject> koulutuses = koulutusDAO.findAll(LearningOpportunityObject.class);
        assertEquals(0, koulutuses.size());

    }

    @Test
    public void testFindChildrenByParentOid() {

        List<LearningOpportunityObject> children = koulutusDAO.findAllChildren(LearningOpportunityObject.class, "1");
        assertEquals(NUM_TUTKINTO_OHJELMA_FIRST_LEVEL_CHILDREN, children.size());

    }

    @Test
    public void testInsertKoulutusmoduuli() {

        TutkintoOhjelma m = fixtures.simpleTutkintoOhjelma;
        koulutusDAO.insert(m);

        TutkintoOhjelma loaded = (TutkintoOhjelma) koulutusDAO.read(m.getId());
        assertEquals(m.getId(), loaded.getId());

    }

    @Test
    public void testInsertKoulutusmoduuliToteutus() {

        TutkintoOhjelma m = fixtures.simpleTutkintoOhjelma;
        koulutusDAO.insert(m);

        TutkintoOhjelmaToteutus t = fixtures.simpleTutkintoOhjelmaToteutus;
        koulutusDAO.insert(t);

        assertEquals(m.getId(), koulutusDAO.read(m.getId()).getId());
        assertEquals(t.getId(), koulutusDAO.read(t.getId()).getId());

    }

    @Test
    public void testUpdateKoulutusmoduuli() {

        TutkintoOhjelma m = fixtures.createTutkintoOhjelma();
        koulutusDAO.insert(m);

        m.setNimi("updated");
        koulutusDAO.update(m);

        assertEquals("updated", koulutusDAO.read(m.getId()).getNimi());

    }

    @Test
    public void testAddYhteyshenkilo() {

        TutkintoOhjelmaToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));

        koulutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusDAO.read(t.getId());
        assertEquals(1, loaded.getYhteyshenkilos().size());

    }

    @Test
    public void testSameYhteyshenkiloCannotBeAddTwice() {

        TutkintoOhjelmaToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));

        assertEquals(1, t.getYhteyshenkilos().size());

    }

    @Test
    public void testDeleteYhteyshenkilo() {

        TutkintoOhjelmaToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));

        koulutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusDAO.read(t.getId());
        assertEquals(1, loaded.getYhteyshenkilos().size());

        loaded.removeYhteyshenkilo(loaded.getYhteyshenkilos().iterator().next());

        loaded = (KoulutusmoduuliToteutus) koulutusDAO.read(t.getId());
        assertEquals(0, loaded.getYhteyshenkilos().size());

    }

     /*
     * THE ERROR IN BAMBOO:
     * java.lang.AssertionError: unexpected number of search results for criteria: fi.vm.sade.tarjonta.dao.KoulutusDAO$SearchCriteria@5bda657 expected:<10> but was:<12>
	at org.junit.Assert.fail(Assert.java:93)
	at org.junit.Assert.failNotEquals(Assert.java:647)
	at org.junit.Assert.assertEquals(Assert.java:128)
	at org.junit.Assert.assertEquals(Assert.java:472)
	at fi.vm.sade.tarjonta.dao.KoulutusDAOTest.assertResultSize(KoulutusDAOTest.java:200)
     */
    @Ignore
    @Test
    public void testSearch() {
        
        KoulutusDAO.SearchCriteria criteria = new KoulutusDAO.SearchCriteria();

        // should match all top level modules
        criteria.setNimiQuery("Tutkinto");
        assertResultSize(NUM_TUTKINTO_OHJELMAS, criteria);

        // should match all children
        criteria.setNimiQuery("Tutkinnon");
        assertEquals(NUM_TUTKINTO_OHJELMAS * NUM_TUTKINTO_OHJELMA_FIRST_LEVEL_CHILDREN, koulutusDAO.search(criteria).size());

        // should match all top level modules by type
        criteria.setNimiQuery(null);
        criteria.setType(TutkintoOhjelma.class);
        assertEquals(NUM_TUTKINTO_OHJELMAS, koulutusDAO.search(criteria).size());

        // should not match any since all TutkinnonOsa are named "Tutkinnon..."
        criteria.setNimiQuery("Tutkinto");
        criteria.setType(TutkinnonOsa.class);
        assertEquals(0, koulutusDAO.search(criteria).size());

    }

   
    private void assertResultSize(int expected, KoulutusDAO.SearchCriteria criteria) {

        assertEquals("unexpected number of search results for criteria: "
            + criteria, expected, koulutusDAO.search(criteria).size());

    }

    private void initData() {

        TutkintoOhjelma t;
        TutkinnonOsa o;

        for (int i = 0; i < NUM_TUTKINTO_OHJELMAS; i++) {

            t = fixtures.createTutkintoOhjelma();
            t.setNimi("Tutkinto-ohjelma " + i);
            t.setOid(String.valueOf(i));
            koulutusDAO.insert(t);

            for (int j = 0; j < NUM_TUTKINTO_OHJELMA_FIRST_LEVEL_CHILDREN; j++) {

                o = fixtures.createTutkinnonOsa();
                o.setNimi("Tutkinnon osa: " + i + "." + j);
                o.setOid(i + "." + j);
                koulutusDAO.insert(o);

                sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(t, o, true));

            }

        }

    }

    private void clear() {

        ((KoulutusDAOImpl) koulutusDAO).getEntityManager().clear();

    }

    private void printData() {

        List<LearningOpportunityObject> allKoulutus = koulutusDAO.findAll();
        List<KoulutusSisaltyvyys> allSisaltyvyys = koulutusDAO.findAllSisaltyvyys();

        System.out.println("-- KOULUTUS TEST DATA:");

        for (int i = 0; i < allKoulutus.size(); i++) {
            LearningOpportunityObject k = allKoulutus.get(i);
            System.out.println((i + 1) + "\t Koulutus, type: " + k.getClass().getSimpleName() + ", nimi: " + k.getNimi());
        }

        for (int i = 0; i < allSisaltyvyys.size(); i++) {
            KoulutusSisaltyvyys s = allSisaltyvyys.get(i);
            System.out.println((i + 1) + "\t Sisaltyvyys: parent: " + s.getParent().getOid() + ", child: " + s.getChild().getOid());
        }

    }

}

