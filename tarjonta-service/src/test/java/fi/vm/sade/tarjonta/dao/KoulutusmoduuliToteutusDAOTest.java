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

import fi.vm.sade.tarjonta.model.*;
import java.util.Date;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
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
public class KoulutusmoduuliToteutusDAOTest {

    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliToteutusDAOTest.class);

    @Autowired
    private KoulutusDAO koulutusDAO;

    private TutkintoOhjelma defaultModuuli;

    private KoulutusmoduuliToteutus defaultToteutus;

    private static final Date ALKAMIS_PVM = new Date();

    private static final String TARJOAJA_1_OID = "http://organisaatio1";

    private static final String TARJOAJA_2_OID = "http://organisaatio2";

    private static final String TOTEUTUS_1_NIMI = "Toteutus 1 Nimi";

    private static final String TOTEUTUS_1_OID = "Toteutus 1 OID";

    private static final String MAKSULLISUUS = "5 EUR/month";

    @Before
    public void setUp() {

        defaultModuuli = new TutkintoOhjelma();
        defaultModuuli.setOid("http://someoid");
        defaultModuuli.setTutkintoOhjelmanNimi("Junit Tutkinto");
        defaultModuuli.setKoulutusKoodi("123456");
        koulutusDAO.insert(defaultModuuli);

        defaultToteutus = new TutkintoOhjelmaToteutus(defaultModuuli);
        defaultToteutus.setNimi(TOTEUTUS_1_NIMI);
        defaultToteutus.setOid(TOTEUTUS_1_OID);
        defaultToteutus.setKoulutuksenAlkamisPvm(ALKAMIS_PVM);
        defaultToteutus.setMaksullisuus(MAKSULLISUUS);
        koulutusDAO.insert(defaultToteutus);

    }

    @Test
    public void testSavedTutkintoOhjelmaCanBeFoundById() {

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusDAO.read(defaultToteutus.getId());
        assertNotNull(loaded);
        assertEquals(TOTEUTUS_1_NIMI, loaded.getNimi());
        assertEquals(TOTEUTUS_1_OID, loaded.getOid());
        assertEquals(ALKAMIS_PVM, loaded.getKoulutuksenAlkamisPvm());
        assertEquals(MAKSULLISUUS, loaded.getMaksullisuus());
        assertEquals(defaultModuuli.getOid(), loaded.getKoulutusmoduuli().getOid());


    }

    @Test
    public void testAddTarjoajaToToteutus() {

        assertEquals(0, defaultToteutus.getTarjoajat().size());

        defaultToteutus.addTarjoaja(TARJOAJA_1_OID);
        KoulutusmoduuliToteutus loaded = updateAndRead(defaultToteutus);
        assertEquals(1, loaded.getTarjoajat().size());

    }

    @Test
    public void testAddMultipleTarjoajaToToteutus() {

        defaultToteutus.addTarjoaja(TARJOAJA_1_OID);
        defaultToteutus.addTarjoaja(TARJOAJA_2_OID);
        KoulutusmoduuliToteutus loaded = updateAndRead(defaultToteutus);
        assertEquals(2, loaded.getTarjoajat().size());

    }

    @Test
    public void testRemoveTarjoaja() {

        defaultToteutus.addTarjoaja(TARJOAJA_1_OID);
        KoulutusmoduuliToteutus loaded = updateAndRead(defaultToteutus);

        assertEquals(1, loaded.getTarjoajat().size());
        loaded.removeTarjoaja(TARJOAJA_1_OID);

        assertEquals(0, loaded.getTarjoajat().size());

        loaded = updateAndRead(loaded);
        assertEquals(0, loaded.getTarjoajat().size());

    }

    @Test
    public void testDeletingToteutusDoesNotDeleteModuuli() {

        // delete KoulutusmoduuliToteutus 
        koulutusDAO.remove(defaultToteutus);
        
        // check that we can still load Koulutusmoduuli
        assertNotNull(koulutusDAO.read(defaultModuuli.getId()));

    }

    
    private KoulutusmoduuliToteutus updateAndRead(KoulutusmoduuliToteutus toteutus) {
        koulutusDAO.update(toteutus);
        return (KoulutusmoduuliToteutus) koulutusDAO.read(toteutus.getId());
    }

}

