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
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria.Field;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import junit.framework.Assert;
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
import java.util.List;

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
    private EntityManager em;
    @Autowired
    private HakuDAOImpl dao;

    public HakuDAOImplTest() {
    }

    public void setUp() {
        em = dao.getEntityManager();
        super.initializeData(em, fixtures);
    }

    public void cleanUp() {
        super.clean();
    }

    @Test
    public void testFindByOid() {
        setUp();
        Haku result = dao.findByOid(HAKU_OID1);
        assertEquals(haku1, result);
        assertEquals(3, result.getHakukohdes().size());

        result = dao.findByOid("none");
        assertEquals(null, result);
        cleanUp();
    }

    @Test
    public void testFindHakukohdeHakus() {
        setUp();
        assertEquals(2, dao.findAll().size());

        //TODO:If I have understood this correctly, it should output 3 items, not 6? 
        List<Haku> findHakukohdeHakus = dao.findHakukohdeHakus(haku1);
        assertEquals(3, findHakukohdeHakus.size());

        findHakukohdeHakus = dao.findHakukohdeHakus(haku2);
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

        Assert.assertEquals(2, dao.findByOids(Lists.newArrayList(OID1, OID2)).size());
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
        List<String> result = dao.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        assertEquals(2, result.size());

        //countilla
        count = 1;
        result = dao.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        assertEquals(1, result.size());

        //indexillä
        count = 0;
        index = 1;
        result = dao.findOIDByCriteria(count, index, new ArrayList<HakuSearchCriteria>());
        assertEquals(1, result.size());

        count = 0;
        index = 0;

        //hakukauden vuodella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUVUOSI, 2014).build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));


        //hakukauden kaudella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUKAUSI, "k").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));


        //koulutuksen alkamisvuodella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOULUTUKSEN_ALKAMISVUOSI, 2014).build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));


        //koulutuksen alkamiskaudella
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOULUTUKSEN_ALKAMISKAUSI, "k").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //hakutapa
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUTAPA, "hakutapa2").build();
        assertResult(OID2, dao.findOIDByCriteria(count, index, criteria));

        //hakutyyppi
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.HAKUTYYPPI, "hakutyyppi1").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //kohdejoukko
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.KOHDEJOUKKO, "kohdejoukko2").build();
        assertResult(OID2, dao.findOIDByCriteria(count, index, criteria));

        //tila
        criteria = new HakuSearchCriteria.Builder().mustMatch(Field.TILA, TarjontaTila.VALMIS).build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //nimi like
        criteria = new HakuSearchCriteria.Builder().like(Field.HAKUSANA, "%nimi1%").build();
        assertResult(OID1, dao.findOIDByCriteria(count, index, criteria));

        //nimi like
        criteria = new HakuSearchCriteria.Builder().like(Field.HAKUSANA, "%nimi2%").build();
        assertResult(OID2, dao.findOIDByCriteria(count, index, criteria));

        //nimi like mixed case
        criteria = new HakuSearchCriteria.Builder().like(Field.HAKUSANA, "%NIMI2%").build();
        assertResult(OID2, dao.findOIDByCriteria(count, index, criteria));

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

        List<Object> oids = dao.findByCriteria(count, startIndex, builder.build(), true);

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

        List<Object> oids = dao.findByCriteria(count, startIndex, builder.build(), true);

        assertTrue(oids.size() == 1);
    }

    @Test
    public void thatHakuIsNotFoundWithIncompleteKoodiUri() {
        createHakuWithKoodiVersions();

        int count = 0;
        int startIndex = 0;

        HakuSearchCriteria.Builder builder = new HakuSearchCriteria.Builder();
        builder.mustMatch(Field.HAKUKAUSI, "kausi");

        List<Object> oids = dao.findByCriteria(count, startIndex, builder.build(), true);

        assertTrue(oids.isEmpty());
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
        dao.insert(haku);
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
        dao.insert(haku);
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
        dao.insert(haku);
    }

    private void assertResult(String oid, List<String> result) {
        assertEquals(1, result.size());
        assertEquals(oid, result.get(0));
    }


}