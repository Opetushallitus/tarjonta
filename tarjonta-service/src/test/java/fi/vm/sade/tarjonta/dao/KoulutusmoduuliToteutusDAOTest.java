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

import fi.vm.sade.tarjonta.TarjontaDatabasePrinter;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * KoulutusmoduuliDAO and KoulutusmoduuliTotetusDAO were merged hence dao under
 * test is KoulutusDAO. TOOD: merge tests too.
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliToteutusDAOTest {

    private static final Logger log = LoggerFactory.getLogger(KoulutusmoduuliToteutusDAOTest.class);
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    private TarjontaFixtures fixtures;
    private Koulutusmoduuli defaultModuuli;
    private KoulutusmoduuliToteutus defaultToteutus;
   
    private static final String TARJOAJA_1_OID = "http://organisaatio1";
    private static final String TARJOAJA_2_OID = "http://organisaatio2";
    private static final String TOTEUTUS_1_NIMI = "Toteutus 1 Nimi";
    private static final String TOTEUTUS_1_OID = "Toteutus 1 OID";
    private static final String MAKSULLISUUS = "5 EUR/month";
    private static final Calendar KOULUTUS_ALK_PAIVA = Calendar.getInstance();
    private static final int MAX_ROWS = 3;
    private static final String tarjoaja1 = "0.0.0.0.01";
    private static final String nimi1 = "eka toteutus";
    private static final String tarjoaja2 = "0.0.0.0.02";
    private static final String nimi2 = "toka toteutus";
    private static final String tarjoaja3 = "0.0.0.0.03";
    @Autowired
    ApplicationContext ctx;

    @Before
    public void setUp() {
        koulutusmoduuliToteutusDAO = ctx.getAutowireCapableBeanFactory().createBean(KoulutusmoduuliToteutusDAOImpl.class);
        koulutusmoduuliDAO = ctx.getAutowireCapableBeanFactory().createBean(KoulutusmoduuliDAOImpl.class);
        fixtures = ctx.getAutowireCapableBeanFactory().createBean(TarjontaFixtures.class);

        KOULUTUS_ALK_PAIVA.set(Calendar.YEAR, 2012);
        KOULUTUS_ALK_PAIVA.set(Calendar.DAY_OF_MONTH, 10);
        KOULUTUS_ALK_PAIVA.set(Calendar.MONTH, Calendar.DECEMBER);

        defaultModuuli = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        defaultModuuli.setOid("http://someoid");
        defaultModuuli.setTutkintoOhjelmanNimi("Junit Tutkinto");
        defaultModuuli.setKoulutusKoodi("123456");
        defaultModuuli.setNimi(TarjontaFixtures.createText(TOTEUTUS_1_NIMI, null, null));

        koulutusmoduuliDAO.insert(defaultModuuli);

        defaultToteutus = new KoulutusmoduuliToteutus(defaultModuuli);
        defaultToteutus.setOid(TOTEUTUS_1_OID);
        defaultToteutus.setKoulutuksenAlkamisPvm(KOULUTUS_ALK_PAIVA.getTime());
        defaultToteutus.setMaksullisuus(MAKSULLISUUS);
        defaultToteutus.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        defaultToteutus.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));
        koulutusmoduuliToteutusDAO.insert(defaultToteutus);

        Koulutusmoduuli m1 = fixtures.createTutkintoOhjelma();
        m1.setNimi(TarjontaFixtures.createText(nimi1, null, null));
        m1 = koulutusmoduuliDAO.insert(m1);

        Koulutusmoduuli m2 = fixtures.createTutkintoOhjelma();
        m2.setNimi(TarjontaFixtures.createText(nimi2, null, null));
        m2 = koulutusmoduuliDAO.insert(m2);


        //KOMOTO1
        KoulutusmoduuliToteutus t1 = fixtures.createTutkintoOhjelmaToteutus();
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.YEAR, 2015);
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        cal1.set(Calendar.MONTH, 4); //may -> 1.5.2015

        t1.setKoulutuksenAlkamisPvm(cal1.getTime());
        t1.setTarjoaja(tarjoaja1);
        t1.setKoulutusmoduuli(m1);
        koulutusmoduuliToteutusDAO.insert(t1);

        //KOMOTO2
        KoulutusmoduuliToteutus t2 = fixtures.createTutkintoOhjelmaToteutus();
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.YEAR, 2016);
        cal2.set(Calendar.DAY_OF_MONTH, 1);
        cal2.set(Calendar.MONTH, 5); //june
        t2.setKoulutuksenAlkamisPvm(cal2.getTime());
        t2.setTarjoaja(tarjoaja2);
        t2.setKoulutusmoduuli(m2);
        koulutusmoduuliToteutusDAO.insert(t2);
    }

    @Test
    public void testSavedTutkintoOhjelmaCanBeFoundById() {
        KoulutusmoduuliToteutus loaded = koulutusmoduuliToteutusDAO.read(defaultToteutus.getId());
        assertNotNull(loaded);
        assertEquals(TOTEUTUS_1_OID, loaded.getOid());
        assertEquals(KOULUTUS_ALK_PAIVA.getTime(), loaded.getKoulutuksenAlkamisPvm());
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
        Koulutusmoduuli m = koulutusmoduuliDAO.insert(fixtures.createTutkintoOhjelma());

        t.setKoulutusmoduuli(m);

        Yhteyshenkilo henkilo = new Yhteyshenkilo("12345", "fi");
        henkilo.setEtunimis("John");
        henkilo.setSukunimi("Doe");

        t.addYhteyshenkilo(henkilo);

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
        Koulutusmoduuli m = koulutusmoduuliDAO.insert(fixtures.createTutkintoOhjelma());
        t.setKoulutusmoduuli(m);
        t.addYhteyshenkilo(fixtures.createYhteyshenkilo("12345"));

        koulutusmoduuliToteutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(1, loaded.getYhteyshenkilos().size());

    }

    @Test
    public void testAddLinkkis() {

        KoulutusmoduuliToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        Koulutusmoduuli m = koulutusmoduuliDAO.insert(fixtures.createTutkintoOhjelma());
        t.setKoulutusmoduuli(m);

        // no language
        t.addLinkki(new WebLinkki(WebLinkki.LinkkiTyyppi.MULTIMEDIA, null, "http://link1"));
        // identical, first will get lost
        t.addLinkki(new WebLinkki(WebLinkki.LinkkiTyyppi.MULTIMEDIA, null, "http://link1"));

        // two links of same type and url but different language
        t.addLinkki(new WebLinkki("customtype1", "en", "http://link2"));
        t.addLinkki(new WebLinkki("customtype1", "fi", "http://link2"));

        assertEquals(3, t.getLinkkis().size());

        koulutusmoduuliToteutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(3, loaded.getLinkkis().size());

    }

    @Test
    public void testSetLinkkis() {

        KoulutusmoduuliToteutus t = fixtures.createTutkintoOhjelmaToteutus();
        Koulutusmoduuli m = koulutusmoduuliDAO.insert(fixtures.createTutkintoOhjelma());
        t.setKoulutusmoduuli(m);

        t.addLinkki(new WebLinkki(WebLinkki.LinkkiTyyppi.MULTIMEDIA, null, "http://link1"));

        koulutusmoduuliToteutusDAO.insert(t);

        KoulutusmoduuliToteutus loaded = (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(1, loaded.getLinkkis().size());

        Set<WebLinkki> newSet = new HashSet<WebLinkki>();
        newSet.add(new WebLinkki("type2", null, "http://link2"));
        newSet.add(new WebLinkki("type3", null, "http://link3"));
        loaded.setLinkkis(newSet);

        koulutusmoduuliToteutusDAO.update(loaded);

        loaded = (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.read(t.getId());
        assertEquals(2, loaded.getLinkkis().size());

        // todo: check orphans are deleted (link1)

    }

    @Test
    public void testFindByCriteria() {

        //Searching with list containing tarjoaja1 but not tarjoaja2 and nimi1

        List<String> criteriaList = Arrays.asList(new String[]{tarjoaja1, tarjoaja3});
        List<KoulutusmoduuliToteutus> result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, nimi1, -1, new ArrayList<Integer>());

        assertEquals(1, result.size());

        //Searching with list containing tarjoaja2 but not tarjoaja1 and nimi2

        criteriaList = Arrays.asList(new String[]{tarjoaja2, tarjoaja3});
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, nimi2, -1, new ArrayList<Integer>());

        assertEquals(1, result.size());

        //Searching with list not containing any matching tarjoaja and nimi1

        criteriaList = Arrays.asList(new String[]{tarjoaja3});
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, nimi1, -1, new ArrayList<Integer>());

        assertEquals(0, result.size());

        //Searching with list containing tarjoaja1 but not tarjoaja2 and nimi2

        criteriaList = Arrays.asList(new String[]{tarjoaja1, tarjoaja3});
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, nimi2, -1, new ArrayList<Integer>());

        assertEquals(0, result.size());

        //Searching with an empty list and nimi1

        criteriaList = new ArrayList<String>();
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, nimi1, -1, new ArrayList<Integer>());

        assertEquals(1, result.size());
        assertEquals(nimi1, result.get(0).getKoulutusmoduuli().getNimi().getTekstiForKieliKoodi("fi"));

        //Searching with an empty list and null in nimi

        criteriaList = new ArrayList<String>();
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, null, -1, new ArrayList<Integer>());

        assertEquals(MAX_ROWS, result.size());

        //Searching with start year 2015
        criteriaList = new ArrayList<String>();
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, null, 2015, new ArrayList<Integer>());

        assertEquals(1, result.size());

    }

    @Test
    public void findByCriteriaMonthList() {
        //Searching with months 4 - 6 and 12    
        List<String> criteriaList = new ArrayList<String>();
        List<Integer> months = new ArrayList<Integer>();
        months.add(4);
        months.add(5);
        months.add(6); //no data
        months.add(12);

        List<KoulutusmoduuliToteutus> result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, null, -1, months);
        assertEquals(MAX_ROWS, result.size());

        //Searching with months 1 - 6  and year 2016  
        criteriaList = new ArrayList<String>();
        result = koulutusmoduuliToteutusDAO.findByCriteria(criteriaList, null, 2016, months);

        assertEquals(1, result.size());
    }
}
