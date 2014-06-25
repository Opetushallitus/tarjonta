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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO.SearchCriteria;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys.ValintaTyyppi;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

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
 * KoulutusmoduuliDAO and KoulutusmoduuliTotetusDAO were merged hence dao under
 * test is KoulutusDAO. TOOD: merge tests too.
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
    private TarjontaFixtures fixtures;

    @Autowired
    private TarjontaDatabasePrinter dbPrinter;

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

        m.setNimi(TarjontaFixtures.createText("after update", null, null));
        koulutusmoduuliDAO.update(m);

        assertEquals("after update", koulutusmoduuliDAO.read(m.getId()).getNimi().getTekstiForKieliKoodi("fi"));

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
        newTutkintoOhjelma.setKoulutusalaUri("new koulutusala");
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

    @Test
    public void testFindParentKomo() {
        String KOULUTUSKOODI = "uri:koulutuskoodi";
        String KOULUTUSOHJELMAKOODI1 = "uri:koulutusohjelmakoodi1";

        //Create tutkinto (parent)
        Koulutusmoduuli tutkinto = fixtures.createTutkintoOhjelma(KoulutusmoduuliTyyppi.TUTKINTO);
        tutkinto.setKoulutusUri(KOULUTUSKOODI);
        tutkinto.setKoulutusohjelmaUri(null);

        tutkinto = this.koulutusmoduuliDAO.insert(tutkinto);

        String parentOid = tutkinto.getOid();

        //Create koulutusohjelma (child)
        Koulutusmoduuli koulutusohjelma = fixtures.createTutkintoOhjelma(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusohjelma.setKoulutusUri(KOULUTUSKOODI);
        koulutusohjelma.setKoulutusohjelmaUri(KOULUTUSOHJELMAKOODI1);

        koulutusohjelma = this.koulutusmoduuliDAO.insert(koulutusohjelma);

        //Create hierarchy between the above too komos
        KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys();
        sisaltyvyys.setYlamoduuli(tutkinto);
        sisaltyvyys.addAlamoduuli(koulutusohjelma);
        sisaltyvyys.setValintaTyyppi(ValintaTyyppi.SOME_OFF);
        this.sisaltyvyysDAO.insert(sisaltyvyys);
        tutkinto.addSisaltyvyys(sisaltyvyys);
        this.koulutusmoduuliDAO.update(tutkinto);

        Koulutusmoduuli resultKomo = this.koulutusmoduuliDAO.findParentKomo(koulutusohjelma);
        assertTrue(resultKomo.getOid().equals(parentOid));
    }

    @Test
    public void testSearchKoulutusmoduulit() {
        String KOULUTUSKOODI = "uri:koulutuskoodi";
        String KOULUTUSOHJELMAKOODI1 = "uri:koulutusohjelmakoodi1";
        String KOULUTUSOHJELMAKOODI2 = "uri:koulutusohjelmakoodi2";

        //KOMO1
        Koulutusmoduuli koulutus = fixtures.createTutkintoOhjelma();
        koulutus.setKoulutusUri(KOULUTUSKOODI);
        koulutus.setKoulutusohjelmaUri(KOULUTUSOHJELMAKOODI1);
        koulutusmoduuliDAO.insert(koulutus);

        //KOMO2
        koulutus = fixtures.createTutkintoOhjelma();
        koulutus.setKoulutusUri(KOULUTUSKOODI);
        koulutus.setKoulutusohjelmaUri(KOULUTUSOHJELMAKOODI2);
        koulutusmoduuliDAO.insert(koulutus);

        //KOMO3o
        koulutus = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(koulutus);

        SearchCriteria criteria = new SearchCriteria();
        criteria.setKoulutusKoodi(KOULUTUSKOODI);
        List<Koulutusmoduuli> komos = this.koulutusmoduuliDAO.search(criteria);

        assertEquals(2, komos.size());

        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMAKOODI1);
        komos = this.koulutusmoduuliDAO.search(criteria);

        assertEquals(1, komos.size());
        assertEquals(KOULUTUSOHJELMAKOODI1, komos.get(0).getKoulutusohjelmaUri());

        criteria = new SearchCriteria();
        komos = this.koulutusmoduuliDAO.search(criteria);

        assertTrue(komos.size() > 2);

    }

    @Test
    public void testFindLukiolinja() {
        String KOULUTUSKOODI = "uri:yotutkinto";
        String LUKIOLINJA1 = "uri:lukiolinja1";
        String LUKIOLINJA2 = "uri:lukiolinja2";

        //KOMO1
        Koulutusmoduuli koulutus = fixtures.createTutkintoOhjelma();
        koulutus.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        koulutus.setKoulutusUri(KOULUTUSKOODI);
        koulutus.setLukiolinjaUri(LUKIOLINJA1);
        koulutus.setKoulutusohjelmaUri(null);
        koulutusmoduuliDAO.insert(koulutus);

        //KOMO2
        Koulutusmoduuli koulutus1 = fixtures.createTutkintoOhjelma();
        koulutus1.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        koulutus1.setKoulutusUri(KOULUTUSKOODI);
        koulutus1.setLukiolinjaUri(LUKIOLINJA2);
        koulutus1.setKoulutusohjelmaUri(null);
        koulutusmoduuliDAO.insert(koulutus1);

        Koulutusmoduuli res = koulutusmoduuliDAO.findLukiolinja(KOULUTUSKOODI, LUKIOLINJA1);
        assertTrue(res.getLukiolinjaUri().equals(LUKIOLINJA1));

        res = koulutusmoduuliDAO.findLukiolinja(KOULUTUSKOODI, LUKIOLINJA2);
        assertTrue(res.getLukiolinjaUri().equals(LUKIOLINJA2));
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
        m.setKoulutusUri("123456");
        m.setTutkintoUri("JUnit Tutkinto");
        m.setOid("http://oid/12345");
        return m;

    }

    private void initData() {

        fixtures.deleteAll();

        Koulutusmoduuli t;
        Koulutusmoduuli o;

        for (int i = 0; i < NUM_TUTKINTO_TOP_LEVEL_MODUULI; i++) {

            t = fixtures.createTutkintoOhjelma();
            t.setNimi(TarjontaFixtures.createText("Tutkinto-ohjelma " + i, null, null));
            t.setOid(String.valueOf(i));
            koulutusmoduuliDAO.insert(t);

            for (int j = 0; j < NUM_TUTKINTO_SECOND_LEVEL_MODUULI; j++) {

                o = fixtures.createTutkinnonOsa();
                o.setNimi(TarjontaFixtures.createText("Tutkinnon osa: " + i + "." + j, null, null));
                o.setOid(i + "." + j);
                koulutusmoduuliDAO.insert(o);
                sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(t, o, KoulutusSisaltyvyys.ValintaTyyppi.SOME_OFF));

            }

        }

    }

}
