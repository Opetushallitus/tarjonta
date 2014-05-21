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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.OptimisticLockException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.log.client.LoggerHelper;
import fi.vm.sade.log.client.LoggerMock;
import fi.vm.sade.log.model.Tapahtuma;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
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
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusHakukohteelleTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PainotettavaOppiaineTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.TarkistaKoulutusKopiointiTyyppi;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;

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
public class TarjontaAdminServiceTest extends SecurityAwareTestBase {

    @Autowired
    private TarjontaAdminService adminService;
    @Autowired
    private TarjontaPublicService publicService;
    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;
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
    @Autowired
    private OidService oidService;
    @Autowired
    private ParameterServices parameterService;

    private KoulutuksenKestoTyyppi kesto3Vuotta;
    private static final Logger log = LoggerFactory.getLogger(TarjontaAdminServiceTest.class);

    /* Known koulutus that is inserted for each test. */
    private static final String SAMPLE_KOULUTUS_OID = "1.2.3.4.5";
    private static final String SAMPLE_TARJOAJA = "1.2.3.4.5";
    private static final String KOULUTUSKOODI = "uri:koodi1";
    private static final String KOKOODI = "uri:kokoodi1";

    @Before
    @Override
    public void before() {
        LoggerHelper.init(new LoggerMock());
        try {
            Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn("foofoooid");
        } catch (OIDCreationException e) {
            throw new RuntimeException(e);
        }

        OrganisaatioPerustieto perustieto = new OrganisaatioPerustieto();
        perustieto.setNimi("fi", "org nimi fi");
        perustieto.setOid("1.2.3.4.5");
        Mockito.reset(organisaatioSearchService);
        Mockito.stub(organisaatioSearchService.findByOidSet(Sets.newHashSet("1.2.3.4.5"))).toReturn(Lists.newArrayList(perustieto));
        super.before();
        kesto3Vuotta = new KoulutuksenKestoTyyppi();
        kesto3Vuotta.setArvo("3");
        kesto3Vuotta.setYksikko("kesto/vuosi");
        insertSampleKoulutus();
    }

    @After
    public void doAfter() {
        Tapahtuma t = LoggerHelper.getAuditRootTapahtuma();
        t.setHost("junit");
        t.setSystem("junit");
        t.setTarget("junit");
        t.setTargetType("junit");
        t.setType("JUNIT");
        t.setUser("NA");
        t.setUserActsForUser("NA");

        LoggerHelper.log();
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
        //assertTrue(toteutus.getKoulutusaste().contains("koulutusaste/lukio"));
    }

    @Test
    public void testUpdateKoulutusHappyPath() {
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals(1, toteutus.getYhteyshenkilos().size());
        PaivitaKoulutusTyyppi update = createPaivitaKoulutusTyyppi();
        update.setVersion(toteutus.getVersion());
        adminService.paivitaKoulutus(update);
        toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertEquals("new-value", toteutus.getSuunniteltukestoArvo());
        assertEquals("new-units", toteutus.getSuunniteltukestoYksikkoUri());
        assertEquals(fi.vm.sade.tarjonta.shared.types.TarjontaTila.VALMIS, toteutus.getTila());
    }

    @Test
    public void testUpdateKoulutusNonExistingOrg() {
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        PaivitaKoulutusTyyppi update = createPaivitaKoulutusTyyppi();
        update.setTarjoaja("non.existing.tarjoaja.oid");
        update.setVersion(toteutus.getVersion());
        try {
            adminService.paivitaKoulutus(update);
            fail("should not succeed");
        } catch (Throwable t) {
            assertTrue(t.getMessage().contains("nonexisting.organisation.error"));
        }
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
        } catch (OptimisticLockException ole) {
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
        update.setVersion(2L);
        adminService.paivitaKoulutus(update);
        vastaus = publicService.lueKoulutus(kysely);
        assertNotNull(vastaus);
    }

    @Test
    public void testOptimisticLockingHakukohde() {

        final Hakukohde hk = fixtures.createPersistedHakukohdeWithKoulutus();
        final long version = hk.getVersion();

        final HakukohdeTyyppi hakukohdetyyppi = publicService.lueHakukohde(new LueHakukohdeKyselyTyyppi(hk.getOid())).getHakukohde();
        //update with illegal version
        hakukohdetyyppi.setVersion(999l);

        try {
            adminService.paivitaHakukohde(hakukohdetyyppi);
            fail("Should throw exception!");
        } catch (OptimisticLockException ole) {
            //all is good...
        }

        //proper version
        hakukohdetyyppi.setVersion(version);
        adminService.paivitaHakukohde(hakukohdetyyppi).getVersion();

        //proper version again
        hakukohdetyyppi.setVersion(version+1l);
        adminService.paivitaHakukohde(hakukohdetyyppi);
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

        // Oid to be used in the test to identify a komoto
        String oid = "54.54.54.54.54.54";

        // Testing removal of the sample komoto. It does not have a hakukohde so
        // removal should succeed
        int komotosOriginalSize = this.koulutusmoduuliToteutusDAO.findAll()
                .size();

        this.adminService.poistaKoulutus(SAMPLE_KOULUTUS_OID);
        assertTrue(komotosOriginalSize == (this.koulutusmoduuliToteutusDAO
                .findAll().size() + 1));

        // Creating a komoto with hakukohde (this can not be removed so removal
        // should fail)
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
        valintakoe.setHakukohde(hakukohde);

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
        hakukohdeDAO.updateValintakoe(valintakoeList, hakukohde.getOid());

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
        aika.setValintakoe(valintakoe);

        MonikielinenTeksti mo = new MonikielinenTeksti();
        mo.setTekstiKaannos("fi", "TESTIA");
        valintakoe.setKuvaus(mo);
        valintakoe.setTyyppiUri("uri:valintakoe");
        valintakoe.setAjankohtas(ajankohtas);

        return valintakoe;
    }

    @Test
    public void testCRUDHakukohde() {

        
        String oid;
        String oid2;
        //Creating a hakukohde with an ongoing hakuaika. Removal should fail
        HakukohdeTyyppi hakukohde = fixtures.createHakukohdeTyyppi();
        KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        hakukohde.getHakukohteenKoulutusOidit().add(SAMPLE_KOULUTUS_OID);
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
        
        hakukohde.setHakukohteenHakuOid(haku.getOid());
        hakukohde.setKaytetaanHaunPaattymisenAikaa(Boolean.FALSE);
        
        //stub parameter to forbid addition
        Mockito.stub(parameterService.parameterCanAddHakukohdeToHaku(haku.getOid())).toReturn(false);

        
        try{
            HakukohdeTyyppi hakukohdeTyyppi = adminService.lisaaHakukohde(hakukohde);
            fail("should not suceed!");
        } catch (NotAuthorizedException nae) {
            //expected!
        }

        //stub parameter to allow addition and removals
        Mockito.stub(parameterService.parameterCanAddHakukohdeToHaku(hakukohde.getHakukohteenHakuOid())).toReturn(true);
        Mockito.stub(parameterService.parameterCanRemoveHakukohdeFromHaku(hakukohde.getHakukohteenHakuOid())).toReturn(true);
        
        HakukohdeTyyppi hakukohdeTyyppi = adminService.lisaaHakukohde(hakukohde);
        oid = hakukohdeTyyppi.getOid();
        

        int originalSize = this.hakukohdeDAO.findAll().size();

        try {
            HakukohdeTyyppi hakukohdeT = new HakukohdeTyyppi();
            hakukohdeT.setOid(oid);
            this.adminService.poistaHakukohde(hakukohdeT);
            fail("should not succeed!");
        } catch (HakukohdeUsedException ex) {
            log.debug("Exception thrown");
        }
        assertTrue(this.hakukohdeDAO.findAll().size() == originalSize);


        //Creating a hakukohde with a hakuaika in the future. Removal should succeed
        hakukohde = fixtures.createHakukohdeTyyppi();
        hakukohde.getHakukohteenKoulutusOidit().add(SAMPLE_KOULUTUS_OID);
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
        
        //stub parameters, parameter says no add
        Mockito.stub(parameterService.parameterCanAddHakukohdeToHaku(haku.getOid())).toReturn(false);

        hakukohde.setHakukohteenHakuOid(haku.getOid());
        hakukohde.setKaytetaanHaunPaattymisenAikaa(Boolean.FALSE);
        try {
            HakukohdeTyyppi hk = adminService.lisaaHakukohde(hakukohde);
            fail("should not succeed!");
        } catch (NotAuthorizedException nae) {
            //expected
        }

        //stub parameters, parameter says yes add
        Mockito.stub(parameterService.parameterCanAddHakukohdeToHaku(haku.getOid())).toReturn(true);
        Mockito.stub(parameterService.parameterCanRemoveHakukohdeFromHaku(haku.getOid())).toReturn(true);
        HakukohdeTyyppi hk = adminService.lisaaHakukohde(hakukohde);
        oid2=hk.getOid();

        originalSize = this.hakukohdeDAO.findAll().size();

        LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
        kysely.setOid(oid2);
        LueHakukohdeVastausTyyppi vastaus = this.publicService.lueHakukohde(kysely);
        assertNotNull(vastaus);

        HakukohdeTyyppi dto = vastaus.getHakukohde();
        assertEquals(0, dto.getPainotettavatOppiaineet().size());
        PainotettavaOppiaineTyyppi oppiaine = new PainotettavaOppiaineTyyppi();
        oppiaine.setOppiaine("foo");
        oppiaine.setPainokerroin(1);
        dto.getPainotettavatOppiaineet().add(oppiaine);
        dto.setVersion(dto.getVersion());
        dto = this.adminService.paivitaHakukohde(dto);
        assertEquals(1, dto.getPainotettavatOppiaineet().size());
        assertEquals("foo", dto.getPainotettavatOppiaineet().get(0).getOppiaine());
        assertEquals("painokerroin", "1.0",  "" + dto.getPainotettavatOppiaineet().get(0).getPainokerroin());

        assertNotNull(dto.getPainotettavatOppiaineet().get(0).getPainotettavaOppiaineTunniste());
        assertTrue(dto.getPainotettavatOppiaineet().get(0).getVersion() == 0);

        //edit oppiaineet
        PainotettavaOppiaineTyyppi oppiaine2 = new PainotettavaOppiaineTyyppi();
        oppiaine2.setOppiaine("bar");
        oppiaine2.setPainokerroin(9);
        dto.getPainotettavatOppiaineet().add(oppiaine2);
        dto = this.adminService.paivitaHakukohde(dto);
        assertEquals(2, dto.getPainotettavatOppiaineet().size());

        assertTrue(dto.getPainotettavatOppiaineet().get(0).getOppiaine().equals(oppiaine.getOppiaine())
                || dto.getPainotettavatOppiaineet().get(1).getOppiaine().equals(oppiaine.getOppiaine()));
        assertTrue(oppiaine.getPainokerroin() == dto.getPainotettavatOppiaineet().get(0).getPainokerroin()
                || oppiaine.getPainokerroin() == dto.getPainotettavatOppiaineet().get(1).getPainokerroin());
        assertNotNull(dto.getPainotettavatOppiaineet().get(0).getPainotettavaOppiaineTunniste());
        assertTrue(dto.getPainotettavatOppiaineet().get(0).getVersion() == 0);

        dto.getPainotettavatOppiaineet().remove(0);
        dto = this.adminService.paivitaHakukohde(dto);
        assertEquals(1, dto.getPainotettavatOppiaineet().size());

//        assertEquals(oppiaine2.getOppiaine(),dto.getPainotettavatOppiaineet().get(0).getOppiaine());
//        assertEquals(oppiaine2.getPainokerroin(),dto.getPainotettavatOppiaineet().get(0).getPainokerroin());
        assertNotNull(dto.getPainotettavatOppiaineet().get(0).getPainotettavaOppiaineTunniste());
        assertTrue(dto.getPainotettavatOppiaineet().get(0).getVersion() == 0);

        dto.getPainotettavatOppiaineet().remove(0);
        dto = this.adminService.paivitaHakukohde(dto);
        assertEquals(0, dto.getPainotettavatOppiaineet().size());

        HakukohdeTyyppi hakukohdeT = new HakukohdeTyyppi();
        hakukohdeT.setOid(oid2);
        this.adminService.poistaHakukohde(hakukohdeT);
        assertTrue(this.hakukohdeDAO.findAll().size() == (originalSize - 1));
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
        assertEquals(KOULUTUSKOODI, komo.getKoulutusUri());
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

        boolean kopiointiSallittu = adminService.tarkistaKoulutuksenKopiointi(kysely1);

        TarkistaKoulutusKopiointiTyyppi kysely2 = new TarkistaKoulutusKopiointiTyyppi();
        kysely2.setKoulutusLuokitusKoodi("321101");
        kysely2.setKoulutusohjelmaKoodi("1603");
        kysely2.getKoulutuslajis().add("koulutuslaji/lahiopetus");
        kysely2.getOpetuskielis().add("opetuskieli/fi");
        kysely2.setKoulutusAlkamisPvm(getDateFromString("02.08.2013"));
        kysely2.setTarjoajaOid(SAMPLE_TARJOAJA);
        kysely2.setPohjakoulutus("koulutusaste/lukio");

        boolean kopiontiSallittu2 = adminService.tarkistaKoulutuksenKopiointi(kysely2);

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
        moduuli.setKoulutusUri("321101");
        moduuli.setKoulutusohjelmaUri("1603");
        koulutusmoduuliDAO.insert(moduuli);

        LisaaKoulutusTyyppi lisaaKoulutus = createSampleKoulutus();
        adminService.lisaaKoulutus(lisaaKoulutus);

        flush();

        KoulutusmoduuliToteutus loaded = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertNotNull("koulutus was not inserted by lisaaKoulutus", loaded);

        assertKoulutusEquals(lisaaKoulutus, loaded);


    }

    private void assertKoulutusEquals(LisaaKoulutusTyyppi expected, KoulutusmoduuliToteutus actual) {

        assertEquals(expected.getYhteyshenkiloTyyppi().size(), actual.getYhteyshenkilos().size());

    }

    private LisaaKoulutusTyyppi createSampleKoulutus() {

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();
        lisaaKoulutus.setTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.LUONNOS);
        lisaaKoulutus.setKoulutusKoodi(createKoodi("321101"));
        lisaaKoulutus.setKoulutusohjelmaKoodi(createKoodi("1603"));
        lisaaKoulutus.getOpetusmuoto().add(createKoodi("opetusmuoto/aikuisopetus"));
        lisaaKoulutus.getOpetuskieli().add(createKoodi("opetuskieli/fi"));
        lisaaKoulutus.getKoulutuslaji().add(createKoodi("koulutuslaji/lahiopetus"));
        //lisaaKoulutus.setKoulutusaste(createKoodi("koulutusaste/lukio"));
        lisaaKoulutus.setPohjakoulutusvaatimus(createKoodi("koulutusaste/lukio"));
        lisaaKoulutus.setTarjoaja(SAMPLE_TARJOAJA);
        lisaaKoulutus.setOid(SAMPLE_KOULUTUS_OID);
        lisaaKoulutus.setKoulutuksenAlkamisPaiva(getDateFromString("02.02.2013"));
        lisaaKoulutus.setKesto(kesto3Vuotta);
        lisaaKoulutus.getYhteyshenkiloTyyppi().add(createYhteyshenkilo());
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
        paivitaKoulutus.setTarjoaja(SAMPLE_TARJOAJA);
        paivitaKoulutus.setKoulutuksenAlkamisPaiva(new Date());
        paivitaKoulutus.setKoulutusKoodi(createKoodi("do-not-update-this"));
        paivitaKoulutus.setKoulutusohjelmaKoodi(createKoodi("do-not-update-this"));
        paivitaKoulutus.getOpetusmuoto().add(createKoodi("new-opetusmuoto"));
        paivitaKoulutus.getOpetuskieli().add(createKoodi("updated-kieli"));

        return paivitaKoulutus;
    }

    private KoulutusmoduuliToteutus createKomotoWithKomoTarjoajaPohjakoulutus(Koulutusmoduuli komo, String tarjoajaOid, String komotoOid, String pohjakoulutusvaatimus) {

        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid(komotoOid);
        komoto.setTila(fi.vm.sade.tarjonta.shared.types.TarjontaTila.LUONNOS);
        komoto.setKoulutusmoduuli(komo);
        komoto.setKoulutusasteUri("koulutusaste/lukio");
        komoto.setOpetusmuoto(EntityUtils.toKoodistoUriSet(createKoodistoList("opetusmuoto/aikuisopetus")));
        komoto.setOpetuskieli(EntityUtils.toKoodistoUriSet(createKoodistoList("opetuskieli/fi")));
        komoto.setKoulutuslajis(EntityUtils.toKoodistoUriSet(createKoodistoList("koulutuslaji/lahiopetus")));
        komoto.setTarjoaja(tarjoajaOid);
        komoto.setKoulutuksenAlkamisPvm(Calendar.getInstance().getTime());
        komoto.setPohjakoulutusvaatimusUri("koulutusaste/lukio");
        komoto.setSuunniteltuKesto("kesto/vuosi", "3");
        komoto.setPohjakoulutusvaatimusUri(pohjakoulutusvaatimus);
        return komoto;
    }

    private List<KoodistoKoodiTyyppi> createKoodistoList(String koodiUri) {
        List<KoodistoKoodiTyyppi> opetusmuotos = new ArrayList<KoodistoKoodiTyyppi>();
        opetusmuotos.add(createKoodi(koodiUri));
        return opetusmuotos;
    }

    @Test
    public void testProtectedResources() {
        // Oid to be used in the test to identify a komoto
        String oid = "54.54.54.54.54.54";

        // Creating a komoto with hakukohde (this can not be removed so removal
        // should fail)
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
        HakukohdeLiite liite = new HakukohdeLiite();
        liite.setHakukohde(hakukohde);
        liite.setLiitetyyppi("tyyppi");
        liite.setHakukohdeLiiteNimi("nimi");
        liite.setErapaiva(new Date());
        hakukohde.addLiite(liite);
        Valintakoe valintakoe = new Valintakoe();
        valintakoe.setHakukohde(hakukohde);
        hakukohde.addValintakoe(valintakoe);
        this.hakukohdeDAO.update(hakukohde);
        hakukohde = hakukohdeDAO.read(hakukohde.getId());
        String hakukohdeTunniste = Long.toString(hakukohde.getLiites().iterator().next().getId());
        String valintakoeTunniste = Long.toString(hakukohde.getValintakoes().iterator().next().getId());
        komoto.addHakukohde(hakukohde);
        this.koulutusmoduuliToteutusDAO.update(komoto);

        //unauthenticated user
        setAuthentication(null);

        try {
            HakukohdeTyyppi newHakukohde = new HakukohdeTyyppi();
            newHakukohde.setHakukohteenHakuOid("haku-oid");
            newHakukohde.getHakukohteenKoulutusOidit().add(lisaaKoulutus.getOid());
            //stub parameter to forbid addition
            Mockito.stub(parameterService.parameterCanAddHakukohdeToHaku("haku-oid")).toReturn(true);

            adminService.lisaaHakukohde(newHakukohde);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            LisaaKoulutusTyyppi koulutus = new LisaaKoulutusTyyppi();
            koulutus.setTarjoaja("tarjoaja-oid");
            adminService.lisaaKoulutus(koulutus);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.lisaaKoulutusmoduuli( new KoulutusmoduuliKoosteTyyppi());
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            LisaaKoulutusHakukohteelleTyyppi q = new LisaaKoulutusHakukohteelleTyyppi();
            q.setHakukohdeOid(hakukohde.getOid());
            adminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(q);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohde.getOid());
            LueHakukohdeVastausTyyppi hakukohdeVastaus = publicService.lueHakukohde(kysely);
            adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            PaivitaKoulutusTyyppi koulutus = new PaivitaKoulutusTyyppi();
            koulutus.setTarjoaja("123");
            adminService.paivitaKoulutus(koulutus);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.paivitaKoulutusmoduuli(null);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            PaivitaTilaTyyppi paivitaTilat = new PaivitaTilaTyyppi();
            GeneerinenTilaTyyppi tilatyyppi = new GeneerinenTilaTyyppi();
            tilatyyppi.setSisalto(SisaltoTyyppi.HAKU);
            tilatyyppi.setTila(TarjontaTila.JULKAISTU);
            paivitaTilat.getTilaOids().add(tilatyyppi);
            adminService.paivitaTilat(paivitaTilat);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.paivitaValintakokeitaHakukohteelle(hakukohde.getOid(), new ArrayList());
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
            hakukohdeTyyppi.setOid(hakukohde.getOid());
            adminService.poistaHakukohde(hakukohdeTyyppi);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.poistaHakukohdeLiite(hakukohdeTunniste);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.poistaKoulutus(komoto.getOid());
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.poistaValintakoe(valintakoeTunniste);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.tallennaLiitteitaHakukohteelle(hakukohde.getOid(), new ArrayList());
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.tallennaMetadata(null, null, null, null);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.tallennaValintakokeitaHakukohteelle(hakukohde.getOid(), new ArrayList());
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }
    }

    @Test
    @Ignore // SPRINGIN BUGI SPR-8857 ESTÄÄ TÄMÄN TESTIN AJAMISEN
    public void testProtectedResourcesVirkailija() {
        // Oid to be used in the test to identify a komoto
        String oid = "54.54.54.54.54.54";

        // Creating a komoto with hakukohde (this can not be removed so removal
        // should fail)
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
        hakukohde.setKaksoisTutkinto(false);
        hakukohde = this.hakukohdeDAO.insert(hakukohde);
        HakukohdeLiite liite = new HakukohdeLiite();
        liite.setHakukohde(hakukohde);
        liite.setLiitetyyppi("tyyppi");
        liite.setHakukohdeLiiteNimi("nimi");
       liite.setErapaiva(new Date());
        hakukohde.addLiite(liite);
        Valintakoe valintakoe = new Valintakoe();
        valintakoe.setHakukohde(hakukohde);
        hakukohde.addValintakoe(valintakoe);
        this.hakukohdeDAO.update(hakukohde);
        hakukohde = hakukohdeDAO.read(hakukohde.getId());
        String hakukohdeLiiteTunniste = Long.toString(hakukohde.getLiites().iterator().next().getId());
        String valintakoeTunniste = Long.toString(hakukohde.getValintakoes().iterator().next().getId());

        komoto.addHakukohde(hakukohde);
        this.koulutusmoduuliToteutusDAO.update(komoto);

        String LUKIOTUTKINTO = "yoTutkinto11";

        KoulutusmoduuliKoosteTyyppi koulutusmoduuliParentT = new KoulutusmoduuliKoosteTyyppi();
        koulutusmoduuliParentT.setOid("1.2.3.4.5.6.7");
        koulutusmoduuliParentT.setKoulutuskoodiUri(LUKIOTUTKINTO);
        //koulutusmoduuliT.//setKoulutusohjelmakoodiUri(KOKOODI);
        koulutusmoduuliParentT.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koulutusmoduuliParentT.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        adminService.lisaaKoulutusmoduuli(koulutusmoduuliParentT);

        setCurrentUser("user-oid", getAuthority("APP_" + TarjontaPermissionServiceImpl.TARJONTA + "_CRUD", SAMPLE_TARJOAJA));

        try {
            HakukohdeTyyppi newHakukohde = fixtures.createHakukohdeTyyppi();
            newHakukohde.setOid("oid");
            newHakukohde.setKaksoisTutkinto(false);
            KoulutusKoosteTyyppi koulutus = new KoulutusKoosteTyyppi();
            koulutus.setTarjoaja(SAMPLE_TARJOAJA);
            koulutus.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
            newHakukohde.setKaytetaanHaunPaattymisenAikaa(true);
            newHakukohde.setHakukohteenHakuOid(haku.getOid());
            newHakukohde.getHakukohdeKoulutukses().add(koulutus);

            adminService.lisaaHakukohde(newHakukohde);
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to add hakukohde");
        }

        try {
            LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
            kysely.setOid(SAMPLE_KOULUTUS_OID);
            LueKoulutusVastausTyyppi luettuKoulutus = publicService
                    .lueKoulutus(kysely);

            LisaaKoulutusTyyppi koulutus = new LisaaKoulutusTyyppi();
            koulutus.setTarjoaja(SAMPLE_TARJOAJA);
            koulutus.setKoulutusKoodi(luettuKoulutus.getKoulutusKoodi());
            // koulutus.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
            adminService.lisaaKoulutus(koulutus);
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to add koulutus");
        } catch (Exception e) {
            // "ok"
        }

        try {
            adminService.lisaaKoulutusmoduuli(null);
            fail("virkailija should noe be able to add koulutusmoduuli");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            LisaaKoulutusHakukohteelleTyyppi q = new LisaaKoulutusHakukohteelleTyyppi();
            q.setHakukohdeOid(hakukohde.getOid());
            q.getKoulutusOids().add(SAMPLE_KOULUTUS_OID);
            q.setLisaa(true);
            adminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(q);

        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to edit hakukohde");
        }

        try {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohde.getOid());
            LueHakukohdeVastausTyyppi hakukohdeVastaus = publicService.lueHakukohde(kysely);
            adminService.paivitaHakukohde(hakukohdeVastaus.getHakukohde());
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to update hakukohde");
        } catch (Exception e) {
            //"ok"
        }

        try {
            LueKoulutusVastausTyyppi lKoulutus = publicService.lueKoulutus(new LueKoulutusKyselyTyyppi(SAMPLE_KOULUTUS_OID));
            PaivitaKoulutusTyyppi update = new PaivitaKoulutusTyyppi();
            update.setOid(lKoulutus.getOid());
            update.setVersion(lKoulutus.getVersion());
            update.setTarjoaja(SAMPLE_TARJOAJA);
            update.setTila(TarjontaTila.VALMIS);
            update.setKesto(lKoulutus.getKesto());

            adminService.paivitaKoulutus(update);
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to update koulutus");
        } catch (Exception e) {
            //"ok"
        }


        try {
            adminService.poistaValintakoe(valintakoeTunniste);
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to edit hakukohde");
        }

        try {
            adminService.paivitaKoulutusmoduuli(null);
            fail("virkailija should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            PaivitaTilaTyyppi paivitaTilat = new PaivitaTilaTyyppi();
            GeneerinenTilaTyyppi tilatyyppi = new GeneerinenTilaTyyppi();
            tilatyyppi.setSisalto(SisaltoTyyppi.HAKU);
            tilatyyppi.setTila(TarjontaTila.JULKAISTU);
            paivitaTilat.getTilaOids().add(tilatyyppi);
            adminService.paivitaTilat(paivitaTilat);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.paivitaValintakokeitaHakukohteelle(hakukohde.getOid(), new ArrayList());
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to update hakukohde");
        }

        try {
            HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
            hakukohdeTyyppi.setOid(hakukohde.getOid());
            adminService.poistaHakukohde(hakukohdeTyyppi);
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to remove hakukohde");
        } catch (Exception e) {
            //"ok"
        }

        try {
            adminService.poistaHakukohdeLiite(hakukohdeLiiteTunniste);
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to edit hakukohde");
        }

        try {
            adminService.poistaKoulutus(komoto.getOid());
        } catch (NotAuthorizedException rte) {
            fail("virkailja should be able to remove koulutus");
        } catch (Exception e) {
            //"ok"
        }


        try {
            adminService.tallennaLiitteitaHakukohteelle(hakukohde.getOid(), new ArrayList());
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to edit hakukohde");
        }

        try {
            adminService.tallennaMetadata(null,  null,  null,  null);
            fail("unauthenticated user should not be able to access the service");
        } catch (NotAuthorizedException rte) {
            assertNoPermission(rte);
        }

        try {
            adminService.tallennaValintakokeitaHakukohteelle(hakukohde.getOid(), new ArrayList());
        } catch (NotAuthorizedException rte) {
            fail("virkailija should be able to edit hakukohde");
        }
    }

    private void assertNoPermission(RuntimeException rte) {
        assertTrue("Exception class mismatch, was excepting " + rte.getClass().getName(), NotAuthorizedException.class==rte.getClass());
        assertTrue("Exception message mismatch, was excepting 'no.permission', got '" + rte.getMessage() + "'", "no.permission".equals(rte.getMessage()));
    }
}
