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
package fi.vm.sade.tarjonta.dao;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MassakopiointiDAOTest {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MassakopiointiDAOTest.class);

    private static final Date DATE = (new DateTime(2014, 1, 6, 0, 0, 0, 0)).toDate();
    private static final Date ANOTHER_DATE = (new DateTime(2014, 3, 9, 0, 0, 0, 0)).toDate();

    private static final String HAKU_OID_1 = "HAKU_OID_1";
    private static final String HAKU_OID_2 = "HAKU_OID_2";

    private static final String ANY_UNIQUE_OID_1 = "ANY_OID_1";
    private static final String ANY_UNIQUE_OID_2 = "ANY_OID_2";
    private static final String ANY_UNIQUE_OID_3 = "ANY_OID_3";
    private static final String ANY_UNIQUE_OID_4 = "ANY_OID_4";
    private static final String ANY_UNIQUE_OID_5 = "ANY_OID_5";

    private static final String KOULUTUS_URI1 = "koulutus_uri_1";
    private static final String KOULUTUS_URI2 = "koulutus_uri_2";
    private static final String KOULUTUS_URI3 = "koulutus_uri_3";
    private static final String KOULUTUS_URI4 = "koulutus_uri_4";
    private static final String KOULUTUS_URI5 = "koulutus_uri_5";

    private KoulutusmoduuliToteutus toteutus1, toteutus2, toteutus3, toteutus4, toteutus5;
    private final MetaObject metaJson = new MetaObject();

    @Autowired
    private MassakopiointiDAO instance;

    @Autowired
    private TarjontaFixtures fixtures;

    public MassakopiointiDAOTest() {
    }

    private static KoulutusmoduuliToteutus createKomoto(String oid, String uri) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setKoulutuksenAlkamisPvm(DATE);
        komoto.setOid(oid);
        komoto.setKoulutusUri(uri);
        return komoto;
    }

    @Before
    public void setUp() {
        toteutus1 = createKomoto(ANY_UNIQUE_OID_1, KOULUTUS_URI1);
        toteutus2 = createKomoto(ANY_UNIQUE_OID_2, KOULUTUS_URI2);
        toteutus3 = createKomoto(ANY_UNIQUE_OID_3, KOULUTUS_URI3);
        toteutus4 = createKomoto(ANY_UNIQUE_OID_4, KOULUTUS_URI4);
        toteutus5 = createKomoto(ANY_UNIQUE_OID_5, KOULUTUS_URI5);
    }

    /**
     * Test MassakopiointiDAO.
     */
    @Test
    public void testAllOperations() {
        instance.saveEntityAsJson(HAKU_OID_1, ANY_UNIQUE_OID_1, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus1, metaJson);
        instance.saveEntityAsJson(HAKU_OID_1, ANY_UNIQUE_OID_2, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus2, metaJson);

        instance.saveEntityAsJson(HAKU_OID_2, ANY_UNIQUE_OID_3, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus3, metaJson);
        instance.saveEntityAsJson(HAKU_OID_2, ANY_UNIQUE_OID_4, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus4, metaJson);
        instance.saveEntityAsJson(HAKU_OID_2, ANY_UNIQUE_OID_5, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus5, metaJson);

        /*
         * findAll
         */
        List<Massakopiointi> all = instance.findAll();
        assertEquals("findAll", 5, all.size());

        /*
         * search by search criteria
         */
        List<Massakopiointi> search = instance.search(new MassakopiointiDAO.SearchCriteria(HAKU_OID_1, "asdads", Massakopiointi.Tyyppi.KOMOTO_ENTITY));
        assertEquals("search 1/2 by 3 params", 0, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(HAKU_OID_1, null, Massakopiointi.Tyyppi.KOMOTO_ENTITY));
        assertEquals("search 2/2 by 3 params", 2, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, ANY_UNIQUE_OID_2, Massakopiointi.Tyyppi.KOMOTO_ENTITY));
        assertEquals("search 1/3 by 2 params", 1, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, Massakopiointi.Tyyppi.KOMOTO_ENTITY));
        assertEquals("search 2/3 by 2 params", 5, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, Massakopiointi.Tyyppi.HAKUKOHDE_ENTITY));
        assertEquals("search 3/3 by 2 params", 0, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(HAKU_OID_2, null, null));
        assertEquals("search by 1 params", 3, search.size());

        /*
         * find
         */
        Object nullResult = instance.find(HAKU_OID_2, ANY_UNIQUE_OID_1, KoulutusmoduuliToteutus.class);
        assertNull("findByOid -  required null", nullResult);

        KoulutusmoduuliToteutus kt1 = (KoulutusmoduuliToteutus) instance.find(HAKU_OID_1, ANY_UNIQUE_OID_1, KoulutusmoduuliToteutus.class);
        assertNotNull("findByOid - result KoulutusmoduuliToteutus?", kt1);
        assertEquals(KOULUTUS_URI1, kt1.getKoulutusUri());
        assertEquals(DATE, kt1.getKoulutuksenAlkamisPvm());

        /*
         * findByHakuOid
         */
        List<Massakopiointi> resultByhaku = instance.findByHakuOid(HAKU_OID_1);
        assertEquals("findByHakuOid", 2, resultByhaku.size());

        resultByhaku = instance.findByHakuOid(HAKU_OID_2);
        assertEquals("findByHakuOid", 3, resultByhaku.size());

        /*
         * findByHakuOidAndOids
         */
        List<Massakopiointi> findByHakuOidAndOids = instance.findByHakuOidAndOids(HAKU_OID_2, Lists.<String>newArrayList(ANY_UNIQUE_OID_1));
        assertEquals("findByHakuOidAndOids", 0, findByHakuOidAndOids.size());

        findByHakuOidAndOids = instance.findByHakuOidAndOids(HAKU_OID_2, Lists.<String>newArrayList(ANY_UNIQUE_OID_3, ANY_UNIQUE_OID_4));
        assertEquals("findByHakuOidAndOids", 2, findByHakuOidAndOids.size());
        /*
         * updateTila
         */
        long updateTila = instance.updateTila(HAKU_OID_2, ANY_UNIQUE_OID_5, Massakopiointi.KopioinninTila.COPIED, ANOTHER_DATE);
        assertEquals(1, updateTila);

        kt1 = (KoulutusmoduuliToteutus) instance.find(HAKU_OID_1, ANY_UNIQUE_OID_1, KoulutusmoduuliToteutus.class);
        assertNotNull("findByOid - result KoulutusmoduuliToteutus?", kt1);
        assertEquals(KOULUTUS_URI1, kt1.getKoulutusUri());
        // assertEquals(ANOTHER_DATE, kt1.getKoulutuksenAlkamisPvm());
        assertEquals(TarjontaTila.KOPIOITU, kt1.getTila());

        /*
         * delete
         */
        long deleted = instance.deleteAllByHakuOid(HAKU_OID_1);
        assertEquals("deleteAllByHakuOid", 2, deleted);

        deleted = instance.deleteByHakuOidAndKopioinninTila(HAKU_OID_2, Massakopiointi.KopioinninTila.ERROR);
        assertEquals("deleteByHakuOidAndKopioinninTila", 0, deleted);

        deleted = instance.deleteByHakuOidAndKopioinninTila(HAKU_OID_2, Massakopiointi.KopioinninTila.COPIED);
        assertEquals("deleteByHakuOidAndKopioinninTila", 1, deleted);
    }

    @Test
    public void testConversions() {
        Koulutusmoduuli komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        KoulutusmoduuliToteutus komoto1 = fixtures.createTutkintoOhjelmaToteutus(ANY_UNIQUE_OID_1);
        komoto1.setKoulutusmoduuli(komo);

        String json = null;
        try {
            json = instance.convertToJson(komoto1);
        } catch (IOException ex) {
            fail("conversion error from entity to json : " + ex.getMessage());
        }
        assertNotNull("KoulutusmoduuliToteutus - not nullable", json);
        assertTrue(json.length() > 0);

        try {
            komoto1 = (KoulutusmoduuliToteutus) instance.convertToEntity(json, KoulutusmoduuliToteutus.class);
        } catch (IOException ex) {
            LOG.error(json);
            fail("conversion error from json to entity : " + ex.getMessage());
        }
        assertEquals(ANY_UNIQUE_OID_1, komoto1.getOid());
    }
}
