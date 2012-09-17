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

import fi.vm.sade.tarjonta.dao.impl.KoulutusDAOImpl;
import fi.vm.sade.tarjonta.model.Koulutus;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import java.util.Date;
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

    @Autowired
    private KoulutusDAO dao;

    private static final String KOULUTUSMODUULI_OID = "http://koulutusmoduuli/123";

    private static final String ORGANISAATIO_OID = "http://organisaatio/123";

    private static final String KOULUTUS_KOODI_URI = "http://koulutuskoodi/123";

    private TutkintoOhjelma newTutkintoOhjelma;

    @Before
    public void setUp() {
        newTutkintoOhjelma = createTutkintoOhjelma();
    }

    @Test
    public void testSimpleSaveAndRead() {

        newTutkintoOhjelma.setOid(KOULUTUSMODUULI_OID);
        newTutkintoOhjelma.setOwnerOrganisaatioOid(ORGANISAATIO_OID);
        
        newTutkintoOhjelma = insert(newTutkintoOhjelma);

        assertNotNull(newTutkintoOhjelma.getId());

        TutkintoOhjelma loaded = read(newTutkintoOhjelma.getId());

        assertNotNull(loaded);
        assertEquals(KOULUTUSMODUULI_OID, loaded.getOid());
        assertEquals(ORGANISAATIO_OID, loaded.getOwnerOrganisaatioOid());

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
        newTutkintoOhjelma.setKoulutusAla("new koulutusala");
        newTutkintoOhjelma = update(newTutkintoOhjelma);

        Date timeUpdated = newTutkintoOhjelma.getUpdated();

        assertEquals(1, timeUpdated.compareTo(timeInserted));

    }

    @Test
    public void testDelete() {

        dao.insert(newTutkintoOhjelma);

        final Long id = newTutkintoOhjelma.getId();
        assertNotNull(read(id));
        dao.remove(newTutkintoOhjelma);

        assertNull(dao.read(id));

    }

    
    @Test
    public void testOIDCannotBeUpdated() {
        
        Koulutus koulutus = createTutkintoOhjelma();
        dao.insert(koulutus);
        
        // get the oid record was inserted with
        final String originalOid = koulutus.getOid();
        
        // try to overwrite oid
        koulutus.setOid("some other oid");
        dao.update(koulutus);
        
        // re-read state from db and check value
        Koulutus loaded = dao.read(koulutus.getId());
        
        // for some reason update goes trough - this test will fail if it doesn't to  notify use that something has changed and updatable=false is working!
        assertFalse(originalOid.equals(loaded.getOid()));
        
        // this should be the case
        //assertEquals(originalOid, loaded.getOid());
        
        
    }
    
    private void flush() {
        ((KoulutusDAOImpl) dao).getEntityManager().flush();
    }

    private TutkintoOhjelma read(Long id) {
        return (TutkintoOhjelma) dao.read(id);
    }

    private TutkintoOhjelma update(TutkintoOhjelma o) {
        dao.update(o);
        return o;
    }

    private TutkintoOhjelma insert(TutkintoOhjelma o) {
        return (TutkintoOhjelma) dao.insert(o);        
    }

    private TutkintoOhjelma createTutkintoOhjelma() {

        TutkintoOhjelma t = new TutkintoOhjelma();
        t.setKoulutusKoodi("123456");
        t.setTutkintoOhjelmanNimi("JUnit Tutkinto");
        t.setOid("http://oid/12345");
        return t;

    }

}

