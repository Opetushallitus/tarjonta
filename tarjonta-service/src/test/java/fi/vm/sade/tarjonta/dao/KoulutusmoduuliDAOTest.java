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

import fi.vm.sade.tarjonta.KoulutusDatabasePrinter;
import fi.vm.sade.tarjonta.KoulutusFixtures;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * KoulutusmoduuliDAO and KoulutusmoduuliTotetusDAO were merged hence dao under test is KoulutusDAO. TOOD: merge tests too.
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliDAOTest {

    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliDAOTest.class);

    private static final int NUM_TUTKINTO_TOP_LEVEL_MODUULI = 10;

    private static final int NUM_TUTKINTO_SECOND_LEVEL_MODUULI = 4;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    private static final String KOULUTUSMODUULI_OID = "http://koulutusmoduuli/123";

    private static final String ORGANISAATIO_OID = "http://organisaatio/123";

    private static final String KOULUTUS_KOODI_URI = "http://koulutuskoodi/123";

    private Koulutusmoduuli newTutkintoOhjelma;

    @Autowired
    private KoulutusFixtures fixtures;

    @Autowired
    private KoulutusDatabasePrinter dbPrinter;

    @Before
    public void setUp() {
        newTutkintoOhjelma = createTutkintoOhjelma();
        fixtures.recreate();
        initData();
    }

    @Test
    public void testSimpleSaveAndRead() {

        newTutkintoOhjelma.setOid(KOULUTUSMODUULI_OID);
        newTutkintoOhjelma.setOmistajaOrganisaatioOid(ORGANISAATIO_OID);

        newTutkintoOhjelma = insert(newTutkintoOhjelma);

        assertNotNull(newTutkintoOhjelma.getId());

        Koulutusmoduuli loaded = read(newTutkintoOhjelma.getId());

        assertNotNull(loaded);
        assertEquals(KOULUTUSMODUULI_OID, loaded.getOid());
        assertEquals(ORGANISAATIO_OID, loaded.getOmistajaOrganisaatioOid());

    }

    @Test
    public void testUpdateKoulutusmoduuli() {

        Koulutusmoduuli m = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(m);

        m.setNimi("after update");
        koulutusmoduuliDAO.update(m);

        assertEquals("after update", koulutusmoduuliDAO.read(m.getId()).getNimi());

    }

    @Test
    public void testInsertKoulutusmoduuli() {

        Koulutusmoduuli m = fixtures.simpleTutkintoOhjelma;
        koulutusmoduuliDAO.insert(m);

        Koulutusmoduuli loaded = koulutusmoduuliDAO.read(m.getId());
        assertEquals(m.getId(), loaded.getId());

    }

    @Test
    public void testFindChildrenByParentOid() {

        List<Koulutusmoduuli> alamoduuliList = koulutusmoduuliDAO.getAlamoduuliList("1");
        assertEquals(NUM_TUTKINTO_SECOND_LEVEL_MODUULI, alamoduuliList.size());

    }

    @Test
    public void savingModuuliUpdatesUpdatedTimestamp() throws Exception {

        assertNull(newTutkintoOhjelma.getUpdated());

        newTutkintoOhjelma = insert(newTutkintoOhjelma);

        Date timeInserted = newTutkintoOhjelma.getUpdated();
        assertNotNull(timeInserted);

        // wait a moment to make sure some time has elapsed
        Thread.sleep(50L);

        // change something and update
        newTutkintoOhjelma.setKoulutusala("new koulutusala");
        newTutkintoOhjelma = update(newTutkintoOhjelma);

        Date timeUpdated = newTutkintoOhjelma.getUpdated();

        assertEquals(1, timeUpdated.compareTo(timeInserted));

    }

    @Test
    public void testDelete() {

        koulutusmoduuliDAO.insert(newTutkintoOhjelma);

        final Long id = newTutkintoOhjelma.getId();
        assertNotNull(read(id));
        koulutusmoduuliDAO.remove(newTutkintoOhjelma);

        assertNull(koulutusmoduuliDAO.read(id));

    }

    @Test
    public void testOIDCannotBeUpdated() {

        Koulutusmoduuli koulutus = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(koulutus);

        // get the oid record was inserted with
        final String originalOid = koulutus.getOid();

        // try to overwrite oid
        koulutus.setOid("some other oid");
        koulutusmoduuliDAO.update(koulutus);

        // re-read state from db and check value
        BaseKoulutusmoduuli loaded = koulutusmoduuliDAO.read(koulutus.getId());

        // for some reason update goes trough - this test will fail if it doesn't to  notify use that something has changed and updatable=false is working!
        assertFalse(originalOid.equals(loaded.getOid()));

        // this should be the case
        //assertEquals(originalOid, loaded.getOid());


    }

    private void flush() {
        ((KoulutusmoduuliDAOImpl) koulutusmoduuliDAO).getEntityManager().flush();
    }

    private Koulutusmoduuli read(Long id) {
        return koulutusmoduuliDAO.read(id);
    }

    private Koulutusmoduuli update(Koulutusmoduuli o) {
        koulutusmoduuliDAO.update(o);
        return o;
    }

    private Koulutusmoduuli insert(Koulutusmoduuli o) {
        return koulutusmoduuliDAO.insert(o);
    }

    private Koulutusmoduuli createTutkintoOhjelma() {

        Koulutusmoduuli m = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setKoulutusKoodi("123456");
        m.setTutkintoOhjelmanNimi("JUnit Tutkinto");
        m.setOid("http://oid/12345");
        return m;

    }

    private void initData() {

        fixtures.removeAll();

        Koulutusmoduuli t;
        Koulutusmoduuli o;

        for (int i = 0; i < NUM_TUTKINTO_TOP_LEVEL_MODUULI; i++) {

            t = fixtures.createTutkintoOhjelma();
            t.setNimi("Tutkinto-ohjelma " + i);
            t.setOid(String.valueOf(i));
            koulutusmoduuliDAO.insert(t);

            for (int j = 0; j < NUM_TUTKINTO_SECOND_LEVEL_MODUULI; j++) {

                o = fixtures.createTutkinnonOsa();
                o.setNimi("Tutkinnon osa: " + i + "." + j);
                o.setOid(i + "." + j);
                koulutusmoduuliDAO.insert(o);
                sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(t, o, KoulutusSisaltyvyys.ValintaTyyppi.SOME_OFF));

            }

        }

    }

}

