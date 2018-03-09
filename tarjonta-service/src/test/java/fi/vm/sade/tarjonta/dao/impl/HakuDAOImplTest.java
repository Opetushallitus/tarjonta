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
package fi.vm.sade.tarjonta.dao.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutusTarjoajatiedot;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.impl.resources.v1.ConverterV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.HakuResourceImplV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.OidServiceMock;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakeHakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakkeetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdeSearchService;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@Transactional
public class HakuDAOImplTest extends TestData {

    @Autowired
    private TarjontaFixtures fixtures;

    @Autowired
    private HakuDAOImpl hakuDAO;

    @Autowired
    OidServiceMock oidServiceMock;

    public HakuDAOImplTest() {
    }

    public void setUp() {
        EntityManager em = hakuDAO.getEntityManager();
        super.initializeData(em, fixtures);
    }

    public void cleanUp() {
        super.clean();
    }

    @Before
    public void deleteAllHakus() {
        for (Haku haku : hakuDAO.findAll()) {
            hakuDAO.remove(haku);
        }
    }

    @Test
    public void testFindByOid() {
        setUp();
        Haku result = hakuDAO.findByOid(HAKU_OID1);
        assertEquals(haku1, result);
        assertEquals(4, result.getHakukohdes().size());

        result = hakuDAO.findByOid("none");
        assertEquals(null, result);
        cleanUp();
    }

    @Test
    public void testFindHakukohdeHakus() {
        setUp();
        assertEquals(2, hakuDAO.findAll().size());

        List<Haku> findHakukohdeHakus = hakuDAO.findHakukohdeHakus(haku1);
        assertEquals(4, findHakukohdeHakus.size());

        findHakukohdeHakus = hakuDAO.findHakukohdeHakus(haku2);
        assertEquals(0, findHakukohdeHakus.size());
        cleanUp();
    }


    @Test
    public void testMultiGet() {
        super.clean();
        final String OID1 = "1.2.3";
        final String OID2 = "2.2.3";

        createHaku1(OID1);
        createHaku2(OID2);

        Assert.assertEquals(2, hakuDAO.findByOids(Lists.newArrayList(OID1, OID2)).size());
    }

    @Test
    public void testFindHakuByCriteria() {
        super.clean();
        final String OID1 = "1.2.3";
        final String OID2 = "2.2.3";

        createHaku1(OID1);
        createHaku2(OID2);

        int count = 0;
        int index = 0;
        List<HakuSearchCriteria> criteria;
        List<String> result = hakuDAO.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        assertEquals(2, result.size());

        //countilla
        count = 1;
        result = hakuDAO.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        assertEquals(1, result.size());

        //indexillä
        count = 0;
        index = 1;
        result = hakuDAO.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        assertEquals(1, result.size());

        count = 0;
        index = 0;

        //hakukauden vuodella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUVUOSI, 2014).build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));


        //hakukauden kaudella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUKAUSI, "k").build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));


        //koulutuksen alkamisvuodella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOULUTUKSEN_ALKAMISVUOSI, 2014).build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));


        //koulutuksen alkamiskaudella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOULUTUKSEN_ALKAMISKAUSI, "k").build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));

        //hakutapa
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUTAPA, "hakutapa2").build();
        assertResult(OID2, hakuDAO.findOIDByCriteria(count, index, criteria));

        //hakutyyppi
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUTYYPPI, "hakutyyppi1").build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));

        //kohdejoukko
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOHDEJOUKKO, "kohdejoukko2").build();
        assertResult(OID2, hakuDAO.findOIDByCriteria(count, index, criteria));

        //tila
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.TILA, TarjontaTila.VALMIS).build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));

        //nimi like
        criteria = new HakuSearchCriteria.Builder().like(Field.HAKUSANA, "%nimi1%").build();
        assertResult(OID1, hakuDAO.findOIDByCriteria(count, index, criteria));

        //nimi like
        criteria = new HakuSearchCriteria.Builder().like(Field.HAKUSANA, "%nimi2%").build();
        assertResult(OID2, hakuDAO.findOIDByCriteria(count, index, criteria));

        //nimi like mixed case
        criteria = new HakuSearchCriteria.Builder().like(Field.HAKUSANA, "%NIMI2%").build();
        assertResult(OID2, hakuDAO.findOIDByCriteria(count, index, criteria));

    }

    @Test
    public void thatHakuIsFoundWithKoodiVersions() {
        createHakuWithKoodiVersions();

        int count = 0;
        int startIndex = 0;

        HakuSearchCriteria.Builder builder = new HakuSearchCriteria.Builder();
        builder.mustMatch(Field.HAKUKAUSI, "kausi_k#1");
        builder.mustMatch(Field.HAKUTAPA, "hakutapa_01#1");
        builder.mustMatch(Field.HAKUTYYPPI, "hakutyyppi_01#1");
        builder.mustMatch(Field.KOHDEJOUKKO, "haunkohdejoukko_01#1");
        builder.mustMatch(Field.KOULUTUKSEN_ALKAMISKAUSI, "kausi_k#1");

        List<Object> oids = hakuDAO.findByCriteria(count, startIndex, builder.build(), true);

        assertTrue(oids.size() == 1);
    }

    @Test
    public void thatHakuIsFoundWihtoutKoodiversions() {
        createHakuWithKoodiVersions();

        int count = 0;
        int startIndex = 0;

        HakuSearchCriteria.Builder builder = new HakuSearchCriteria.Builder();
        builder.mustMatch(Field.HAKUKAUSI, "kausi_k");
        builder.mustMatch(Field.HAKUTAPA, "hakutapa_01");
        builder.mustMatch(Field.HAKUTYYPPI, "hakutyyppi_01");
        builder.mustMatch(Field.KOHDEJOUKKO, "haunkohdejoukko_01");
        builder.mustMatch(Field.KOULUTUKSEN_ALKAMISKAUSI, "kausi_k");

        List<Object> oids = hakuDAO.findByCriteria(count, startIndex, builder.build(), true);

        assertTrue(oids.size() == 1);
    }

    @Test
    public void thatHakuIsNotFoundWithIncompleteKoodiUri() {
        createHakuWithKoodiVersions();

        int count = 0;
        int startIndex = 0;

        HakuSearchCriteria.Builder builder = new HakuSearchCriteria.Builder();
        builder.mustMatch(Field.HAKUKAUSI, "kausi");

        List<Object> oids = hakuDAO.findByCriteria(count, startIndex, builder.build(), true);

        assertTrue(oids.isEmpty());
    }

    @Test
    public void thatLastUpdatedDateIsUpdatedWhenHakuIsDeleted() {
        createHaku1(HAKU_OID1);
        hakuDAO.safeDelete(HAKU_OID1, "1.2.3");

        Haku haku = hakuDAO.findByOid(HAKU_OID1);
        assertTrue(haku.getLastUpdateDate().after(getLastUpdatedDate()));
    }

    @Test
    public void thatHakukohteidenTarjoajatAreFetchedForHaku() {
        createHakuWithMontaTarjoajaa();
        Set<String> oids = hakuDAO.findOrganisaatioOidsFromHakukohteetByHakuOid("1.1.1");

        assertTrue(oids.size() == 4);
        assertTrue(oids.contains("2.2.2"));
        assertTrue(oids.contains("4.4.4"));
        assertTrue(oids.contains("6.6.6"));
        assertTrue(oids.contains("8.8.8"));
    }

    @Test
    public void thatHakukohteidenTarjoajatDoesNotFailWithEmptyResults() {
        Set<String> oids = hakuDAO.findOrganisaatioOidsFromHakukohteetByHakuOid("1.1.1");
        assertTrue(oids.isEmpty());
    }

    @Test
    public void thatOrganisaatioryhmaOidsAreFound() {
        Haku haku = fixtures.createHakuWithOrganisaatioryhmat();
        List<String> oids = hakuDAO.findOrganisaatioryhmaOids(haku.getOid());
        assertTrue(oids.size() == 2);
        assertTrue(oids.contains("1.2.3"));
        assertTrue(oids.contains("4.5.6"));
    }

    @Test
    public void thatEmptyOrganisaatioryhmaOidsIsReturned() {
        Haku haku = fixtures.createPersistedHaku();
        List<String> oids = hakuDAO.findOrganisaatioryhmaOids(haku.getOid());
        assertTrue(oids.isEmpty());
    }

    @Test
    public void shouldFindHakuToSync() {
        String expectedOid = fixtures.createPersistedHaku().getOid();
        Calendar calToday = new GregorianCalendar();
        calToday.set(2016, 7, 15);
        Set<String> hakuOids = hakuDAO.findHakusToSync(calToday.getTime());
        assertTrue("should not be empty:" + hakuOids, !hakuOids.isEmpty());
        assertTrue("should contain correct hakuOid:" + hakuOids, hakuOids.contains(expectedOid));
    }

    @Test
    public void shouldNotFindTooEarlyHakuToSync() {
        fixtures.createPersistedHaku();
        Calendar calToday = new GregorianCalendar();
        calToday.set(2016, 6, 31);
        Set<String> hakuOids = hakuDAO.findHakusToSync(calToday.getTime());
        assertTrue("should be empty: " + hakuOids, hakuOids.isEmpty());
    }

    @Test
    public void shouldFindTooLateHakuToSync() {
        fixtures.createPersistedHaku();
        Calendar calToday = new GregorianCalendar();
        calToday.set(2016, 8, 1);
        Set<String> hakuOids = hakuDAO.findHakusToSync(calToday.getTime());
        assertTrue("should be empty: " + hakuOids, hakuOids.isEmpty());
    }

    @Test
    public void shouldFindHakuWithNullTimestampToSync() {
        Haku haku = fixtures.createPersistedHaku();
        haku.setAutosyncTarjontaFrom(null);
        haku.setAutosyncTarjontaTo(null);
        hakuDao.update(haku);

        Calendar calToday = new GregorianCalendar();
        calToday.set(2016, 8, 15);
        Set<String> hakuOids = hakuDAO.findHakusToSync(calToday.getTime());
        assertTrue("should contain correct hakuOid: " + hakuOids, hakuOids.contains(haku.getOid()));
    }

    @Test
    public void shouldFindHakuWithNullFromTimestampToSync() {
        Haku haku = fixtures.createPersistedHaku();
        haku.setAutosyncTarjontaFrom(null);
        hakuDao.update(haku);

        Calendar calToday = new GregorianCalendar();
        calToday.set(2016, 5, 15);
        Set<String> hakuOids = hakuDAO.findHakusToSync(calToday.getTime());
        assertTrue("should contain correct hakuOid:" + hakuOids, hakuOids.contains(haku.getOid()));
    }

    @Test
    public void shouldFindHakuWithNullToTimestampToSync() {
        Haku haku = fixtures.createPersistedHaku();
        haku.setAutosyncTarjontaTo(null);
        hakuDao.update(haku);

        Calendar calToday = new GregorianCalendar();
        calToday.set(2016, 9, 15);
        Set<String> hakuOids = hakuDAO.findHakusToSync(calToday.getTime());
        assertTrue("should contain correct hakuOid:" + hakuOids, hakuOids.contains(haku.getOid()));
    }

    @Test
    public void thatHakusWithAtaruFormsAreFound() {
        String ataruLomakeAvain1 = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeeee";
        String ataruLomakeAvain2 = "ffffffff-ffff-ffff-ffff-fffffffffffff";
        String HAKU_OID3 = "hakuoid3";
        String HAKU_OID4 = "hakuoid4";

        Haku haku1 = createHaku(HAKU_OID1);
        Haku haku2 = createHaku(HAKU_OID2);
        Haku haku3 = createHaku(HAKU_OID3);
        Haku haku4 = createHaku(HAKU_OID4); // No ataru lomake for this haku

        haku1.setAtaruLomakeAvain(ataruLomakeAvain1);
        haku2.setAtaruLomakeAvain(ataruLomakeAvain1);
        haku3.setAtaruLomakeAvain(ataruLomakeAvain2);

        hakuDAO.insert(haku1);
        hakuDAO.insert(haku2);
        hakuDAO.insert(haku3);
        hakuDAO.insert(haku4);

        List<Haku> ataruFormKeys = hakuDAO.findHakusWithAtaruFormKeys();
        assertTrue(ataruFormKeys.size() == 3);
    }
    private HakukohdeSearchService hakukohdeSearchReturning(HakukohteetVastaus vastaus) {
        HakukohdeSearchService h = Mockito.mock(HakukohdeSearchService.class);
        Mockito.when(h.haeHakukohteet(Mockito.any())).thenReturn(vastaus);
        return h;
    }
    private HakuResourceImplV1 hakuResourceWith(HakuDAOImpl hakuDao, HakukohdeSearchService hakukohdeSearchService) {
        HakuResourceImplV1 hakuResourceImplV1 = new HakuResourceImplV1();
        ConverterV1 c = Mockito.mock(ConverterV1.class);
        Mockito.when(c.fromHakuToAtaruLomakeHakuRDTO(Mockito.any())).then(a -> {
            Haku h = (Haku)a.getArguments()[0];
            AtaruLomakeHakuV1RDTO at = new AtaruLomakeHakuV1RDTO();
            at.setOid(h.getOid());
            return at;
        });
        ReflectionTestUtils.setField(hakuResourceImplV1, "converterV1",c);

        ReflectionTestUtils.setField(hakuResourceImplV1, "hakuDAO",hakuDAO);
        ReflectionTestUtils.setField(hakuResourceImplV1, "hakukohdeSearchService",hakukohdeSearchService);
        return hakuResourceImplV1;
    }

    @Test
    public void thatHakusWithAtaruFormsAreFilteredForOrganisation() {
        String ataruLomakeAvain1 = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeeee";
        String ataruLomakeAvain2 = "ffffffff-ffff-ffff-ffff-fffffffffffff";
        String HAKU_OID3 = "hakuoid3";
        String HAKU_OID4 = "hakuoid4";
        String organisaatioOid1 = "foo1";
        String organisaatioOid2 = "foo2";
        String organisaatioOid3 = "foo3";
        String organisaatioOid4 = "foo4";
        String[] organisaatioOids1 = {organisaatioOid1, organisaatioOid4};
        String[] organisaatioOids2 = {organisaatioOid2};
        String[] organisaatioOids3 = {organisaatioOid3};

        Haku haku1 = createHaku(HAKU_OID1);
        Haku haku2 = createHaku(HAKU_OID2);
        Haku haku3 = createHaku(HAKU_OID3);
        Haku haku4 = createHaku(HAKU_OID4); // No ataru lomake for this haku

        haku1.setTarjoajaOids(organisaatioOids1);
        haku2.setTarjoajaOids(organisaatioOids2);
        haku3.setTarjoajaOids(organisaatioOids3);

        haku1.setAtaruLomakeAvain(ataruLomakeAvain1);
        haku2.setAtaruLomakeAvain(ataruLomakeAvain1);
        haku3.setAtaruLomakeAvain(ataruLomakeAvain2);

        hakuDAO.insert(haku1);
        hakuDAO.insert(haku2);
        hakuDAO.insert(haku3);
        hakuDAO.insert(haku4);

        List<String> emptyOids = new ArrayList<>();
        List<Haku> resultAll = hakuDAO.findHakusWithAtaruFormKeys();
        assertTrue(resultAll.size() == 3);
        List<String> allOids = getHakuOids(resultAll);
        assertEquals(allOids.contains(HAKU_OID1), true);
        assertEquals(allOids.contains(HAKU_OID2), true);
        assertEquals(allOids.contains(HAKU_OID3), true);
        assertEquals(allOids.contains(HAKU_OID4), false);

        HakukohteetVastaus v = new HakukohteetVastaus();
        v.setHakukohteet(Collections.emptyList());
        HakukohdeSearchService hakukohdeSearchService = hakukohdeSearchReturning(v);
        HakuResourceImplV1 hakuResourceImplV1 = hakuResourceWith(hakuDAO, hakukohdeSearchService);

        List<String> unknownOids = new ArrayList<>();
        unknownOids.add("foobar");
        List<AtaruLomakkeetV1RDTO> resultUnknown = hakuResourceImplV1.findAtaruFormUsage(unknownOids).getResult();
        assertTrue(resultUnknown.size() == 0);

        List<String> filterOids1 = new ArrayList<>();
        filterOids1.add(organisaatioOid1);
        filterOids1.add(organisaatioOid3);
        List<AtaruLomakkeetV1RDTO> result1 = hakuResourceImplV1.findAtaruFormUsage(filterOids1).getResult();
        assertTrue(result1.size() == 2);
        List<String> resultOids1 = getAtaruHakuOids(result1);
        assertEquals(resultOids1.contains(HAKU_OID1), true);
        assertEquals(resultOids1.contains(HAKU_OID2), false);
        assertEquals(resultOids1.contains(HAKU_OID3), true);
        assertEquals(resultOids1.contains(HAKU_OID4), false);

        List<String> filterOids2 = new ArrayList<>();
        filterOids2.add(organisaatioOid2);
        List<AtaruLomakkeetV1RDTO> result2 = hakuResourceImplV1.findAtaruFormUsage(filterOids2).getResult();
        assertTrue(result2.size() == 1);
        List<String> resultOids2 = getAtaruHakuOids(result2);
        assertEquals(resultOids2.contains(HAKU_OID1), false);
        assertEquals(resultOids2.contains(HAKU_OID2), true);
        assertEquals(resultOids2.contains(HAKU_OID3), false);
        assertEquals(resultOids2.contains(HAKU_OID4), false);

        List<String> filterOids3 = new ArrayList<>();
        filterOids3.add(organisaatioOid4);
        List<AtaruLomakkeetV1RDTO> result3 = hakuResourceImplV1.findAtaruFormUsage(filterOids3).getResult();
        assertTrue(result3.size() == 1);
        List<String> resultOids3 = getAtaruHakuOids(result3);
        assertEquals(resultOids3.contains(HAKU_OID1), true);
        assertEquals(resultOids3.contains(HAKU_OID2), false);
        assertEquals(resultOids3.contains(HAKU_OID3), false);
        assertEquals(resultOids3.contains(HAKU_OID4), false);
    }

    private List<String> getHakuOids(List<Haku> hakus) {
        List<String> result = new ArrayList<>();
        for(Haku haku : hakus) {
            result.add(haku.getOid());
        }
        return result;
    }
    private List<String> getAtaruHakuOids(List<AtaruLomakkeetV1RDTO> hakus) {
        return hakus.stream().flatMap(a -> a.getHaut().stream()).map(h -> h.getOid()).collect(Collectors.toList());
    }
    private void createHakuWithMontaTarjoajaa() {
        Haku haku = new Haku();
        haku.setOid("1.1.1");
        haku.setTila(TarjontaTila.VALMIS);
        haku.setHakutyyppiUri("hakutyyppi_01#1");
        haku.setHakukausiUri("kausi_k#1");
        haku.setHakutapaUri("hakutapa_01#1");
        haku.setKohdejoukkoUri("haunkohdejoukko_01#1");
        haku.setHakukausiVuosi(2014);

        hakuDAO.insert(haku);

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid(oidServiceMock.getOid());

        KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("2.2.2");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("3.3.3", tarjoajatiedot);

        tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("4.4.4");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("5.5.5", tarjoajatiedot);

        hakukohde.setHaku(haku);
        hakukohdeDAO.insert(hakukohde);

        hakukohde = new Hakukohde();
        hakukohde.setOid(oidServiceMock.getOid());

        tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("6.6.6");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("7.7.7", tarjoajatiedot);

        hakukohde.setHaku(haku);
        hakukohdeDAO.insert(hakukohde);

        hakukohde = fixtures.createPersistedHakukohdeWithKoulutus("8.8.8");
        hakukohde.setHaku(haku);
        hakukohdeDAO.update(hakukohde);
    }

    private void createHakuWithKoodiVersions() {
        Haku haku = new Haku();
        haku.setOid("1.2.3");
        haku.setNimi(new MonikielinenTeksti("fi", "haku_fi"));
        haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
        haku.setKoulutuksenAlkamisVuosi(2014);
        haku.setTila(TarjontaTila.VALMIS);
        haku.setHakutyyppiUri("hakutyyppi_01#1");
        haku.setHakukausiUri("kausi_k#1");
        haku.setHakutapaUri("hakutapa_01#1");
        haku.setKohdejoukkoUri("haunkohdejoukko_01#1");
        haku.setHakukausiVuosi(2014);
        hakuDAO.insert(haku);
    }

    private Haku createHaku(String oid) {
        Haku haku = new Haku();
        haku.setOid(oid);
        haku.setNimi(new MonikielinenTeksti("fi", "nimi1_fi"));
        haku.setKoulutuksenAlkamiskausiUri("k#1");
        haku.setKoulutuksenAlkamisVuosi(2014);
        haku.setTila(TarjontaTila.VALMIS);
        haku.setHakutyyppiUri("hakutyyppi1#1");
        haku.setHakukausiUri("k#1");
        haku.setHakutapaUri("hakutapa1#1");
        haku.setKohdejoukkoUri("kohdejoukko1#1");
        haku.setHakukausiVuosi(2014);
        haku.setLastUpdateDate(getLastUpdatedDate());
        return haku;
    }

    private void createHaku1(String oid) {
        Haku haku = createHaku(oid);
        hakuDAO.insert(haku);
    }

    private Date getLastUpdatedDate() {
        DateTime dateTime = new DateTime().withYear(2000);
        return dateTime.toDate();
    }

    private void createHaku2(String oid) {
        Haku haku = new Haku();
        haku.setNimi(new MonikielinenTeksti("fi", "nimi2_fi"));
        haku.setOid(oid);
        haku.setKoulutuksenAlkamiskausiUri("s#1");
        haku.setKoulutuksenAlkamisVuosi(2015);
        haku.setTila(TarjontaTila.KOPIOITU);
        haku.setHakutyyppiUri("hakutyyppi2#1");
        haku.setHakukausiUri("s#1");
        haku.setHakutapaUri("hakutapa2#1");
        haku.setKohdejoukkoUri("kohdejoukko2#1");
        haku.setHakukausiVuosi(2015);
        hakuDAO.insert(haku);
    }

    private void assertResult(String oid, List<String> result) {
        assertEquals(1, result.size());
        assertEquals(oid, result.get(0));
    }
}
