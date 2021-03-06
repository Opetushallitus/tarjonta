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
import com.mysema.commons.lang.Pair;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Massakopiointi;
import fi.vm.sade.tarjonta.service.copy.MetaObject;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


@Transactional
public class MassakopiointiDAOTest extends TestUtilityBase {

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

    private static final String NEW_OID1 = "1213424234235";

    private static final String PROSESS_ID1 = "prosess_id_1";
    private static final String PROSESS_ID2 = "prosess_id_2";

    private KoulutusmoduuliToteutus toteutus1, toteutus2, toteutus3, toteutus4, toteutus5;
    private MetaObject metaJson = null;

    @Autowired
    private MassakopiointiDAO instance;

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
        cleanDb();

        metaJson = new MetaObject();
        metaJson.addHakukohdeOid("oid1");
        metaJson.addKomotoOid("oid2");
        metaJson.setOriginalKomoOid("oid3");

        toteutus1 = createKomoto(ANY_UNIQUE_OID_1, KOULUTUS_URI1);
        toteutus2 = createKomoto(ANY_UNIQUE_OID_2, KOULUTUS_URI2);
        toteutus3 = createKomoto(ANY_UNIQUE_OID_3, KOULUTUS_URI3);
        toteutus4 = createKomoto(ANY_UNIQUE_OID_4, KOULUTUS_URI4);
        toteutus5 = createKomoto(ANY_UNIQUE_OID_5, KOULUTUS_URI5);
    }

    private void cleanDb() {
        for (Massakopiointi massakopiointi : instance.findAll()) {
            instance.remove(massakopiointi);
        }
    }

    /**
     * Test MassakopiointiDAO.
     */
    @Test
    public void testAllOperations() {
        instance.saveEntityAsJson(HAKU_OID_1, ANY_UNIQUE_OID_1, NEW_OID1, PROSESS_ID1, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus1, metaJson);
        instance.saveEntityAsJson(HAKU_OID_1, ANY_UNIQUE_OID_2, NEW_OID1, PROSESS_ID1, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus2, metaJson);

        instance.saveEntityAsJson(HAKU_OID_2, ANY_UNIQUE_OID_3, NEW_OID1, PROSESS_ID2, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus3, metaJson);
        instance.saveEntityAsJson(HAKU_OID_2, ANY_UNIQUE_OID_4, NEW_OID1, PROSESS_ID2, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus4, metaJson);
        instance.saveEntityAsJson(HAKU_OID_2, ANY_UNIQUE_OID_5, NEW_OID1, PROSESS_ID2, Massakopiointi.Tyyppi.KOMOTO_ENTITY, KoulutusmoduuliToteutus.class, toteutus5, metaJson);

        /*
         * findAll
         */
        List<Massakopiointi> all = instance.findAll();
        assertEquals("findAll", 5, all.size());

        /*
         * rowCount
         */
        assertEquals("rowCount 1/2", 2l, instance.rowCount(PROSESS_ID1, HAKU_OID_1));
        assertEquals("rowCount 2/2", 3l, instance.rowCount(PROSESS_ID2, HAKU_OID_2));

        /*
         * search by search criteria
         */
        List<Massakopiointi> search = instance.search(new MassakopiointiDAO.SearchCriteria(HAKU_OID_1, "asdads", null, Massakopiointi.Tyyppi.KOMOTO_ENTITY, null, null));
        assertEquals("search 1/2 by 3 params", 0, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(HAKU_OID_1, null, null, Massakopiointi.Tyyppi.KOMOTO_ENTITY, null, null));
        assertEquals("search 2/2 by 3 params", 2, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, ANY_UNIQUE_OID_2, null, Massakopiointi.Tyyppi.KOMOTO_ENTITY, null, null));
        assertEquals("search 1/3 by 2 params", 1, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, null, Massakopiointi.Tyyppi.KOMOTO_ENTITY, null, null));
        assertEquals("search 2/3 by 2 params", 5, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, null, Massakopiointi.Tyyppi.HAKUKOHDE_ENTITY, null, null));
        assertEquals("search 3/3 by 2 params", 0, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(HAKU_OID_2, null, null, null, null, null));
        assertEquals("search by 1 params", 3, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, NEW_OID1, null, null, null));
        assertEquals("search new oid", 5, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, null, null, PROSESS_ID1, null));
        assertEquals("search processId1", 2, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, null, null, PROSESS_ID2, null));
        assertEquals("search processId2", 3, search.size());

        search = instance.search(new MassakopiointiDAO.SearchCriteria(null, null, null, null, PROSESS_ID2, Massakopiointi.KopioinninTila.READY_FOR_COPY));
        assertEquals("search processId2", 3, search.size());

        /*
         * find
         */
        Object nullResult = instance.find(PROSESS_ID2, ANY_UNIQUE_OID_1, KoulutusmoduuliToteutus.class);
        assertNull("findByOid -  required null", nullResult);
        Pair<Object, MetaObject> find = instance.find(PROSESS_ID1, ANY_UNIQUE_OID_1, KoulutusmoduuliToteutus.class);
        KoulutusmoduuliToteutus kt1 = (KoulutusmoduuliToteutus) find.getFirst();

        assertNotNull("findByOid - result KoulutusmoduuliToteutus?", kt1);
        assertEquals("findByOid koulutus uri", KOULUTUS_URI1, kt1.getKoulutusUri());
        assertEquals("findByOid date", DATE, kt1.getKoulutuksenAlkamisPvm());
        // assertEquals("findByOid tila", TarjontaTila.KOPIOITU, kt1.getTila());
        assertEquals("findByOid meta komo", "oid3", find.getSecond().getOriginalKomoOid());
        assertEquals("findByOid meta hakukohde", "oid1", find.getSecond().getHakukohdeOids().iterator().next());
        assertEquals("findByOid meta komoto", "oid2", find.getSecond().getKomotoOids().iterator().next());


        /*
         * findByHakuOid
         */
        List<Massakopiointi> resultByhaku = instance.findByHakuOid(HAKU_OID_1);
        assertEquals("findByHakuOid 1/2", 2, resultByhaku.size());

        resultByhaku = instance.findByHakuOid(HAKU_OID_2);
        assertEquals("findByHakuOid 2/2", 3, resultByhaku.size());

        /*
         * findByHakuOidAndOids
         */
        List<Massakopiointi> findByHakuOidAndOids = instance.findByHakuOidAndOids(HAKU_OID_2, Lists.newArrayList(ANY_UNIQUE_OID_1));
        assertEquals("findByHakuOidAndOids 1/2", 0, findByHakuOidAndOids.size());

        findByHakuOidAndOids = instance.findByHakuOidAndOids(HAKU_OID_2, Lists.newArrayList(ANY_UNIQUE_OID_3, ANY_UNIQUE_OID_4));
        assertEquals("findByHakuOidAndOids 2/2", 2, findByHakuOidAndOids.size());

        /*
         * updateTila
         */
        long updateTila = instance.updateTila(PROSESS_ID2, ANY_UNIQUE_OID_5, Massakopiointi.KopioinninTila.COPIED, ANOTHER_DATE);
        assertEquals("updateTila", 1, updateTila);

        /*
         * delete
         */
        long deleted = instance.deleteAllByHakuOid(HAKU_OID_1);
        assertEquals("deleteAllByHakuOid", 2, deleted);

        deleted = instance.deleteByHakuOidAndKopioinninTila(HAKU_OID_2, Massakopiointi.KopioinninTila.ERROR);
        assertEquals("deleteByHakuOidAndKopioinninTila 1/2", 0, deleted);

        deleted = instance.deleteByHakuOidAndKopioinninTila(HAKU_OID_2, Massakopiointi.KopioinninTila.COPIED);
        assertEquals("deleteByHakuOidAndKopioinninTila 2/2", 1, deleted);
    }

}
