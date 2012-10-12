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

        // 1. hakukohde
        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setHaku(haku);
        hakukohde.setHakukohdeNimi("Peltikorjaajan perustutkinto");
        hakukohde.setTila(KoodistoContract.TarjontaTilat.JULKAISTU);
        hakukohdeDAO.insert(hakukohde);

        // 1. koulutusmoduuli+toteutus
        koulutusmoduuli = fixtures.createTutkintoOhjelma();
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
        hakukohde.setHaku(haku);
        hakukohde.setTila(KoodistoContract.TarjontaTilat.VALMIS);
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
        // haku1, hakukohde1, koulutusmoduuli2, organisaatioB
        // haku1, hakukohde2, koulutusmoduuli1, organisaatioA
        // haku1, hakukohde2, koulutusmoduuli2, organisaatioB

        assertEquals(4, rivit.size());

        rivi = rivit.get(0);

        haku = rivi.getHaku();
        hakukohde = rivi.getHakukohde();
        koulutus = rivi.getKoulutus();

        assertEquals(YHTEISHAKU, haku.getHakutapa());
        assertEquals("Peltikorjaajan perustutkinto", hakukohde.getNimi());
        assertEquals(KoodistoContract.TarjontaTilat.JULKAISTU, hakukohde.getTila());
        assertEquals(ORGANISAATIO_A, koulutus.getTarjoaja());

        rivi = rivit.get(1);
        haku = rivi.getHaku();
        hakukohde = rivi.getHakukohde();

        assertEquals(YHTEISHAKU, haku.getHakutapa());
        assertEquals("Peltikorjaajan perustutkinto", hakukohde.getNimi());
        assertEquals(KoodistoContract.TarjontaTilat.JULKAISTU, hakukohde.getTila());

        rivi = rivit.get(2);
        haku = rivi.getHaku();
        hakukohde = rivi.getHakukohde();

        assertEquals(YHTEISHAKU, haku.getHakutapa());
        assertEquals("Taidemaalarin erikoistutkinto", hakukohde.getNimi());
        assertEquals(KoodistoContract.TarjontaTilat.VALMIS, hakukohde.getTila());

        rivi = rivit.get(3);
        haku = rivi.getHaku();
        hakukohde = rivi.getHakukohde();

        assertEquals(YHTEISHAKU, haku.getHakutapa());
        assertEquals("Taidemaalarin erikoistutkinto", hakukohde.getNimi());
        assertEquals(KoodistoContract.TarjontaTilat.VALMIS, hakukohde.getTila());

    }

}

