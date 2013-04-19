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

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.Valintakoe;
import javax.persistence.EntityManager;
import static org.junit.Assert.*;

/**
 *
 * @author Jani Wilén
 */
public class TestData {

    protected static final int VALINTAKOE_COUNT_FOR_OID1 = 3;
    protected static final String HAKU_OID1 = "hakuoid1";
    protected static final String HAKU_OID2 = "hakuoid2";
    protected static final String OID1 = "oid_1";
    protected static final String OID2 = "oid_2";
    protected static final String OID3 = "oid_3";
    protected static final String HUMAN_READABLE_NAME_1 = "human_readable_name";
    protected static final String KOODISTO_URI_1 = "koodisto_uri";
    protected Hakukohde kohde1, kohde2, kohde3;
    protected Valintakoe koe1, koe2, koe3, koe4;
    protected Haku haku1, haku2;
    private TarjontaFixtures fixtures;
    private EntityManager em;

    public TestData() {
    }

    public void initializeData(EntityManager em, TarjontaFixtures fixtures) {
        this.fixtures = fixtures;
        this.em = em;

        Koulutusmoduuli komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        persist(komo);

        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus("123455");
        komoto.setKoulutusmoduuli(komo);
        komo.addKoulutusmoduuliToteutus(komoto);

        persist(komoto);

        haku1 = fixtures.createHaku();
        haku1.setOid(HAKU_OID1);
        persist(haku1);

        haku2 = fixtures.createHaku();
        haku2.setOid(HAKU_OID2);
        persist(haku2);

        /*
         * CREATE OBJECTS
         */

        kohde1 = fixtures.createHakukohde();
        kohde1.setHakukohdeKoodistoNimi(HUMAN_READABLE_NAME_1);
        kohde1.setHakukohdeNimi(KOODISTO_URI_1);
        kohde1.setOid(OID1); //three exams

        kohde2 = fixtures.createHakukohde();
        kohde2.setOid(OID2); //one exam

        kohde3 = fixtures.createHakukohde();
        kohde3.setOid(OID3); //no exams

        koe1 = fixtures.createValintakoe();
        koe2 = fixtures.createValintakoe();
        koe3 = fixtures.createValintakoe();
        koe4 = fixtures.createValintakoe();

        /* 
         * DATA MAPPING 
         */
        kohde1.addKoulutusmoduuliToteutus(komoto);
        komoto.addHakukohde(kohde1);

        kohde1.setHaku(haku1);
        kohde2.setHaku(haku1);
        kohde3.setHaku(haku1);

        haku1.addHakukohde(kohde1);
        haku1.addHakukohde(kohde2);
        haku1.addHakukohde(kohde3);

        /* 
         * PERSIST ALL OBJECTS 
         */

        //hakukohde1 <- koe1, koe2, koe3
        kohde1.addValintakoe(koe1);
        kohde1.addValintakoe(koe2);
        kohde1.addValintakoe(koe3);

        //hakukohde2 <- koe4
        kohde2.addValintakoe(koe4);

        persist(kohde1);
        persist(kohde2);
        persist(kohde3);

        check(3, kohde1);
        check(1, kohde2);
        check(0, kohde3);
    }

    private void check(int items, Hakukohde kohde) {
        Hakukohde k = em.find(Hakukohde.class, kohde.getId());
        em.detach(k);
        assertEquals(items, k.getValintakoes().size());
    }

    protected void persist(Object o) {
        em.persist(o);
        em.flush();
        em.detach(o);

        //a quick check
        if (o instanceof Haku) {
            Haku haku = (Haku) o;
            assertNotNull(em.find(Haku.class, haku.getId()));
        } else if (o instanceof Hakukohde) {
            Hakukohde h = (Hakukohde) o;
            assertNotNull(em.find(Hakukohde.class, h.getId()));
        } else if (o instanceof Valintakoe) {
            Valintakoe v = (Valintakoe) o;
            Valintakoe find = em.find(Valintakoe.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else if (o instanceof KoulutusmoduuliToteutus) {
            KoulutusmoduuliToteutus v = (KoulutusmoduuliToteutus) o;
            KoulutusmoduuliToteutus find = em.find(KoulutusmoduuliToteutus.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else if (o instanceof Koulutusmoduuli) {
            Koulutusmoduuli v = (Koulutusmoduuli) o;
            Koulutusmoduuli find = em.find(Koulutusmoduuli.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else {
            fail("Found an unknown object type : " + o.toString());
        }
    }

    public void clean() {
        remove(koe1);
        remove(koe2);
        remove(koe3);
        remove(koe4);

        remove(kohde1);
        remove(kohde2);
        remove(kohde3);

        remove(haku1);
        remove(haku2);
    }

    private void remove(Object o) {
        if (o instanceof Hakukohde) {
            em.remove(em.find(Hakukohde.class, ((Hakukohde) o).getId()));
        } else if (o instanceof Valintakoe) {
            em.remove(em.find(Valintakoe.class, ((Valintakoe) o).getId()));
        } else if (o instanceof Haku) {
            em.remove(em.find(Haku.class, ((Haku) o).getId()));
        }
    }
}
