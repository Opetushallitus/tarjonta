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
import fi.vm.sade.tarjonta.model.KoulutusRakenne;
import fi.vm.sade.tarjonta.model.KoulutusRakenne.SelectorType;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusRakenneDAOTest {

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Autowired
    private KoulutusRakenneDAO rakenneDAO;

    @Autowired
    private KoulutusFixtures fixtures;

    private Koulutusmoduuli parent;

    private KoulutusRakenne rakenne1;

    private KoulutusRakenne rakenne2;

    private Koulutusmoduuli child1;

    private Koulutusmoduuli child2;

    @Before
    public void setUp() {

        parent = fixtures.createTutkintoOhjelma();
        child1 = fixtures.createTutkintoOhjelma();
        child2 = fixtures.createTutkintoOhjelma();

        koulutusDAO.insert(child1);
        koulutusDAO.insert(child2);
        koulutusDAO.insert(parent);

        rakenne1 = new KoulutusRakenne();
        rakenne1.setSelector(SelectorType.ALL_OFF);
        rakenne1.setParent(parent);
        rakenne1.addChild(child1);

        rakenne2 = new KoulutusRakenne();
        rakenne2.setSelector(SelectorType.ONE_OFF);
        rakenne2.setParent(parent);
        rakenne2.addChild(child1);

        rakenneDAO.insert(rakenne1);
        rakenneDAO.insert(rakenne2);

        clear();

    }

    @Test
    public void testInsert() {

        Koulutusmoduuli loaded = (Koulutusmoduuli) koulutusDAO.read(parent.getId());
        assertEquals(2, loaded.getStructures().size());

    }

    @Test
    public void testDelete() {

        rakenneDAO.remove(rakenneDAO.read(rakenne1.getId()));
        rakenneDAO.remove(rakenneDAO.read(rakenne2.getId()));

        flush();
        clear();

        // check that deleting has not been cascaded
        assertNotNull(koulutusDAO.read(parent.getId()));
        assertNotNull(koulutusDAO.read(child1.getId()));

        // and that parent does not contain the structure anymore
        assertEquals(0, koulutusDAO.read(parent.getId()).getStructures().size());

    }

    @Test
    public void testUpdate() {

        // re-read since we're going to call merge on the object
        rakenne1 = rakenneDAO.read(rakenne1.getId());
        assertEquals(1, rakenne1.getChildren().size());

        // create and persist new child
        Koulutusmoduuli newChild = fixtures.createTutkintoOhjelma();
        koulutusDAO.insert(newChild);

        // add to structure and update
        rakenne1.addChild(newChild);
        rakenneDAO.update(rakenne1);

        clear();

        rakenne1 = rakenneDAO.read(rakenne1.getId());
        assertEquals(2, rakenne1.getChildren().size());

    }

    private void flush() {
        ((KoulutusDAOImpl) koulutusDAO).getEntityManager().flush();
    }

    private void clear() {
        ((KoulutusDAOImpl) koulutusDAO).getEntityManager().clear();
    }

}

