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
package fi.vm.sade.tarjonta.publication.impl;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.TarjontaTila;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jani
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class PublicationDataServiceImplTest {

    private PublicationDataService publicationDataService;
    private Koulutusmoduuli komo1, komo2, komo3;
    private KoulutusmoduuliToteutus komoto1;
    private Haku haku1;
    private Hakukohde hakukohde1;
    private TarjontaFixtures fixtures;
    @PersistenceContext
    public EntityManager em;

    public PublicationDataServiceImplTest() {
    }

    @Before
    public void setUp() {
        em.clear();

        //We could autowire the class, but then a 
        //test debug mode (at least in Netbean) fails.
        publicationDataService = new PublicationDataServiceImpl();
        Whitebox.setInternalState(publicationDataService, "em", em);

        fixtures = new TarjontaFixtures();
        fixtures.recreate();

        komo1 = fixtures.createTutkintoOhjelma();
        komo2 = fixtures.createTutkintoOhjelma();
        komo3 = fixtures.createTutkintoOhjelma();

        komoto1 = fixtures.createTutkintoOhjelmaToteutus();
        komoto1.setKoulutusmoduuli(komo1);
        komo1.addKoulutusmoduuliToteutus(komoto1);

        haku1 = fixtures.createHaku();

        hakukohde1 = fixtures.createHakukohde();
        hakukohde1.setHaku(haku1);
        komoto1.addHakukohde(hakukohde1);
        haku1.addHakukohde(hakukohde1);


        //Persist all entityes used in the test.
        em.persist(komo1);
        em.persist(komo2);
        em.persist(komo3);
        em.persist(komoto1);
        em.persist(haku1);
        em.persist(hakukohde1);
        em.flush();
    }

    @Test
    public void testNewKoulutusmoduuliIsInSuunnitteluState() {
        changeTila(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);

        Koulutusmoduuli k1 = em.find(Koulutusmoduuli.class, komo1.getId());
        Koulutusmoduuli k2 = em.find(Koulutusmoduuli.class, komo2.getId());
        Koulutusmoduuli k3 = em.find(Koulutusmoduuli.class, komo3.getId());
        em.detach(k1);
        em.detach(k2);
        em.detach(k3);

        assertNotNull(k1);
        assertNotNull(k2);
        assertNotNull(k3);

        assertEquals(TarjontaTila.LUONNOS, k1.getTila());
        assertEquals(TarjontaTila.LUONNOS, k2.getTila());
        assertEquals(TarjontaTila.LUONNOS, k3.getTila());

        List<GeneerinenTilaTyyppi> list = new ArrayList<GeneerinenTilaTyyppi>();
        GeneerinenTilaTyyppi g1 = new GeneerinenTilaTyyppi();
        g1.setOid(k2.getOid());
        g1.setSisalto(SisaltoTyyppi.KOMO);
        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.VALMIS);
        list.add(g1);

        GeneerinenTilaTyyppi g2 = new GeneerinenTilaTyyppi();
        g2.setOid(k3.getOid());
        g2.setSisalto(SisaltoTyyppi.KOMO);
        g2.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.JULKAISTU);
        list.add(g2);

        publicationDataService.updatePublicationStatus(list);

        k1 = em.find(Koulutusmoduuli.class, komo1.getId());
        em.detach(k1);
        assertEquals(TarjontaTila.LUONNOS, k1.getTila());

        k2 = em.find(Koulutusmoduuli.class, komo2.getId());
        em.detach(k2);
        assertEquals(TarjontaTila.VALMIS, k2.getTila());

        k3 = em.find(Koulutusmoduuli.class, komo3.getId());
        em.detach(k3);
        assertEquals(TarjontaTila.JULKAISTU, k3.getTila());
    }

    /**
     * Test of listKoulutusmoduuliToteutus method, of class
     * PublicationDataServiceImpl.
     */
    @Test
    public void testListKoulutusmoduuliToteutus() {
        changeTila(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);

        List result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(0, result.size());

        changeTila(TarjontaTila.LUONNOS, TarjontaTila.JULKAISTU);

        result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(0, result.size());

        changeTila(TarjontaTila.JULKAISTU, TarjontaTila.LUONNOS);

        result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(0, result.size());

        changeTila(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);

        result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(1, result.size());
    }

    /**
     * Test of listHakukohde method, of class PublicationDataServiceImpl.
     */
    @Test
    public void testListHakukohde() {
        changeTila(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);
        List result = publicationDataService.listHakukohde();
        assertEquals(0, result.size());

        changeTila(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        result = publicationDataService.listHakukohde();
        assertEquals(1, result.size());
    }

    /**
     * Test of listHaku method, of class PublicationDataServiceImpl.
     */
    @Test
    public void testListHaku() {
        changeTila(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);
        List result = publicationDataService.listHaku();
        assertEquals(0, result.size());

        changeTila(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        result = publicationDataService.listHaku();

        assertEquals(1, result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testisValidStateChangeNullParam() {
        publicationDataService.isValidStateChange(null);
    }

    @Test
    public void testisValidStateChangeKOMO() {

        //set the base state
        changeTila(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);

        GeneerinenTilaTyyppi g1 = new GeneerinenTilaTyyppi();
        g1.setOid(komo1.getOid());
        g1.setSisalto(SisaltoTyyppi.KOMO);

        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.LUONNOS);
        assertEquals(true, publicationDataService.isValidStateChange(g1));

        //set the 'to' state
        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.VALMIS);
        assertEquals(true, publicationDataService.isValidStateChange(g1));

        changeTila(TarjontaTila.VALMIS, TarjontaTila.VALMIS);
        assertEquals(true, publicationDataService.isValidStateChange(g1));

        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.JULKAISTU);
        assertEquals(true, publicationDataService.isValidStateChange(g1));

        changeTila(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.JULKAISTU);
        assertEquals(true, publicationDataService.isValidStateChange(g1));

        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.PERUTTU);
        assertEquals(true, publicationDataService.isValidStateChange(g1));

        changeTila(TarjontaTila.PERUTTU, TarjontaTila.PERUTTU);
        g1.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.PERUTTU);
        assertEquals(false, publicationDataService.isValidStateChange(g1));
    }

    /**
     * Test of updatePublicationStatus method, of class
     * PublicationDataServiceImpl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePublicationStatus() {
        publicationDataService.updatePublicationStatus(null);
    }

    private void changeTila(TarjontaTila tilaKomo, TarjontaTila other) {
        komo1.setTila(tilaKomo);
        komoto1.setTila(other);
        haku1.setTila(other);
        hakukohde1.setTila(other);
        em.merge(komo1);
        em.merge(komo2);
        em.merge(komo3);
        em.merge(komoto1);
        em.merge(haku1);
        em.merge(hakukohde1);
        em.flush();
    }
}
