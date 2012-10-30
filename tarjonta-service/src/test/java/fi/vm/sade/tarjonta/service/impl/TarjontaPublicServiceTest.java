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

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.TarjontaDatabasePrinter;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.impl.HakuDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakukohdeKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutusKoosteTyyppi;

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
public class TarjontaPublicServiceTest {

    private static final String YHTEISHAKU = "http://hakutapa/yhteishaku";

    private static final String ORGANISAATIO_A = "1.2.3.4.5";

    private static final String ORGANISAATIO_B = "1.2.3.4.6";

    private static final String KOMOTO_OID = "11.12.23.34.56";

    private static final String HAKUKOHDE_OID = "12.13.24.35.57";
    
    private static final String KOULUTUSKOODI = "uri:koulutuskoodi";
	private static final String KOULUTUSOHJELMAKOODI1 = "uri:koulutusohjelmakoodi1";
	private static final String KOULUTUSOHJELMAKOODI2 = "uri:koulutusohjelmakoodi2";

    @Autowired
    private TarjontaPublicService service;

    @Autowired
    private TarjontaFixtures fixtures;

    @Autowired
    private TarjontaDatabasePrinter db;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Before
    public void setUp() {

        fixtures.deleteAll();

        Koulutusmoduuli koulutusmoduuli;
        KoulutusmoduuliToteutus koulutusmoduuliToteutus;

        // jaettu haku
        Haku haku = fixtures.createHaku();
        haku.setNimiFi("yhteishaku 1");
        haku.setHakutapaUri(YHTEISHAKU);
        hakuDAO.insert(haku);

        // 0. koulutusmoduuli+toteutus lisätään testaamaan hakukohteiden haun oikeellisuutta sekä yhden koulutusmoduulin lukua.
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
        koulutusmoduuli.setKoulutusKoodi(KOULUTUSKOODI);
        koulutusmoduuli.setKoulutusohjelmaKoodi(KOULUTUSOHJELMAKOODI1);
        koulutusmoduuliDAO.insert(koulutusmoduuli);
        koulutusmoduuliToteutus = fixtures.createTutkintoOhjelmaToteutus(KOMOTO_OID);
        koulutusmoduuliToteutus.setTarjoaja(ORGANISAATIO_A);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);
        koulutusmoduuliToteutusDAO.insert(koulutusmoduuliToteutus);

        // 1. hakukohde oid is given to be able to test lueHakukohde method.
        Hakukohde hakukohde = fixtures.createHakukohdeWithGivenOid(HAKUKOHDE_OID);//fixtures.createHakukohde();
        hakukohde.setHaku(haku);
        hakukohde.setHakukohdeNimi("Peltikorjaajan perustutkinto");
        hakukohde.setHakukohdeKoodistoNimi("Peltikorjaajan perustutkinto");
        hakukohde.setTila(TarjontaTila.VALMIS);
        hakukohdeDAO.insert(hakukohde);

        // 1. koulutusmoduuli+toteutus
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
        koulutusmoduuli.setKoulutusKoodi(KOULUTUSKOODI);
        koulutusmoduuli.setKoulutusohjelmaKoodi(KOULUTUSOHJELMAKOODI2);
        koulutusmoduuliDAO.insert(koulutusmoduuli);
        koulutusmoduuliToteutus = fixtures.createTutkintoOhjelmaToteutus();
        koulutusmoduuliToteutus.setTarjoaja(ORGANISAATIO_A);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);
        koulutusmoduuliToteutusDAO.insert(koulutusmoduuliToteutus);

        // liitä koulutus hakukohteeseen
        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);
        hakukohdeDAO.update(hakukohde);

        // 2. hakukohde
        hakukohde = fixtures.createHakukohde();
        hakukohde.setHakukohdeNimi("Taidemaalarin erikoistutkinto");
        hakukohde.setHakukohdeKoodistoNimi("Taidemaalarin erikoistutkinto");
        hakukohde.setHaku(haku);
        hakukohde.setTila(TarjontaTila.VALMIS);
        hakukohdeDAO.insert(hakukohde);

        // 2. koulutusmoduuli+toteutus, eri toteuttaja organisaatio
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
        koulutusmoduuliDAO.insert(koulutusmoduuli);
        koulutusmoduuliToteutus = fixtures.createTutkintoOhjelmaToteutus();
        koulutusmoduuliToteutus.setTarjoaja(ORGANISAATIO_B);
        koulutusmoduuliToteutus.setKoulutusmoduuli(koulutusmoduuli);

        // liitä koulutus 2:een hakukohteeseen
        koulutusmoduuliToteutusDAO.insert(koulutusmoduuliToteutus);
        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutus);
        hakukohdeDAO.update(hakukohde);

    }

    @Test
    public void testEtsiHakukohteet() {

        HakukohdeTulos rivi;
        HakuKoosteTyyppi haku;
        HakukohdeKoosteTyyppi hakukohde;
        KoulutusKoosteTyyppi koulutus;

        HaeHakukohteetKyselyTyyppi kysely = new HaeHakukohteetKyselyTyyppi();
        HaeHakukohteetVastausTyyppi vastaus = service.haeHakukohteet(kysely);

        assertNotNull(vastaus);

        List<HakukohdeTulos> rivit = vastaus.getHakukohdeTulos();

        // vastaus pitäisi olla:
        //
        // haku1, hakukohde1, koulutusmoduuli1, organisaatioA
        // haku1, hakukohde2, koulutusmoduuli2, organisaatioB

        assertEquals(2, rivit.size());

        rivi = rivit.get(0);

        haku = rivi.getHaku();
        hakukohde = rivi.getHakukohde();
        koulutus = rivi.getKoulutus();

        assertEquals(YHTEISHAKU, haku.getHakutapa());
        assertEquals("Peltikorjaajan perustutkinto", hakukohde.getNimi());
        assertEquals(TarjontaTila.VALMIS.name(), hakukohde.getTila());
        assertEquals(ORGANISAATIO_A, koulutus.getTarjoaja());

        rivi = rivit.get(1);
        haku = rivi.getHaku();
        hakukohde = rivi.getHakukohde();

        assertEquals(YHTEISHAKU, haku.getHakutapa());
        assertEquals("Taidemaalarin erikoistutkinto", hakukohde.getNimi());
        assertEquals(TarjontaTila.VALMIS.name(), hakukohde.getTila());

    }

    @Test
    public void testEtsiKoulutukset() {

        KoulutusTulos rivi;
        KoulutusKoosteTyyppi koulutus;

        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        HaeKoulutuksetVastausTyyppi vastaus = service.haeKoulutukset(kysely);

        assertNotNull(vastaus);

        List<KoulutusTulos> rivit = vastaus.getKoulutusTulos();

        // vastauksessa pitäisi olla kolme riviä, yksi kullekin koulutukselle

        assertEquals(3, rivit.size());

        rivi = rivit.get(0);

        koulutus = rivi.getKoulutus();

        assertEquals(ORGANISAATIO_A, koulutus.getTarjoaja());

        rivi = rivit.get(1);
        koulutus = rivi.getKoulutus();
        assertEquals(ORGANISAATIO_A, koulutus.getTarjoaja());

        rivi = rivit.get(2);
        koulutus = rivi.getKoulutus();
        assertEquals(ORGANISAATIO_B, koulutus.getTarjoaja());

    }
    
    @Test
    public void testEtsiKoulutusmoduulit() {

        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutuskoodiUri(KOULUTUSKOODI);
        HaeKoulutusmoduulitVastausTyyppi vastaus = service.haeKoulutusmoduulit(kysely);
        
        assertEquals(2, vastaus.getKoulutusmoduuliTulos().size());
        
        kysely.setKoulutusohjelmakoodiUri(KOULUTUSOHJELMAKOODI1);
        vastaus = service.haeKoulutusmoduulit(kysely);
        
        assertEquals(1, vastaus.getKoulutusmoduuliTulos().size());
        assertEquals(KOULUTUSOHJELMAKOODI1, vastaus.getKoulutusmoduuliTulos().get(0).getKoulutusmoduuli().getKoulutusohjelmakoodiUri());
        
        kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        vastaus = service.haeKoulutusmoduulit(kysely);
        
        assertTrue(vastaus.getKoulutusmoduuliTulos().size() > 2);

    }

    @Test
    public void testLueKoulutus() {

    	LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
    	kysely.setOid(KOMOTO_OID);

    	LueKoulutusVastausTyyppi vastaus = service.lueKoulutus(kysely);

        assertNotNull(vastaus);

        assertTrue(vastaus.getOpetuskieli().get(0).getUri().equals("http://kielet/fi"));
        assertEquals(1, vastaus.getYhteyshenkilo().size());

    }

    @Test
    public void testLueHakukohde() {

    	LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
    	kysely.setOid(HAKUKOHDE_OID);

    	LueHakukohdeVastausTyyppi vastaus = service.lueHakukohde(kysely);

        assertNotNull(vastaus);
        assertTrue(vastaus.getHakukohde().getHakukohdeNimi().equals("Peltikorjaajan perustutkinto"));
    }

}

