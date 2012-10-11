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
import fi.vm.sade.tarjonta.model.*;
import java.util.Date;
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
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusFixtures fixtures;

    private Koulutusmoduuli defaultModuuli;

    private KoulutusmoduuliToteutus defaultToteutus;

    private static final Date ALKAMIS_PVM = new Date();

    private static final String TARJOAJA_1_OID = "http://organisaatio1";

    private static final String TARJOAJA_2_OID = "http://organisaatio2";

    private static final String TOTEUTUS_1_NIMI = "Toteutus 1 Nimi";

    private static final String TOTEUTUS_1_OID = "Toteutus 1 OID";

    private static final String MAKSULLISUUS = "5 EUR/month";

    @Before
    public void setUp() {

        defaultModuuli = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        defaultModuuli.setOid("http://someoid");
        defaultModuuli.setTutkintoOhjelmanNimi("Junit Tutkinto");
        defaultModuuli.setKoulutusKoodi("123456");
        koulutusmoduuliDAO.insert(defaultModuuli);

        defaultToteutus = new KoulutusmoduuliToteutus(defaultModuuli);
        defaultToteutus.setNimi(TOTEUTUS_1_NIMI);
        defaultToteutus.setOid(TOTEUTUS_1_OID);
        defaultToteutus.setKoulutuksenAlkamisPvm(ALKAMIS_PVM);
        defaultToteutus.setMaksullisuus(MAKSULLISUUS);
        defaultToteutus.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        defaultToteutus.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));
        koulutusmoduuliToteutusDAO.insert(defaultToteutus);

    }

    @Test
    public void testSavedTutkintoOhjelmaCanBeFoundById() {

        KoulutusmoduuliToteutus loaded = koulutusmoduuliToteutusDAO.read(defaultToteutus.getId());
        assertNotNull(loaded);
        assertEquals(TOTEUTUS_1_NIMI, loaded.getNimi());
        assertEquals(TOTEUTUS_1_OID, loaded.getOid());
        assertEquals(ALKAMIS_PVM, loaded.getKoulutuksenAlkamisPvm());
        assertEquals(MAKSULLISUUS, loaded.getMaksullisuus());
        assertEquals(defaultModuuli.getOid(), loaded.getKoulutusmoduuli().getOid());


    }

    @Test
    public void testDeletingToteutusDoesNotDeleteModuuli() {

        koulutusmoduuliToteutusDAO.remove(defaultToteutus);
        assertNotNull(koulutusmoduuliDAO.read(defaultModuuli.getId()));

    }

    @Test
    public void testSameYhteyshenkiloCannotBeAddTwice() {

        KoulutusmoduuliToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));

        assertEquals(1, t.getYhteyshenkilos().size());

    }

    @Test
    public void testDeleteYhteyshenkilo() {

        KoulutusmoduuliToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));

        koulutusmoduuliToteutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(1, loaded.getYhteyshenkilos().size());

        loaded.removeYhteyshenkilo(loaded.getYhteyshenkilos().iterator().next());

        loaded = koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(0, loaded.getYhteyshenkilos().size());

    }

    @Test
    public void testAddYhteyshenkilo() {

        KoulutusmoduuliToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        t.addYhteyshenkilo(new Yhteyshenkilo("12345", "fi"));

        koulutusmoduuliToteutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(1, loaded.getYhteyshenkilos().size());

    }

    private KoulutusmoduuliToteutus updateAndRead(KoulutusmoduuliToteutus toteutus) {

        koulutusmoduuliToteutusDAO.update(toteutus);
        return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.read(toteutus.getId());

    }

}

