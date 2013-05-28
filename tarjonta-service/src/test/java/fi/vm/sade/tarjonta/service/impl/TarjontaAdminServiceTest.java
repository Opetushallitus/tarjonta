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

import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
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
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.*;


import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.OptimisticLockException;

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
@ActiveProfiles("embedded-solr")
@Transactional()
public class TarjontaAdminServiceTest {

    @Autowired
    private TarjontaAdminService adminService;
    @Autowired 
    private TarjontaPublicService publicService;
    
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

    private static final Logger log = LoggerFactory.getLogger(TarjontaAdminServiceTest.class);

    /* Known koulutus that is inserted for each test. */
    private static final String SAMPLE_KOULUTUS_OID = "1.2.3.4.5";

    private static final String SAMPLE_TARJOAJA = "1.2.3.4.5";

    private static final String KOULUTUSKOODI = "uri:koodi1";

    private static final String KOKOODI = "uri:kokoodi1";

    @Before
    public void setUp() {

        kesto3Vuotta = new KoulutuksenKestoTyyppi();
        kesto3Vuotta.setArvo("3");
        kesto3Vuotta.setYksikko("kesto/vuosi");

        insertSampleKoulutus();

    }



    @Test(expected = Exception.class)
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
        PaivitaKoulutusTyyppi update= createPaivitaKoulutusTyyppi();
        update.setVersion(toteutus.getVersion());
        
        adminService.paivitaKoulutus(update);
        toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals("new-value", toteutus.getSuunniteltuKestoArvo());
        assertEquals("new-units", toteutus.getSuunniteltuKestoYksikko());
        assertEquals( fi.vm.sade.tarjonta.model.TarjontaTila.VALMIS, toteutus.getTila());
    }

    @Test
    public void testOptimisticLockingKoulutus() {
        LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
        kysely.setOid(SAMPLE_KOULUTUS_OID);
        LueKoulutusVastausTyyppi vastaus = publicService.lueKoulutus(kysely);
        assertNotNull(vastaus);
        
        PaivitaKoulutusTyyppi update = createPaivitaKoulutusTyyppi();
        
        //update with illegal version
        update.setVersion(100L);
        try {
            adminService.paivitaKoulutus(update);
            fail("Should throw exception!");
        } catch (OptimisticLockException ole){
            //all is good...
        }
        //update with proper version
        update.setVersion(0L);
        adminService.paivitaKoulutus(update);
        vastaus = publicService.lueKoulutus(kysely);
        assertNotNull(vastaus);
        //update with proper version
        update.setVersion(1L);
        adminService.paivitaKoulutus(update);
        vastaus = publicService.lueKoulutus(kysely);
        assertNotNull(vastaus);
    }
    
    @Test
    public void testUpdateKoulutusYhteyshenkilo() {
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals(1, toteutus.getYhteyshenkilos().size());
        assertEquals("Kalle Matti", toteutus.getYhteyshenkilos().iterator().next().getEtunimis());

        PaivitaKoulutusTyyppi createPaivitaKoulutusTyyppi = createPaivitaKoulutusTyyppi();
        createPaivitaKoulutusTyyppi.setVersion(toteutus.getVersion());
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
        	log.debug("Exception thrown: " + ex.getMessage());
        }
        assertTrue(this.koulutusmoduuliToteutusDAO.findAll().size() == komotosOriginalSize);
    }

    @Test
    public void testValintakoeUpdate() {
        Hakukohde hakukohde = fixtures.createHakukohde();



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


        Valintakoe valintakoe = getValintakoe();

        hakukohde.getValintakoes().add(valintakoe);


        hakukohde = this.hakukohdeDAO.insert(hakukohde);

        Hakukohde hk = this.hakukohdeDAO.findHakukohdeByOid(hakukohde.getOid());

        List<Valintakoe> valintakoes = new ArrayList<Valintakoe>(hk.getValintakoes());

        Long valintaKoeId = valintakoes.get(0).getId();

        if (valintaKoeId == null) {
            fail("Valintakoe id was null");
        }

        Valintakoe updatedValintaKoe = getValintakoe();
        updatedValintaKoe.setId(valintaKoeId);
        final String muokattuUri = "uri:muokattu";
        updatedValintaKoe.setTyyppiUri(muokattuUri);
        List<Valintakoe> valintakoeList = new ArrayList<Valintakoe>();
        valintakoeList.add(updatedValintaKoe);
        hakukohdeDAO.updateValintakoe(valintakoeList,hakukohde.getOid());

        hk = this.hakukohdeDAO.findHakukohdeByOid(hakukohde.getOid());

        valintakoes = new ArrayList<Valintakoe>(hk.getValintakoes());
        log.info("Tyyppi uri : {}", valintakoes.get(0).getTyyppiUri());
        assertTrue(valintakoes.get(0).getTyyppiUri().equalsIgnoreCase(muokattuUri));

    }

    private Valintakoe getValintakoe() {
        ValintakoeAjankohta aika = new ValintakoeAjankohta();
        Osoite valOsoite = new Osoite();
        valOsoite.setOsoiterivi1("Katu 12");
        valOsoite.setPostitoimipaikka("Helsinki");
        valOsoite.setPostinumero("00000");
        aika.setAjankohdanOsoite(valOsoite);
        aika.setAlkamisaika(new Date());
        Calendar paatPvm = Calendar.getInstance();
        paatPvm.set(Calendar.YEAR, paatPvm.get(Calendar.YEAR) + 1);
        aika.setPaattymisaika(paatPvm.getTime());
        aika.setLisatietoja("TIETOA");
        Set<ValintakoeAjankohta> ajankohtas = new HashSet<ValintakoeAjankohta>();
        ajankohtas.add(aika);

        Valintakoe valintakoe = new Valintakoe();
        MonikielinenTeksti mo = new MonikielinenTeksti();
        mo.setTekstiKaannos("fi","TESTIA");
        valintakoe.setKuvaus(mo);
        valintakoe.setTyyppiUri("uri:valintakoe");
        valintakoe.setAjankohtas(ajankohtas);

        return valintakoe;
    }
    
    @Test
    public void testCRUDHakukohde() {
    	
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
        	log.debug("Exception thrown");
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
        
        LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
        kysely.setOid(oid2);
        LueHakukohdeVastausTyyppi vastaus = this.publicService.lueHakukohde(kysely);
        assertNotNull(vastaus);

        HakukohdeTyyppi dto = vastaus.getHakukohde();
        assertEquals(0,dto.getPainotettavatOppiaineet().size());
        PainotettavaOppiaineTyyppi oppiaine = new PainotettavaOppiaineTyyppi();
        oppiaine.setOppiaine("foo");
        oppiaine.setPainokerroin(1);
        dto.getPainotettavatOppiaineet().add(oppiaine);
        dto.setVersion(dto.getVersion());
        dto=this.adminService.paivitaHakukohde(dto);
        assertEquals(1,dto.getPainotettavatOppiaineet().size());
        assertEquals("foo",dto.getPainotettavatOppiaineet().get(0).getOppiaine());
        assertEquals(1,dto.getPainotettavatOppiaineet().get(0).getPainokerroin());
        assertNotNull(dto.getPainotettavatOppiaineet().get(0).getPainotettavaOppiaineTunniste());
        assertTrue(dto.getPainotettavatOppiaineet().get(0).getVersion()==0);


        //edit oppiaineet
        PainotettavaOppiaineTyyppi oppiaine2 = new PainotettavaOppiaineTyyppi();
        oppiaine2.setOppiaine("bar");
        oppiaine2.setPainokerroin(9);
        dto.getPainotettavatOppiaineet().add(oppiaine2);
        dto=this.adminService.paivitaHakukohde(dto);
        assertEquals(2,dto.getPainotettavatOppiaineet().size());

        assertTrue(dto.getPainotettavatOppiaineet().get(0).getOppiaine().equals(oppiaine.getOppiaine())
                || dto.getPainotettavatOppiaineet().get(1).getOppiaine().equals(oppiaine.getOppiaine()));
        assertTrue(oppiaine.getPainokerroin() == dto.getPainotettavatOppiaineet().get(0).getPainokerroin()
                || oppiaine.getPainokerroin() == dto.getPainotettavatOppiaineet().get(1).getPainokerroin());
        assertNotNull(dto.getPainotettavatOppiaineet().get(0).getPainotettavaOppiaineTunniste());
        assertTrue(dto.getPainotettavatOppiaineet().get(0).getVersion()==0);
        
        dto.getPainotettavatOppiaineet().remove(0);
        dto=this.adminService.paivitaHakukohde(dto);
        assertEquals(1,dto.getPainotettavatOppiaineet().size());

//        assertEquals(oppiaine2.getOppiaine(),dto.getPainotettavatOppiaineet().get(0).getOppiaine());
//        assertEquals(oppiaine2.getPainokerroin(),dto.getPainotettavatOppiaineet().get(0).getPainokerroin());
        assertNotNull(dto.getPainotettavatOppiaineet().get(0).getPainotettavaOppiaineTunniste());
        assertTrue(dto.getPainotettavatOppiaineet().get(0).getVersion()==0);
        
        dto.getPainotettavatOppiaineet().remove(0);
        dto=this.adminService.paivitaHakukohde(dto);
        assertEquals(0,dto.getPainotettavatOppiaineet().size());

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

        KoulutusmoduuliKoosteTyyppi koulutusmoduuliT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliT.setOid(oid);
        koulutusmoduuliT.setKoulutuskoodiUri(KOULUTUSKOODI);
        koulutusmoduuliT.setKoulutusohjelmakoodiUri(KOKOODI);
        koulutusmoduuliT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusmoduuliT.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliT);

        SearchCriteria sc = new SearchCriteria();
        sc.setKoulutusKoodi(KOULUTUSKOODI);
        sc.setKoulutusohjelmaKoodi(KOKOODI);
        Koulutusmoduuli komo = this.koulutusmoduuliDAO.search(sc).get(0);
        assertEquals(KOULUTUSKOODI, komo.getKoulutusKoodi());

    }
    
    @Test
    public void testLisaaLukiokoulutusmoduuliHappyPath() {
        String oidParent = "oid:" + System.currentTimeMillis();
        String oidChild = oidParent + 2;
        String LUKIOTUTKINTO = "yoTutkinto11";
        String LUKIOLINJA = "jokuMediaLinja";
        
        KoulutusmoduuliKoosteTyyppi koulutusmoduuliParentT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliParentT.setOid(oidParent);
        koulutusmoduuliParentT.setKoulutuskoodiUri(LUKIOTUTKINTO);
        //koulutusmoduuliT.//setKoulutusohjelmakoodiUri(KOKOODI);
        koulutusmoduuliParentT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusmoduuliParentT.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliParentT);
        
        KoulutusmoduuliKoosteTyyppi koulutusmoduuliChildT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliChildT.setOid(oidChild);
        koulutusmoduuliChildT.setParentOid(oidParent);
        koulutusmoduuliChildT.setKoulutuskoodiUri(LUKIOTUTKINTO);
        koulutusmoduuliChildT.setLukiolinjakoodiUri(LUKIOLINJA);
        koulutusmoduuliChildT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusmoduuliChildT.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliChildT);
        
        Koulutusmoduuli child = koulutusmoduuliDAO.findLukiolinja(LUKIOTUTKINTO, LUKIOLINJA);
        assertTrue(child.getOid().equals(oidChild));
    }
    
    @Test
    public void testLisaaLukiokoulutusHappyPath() {
        String oidParent = "oid:" + System.currentTimeMillis();
        String oidChild = oidParent + 2;
        String komotoOid = oidChild + 2;
        
        String LUKIOTUTKINTO = "yoTutkinto11";
        String LUKIOLINJA = "jokuMediaLinja";
        
        KoulutusmoduuliKoosteTyyppi koulutusmoduuliParentT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliParentT.setOid(oidParent);
        koulutusmoduuliParentT.setKoulutuskoodiUri(LUKIOTUTKINTO);
        //koulutusmoduuliT.//setKoulutusohjelmakoodiUri(KOKOODI);
        koulutusmoduuliParentT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusmoduuliParentT.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliParentT);
        
        KoulutusmoduuliKoosteTyyppi koulutusmoduuliChildT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliChildT.setOid(oidChild);
        koulutusmoduuliChildT.setParentOid(oidParent);
        koulutusmoduuliChildT.setKoulutuskoodiUri(LUKIOTUTKINTO);
        koulutusmoduuliChildT.setLukiolinjakoodiUri(LUKIOLINJA);
        koulutusmoduuliChildT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusmoduuliChildT.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliChildT);
        
        Koulutusmoduuli child = koulutusmoduuliDAO.findLukiolinja(LUKIOTUTKINTO, LUKIOLINJA);
        assertTrue(child.getOid().equals(oidChild));
        
        LisaaKoulutusTyyppi koulutusTyyppi = createSampleKoulutus();
        koulutusTyyppi.setOid(komotoOid);
        koulutusTyyppi.setKoulutusKoodi(createKoodi(LUKIOTUTKINTO));
        koulutusTyyppi.setKoulutusohjelmaKoodi(null);
        koulutusTyyppi.setLukiolinjaKoodi(createKoodi(LUKIOLINJA));
        koulutusTyyppi.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        adminService.lisaaKoulutus(koulutusTyyppi);
        
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(komotoOid);
        assertTrue(komoto != null);
    }
    
    

    private Date getDateFromString(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return sdf.parse(dateStr);
        } catch (Exception exp) {
            return new Date();
        }
    }

    @Test
    public void testKoulutusKopiointiTarkistus() {



       TarkistaKoulutusKopiointiTyyppi kysely1 = new TarkistaKoulutusKopiointiTyyppi();

        kysely1.setKoulutusAlkamisPvm(getDateFromString("01.02.2013"));

        kysely1.setKoulutusLuokitusKoodi("321101");
        kysely1.setKoulutusohjelmaKoodi("1603");
        kysely1.setPohjakoulutus("koulutusaste/lukio");
        kysely1.getKoulutuslajis().add("koulutuslaji/lahiopetus");
        kysely1.getOpetuskielis().add("opetuskieli/fi");
        kysely1.setTarjoajaOid(SAMPLE_TARJOAJA);

        boolean kopiointiSallittu =  adminService.tarkistaKoulutuksenKopiointi(kysely1);

        TarkistaKoulutusKopiointiTyyppi kysely2 = new TarkistaKoulutusKopiointiTyyppi();
        kysely2.setKoulutusLuokitusKoodi("321101");
        kysely2.setKoulutusohjelmaKoodi("1603");
        kysely2.getKoulutuslajis().add("koulutuslaji/lahiopetus");
        kysely2.getOpetuskielis().add("opetuskieli/fi");
        kysely2.setKoulutusAlkamisPvm(getDateFromString("02.08.2013"));
        kysely2.setTarjoajaOid(SAMPLE_TARJOAJA);
        kysely2.setPohjakoulutus("koulutusaste/lukio");

        boolean  kopiontiSallittu2 = adminService.tarkistaKoulutuksenKopiointi(kysely2);

        assertFalse(kopiointiSallittu);
        assertTrue(kopiontiSallittu2);
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
        lisaaKoulutus.setPohjakoulutusvaatimus(createKoodi("koulutusaste/lukio"));
        lisaaKoulutus.setTarjoaja(SAMPLE_TARJOAJA);
        lisaaKoulutus.setOid(SAMPLE_KOULUTUS_OID);
        lisaaKoulutus.setKoulutuksenAlkamisPaiva(getDateFromString("02.02.2013"));
        lisaaKoulutus.setKesto(kesto3Vuotta);
        lisaaKoulutus.getYhteyshenkilo().add(createYhteyshenkilo());
        lisaaKoulutus.getLinkki().add(createLinkki("google", null, "http://google.com"));
        lisaaKoulutus.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);


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
    
    //Testing that komoto hierarchy works as expected
    @Test
    public void testKomotoHierarchyUpdate() {
        //Creating a simple komoto hierarchy with parent komoto and two child komotos
        String TARJOAJA_OID1 = "jokin.tarjoaja.oid.1";
        String POHJAKOULUTUSVAATIMUS = "http://vaatimus.fi/yo";
        String KOMOTO_OID1 = "jokin.KOMOTO.oid.1.1.12.2." + System.currentTimeMillis();
        String KOMOTO_OID2 = KOMOTO_OID1 + "1";
        String PARENT_KOMOTO_OID1 = KOMOTO_OID1 + "2";
        createSimpleKomotoHierarchy(KOMOTO_OID1, KOMOTO_OID2, PARENT_KOMOTO_OID1, TARJOAJA_OID1, POHJAKOULUTUSVAATIMUS);
        
        //Creating a simple komoto hierarchy with parent komoto and two child komotos for another tarjoaja
        String TARJOAJA_OID2 = "jokin.tarjoaja.oid.2";
        String KOMOTO_OID3= "jokin.KOMOTO.oid.1.1.12.3." + System.currentTimeMillis();
        String KOMOTO_OID4 = KOMOTO_OID3 + "1";
        String PARENT_KOMOTO_OID2 = KOMOTO_OID3 + "2";
        createSimpleKomotoHierarchy(KOMOTO_OID3, KOMOTO_OID4, PARENT_KOMOTO_OID2, TARJOAJA_OID2, POHJAKOULUTUSVAATIMUS);
        
        //Reading a child komoto from the first komoto hierarchy and updating its koulutuksenAlkamisPvm (a parent komoto field) 
        LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
        kysely.setOid(KOMOTO_OID1);
        LueKoulutusVastausTyyppi vastaus = this.publicService.lueKoulutus(kysely);
        Calendar calDate = Calendar.getInstance();
        calDate.set(Calendar.MONTH, calDate.get(Calendar.MONDAY) + 1); //Changing the month of the date
        Date updatedAlkamisPvm = calDate.getTime();
        PaivitaKoulutusTyyppi paivita = convertLueToPaivita(vastaus);
        paivita.setKoulutuksenAlkamisPaiva(updatedAlkamisPvm);
        paivita.setVersion(vastaus.getVersion());
        adminService.paivitaKoulutus(paivita);
        
        //Verifying that all komotos in the hierarchy have the updated date
        LueKoulutusKyselyTyyppi kysely1 = new LueKoulutusKyselyTyyppi();
        kysely1.setOid(KOMOTO_OID1);
        LueKoulutusVastausTyyppi komoto = publicService.lueKoulutus(kysely1);
        assertTrue(komoto.getKoulutuksenAlkamisPaiva().toGregorianCalendar().get(Calendar.MONTH) == calDate.get(Calendar.MONTH));
        kysely1.setOid(KOMOTO_OID2);
        komoto = publicService.lueKoulutus(kysely1);
        assertTrue(komoto.getKoulutuksenAlkamisPaiva().getMonth() == calDate.get(Calendar.MONTH));
        kysely1.setOid(PARENT_KOMOTO_OID1);
        komoto = publicService.lueKoulutus(kysely1);
        assertTrue(komoto.getKoulutuksenAlkamisPaiva().getMonth() == calDate.get(Calendar.MONTH));
        
        //Verifying that a komoto in the other hierarchy has not been updated
        kysely1.setOid(KOMOTO_OID3);
        komoto = publicService.lueKoulutus(kysely1);
        assertTrue(komoto.getKoulutuksenAlkamisPaiva().toGregorianCalendar().get(Calendar.MONTH) != calDate.get(Calendar.MONTH));
    }
    
    private PaivitaKoulutusTyyppi convertLueToPaivita(LueKoulutusVastausTyyppi vastaus) {
        PaivitaKoulutusTyyppi paivita = new PaivitaKoulutusTyyppi();
        paivita.setKesto(vastaus.getKesto());
        paivita.setKoulutusaste(vastaus.getKoulutusaste());
        paivita.setKoulutusKoodi(vastaus.getKoulutusKoodi());
        paivita.setKoulutusohjelmaKoodi(vastaus.getKoulutusohjelmaKoodi());
        paivita.setKoulutusohjelmanValinta(vastaus.getKoulutusohjelmanValinta());
        paivita.setOid(vastaus.getOid());
        paivita.setPohjakoulutusvaatimus(vastaus.getPohjakoulutusvaatimus());
        paivita.setTarjoaja(vastaus.getTarjoaja());
        paivita.setTila(vastaus.getTila());
        paivita.getOpetuskieli().add(vastaus.getOpetuskieli().get(0));
        paivita.getOpetusmuoto().add(vastaus.getOpetusmuoto().get(0));
        return paivita;
    }

    private void createSimpleKomotoHierarchy(String komotoOid1, String komotoOid2, String parentKomotoOid, String tarjoajaOid, String pohjakoulutusvaatimus) {
        Koulutusmoduuli child = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(child);
        Koulutusmoduuli parent = fixtures.createKoulutusmoduuli(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO);
        parent.getAlamoduuliList().add(child);
        koulutusmoduuliDAO.insert(parent);
        KoulutusmoduuliToteutus komotoChild1 = createKomotoWithKomoTarjoajaPohjakoulutus(child, tarjoajaOid, komotoOid1, pohjakoulutusvaatimus);
   
        koulutusmoduuliToteutusDAO.insert(komotoChild1);
        KoulutusmoduuliToteutus komotoChild2 = createKomotoWithKomoTarjoajaPohjakoulutus(child, tarjoajaOid, komotoOid2, pohjakoulutusvaatimus);
        koulutusmoduuliToteutusDAO.insert(komotoChild2);
        KoulutusmoduuliToteutus komotoParent = createKomotoWithKomoTarjoajaPohjakoulutus(parent, tarjoajaOid, parentKomotoOid, pohjakoulutusvaatimus);
        koulutusmoduuliToteutusDAO.insert(komotoParent);
    }
    
    private KoulutusmoduuliToteutus createKomotoWithKomoTarjoajaPohjakoulutus(Koulutusmoduuli komo, String tarjoajaOid, String komotoOid, String pohjakoulutusvaatimus) {

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid(komotoOid);
        komoto.setTila(fi.vm.sade.tarjonta.model.TarjontaTila.LUONNOS);
        komoto.setKoulutusmoduuli(komo);
        komoto.setKoulutusaste("koulutusaste/lukio");
        komoto.setOpetusmuoto(EntityUtils.toKoodistoUriSet(createKoodistoList("opetusmuoto/aikuisopetus")));
        komoto.setOpetuskieli(EntityUtils.toKoodistoUriSet(createKoodistoList("opetuskieli/fi")));
        komoto.setKoulutuslajis(EntityUtils.toKoodistoUriSet(createKoodistoList("koulutuslaji/lahiopetus")));
        komoto.setTarjoaja(tarjoajaOid);
        komoto.setKoulutuksenAlkamisPvm(Calendar.getInstance().getTime());
        komoto.setPohjakoulutusvaatimus("koulutusaste/lukio");
        komoto.setSuunniteltuKesto("kesto/vuosi", "3");
        komoto.setPohjakoulutusvaatimus(pohjakoulutusvaatimus);
        return komoto;
    }
    
    private List<KoodistoKoodiTyyppi> createKoodistoList(String koodiUri) {
        List<KoodistoKoodiTyyppi> opetusmuotos = new ArrayList<KoodistoKoodiTyyppi>();
        opetusmuotos.add(createKoodi(koodiUri));
        return opetusmuotos;
    }
}
