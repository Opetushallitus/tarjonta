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

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys.ValintaTyyppi;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusSisaltyvyysDAOTest extends TestUtilityBase {

    private Koulutusmoduuli parent;

    private KoulutusSisaltyvyys rakenne1;

    private KoulutusSisaltyvyys rakenne2;

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

        rakenne1 = new KoulutusSisaltyvyys();
        rakenne1.setValintaTyyppi(ValintaTyyppi.ALL_OFF);
        rakenne1.setYlamoduuli(parent);
        rakenne1.addAlamoduuli(child1);

        rakenne2 = new KoulutusSisaltyvyys();
        rakenne2.setValintaTyyppi(ValintaTyyppi.ONE_OFF);
        rakenne2.setYlamoduuli(parent);
        rakenne2.addAlamoduuli(child1);

        sisaltyvyysDAO.insert(rakenne1);
        sisaltyvyysDAO.insert(rakenne2);

        clear();

    }

    @Test
    public void testInsert() {

        Koulutusmoduuli loaded = (Koulutusmoduuli) koulutusDAO.read(parent.getId());
        assertEquals(2, loaded.getSisaltyvyysList().size());

    }

    @Test
    public void testDelete() {

        sisaltyvyysDAO.remove(sisaltyvyysDAO.read(rakenne1.getId()));
        sisaltyvyysDAO.remove(sisaltyvyysDAO.read(rakenne2.getId()));

        flush();
        clear();

        // check that deleting has not been cascaded
        assertNotNull(koulutusDAO.read(parent.getId()));
        assertNotNull(koulutusDAO.read(child1.getId()));

        // and that parent does not contain the structure anymore
        assertEquals(0, koulutusDAO.read(parent.getId()).getSisaltyvyysList().size());

    }

    @Test
    public void testUpdate() {

        // re-read since we're going to call merge on the object
        rakenne1 = sisaltyvyysDAO.read(rakenne1.getId());
        assertEquals(1, rakenne1.getAlamoduuliList().size());

        // create and persist new child
        Koulutusmoduuli newChild = fixtures.createTutkintoOhjelma();
        koulutusDAO.insert(newChild);

        // add to structure and update
        rakenne1.addAlamoduuli(newChild);
        sisaltyvyysDAO.update(rakenne1);

        clear();

        rakenne1 = sisaltyvyysDAO.read(rakenne1.getId());
        assertEquals(2, rakenne1.getAlamoduuliList().size());

    }

    private void flush() {
        ((KoulutusmoduuliDAOImpl) koulutusDAO).getEntityManager().flush();
    }

    private void clear() {
        ((KoulutusmoduuliDAOImpl) koulutusDAO).getEntityManager().clear();
    }

}

