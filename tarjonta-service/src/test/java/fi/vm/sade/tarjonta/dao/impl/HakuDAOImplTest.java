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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class HakuDAOImplTest extends TestData {

    @Autowired
    private TarjontaFixtures fixtures;

    @Autowired
    private HakuDAOImpl hakuDAO;

    private EntityManager em;

    public HakuDAOImplTest() {
    }

    public void setUp() {
        em = hakuDAO.getEntityManager();
        super.initializeData(em, fixtures);
    }

    public void cleanUp() {
        super.clean();
    }

    @Test
    public void testFindByOid() {
        setUp();
        Haku result = hakuDAO.findByOid(HAKU_OID1);
        assertEquals(haku1, result);
        assertEquals(3, result.getHakukohdes().size());

        result = hakuDAO.findByOid("none");
        assertEquals(null, result);
        cleanUp();
    }

    @Test
    public void testFindHakukohdeHakus() {
        setUp();
        assertEquals(2, hakuDAO.findAll().size());

        //TODO:If I have understood this correctly, it should output 3 items, not 6? 
        List<Haku> findHakukohdeHakus = hakuDAO.findHakukohdeHakus(haku1);
        assertEquals(3, findHakukohdeHakus.size());

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

        KoulutusmoduuliToteutusTarjoajatiedot tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("2.2.2");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("3.3.3", tarjoajatiedot);

        tarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        tarjoajatiedot.getTarjoajaOids().add("4.4.4");
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("5.5.5", tarjoajatiedot);

        hakukohde.setHaku(haku);
        hakukohdeDAO.insert(hakukohde);

        hakukohde = new Hakukohde();

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

    private void createHaku1(String oid) {
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
