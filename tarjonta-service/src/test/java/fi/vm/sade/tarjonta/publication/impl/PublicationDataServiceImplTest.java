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

import com.google.common.collect.Lists;
import fi.vm.sade.security.SadeUserDetailsWrapper;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.Tila;
import fi.vm.sade.tarjonta.publication.Tila.Tyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@Transactional
public class PublicationDataServiceImplTest extends TestUtilityBase {

    public static final String TEST_USER_OID = "1.2.246.562.24.64735725450";

    private PublicationDataServiceImpl publicationDataService;
    private Koulutusmoduuli komo1, komo2, komo3;
    private KoulutusmoduuliToteutus komoto1;
    private Haku haku1;
    private Hakukohde hakukohde1;
    @PersistenceContext
    public EntityManager em;
    
    @Before
    public void setUp() {
        em.clear();

        setCurrentUser(TEST_USER_OID, getAuthority("APP_TARJONTA_CRUD", "test.user.oid.123"));

        //We could autowire the class, but then a 
        //test debug mode (at least in Netbean) fails.
        publicationDataService = new PublicationDataServiceImpl();
        Whitebox.setInternalState(publicationDataService, "em", em);
        Whitebox.setInternalState(publicationDataService, "hakukohdeDAO", hakukohdeDao);
        Whitebox.setInternalState(publicationDataService, "komotoDAO", komotoDao);

        TarjontaFixtures fixtures = new TarjontaFixtures();
        fixtures.recreate();

        komo1 = fixtures.createTutkintoOhjelma();
        komo2 = fixtures.createTutkintoOhjelma();
        komo3 = fixtures.createTutkintoOhjelma();

        komoto1 = fixtures.createTutkintoOhjelmaToteutus();
        haku1 = fixtures.createHaku();
        hakukohde1 = fixtures.createHakukohde();

        //set komo ref to komoto and vice versa
        komoto1.setKoulutusmoduuli(komo1);
        komo1.addKoulutusmoduuliToteutus(komoto1);

        //add komoto ref to hakukohde and vice versa
        komoto1.addHakukohde(hakukohde1);
        hakukohde1.addKoulutusmoduuliToteutus(komoto1);

        //set hakukohde ref to haku and vice versa
        haku1.addHakukohde(hakukohde1);
        hakukohde1.setHaku(haku1);

        //Persist all entityes used in the test.
        insertMergeDetach(true);
    }

    @Test
    public void testNewKoulutusmoduuliIsInSuunnitteluState() {
        quickObjectStatusChange(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);

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

        List<Tila> tilat = Lists.newArrayList();
        tilat.add(new Tila(Tyyppi.KOMO, TarjontaTila.VALMIS, k2.getOid()));
        tilat.add(new Tila(Tyyppi.KOMO, TarjontaTila.JULKAISTU, k3.getOid()));
        
        publicationDataService.updatePublicationStatus(tilat);

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
        quickObjectStatusChange(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);
        List result = publicationDataService.listKoulutusmoduuliToteutus();

        assertEquals(0, result.size());

        quickObjectStatusChange(TarjontaTila.LUONNOS, TarjontaTila.JULKAISTU);

        result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(0, result.size());

        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.LUONNOS);

        result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(0, result.size());

        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);

        result = publicationDataService.listKoulutusmoduuliToteutus();
        assertEquals(1, result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testisValidStateChangeNullParam() {
        publicationDataService.isValidStatusChange(null);
    }

    @Test
    public void testisValidStateChangeKOMO() {

        //set the base state
        quickObjectStatusChange(TarjontaTila.LUONNOS, TarjontaTila.LUONNOS);

        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.LUONNOS,  komo1.getOid())));

        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.VALMIS,  komo1.getOid())));

        quickObjectStatusChange(TarjontaTila.VALMIS, TarjontaTila.VALMIS);
        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.VALMIS,  komo1.getOid())));

        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.JULKAISTU,  komo1.getOid())));

        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.JULKAISTU,  komo1.getOid())));

        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.PERUTTU,  komo1.getOid())));

        quickObjectStatusChange(TarjontaTila.PERUTTU, TarjontaTila.PERUTTU);
        assertEquals(true, publicationDataService.isValidStatusChange(new Tila(Tyyppi.KOMO, TarjontaTila.PERUTTU,  komo1.getOid())));
    }

    /**
     * Test of updatePublicationStatus method, of class
     * PublicationDataServiceImpl.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePublicationStatus() {
        publicationDataService.updatePublicationStatus(null);
    }

    @Test
    public void testSearchHakukohteetByHakuOid() {
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);

        ArrayList<String> hakuOids = new ArrayList<String>();
        hakuOids.add(haku1.getOid());

        List<Long> hakukohteetIds = hakukohdeDao.searchHakukohteetByHakuOid(hakuOids, TarjontaTila.JULKAISTU);
        assertEquals(1, hakukohteetIds.size());
        //assertEquals(komoto1.getOid(), hakukohteetIds.get(0).getKoulutusmoduuliToteutuses().iterator().next().getOid());

        hakukohteetIds = hakukohdeDao.searchHakukohteetByHakuOid(hakuOids, TarjontaTila.LUONNOS);
        assertEquals(0, hakukohteetIds.size());
    }

    @Test
    public void testiHakuPublish() {
        List<Tila> tilat = Lists.newArrayList(new Tila(Tyyppi.HAKU, TarjontaTila.JULKAISTU, haku1.getOid()));

        //Happy path, publish everything
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS);
        Date compareDate = getCompareDate();
        publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        checkLastUpdatedFields(true, true, true, compareDate);

        //partial publish - only the haku and hakukohde is published
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS, TarjontaTila.LUONNOS, TarjontaTila.VALMIS);
        compareDate = getCompareDate();
        publicationDataService.updatePublicationStatus(tilat);

        //koulutusohjelma not published as it's still luonnos
        check(TarjontaTila.JULKAISTU, TarjontaTila.LUONNOS, TarjontaTila.JULKAISTU);
        checkLastUpdatedFields(true, false, true, compareDate);
    }

    @Test
    public void testiHakuCancel() {
        List<Tila> tilat = Lists.newArrayList(new Tila(Tyyppi.HAKU, TarjontaTila.PERUTTU, haku1.getOid()));

        //set the base state to ready
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS);
        //cancel haku and hakukohteet
        publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.PERUTTU, TarjontaTila.VALMIS, TarjontaTila.VALMIS);

        //same as above, but toteutus has a different status
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.PERUTTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
    }

    @Test
    public void testiToteutusPublish() {
        List<Tila> tilat = Lists.newArrayList(new Tila(Tyyppi.KOMOTO, TarjontaTila.JULKAISTU, komoto1.getOid()));


        /*
         * Haku not yet ready => no status change for hakukohde.
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS); //set the base state
        Date compareDate = getCompareDate();

        Tilamuutokset tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.VALMIS, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS);
        Assert.assertSame(0,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(1,tilamuutokset.getMuutetutKomotot().size());
        checkLastUpdatedFields(false, true, false, compareDate);

        /*
         * The happy path - in this case also the hakukohde will be published
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS, TarjontaTila.VALMIS);
        compareDate = getCompareDate();
        tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        checkLastUpdatedFields(false, true, true, compareDate);
        check(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        Assert.assertSame(1,tilamuutokset.getMuutetutKomotot().size());
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());

        /*
         * The partial path - only toteutus is published
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.LUONNOS, TarjontaTila.VALMIS, TarjontaTila.LUONNOS);
        compareDate = getCompareDate();
        tilamuutokset =  publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.LUONNOS, TarjontaTila.JULKAISTU, TarjontaTila.LUONNOS);
        checkLastUpdatedFields(false, true, false, compareDate);
        Assert.assertSame(0,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(1,tilamuutokset.getMuutetutKomotot().size());
    }

    @Test
    public void testiHakukohdePublish() {
        
        List<Tila> tilat = Lists.newArrayList(new Tila(Tyyppi.HAKUKOHDE, TarjontaTila.JULKAISTU, hakukohde1.getOid()));


        /*
         * Haku not yet ready => no status change for koulutus.
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS); //set the base state
        Date compareDate = getCompareDate();

        Tilamuutokset tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.VALMIS, TarjontaTila.VALMIS, TarjontaTila.JULKAISTU);
        checkLastUpdatedFields(false, false, true, compareDate);
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(0,tilamuutokset.getMuutetutKomotot().size());

        /*
         * The happy path - in this case the koulutus will be published
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS, TarjontaTila.VALMIS);
        compareDate = getCompareDate();
        tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        em.flush();
        check(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        checkLastUpdatedFields(false, true, true, compareDate);
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(1,tilamuutokset.getMuutetutKomotot().size());

        /*
         * The partial path - only hakukohdeis published
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.LUONNOS, TarjontaTila.VALMIS, TarjontaTila.LUONNOS);
        compareDate = getCompareDate();
        tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.LUONNOS, TarjontaTila.VALMIS, TarjontaTila.JULKAISTU);
        checkLastUpdatedFields(false, false, true, compareDate);
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(0,tilamuutokset.getMuutetutKomotot().size());
    }

    
    @Test
    public void testiHakukohdeCancel() {
        List<Tila> tilat = Lists.newArrayList(new Tila(Tyyppi.HAKUKOHDE, TarjontaTila.PERUTTU, hakukohde1.getOid()));


        /*
         * Haku not yet ready => no status change for koulutus.
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU); //set the base state
        Date compareDate = getCompareDate();

        Tilamuutokset tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.VALMIS, TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU);
        checkLastUpdatedFields(false, false, true, compareDate);
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(0,tilamuutokset.getMuutetutKomotot().size());

        /*
         * The happy path - in this case the koulutus will be cancelled
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU); //set the base state
        compareDate = getCompareDate();
        tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        em.flush();
        check(TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU, TarjontaTila.PERUTTU);
        checkLastUpdatedFields(false, true, true, compareDate);
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(1,tilamuutokset.getMuutetutKomotot().size());

        /*
         * The partial path - only hakukohdeis published
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS, TarjontaTila.JULKAISTU); //set the base state
        compareDate = getCompareDate();
        tilamuutokset = publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.JULKAISTU, TarjontaTila.VALMIS, TarjontaTila.PERUTTU);
        checkLastUpdatedFields(false, false, true, compareDate);
        Assert.assertSame(1,tilamuutokset.getMuutetutHakukohteet().size());
        Assert.assertSame(0,tilamuutokset.getMuutetutKomotot().size());
    }

    private Date getCompareDate() {
        Date r = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
        try {
            Thread.currentThread().sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return r;
    }

    @Test
    public void testiToteutusCancel() {
        
        List<Tila> tilat = Lists.newArrayList(new Tila(Tyyppi.KOMOTO, TarjontaTila.PERUTTU, komoto1.getOid()));

        /*
         * Cancel tutkinto(published -> cancelled) and hakukohde(ready -> cancelled).
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS);
        publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU, TarjontaTila.VALMIS);

        /*
         * Cancel tutkinto(published -> cancelled) and hakukohde(published -> cancelled).
         */
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU);
        publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU, TarjontaTila.PERUTTU);
        
        quickObjectStatusChange(TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.JULKAISTU, TarjontaTila.VALMIS);
        publicationDataService.updatePublicationStatus(tilat);
        check(TarjontaTila.JULKAISTU, TarjontaTila.PERUTTU, TarjontaTila.VALMIS);
    }

    private void insertMergeDetach(final boolean persist) {

        if (persist) {
            em.persist(komo1);
            em.persist(komo2);
            em.persist(komo3);
            em.persist(komoto1);
            em.persist(haku1);
            em.persist(hakukohde1);
        } else {
            komo1 = em.merge(komo1);
            komo2 = em.merge(komo2);
            komo3 = em.merge(komo3);
            komoto1 = em.merge(komoto1);
            haku1 = em.merge(haku1);
            hakukohde1 = em.merge(hakukohde1);
        }

        flush();
    }

    private void flush() {
        em.flush();

        em.detach(komo1);
        em.detach(komo2);
        em.detach(komo3);
        em.detach(komoto1);
        em.detach(haku1);
        em.detach(hakukohde1);
    }

    private void check(TarjontaTila statusHaku, TarjontaTila statusToteutus, TarjontaTila statusHakukohde) {
        Haku h1 = em.find(Haku.class, haku1.getId());
        Hakukohde hk1 = em.find(Hakukohde.class, hakukohde1.getId());
        KoulutusmoduuliToteutus k1 = em.find(KoulutusmoduuliToteutus.class, komoto1.getId());

        em.detach(h1);
        em.detach(hk1);
        em.detach(k1);

        //haku not yet ready.
        assertEquals("haku", statusHaku, h1.getTila());
        assertEquals("toteutus", statusToteutus, k1.getTila());
        assertEquals("hakukohde", statusHakukohde, hk1.getTila());
    }

    private void checkLastUpdatedFields(boolean haku, boolean toteutus, boolean hakukohde, Date compareDate) {
        Haku h1 = em.find(Haku.class, haku1.getId());
        Hakukohde hk1 = em.find(Hakukohde.class, hakukohde1.getId());
        KoulutusmoduuliToteutus k1 = em.find(KoulutusmoduuliToteutus.class, komoto1.getId());

        em.detach(h1);
        em.detach(hk1);
        em.detach(k1);

        if (haku) {
            assertNotNull("haku last update date", h1.getLastUpdateDate());
            Assert.assertTrue("haku last update date", h1.getLastUpdateDate().getTime() > compareDate.getTime());
            assertEquals("haku last update by oid", TEST_USER_OID, h1.getLastUpdatedByOid());
        } else {
            Assert.assertNull("haku last update date", h1.getLastUpdateDate());
            Assert.assertNull("haku last update by oid was not null!!", h1.getLastUpdatedByOid());
        }

        if (toteutus) {
            assertNotNull("toteutus last update date", k1.getUpdated());
            Assert.assertTrue("toteutus last update date not after compare date: " + k1.getUpdated().getTime() + "!>=" + compareDate.getTime(), k1.getUpdated().getTime()>compareDate.getTime());
//            Assert.assertNotSame("toteutus last update date not set, it's zero!!", 0, k1.getUpdated().getTime());
            assertEquals("toteutus last update by oid", TEST_USER_OID, k1.getLastUpdatedByOid());
        } else {
            Assert.assertTrue("toteutus last update date", k1.getUpdated().before(compareDate));
            Assert.assertNull("toteutus last update by oid was not null", k1.getLastUpdatedByOid());
        }

        if (hakukohde) {
            assertNotNull("hakukohde last update date", hk1.getLastUpdateDate());
            assertEquals("hakukohde last update by oid", TEST_USER_OID, hk1.getLastUpdatedByOid());
            Assert.assertTrue("hakukohde last update date", hk1.getLastUpdateDate()==null || hk1.getLastUpdateDate().getTime()>compareDate.getTime());
        } else {
            Assert.assertNull("hakukohde last update date was set!", hk1.getLastUpdateDate());
            Assert.assertNull("hakukohde last update by oid", hk1.getLastUpdatedByOid());
        }
    }

    
    private void quickObjectStatusChange(TarjontaTila komoStatus, TarjontaTila otherStatus) {
        _quickObjectStatusChange(komoStatus, otherStatus);
    }

    private void quickObjectStatusChange(TarjontaTila komoStatus, TarjontaTila hakuStatus, TarjontaTila toteutusStatus, TarjontaTila hakukohdestatus) {
        _quickObjectStatusChange(komoStatus, toteutusStatus, hakuStatus, hakukohdestatus);
    }

    /**
     * Test helper method. Initializes demo data to given status.
     *
     * @param komoStatus
     * @param otherStatus
     */
    private void _quickObjectStatusChange(TarjontaTila komoStatus, TarjontaTila... otherStatus) {
        haku1 = em.find(Haku.class, haku1.getId());
        hakukohde1 = em.find(Hakukohde.class, hakukohde1.getId());
        komo1 = em.find(Koulutusmoduuli.class, komo1.getId());
        komoto1 = em.find(KoulutusmoduuliToteutus.class, komoto1.getId());

        haku1.setLastUpdateDate(null);
        haku1.setLastUpdatedByOid(null);
        komoto1.getUpdated().setTime(0);
        komoto1.setLastUpdatedByOid(null);
        hakukohde1.setLastUpdateDate(null);
        hakukohde1.setLastUpdatedByOid(null);

        komo1.setTila(komoStatus);
        if (otherStatus.length == 3) {
            komoto1.setTila(otherStatus[0]);
            haku1.setTila(otherStatus[1]);
            hakukohde1.setTila(otherStatus[2]);
        } else if (otherStatus.length == 1 && otherStatus != null) {
            final TarjontaTila tila = otherStatus[0];
            komoto1.setTila(tila);
            haku1.setTila(tila);
            hakukohde1.setTila(tila);
        } else {
            throw new RuntimeException("Test data error.");
        }
        insertMergeDetach(false);

        final Haku h1 = em.find(Haku.class, haku1.getId());
        final Hakukohde hk1 = em.find(Hakukohde.class, hakukohde1.getId());
        final KoulutusmoduuliToteutus k1 = em.find(KoulutusmoduuliToteutus.class, komoto1.getId());

        em.detach(h1);
        em.detach(hk1);
        em.detach(k1);

        if (otherStatus.length == 3) {
            assertEquals("komoto", otherStatus[0], k1.getTila());
            assertEquals("haku", otherStatus[1], h1.getTila());
            assertEquals("hakukohde", otherStatus[2], hk1.getTila());
        } else if (otherStatus.length == 1 && otherStatus != null) {
            final TarjontaTila tila = otherStatus[0];
            assertEquals("komoto", tila, k1.getTila());
            assertEquals("haku", tila, h1.getTila());
            assertEquals("hakukohde", tila, hk1.getTila());
        }
    }

    protected final List<GrantedAuthority> getAuthority(String appPermission, String oid) {
        GrantedAuthority orgAuthority = new SimpleGrantedAuthority(String.format("%s", appPermission));
        GrantedAuthority roleAuthority = new SimpleGrantedAuthority(String.format("%s_%s", appPermission, oid));
        return Lists.newArrayList(orgAuthority, roleAuthority);
    }

    protected final void setCurrentUser(final String oid, final List<GrantedAuthority> grantedAuthorities) {
        SadeUserDetailsWrapper sadeUserDetailsWrapper = new SadeUserDetailsWrapper(new UserDetails() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return grantedAuthorities;
            }

            @Override
            public String getPassword() {
                return "no_password";
            }

            @Override
            public String getUsername() {
                return oid;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        }, "FI");
        Authentication auth = new TestingAuthenticationToken(sadeUserDetailsWrapper, null, grantedAuthorities);
        setAuthentication(auth);
    }

    protected final void setAuthentication(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
