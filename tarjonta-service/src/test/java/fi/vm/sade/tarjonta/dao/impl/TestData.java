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
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 *
 * @author Jani Wilén
 */
public class TestData {

	public static final int VALINTAKOE_COUNT_FOR_OID1 = 3;
    public static final String KOMOTO_OID_1 = "komoto_oid1";
    public static final String KOMOTO_OID_2 = "komoto_oid2";
    public static final String KOMOTO_OID_3 = "komoto_oid3";
    public static final String KOMOTO_OID_4 = "komoto_oid4";

    public static final String ORG_OID_1 = "organisation_oid1";
    public static final String KAUSI = "kausi_uri";
    public static final int YEAR = 2014;

    public static final String HAKU_OID1 = "hakuoid1";
    public static final String HAKU_OID2 = "hakuoid2";
    public static final String HAKUKOHDE_OID1 = "hakukohde_oid_1";
    public static final String HAKUKOHDE_OID2 = "hakukohde_oid_2";
    public static final String HAKUKOHDE_OID3 = "hakukohde_oid_3";
    public static final String HUMAN_READABLE_NAME_1 = "human_readable_name";
    public static final String KOODISTO_URI_1 = "koodisto_uri";
    protected Hakukohde kohde1, kohde2, kohde3;
    protected Valintakoe koe1, koe2, koe3, koe4;
    protected Haku haku1, haku2;
    protected Calendar cal1, cal2, cal3;

    private TarjontaFixtures fixtures;
    private EntityManager em;

    private KoulutusmoduuliToteutus komoto1, komoto2, komoto3, komoto4;

    private Koulutusmoduuli komo;

    public KoulutusmoduuliToteutus getPersistedKomoto1() {
        return komoto1;
    }

    public KoulutusmoduuliToteutus getPersistedKomoto2() {
        return komoto2;
    }

    public KoulutusmoduuliToteutus getPersistedKomoto3() {
        return komoto3;
    }

    public KoulutusmoduuliToteutus getPersistedKomoto4() {
        return komoto4;
    }
    
    public Haku getHaku1() {
		return haku1;
	}
    
    public Haku getHaku2() {
		return haku2;
	}
    
    public TestData() {
    }

    @Transactional
    public void initializeData(EntityManager em, TarjontaFixtures fixtures) {
        this.fixtures = fixtures;
        this.em = em;

        komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        persist(komo);

        cal1 = Calendar.getInstance();
        cal1.set(Calendar.YEAR, YEAR);
        cal1.set(Calendar.DAY_OF_MONTH, 1);

        cal2 = Calendar.getInstance();
        cal2.set(Calendar.YEAR, YEAR);
        cal2.set(Calendar.DAY_OF_MONTH, 5);

        cal3 = Calendar.getInstance();
        cal3.set(Calendar.YEAR, YEAR);
        cal3.set(Calendar.DAY_OF_MONTH, 10);

        komoto1 = addKomoto(KOMOTO_OID_1); //legacy komoto

        KoulutusmoduuliToteutus t2Normal = createKomoto(KOMOTO_OID_2);
        komoto2 = addKomoto(t2Normal, cal1.getTime());

        KoulutusmoduuliToteutus t3NoAlkamispvm = createKomoto(KOMOTO_OID_3);
        t3NoAlkamispvm.clearKoulutuksenAlkamisPvms(); //must be an empty date list!!!
        komoto3 = addKomoto(t3NoAlkamispvm, null);

        KoulutusmoduuliToteutus t4WithDates = createKomoto(KOMOTO_OID_4);
        t4WithDates.addKoulutuksenAlkamisPvms(cal2.getTime());
        t4WithDates.addKoulutuksenAlkamisPvms(cal3.getTime());

        komoto4 = addKomoto(t4WithDates, null);

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
        kohde1.setOid(HAKUKOHDE_OID1); //three exams

        kohde2 = fixtures.createHakukohde();
        kohde2.setOid(HAKUKOHDE_OID2); //one exam

        kohde3 = fixtures.createHakukohde();
        kohde3.setOid(HAKUKOHDE_OID3); //no exams

        koe1 = fixtures.createValintakoe();
        koe2 = fixtures.createValintakoe();
        koe3 = fixtures.createValintakoe();
        koe4 = fixtures.createValintakoe();

        /* 
         * DATA MAPPING 
         */
        kohde1.addKoulutusmoduuliToteutus(komoto1);
        komoto1.addHakukohde(kohde1);

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
        assertEquals(items, k.getValintakoes().size());
    }

    protected void persist(Object o) {
        em.persist(o);
        em.flush();

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

    private KoulutusmoduuliToteutus addKomoto(String oid) {
        return addKomoto(fixtures.createTutkintoOhjelmaToteutus(oid), cal1.getTime());
    }

    private KoulutusmoduuliToteutus addKomoto(KoulutusmoduuliToteutus komoto, Date date) {
        komoto.setKoulutusmoduuli(komo);
        komo.addKoulutusmoduuliToteutus(komoto);

        if (date != null) {
            komoto.setKoulutuksenAlkamisPvm(date);
        }

        persist(komoto);

        return komoto;
    }

    private KoulutusmoduuliToteutus createKomoto(String oid) {
        KoulutusmoduuliToteutus t3 = new KoulutusmoduuliToteutus(null);
        t3.setOid(oid);
        t3.setKoulutuksenAlkamisPvm(cal1.getTime());
        t3.setAlkamisVuosi(YEAR);
        t3.setAlkamiskausiUri(KAUSI);
        t3.setMaksullisuus(null);
        t3.addOpetuskieli(new KoodistoUri("kieli_uri"));
        t3.addOpetusmuoto(new KoodistoUri("opetusmuoto_uri"));
        t3.addKoulutuslaji("koulutuslaji_uri");
        t3.setTila(TarjontaTila.VALMIS);
        t3.setTarjoaja(ORG_OID_1);
        t3.setPohjakoulutusvaatimusUri("pohjakoulutusvaatimus_uri");

        return t3;
    }
}
