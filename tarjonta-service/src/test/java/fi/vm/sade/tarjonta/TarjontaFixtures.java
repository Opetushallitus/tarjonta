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
package fi.vm.sade.tarjonta;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sets up common test fixtures that can be used through out different
 * DAO/service tests.
 *
 */
@Component
public class TarjontaFixtures {

    public static final String OID_ORGANISAATIO = "1.2.3.4.555";
    public static final String OID_TIETOJENKASITTELYN_KOULUTUS = "dummy";
    public Koulutusmoduuli simpleTutkintoOhjelma;
    public KoulutusmoduuliToteutus simpleTutkintoOhjelmaToteutus;
    public Koulutusmoduuli simpleTutkinnonOsa;
    public Hakukohde simpleHakukohde;
    public Hakukohde hakukohdeWithValintakoe;
    public Haku simpleHaku;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private HakukohdeDAO hakukohdeDAO;
    @Autowired
    private HakuDAO hakuDAO;
    @Autowired
    private KoulutusSisaltyvyysDAO rakenneDAO;
    private static final Random random = new Random(System.currentTimeMillis());

    public void recreate() {

        simpleTutkintoOhjelma = createTutkintoOhjelma();
        simpleTutkintoOhjelmaToteutus = createTutkintoOhjelmaToteutus();
        simpleHakukohde = createHakukohde();
        simpleTutkinnonOsa = createTutkinnonOsa();
        simpleHaku = createHaku();

        hakukohdeWithValintakoe = createHakukohde();
        hakukohdeWithValintakoe.addValintakoe(new Valintakoe());

    }

    public Yhteyshenkilo createYhteyshenkilo(String oid) {

        Yhteyshenkilo h = new Yhteyshenkilo();
        h.setHenkioOid(oid);
        h.setEtunimis("Pekka");
        h.setSukunimi("Yhteyttaja");
        h.setKielis("fi", "en");
        h.setPuhelin("+358 123 123 123");
        h.setSahkoposti("pekka.yhteyttaja@no.such.domain.fi");

        return h;

    }

    public Valintakoe createValintakoe() {
        Valintakoe valintakoe = new Valintakoe();

        valintakoe.setTyyppiUri("uri:uri:ori");

        ValintakoeAjankohta valintakoeAjankohta = new ValintakoeAjankohta();
        valintakoeAjankohta.setLisatietoja("Lisa lisa");
        valintakoeAjankohta.setAlkamisaika(new Date());
        valintakoeAjankohta.setPaattymisaika(new Date());
        valintakoeAjankohta.setValintakoe(valintakoe);

        Osoite osoite = new Osoite();
        osoite.setPostitoimipaikka("HELLSINKI");
        osoite.setPostinumero("666666");
        osoite.setOsoiterivi1("Katu 123");

        valintakoeAjankohta.setAjankohdanOsoite(osoite);

        valintakoe.addAjankohta(valintakoeAjankohta);

        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        monikielinenTeksti.addTekstiKaannos("fi", "testi");
        valintakoe.setKuvaus(monikielinenTeksti);

        return valintakoe;
    }

    public Koulutusmoduuli createTutkintoOhjelma() {
        return createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
    }

    public Koulutusmoduuli createKoulutusmoduuli(KoulutusmoduuliTyyppi tyyppi) {
        Koulutusmoduuli m = new Koulutusmoduuli(tyyppi);
        m.setOid(randomOid("koulutusmoduuli"));
        m.setTutkintoOhjelmanNimi("Simple Tutkinto-Ohjelma");
        m.setEqfLuokitus(randomUri("eqf"));
        m.setNqfLuokitus(randomUri("nqf"));
        m.setKoulutusAste(randomUri("koulutusaste"));
        m.setKoulutusala(randomUri("koulutusala"));
        m.setKoulutusohjelmaKoodi(randomUri("koulutusohjelma"));
        m.setKoulutusKoodi(randomUri("koulutusluokitus"));
        m.setNimi(createText("Koulutusmoduulinimi (fi)", "Koulutusmoduulinimi (sv)", "Koulutusmoduulinimi (en)"));
        m.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value());

        return m;
    }

    /**
     * Only base data, missing all koodisto relations.
     * 
     * @param tyyppi
     * @param oid
     * @param koulutustyyppi
     * @return 
     */
    public Koulutusmoduuli createKoulutusmoduuli(KoulutusmoduuliTyyppi tyyppi, String oid, KoulutusasteTyyppi koulutustyyppi) {
        Preconditions.checkNotNull(tyyppi, "KoulutusmoduuliTyyppi enum cannot be null.");
        Preconditions.checkNotNull(oid, "KOMO OID cannot be null.");
        Preconditions.checkNotNull(koulutustyyppi, "KoulutusasteTyyppi enum cannot be null.");
     
        Koulutusmoduuli m = new Koulutusmoduuli(tyyppi);
        m.setOid(oid);
        m.setKoulutustyyppi(koulutustyyppi.value());

        return m;
    }

    public Koulutusmoduuli createTutkintoOhjelma(KoulutusmoduuliTyyppi tyyppi) {
        Preconditions.checkNotNull(tyyppi, "KoulutusmoduuliTyyppi object cannot be null.");
        Koulutusmoduuli m = new Koulutusmoduuli(tyyppi);
        m.setOid(randomOid("koulutusmoduuli"));
        m.setTutkintoOhjelmanNimi("Simple Tutkinto-Ohjelma");
        m.setEqfLuokitus(randomUri("eqf"));
        m.setNqfLuokitus(randomUri("nqf"));
        m.setKoulutusAste(randomUri("koulutusaste"));
        m.setKoulutusala(randomUri("koulutusala"));
        m.setKoulutusohjelmaKoodi(randomUri("koulutusohjelma"));
        m.setKoulutusKoodi(randomUri("koulutusluokitus"));
        m.setNimi(createText("Koulutusmoduulinimi (fi)", "Koulutusmoduulinimi (sv)", "Koulutusmoduulinimi (en)"));

        return m;

    }

    public KoulutusmoduuliToteutus createTutkintoOhjelmaToteutus() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus(null);
        t.setTarjoaja(OID_ORGANISAATIO);
        t.setOid(randomOid("koulutusmoduulitotetutus"));
        t.setKoulutuksenAlkamisPvm(cal.getTime());
        t.setMaksullisuus(null);
        t.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        t.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));

        return t;

    }

    public KoulutusmoduuliToteutus createTutkintoOhjelmaToteutusWithTarjoajaOid(String tarjoajaOid) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus(null);
        t.setTarjoaja(tarjoajaOid);
        t.setOid(randomOid("koulutusmoduulitotetutus"));
        t.setKoulutuksenAlkamisPvm(cal.getTime());
        t.setMaksullisuus(null);
        t.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        t.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));

        return t;

    }

    public KoulutusmoduuliToteutus createTutkintoOhjelmaToteutus(String komotoOid) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus(null);
        t.setOid(komotoOid);
        t.setKoulutuksenAlkamisPvm(cal.getTime());
        t.setMaksullisuus(null);
        t.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        t.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));
        t.addYhteyshenkilo(createSimpleYhteyshenkilo(null));

        return t;

    }

    public Yhteyshenkilo createSimpleYhteyshenkilo(String oid) {
        Yhteyshenkilo h = new Yhteyshenkilo();
        h.setEtunimis("Irma");
        h.setHenkioOid(oid);
        h.setKielis("sv", "en");
        h.setPuhelin("+358123123123");
        h.setSahkoposti("irma@oph.fi");
        h.setSukunimi("Birgerdahl");
        h.setTitteli("Spesialisti");
        return h;
    }

    public Koulutusmoduuli createTutkinnonOsa() {

        Koulutusmoduuli m = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINNON_OSA);
        m.setOid(randomOid("tutkinnonosa"));
        return m;

    }

    /**
     * Creates a minimal non-persisted Hakukohde.
     *
     * @return
     */
    public Hakukohde createHakukohde() {

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setOid(randomOid("hakukohde"));
        hakukohde.setHakukohdeNimi(randomUri("hakukohde"));
        hakukohde.setAlinValintaPistemaara(10);
        hakukohde.setAloituspaikatLkm(100);
        List<String> hakukelpoisuusUris = new ArrayList<String>();
        hakukelpoisuusUris.add(randomUri("koulutustaso"));
        hakukohde.getHakukelpoisuusVaatimukset().addAll(hakukelpoisuusUris);
        hakukohde.setTila(TarjontaTila.VALMIS);
        hakukohde.setYlinValintaPistemaara(200);
        hakukohde.setLastUpdateDate(new Date());

        return hakukohde;

    }

    public HakukohdeTyyppi createHakukohdeTyyppi() {

        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setOid(randomOid("hakukohde"));
        hakukohde.setHakukohdeNimi(randomUri("hakukohde"));
//        hakukohde.setAlinValintaPistemaara(10);
        hakukohde.setAloituspaikat(100);
        hakukohde.setHakukelpoisuusVaatimukset(randomUri("koulutustaso"));
        hakukohde.setHakukohteenTila(fi.vm.sade.tarjonta.service.types.TarjontaTila.VALMIS);
//        hakukohde.setYlinValintaPistemaara(200);
//        hakukohde.setLastUpdateDate(new Date());

        return hakukohde;

    }

    /**
     * Creates a minimal non-persisted Hakukohde with given oid.
     *
     * @return
     */
    public Hakukohde createHakukohdeWithGivenOid(String hakukohdeOid) {

        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHakukohdeNimi(randomOid("hakukohde"));
        hakukohde.setOid(hakukohdeOid);
        return hakukohde;

    }

    public Hakukohde createPersistedHakukohde() {

        Hakukohde h = createHakukohde();
        h.setHaku(createPersistedHaku());
        return hakukohdeDAO.insert(h);

    }

    public Haku createHaku() {
        Haku haku = new Haku();
        haku.setOid(randomOid("haku"));
        haku.setNimiFi("haku (fi)");
        haku.setNimiEn("haku (en)");
        haku.setNimiSv("haku (sv)");
        haku.setHakukausiVuosi(2013);
        haku.setKoulutuksenAlkamisVuosi(2014);
        haku.setHakukausiUri(randomUri("hakukausi"));
        haku.setKoulutuksenAlkamiskausiUri(randomUri("alkamiskausi"));
        haku.setKohdejoukkoUri(randomUri("haunkohdejoukko"));
        haku.setHakutapaUri(randomUri("hakutapa"));
        haku.setHakutyyppiUri(randomUri("hakutyyppi"));
        haku.setTila(TarjontaTila.LUONNOS);
        haku.setLastUpdateDate(new Date());
        haku.setLastUpdatedByOid("TEST");
        return haku;
    }

    public BinaryData createBinaryData() {
        return createBinaryData("filename", "mimetype");
    }

    public BinaryData createBinaryData(String name, String type) {
        BinaryData image = new BinaryData();

        byte[] bytes = new byte[1];
        bytes[0] = 1;

        image.setData(bytes);
        image.setFilename(name);
        image.setMimeType(type);

        return image;
    }

    public Haku createPersistedHaku() {
        return hakuDAO.insert(createHaku());
    }

    public Hakukohde createPersistedHakukohdeWithKoulutus() {
        return createPersistedHakukohdeWithKoulutus(OID_ORGANISAATIO);
    }

    public Hakukohde createPersistedHakukohdeWithKoulutus(String tarjoajaOid) {

        Hakukohde h = createPersistedHakukohde();//createHakukohde();
        h.setAloituspaikatLkm(1);
        h.setValintojenAloituspaikatLkm(1);
        KoulutusmoduuliToteutus t1 = createTutkintoOhjelmaToteutusWithTarjoajaOid(tarjoajaOid);
        KoulutusmoduuliToteutus t2 = createTutkintoOhjelmaToteutusWithTarjoajaOid(tarjoajaOid);
        KoulutusmoduuliToteutus t3 = createTutkintoOhjelmaToteutusWithTarjoajaOid(tarjoajaOid);

        Koulutusmoduuli m1 = koulutusmoduuliDAO.insert(createTutkintoOhjelma());

        t1.setKoulutusmoduuli(m1);
        t2.setKoulutusmoduuli(m1);
        t3.setKoulutusmoduuli(m1);

        koulutusmoduuliToteutusDAO.insert(t1);
        koulutusmoduuliToteutusDAO.insert(t2);
        koulutusmoduuliToteutusDAO.insert(t3);

        flush();
        h.addKoulutusmoduuliToteutus(t1);
        h.addKoulutusmoduuliToteutus(t2);
        h.addKoulutusmoduuliToteutus(t3);

        t1.addHakukohde(h);
        t2.addHakukohde(h);
        t3.addHakukohde(h);

        hakukohdeDAO.update(h);
        h = hakukohdeDAO.read(h.getId());
        flush();

        hakukohdeDAO.update(h);

        return h;

    }

    public KoulutusmoduuliToteutus createPersistedKoulutusmoduuliToteutusWithMultipleHakukohde() {

        Hakukohde h1 = createPersistedHakukohde();
        Hakukohde h2 = createPersistedHakukohde();
        Hakukohde h3 = createPersistedHakukohde();

        hakukohdeDAO.insert(h1);
        hakukohdeDAO.insert(h2);
        hakukohdeDAO.insert(h3);

        Koulutusmoduuli m1 = createTutkintoOhjelma();
        KoulutusmoduuliToteutus t1 = createTutkintoOhjelmaToteutus();

        t1.addHakukohde(h1);
        t1.addHakukohde(h2);
        t1.addHakukohde(h3);

        koulutusmoduuliDAO.insert(m1);
        t1.setKoulutusmoduuli(m1);

        return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.insert(t1);
    }

    /**
     * Creates structure like (not tree):
     *
     * <pre>
     *    0
     *   / \
     *   1 2
     *   \ /
     *    3
     * </pre>
     *
     * @return
     */
    public Koulutusmoduuli createPersistedKoulutusmoduuliStructure() {

        Koulutusmoduuli root = createTutkintoOhjelma();
        Koulutusmoduuli child1 = createTutkinnonOsa();
        Koulutusmoduuli child2 = createTutkinnonOsa();
        Koulutusmoduuli child3 = createTutkinnonOsa();

        koulutusmoduuliDAO.insert(root);
        koulutusmoduuliDAO.insert(child1);
        koulutusmoduuliDAO.insert(child2);
        koulutusmoduuliDAO.insert(child3);

        rakenneDAO.insert(new KoulutusSisaltyvyys(root, child1, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF));
        rakenneDAO.insert(new KoulutusSisaltyvyys(root, child2, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF));
        rakenneDAO.insert(new KoulutusSisaltyvyys(child1, child3, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF));
        rakenneDAO.insert(new KoulutusSisaltyvyys(child2, child3, KoulutusSisaltyvyys.ValintaTyyppi.SOME_OFF));

        flush();
        clear();

        return (Koulutusmoduuli) koulutusmoduuliDAO.read(root.getId());

    }

    /**
     * Deletes all entities used in testing.
     */
    public void deleteAll() {

        for (Haku o : hakuDAO.findAll()) {
            hakuDAO.remove(o);
        }

        for (Hakukohde o : hakukohdeDAO.findAll()) {
            hakukohdeDAO.remove(o);
        }

        for (KoulutusSisaltyvyys r : rakenneDAO.findAll()) {
            rakenneDAO.remove(r);
        }

        for (KoulutusmoduuliToteutus t : koulutusmoduuliToteutusDAO.findAll()) {
            koulutusmoduuliToteutusDAO.remove(t);
        }

        for (Koulutusmoduuli m : koulutusmoduuliDAO.findAll()) {
            koulutusmoduuliDAO.remove(m);
        }

        flush();
        clear();

    }

    private void flush() {
        ((KoulutusmoduuliDAOImpl) koulutusmoduuliDAO).getEntityManager().flush();
    }

    private void clear() {
        ((KoulutusmoduuliDAOImpl) koulutusmoduuliDAO).getEntityManager().clear();
    }

    private String randomOid(String type) {
        return "1.2.3.4." + type + "." + System.currentTimeMillis() + "-" + Math.abs(random.nextInt());
    }

    private String randomUri(String context) {
        return "http://" + context + "/" + System.currentTimeMillis() + "-" + Math.abs(random.nextInt());
    }

    public static MonikielinenTeksti createText(String textFi, String textSv, String textEn) {

        MonikielinenTeksti teksti = new MonikielinenTeksti();

        if (textFi != null) {
            teksti.addTekstiKaannos("fi", textFi);
        }
        if (textSv != null) {
            teksti.addTekstiKaannos("sv", textSv);
        }
        if (textEn != null) {
            teksti.addTekstiKaannos("en", textEn);
        }

        return teksti;

    }
}
