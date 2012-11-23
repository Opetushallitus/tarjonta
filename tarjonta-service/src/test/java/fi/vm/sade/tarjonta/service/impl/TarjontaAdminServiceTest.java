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
package fi.vm.sade.tarjonta.service.impl;

import org.eclipse.jetty.util.log.Log;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO.SearchCriteria;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakuaika;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.*;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Set;

/**
 *
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class TarjontaAdminServiceTest {

    @Autowired
    private TarjontaAdminService adminService;
    @Autowired
    private TarjontaFixtures fixtures;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    private KoulutuksenKestoTyyppi kesto3Vuotta;

    /* Known koulutus that is inserted for each test. */
    private static final String SAMPLE_KOULUTUS_OID = "1.2.3.4.5";

    @Before
    public void setUp() {

        kesto3Vuotta = new KoulutuksenKestoTyyppi();
        kesto3Vuotta.setArvo("3");
        kesto3Vuotta.setYksikko("kesto/vuosi");

        insertSampleKoulutus();

    }

    @Test(expected = TarjontaBusinessException.class)
    public void testCannotCreateKoulutusWithoutKoulutusmoduuli() {

        LisaaKoulutusTyyppi lisaaKoulutus = createSampleKoulutus();

        lisaaKoulutus.setKoulutusKoodi(createKoodi("unknown-uri"));
        lisaaKoulutus.setKoulutusohjelmaKoodi(createKoodi("unknown-uri"));

        adminService.lisaaKoulutus(lisaaKoulutus);

    }

    @Test
    public void testCreateKoulutusHappyPath() throws Exception {

        // sample koulutus has been inserted before this test, check that data is correct
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertNotNull(toteutus);

        Set<Yhteyshenkilo> yhteyshenkilos = toteutus.getYhteyshenkilos();
        assertEquals(1, yhteyshenkilos.size());

        Yhteyshenkilo actualHenkilo = yhteyshenkilos.iterator().next();
        YhteyshenkiloTyyppi expectedHenkilo = createYhteyshenkilo();

        assertMatch(expectedHenkilo, actualHenkilo);
        assertTrue(toteutus.getKoulutusaste().contains("koulutusaste/lukio"));
    }

    @Test
    public void testUpdateKoulutusHappyPath() {
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals(1, toteutus.getYhteyshenkilos().size());
        adminService.paivitaKoulutus(createPaivitaKoulutusTyyppi());
        toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals("new-value", toteutus.getSuunniteltuKestoArvo());
        assertEquals("new-units", toteutus.getSuunniteltuKestoYksikko());
        assertEquals( TarjontaTila.VALMIS, toteutus.getTila());
    }

    @Test
    public void testUpdateKoulutusYhteyshenkilo() {
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals(1, toteutus.getYhteyshenkilos().size());
        assertEquals("Kalle Matti", toteutus.getYhteyshenkilos().iterator().next().getEtunimis());

        PaivitaKoulutusTyyppi createPaivitaKoulutusTyyppi = createPaivitaKoulutusTyyppi();
        createPaivitaKoulutusTyyppi.getYhteyshenkiloTyyppi().add(createAnotherYhteyshenkilo());
        EntityUtils.copyFields(createPaivitaKoulutusTyyppi, toteutus);
        assertEquals(1, toteutus.getYhteyshenkilos().size());

        koulutusmoduuliToteutusDAO.update(toteutus);
        toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals(1, toteutus.getYhteyshenkilos().size());
    }

    @Test
    public void testPoistaKoulutusHappyPath() throws Exception {

    	//Oid to be used in the test to identify a komoto
    	String oid = "54.54.54.54.54.54";
    	
    	
    	//Testing removal of the sample komoto. It does not have a hakukohde so removal should succeed
    	int komotosOriginalSize = this.koulutusmoduuliToteutusDAO.findAll().size();
    	
    	try {
    		this.adminService.poistaKoulutus(SAMPLE_KOULUTUS_OID);
    		assertTrue(komotosOriginalSize == (this.koulutusmoduuliToteutusDAO.findAll().size() + 1));
    	} catch (Exception ex) {
    		fail();
    	}
        
    	
    	//Creating a komoto with hakukohde (this can not be removed so removal should fail)
    	LisaaKoulutusTyyppi lisaaKoulutus = createSampleKoulutus();
        lisaaKoulutus.setOid(oid);
        LisaaKoulutusVastausTyyppi koulutusV = adminService.lisaaKoulutus(lisaaKoulutus);
        
        Hakukohde hakukohde = fixtures.createHakukohde();
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(oid);
        hakukohde.addKoulutusmoduuliToteutus(komoto);
        
        Haku haku = fixtures.createHaku();
        
        Hakuaika hakuaika = new Hakuaika();
        Calendar alkuPvm = Calendar.getInstance();//.getTime();
        hakuaika.setAlkamisPvm(alkuPvm.getTime());
        Calendar loppuPvm = Calendar.getInstance();
        loppuPvm.set(Calendar.YEAR, loppuPvm.get(Calendar.YEAR) + 1);
        hakuaika.setPaattymisPvm(loppuPvm.getTime());
        haku.addHakuaika(hakuaika);
        this.hakuDAO.insert(haku);
        hakukohde.setHaku(haku);
        hakukohde = this.hakukohdeDAO.insert(hakukohde);
        komoto.addHakukohde(hakukohde);
        this.koulutusmoduuliToteutusDAO.update(komoto);
        
        komotosOriginalSize = this.koulutusmoduuliToteutusDAO.findAll().size();
        try {
        	this.adminService.poistaKoulutus(oid);
        	fail();
        } catch (Exception ex) {
        	Log.debug("Exception thrown: " + ex.getMessage());
        }
        assertTrue(this.koulutusmoduuliToteutusDAO.findAll().size() == komotosOriginalSize);
    }
    
    @Test
    public void testPoistaHakukohde() {
    	
    	String oid = "56.56.56.57.57.57";
    	String oid2 = "56.56.56.57.57.58";
    	//Creating a hakukohde with an ongoing hakuaika. Removal should fail
    	Hakukohde hakukohde = fixtures.createHakukohde();
    	hakukohde.setOid(oid);
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        hakukohde.addKoulutusmoduuliToteutus(komoto);
        Haku haku = fixtures.createHaku();
        Hakuaika hakuaika = new Hakuaika();
        Calendar alkuPvm = Calendar.getInstance();//.getTime();
        alkuPvm.set(Calendar.YEAR, alkuPvm.get(Calendar.YEAR) - 1);
        hakuaika.setAlkamisPvm(alkuPvm.getTime());
        Calendar loppuPvm = Calendar.getInstance();
        loppuPvm.set(Calendar.YEAR, loppuPvm.get(Calendar.YEAR) + 1);
        hakuaika.setPaattymisPvm(loppuPvm.getTime());
        haku.addHakuaika(hakuaika);
        haku = this.hakuDAO.insert(haku);
        hakukohde.setHaku(haku);
        this.hakukohdeDAO.insert(hakukohde);
        
        int originalSize = this.hakukohdeDAO.findAll().size();
        
        try {
        	HakukohdeTyyppi hakukohdeT = new HakukohdeTyyppi();
        	hakukohdeT.setOid(oid);
        	this.adminService.poistaHakukohde(hakukohdeT);
        	fail();
        } catch (Exception ex) {
        	Log.debug("Exception thrown");
        }
        assertTrue(this.hakukohdeDAO.findAll().size() == originalSize);
        
        
        //Creating a hakukohde with a hakuaika in the future. Removal should succeed
        hakukohde = fixtures.createHakukohde();
        hakukohde.setOid(oid2);
        hakukohde.addKoulutusmoduuliToteutus(komoto);
        haku = fixtures.createHaku();
        hakuaika = new Hakuaika();
        alkuPvm = Calendar.getInstance();//.getTime();
        alkuPvm.set(Calendar.YEAR, alkuPvm.get(Calendar.YEAR) + 1);
        hakuaika.setAlkamisPvm(alkuPvm.getTime());
        loppuPvm = Calendar.getInstance();
        loppuPvm.set(Calendar.YEAR, loppuPvm.get(Calendar.YEAR) + 2);
        hakuaika.setPaattymisPvm(loppuPvm.getTime());
        haku.addHakuaika(hakuaika);
        haku = this.hakuDAO.insert(haku);
        hakukohde.setHaku(haku);
        this.hakukohdeDAO.insert(hakukohde);
        
        originalSize = this.hakukohdeDAO.findAll().size();
        
        try {
        	HakukohdeTyyppi hakukohdeT = new HakukohdeTyyppi();
        	hakukohdeT.setOid(oid2);
        	this.adminService.poistaHakukohde(hakukohdeT);
        	assertTrue(this.hakukohdeDAO.findAll().size() == (originalSize - 1));
        } catch (Exception ex) {
            fail();
        }
        
    }

    @Test
    public void testLisaaKoulutusmoduuliHappyPath() {
        String oid = "oid:" + System.currentTimeMillis();
        String koulutuskoodi = "uri:koodi1";
        String koKoodi = "uri:kokoodi1";
        KoulutusmoduuliKoosteTyyppi koulutusmoduuliT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliT.setOid(oid);
        koulutusmoduuliT.setKoulutuskoodiUri(koulutuskoodi);
        koulutusmoduuliT.setKoulutusohjelmakoodiUri(koKoodi);
        koulutusmoduuliT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliT);

        SearchCriteria sc = new SearchCriteria();
        sc.setKoulutusKoodi(koulutuskoodi);
        sc.setKoulutusohjelmaKoodi(koKoodi);
        Koulutusmoduuli komo = this.koulutusmoduuliDAO.search(sc).get(0);
        assertEquals(koulutuskoodi, komo.getKoulutusKoodi());

    }

    private void assertMatch(YhteyshenkiloTyyppi expected, Yhteyshenkilo actual) {

        assertEquals(expected.getEtunimet(), actual.getEtunimis());
        assertEquals(expected.getSukunimi(), actual.getSukunimi());
        assertEquals(expected.getHenkiloOid(), actual.getHenkioOid());
        assertEquals(expected.getPuhelin(), actual.getPuhelin());
        assertEquals(expected.getSahkoposti(), actual.getSahkoposti());
        assertEquals(expected.getTitteli(), actual.getTitteli());

    }

    private void insertSampleKoulutus() {

        Koulutusmoduuli moduuli = fixtures.createTutkintoOhjelma();
        moduuli.setKoulutusKoodi("321101");
        moduuli.setKoulutusohjelmaKoodi("1603");
        koulutusmoduuliDAO.insert(moduuli);

        LisaaKoulutusTyyppi lisaaKoulutus = createSampleKoulutus();
        adminService.lisaaKoulutus(lisaaKoulutus);

        flush();

        KoulutusmoduuliToteutus loaded = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertNotNull("koulutus was not inserted by lisaaKoulutus", loaded);

        assertKoulutusEquals(lisaaKoulutus, loaded);


    }

    private void assertKoulutusEquals(LisaaKoulutusTyyppi expected, KoulutusmoduuliToteutus actual) {

        assertEquals(expected.getYhteyshenkilo().size(), actual.getYhteyshenkilos().size());

    }

    private LisaaKoulutusTyyppi createSampleKoulutus() {

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();
        lisaaKoulutus.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.LUONNOS);
        lisaaKoulutus.setKoulutusKoodi(createKoodi("321101"));
        lisaaKoulutus.setKoulutusohjelmaKoodi(createKoodi("1603"));
        lisaaKoulutus.getOpetusmuoto().add(createKoodi("opetusmuoto/aikuisopetus"));
        lisaaKoulutus.getOpetuskieli().add(createKoodi("opetuskieli/fi"));
        lisaaKoulutus.getKoulutuslaji().add(createKoodi("koulutuslaji/lahiopetus"));
        lisaaKoulutus.setKoulutusaste(createKoodi("koulutusaste/lukio"));
        lisaaKoulutus.setOid(SAMPLE_KOULUTUS_OID);
        lisaaKoulutus.setKoulutuksenAlkamisPaiva(new Date());
        lisaaKoulutus.setKesto(kesto3Vuotta);
        lisaaKoulutus.getYhteyshenkilo().add(createYhteyshenkilo());
        lisaaKoulutus.getLinkki().add(createLinkki("google", null, "http://google.com"));


        return lisaaKoulutus;
    }

    private YhteyshenkiloTyyppi createYhteyshenkilo() {
        YhteyshenkiloTyyppi h = new YhteyshenkiloTyyppi();
        h.setEtunimet("Kalle Matti"); // required
        h.setSukunimi("Kettu-Orava"); // required
        h.setHenkiloOid("fake-oid1"); // not recognized via HenkiloService
        h.setPuhelin("+358 123 123 123"); // optional
        h.setSahkoposti(null); // optional
        h.setTitteli(null); // optional
        h.getKielet().add("fi"); // min 1 required (for now)
        return h;
    }

    private YhteyshenkiloTyyppi createAnotherYhteyshenkilo() {
        YhteyshenkiloTyyppi h = new YhteyshenkiloTyyppi();
        h.setEtunimet("John"); // required
        h.setSukunimi("Doe"); // required
        h.setHenkiloOid("fake-oid2"); // not recognized via HenkiloService
        h.setPuhelin("+358 123 456 789"); // optional
        h.setSahkoposti(null); // optional
        h.setTitteli(null); // optional
        h.getKielet().add("en"); // min 1 required (for now)
        return h;
    }

    @Test
    public void testInitSample() {
        adminService.initSample(new String());
    }

    private static KoodistoKoodiTyyppi createKoodi(String uri) {
        KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        return koodi;
    }

    private static WebLinkkiTyyppi createLinkki(String tyyppi, String kieli, String uri) {
        WebLinkkiTyyppi linkki = new WebLinkkiTyyppi();
        linkki.setTyyppi(tyyppi);
        linkki.setKieli(kieli);
        linkki.setUri(uri);
        return linkki;
    }

    private void flush() {
        ((KoulutusmoduuliToteutusDAOImpl) koulutusmoduuliToteutusDAO).getEntityManager().flush();
    }

    private PaivitaKoulutusTyyppi createPaivitaKoulutusTyyppi() {
        PaivitaKoulutusTyyppi paivitaKoulutus = new PaivitaKoulutusTyyppi();
        paivitaKoulutus.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.VALMIS);
        paivitaKoulutus.setOid(SAMPLE_KOULUTUS_OID);

        KoulutuksenKestoTyyppi kesto = new KoulutuksenKestoTyyppi();
        kesto.setArvo("new-value");
        kesto.setYksikko("new-units");
        paivitaKoulutus.setKesto(kesto);

        paivitaKoulutus.setKoulutuksenAlkamisPaiva(new Date());
        paivitaKoulutus.setKoulutusKoodi(createKoodi("do-not-update-this"));
        paivitaKoulutus.setKoulutusohjelmaKoodi(createKoodi("do-not-update-this"));
        paivitaKoulutus.getOpetusmuoto().add(createKoodi("new-opetusmuoto"));
        paivitaKoulutus.getOpetuskieli().add(createKoodi("updated-kieli"));

        return paivitaKoulutus;
    }
}
