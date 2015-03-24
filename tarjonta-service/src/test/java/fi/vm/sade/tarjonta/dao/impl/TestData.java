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
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import org.joda.time.DateTime;

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
    protected Calendar cal2, cal3;
    protected static final Date KOULUTUS_START_DATE = (new DateTime(2013, 1, 1, 0, 0, 0, 0)).toDate();

    private static final Date DATE = (new DateTime(2014, 1, 6, 0, 0, 0, 0)).toDate();

    private static final Date ANOTHER_DATE = (new DateTime(2014, 3, 9, 0, 0, 0, 0)).toDate();

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

        cal2 = Calendar.getInstance();
        cal2.set(Calendar.YEAR, YEAR);
        cal2.set(Calendar.DAY_OF_MONTH, 5);

        cal3 = Calendar.getInstance();
        cal3.set(Calendar.YEAR, YEAR);
        cal3.set(Calendar.DAY_OF_MONTH, 10);

        komoto1 = addKomoto(KOMOTO_OID_1); //legacy komoto

        KoulutusmoduuliToteutus t2Normal = createKomoto(KOMOTO_OID_2);
        komoto2 = addKomoto(t2Normal, KOULUTUS_START_DATE);

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
        kohde1 = createHakukohde(HAKUKOHDE_OID1);  //three exams

        MonikielinenTeksti aloituspaikatKuvaus = new MonikielinenTeksti();
        aloituspaikatKuvaus.addTekstiKaannos("kieli_en", "Max 10");

        kohde1.setAloituspaikatKuvaus(aloituspaikatKuvaus);

        kohde1.setHakukohdeKoodistoNimi(HUMAN_READABLE_NAME_1);
        kohde1.setHakukohdeNimi(KOODISTO_URI_1);

        kohde2 = createHakukohde(HAKUKOHDE_OID2);//one exam

        kohde3 = createHakukohde(HAKUKOHDE_OID3);//no exams

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

    protected void check(KoulutusmoduuliToteutus k) {
        assertNotNull(k.getOid());
        // assertNotNull(k.getKoulutuksenAlkamisPvm()); tests disabled as tested elsewhere
        assertNotNull("alkamisvuosi", k.getAlkamisVuosi());
        assertEquals(YEAR, k.getAlkamisVuosi().intValue());
        assertEquals(KAUSI, k.getAlkamiskausiUri());
        assertEquals(true, k.getMaksullisuus());
        assertEquals(1, k.getOpetuskielis().size());
        assertEquals(1, k.getOpetusmuotos().size());
        assertEquals(1, k.getKoulutuslajis().size());
        assertEquals(TarjontaTila.VALMIS, k.getTila());
        assertEquals(ORG_OID_1, k.getTarjoaja());
        assertEquals("pohjakoulutusvaatimus_uri", k.getPohjakoulutusvaatimusUri());
        assertEquals("100000000", k.getSuunniteltukestoArvo());
        assertEquals("suuniteltu_kesto_uri", k.getSuunniteltukestoYksikkoUri());

        //KJOH-764 - new fields:
        assertEquals("koulutusala_uri", k.getKoulutusalaUri());
        assertEquals("eqf_uri", k.getEqfUri());
        assertEquals("nqf_uri", k.getNqfUri());
        assertEquals("koulutusaste_uri", k.getKoulutusasteUri());
        assertEquals("koulutus_uri", k.getKoulutusUri());
        assertEquals("koulutusohjelma_uri", k.getKoulutusohjelmaUri());
        assertEquals("tutkinto_uri", k.getTutkintoUri());

        assertEquals("laajuus_arvo", k.getOpintojenLaajuusArvo());
        assertEquals("laajuus_arvo_uri", k.getOpintojenLaajuusarvoUri());
        assertEquals("laajuus_yksikko_uri", k.getOpintojenLaajuusyksikkoUri());
        assertEquals("lukiolinja_uri", k.getLukiolinjaUri());
        assertEquals("koulutustyyppi_uri", k.getKoulutustyyppiUri());
        assertEquals("opintoala_uri", k.getOpintoalaUri());
        assertEquals("tutkintonimike_uri", k.getTutkintonimikeUri());
    }

    private void persist(Object o, boolean validatePersistedData) {
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
            assertNotNull("Persist failed : KoulutusmoduuliToteutus object", find);
            if (validatePersistedData) {
                check(find);
            } else {
                assertNotNull(find.getId());
            }
        } else if (o instanceof Koulutusmoduuli) {
            Koulutusmoduuli v = (Koulutusmoduuli) o;
            Koulutusmoduuli find = em.find(Koulutusmoduuli.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else {
            em.persist(o);
        }
    }

    protected void persist(Object o) {
        persist(o, false);
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
        return addKomoto(createKomoto(oid), KOULUTUS_START_DATE);
    }

    private KoulutusmoduuliToteutus addKomoto(KoulutusmoduuliToteutus komoto, Date date) {
        komoto.setKoulutusmoduuli(komo);
        komo.addKoulutusmoduuliToteutus(komoto);

        if (date != null) {
            komoto.setKoulutuksenAlkamisPvm(date);
        }

        persist(komoto, true);

        return komoto;
    }

    public Hakukohde createHakukohde(String oid) {
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid((oid));
        hakukohde.setHakukohdeNimi("hakukohde_nimi");
        hakukohde.setAlinValintaPistemaara(10);
        hakukohde.setAloituspaikatLkm(100);
        List<String> hakukelpoisuusUris = new ArrayList<String>();
        hakukelpoisuusUris.add("koulutustaso´_uri");
        hakukohde.getHakukelpoisuusVaatimukset().addAll(hakukelpoisuusUris);
        hakukohde.setTila(TarjontaTila.VALMIS);
        hakukohde.setYlinValintaPistemaara(200);
        hakukohde.setLastUpdateDate(new Date());
        return hakukohde;
    }

    private KoulutusmoduuliToteutus createKomoto(String oid) {
        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus();
        t.setOid(oid);
        t.setKoulutuksenAlkamisPvm(KOULUTUS_START_DATE);
        t.setAlkamisVuosi(YEAR);
        t.setAlkamiskausiUri(KAUSI);
        t.setMaksullisuus(true);
        t.addOpetuskieli(new KoodistoUri("kieli_uri"));
        t.addOpetusmuoto(new KoodistoUri("opetusmuoto_uri"));
        t.addKoulutuslaji("koulutuslaji_uri");
        t.setTila(TarjontaTila.VALMIS);
        t.setTarjoaja(ORG_OID_1);
        t.setPohjakoulutusvaatimusUri("pohjakoulutusvaatimus_uri");
        t.setOpintojenLaajuusArvo("laajuus_arvo");
        t.setSuunniteltuKesto("suuniteltu_kesto_uri", "100000000");

        //KJOH-764 - new fields:
        t.setKoulutusalaUri("koulutusala_uri");
        t.setEqfUri("eqf_uri");
        t.setNqfUri("nqf_uri");
        t.setKoulutusasteUri("koulutusaste_uri");
        t.setKoulutusUri("koulutus_uri");
        t.setKoulutusohjelmaUri("koulutusohjelma_uri");
        t.setTutkintoUri("tutkinto_uri");

        t.setOpintojenLaajuusarvoUri("laajuus_arvo_uri");
        t.setOpintojenLaajuusyksikkoUri("laajuus_yksikko_uri");
        t.setLukiolinjaUri("lukiolinja_uri");
        t.setKoulutustyyppiUri("koulutustyyppi_uri");
        t.setOpintoalaUri("opintoala_uri");
        t.setTutkintonimikeUri("tutkintonimike_uri");

        return t;
    }
}
