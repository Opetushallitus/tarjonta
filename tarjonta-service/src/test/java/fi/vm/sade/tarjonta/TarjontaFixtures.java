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

import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sets up common test fixtures that can be used through out different DAO/service tests.
 *
 */
@Component
public class TarjontaFixtures {

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

    public void recreate() {

        simpleTutkintoOhjelma = createTutkintoOhjelma();
        simpleTutkintoOhjelmaToteutus = createTutkintoOhjelmaToteutus();
        simpleHakukohde = createHakukohde();
        simpleTutkinnonOsa = createTutkinnonOsa();
        simpleHaku = createHaku();

        hakukohdeWithValintakoe = createHakukohde();
        hakukohdeWithValintakoe.addValintakoe(new Valintakoe());

    }

    public Koulutusmoduuli createTutkintoOhjelma() {

        Koulutusmoduuli m = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        m.setOid(randomOid("koulutusmoduuli"));
        m.setTutkintoOhjelmanNimi("Simple Tutkinto-Ohjelma");
        m.setKoulutusKoodi("500001");

        return m;

    }

    public KoulutusmoduuliToteutus createTutkintoOhjelmaToteutus() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        KoulutusmoduuliToteutus t = new KoulutusmoduuliToteutus(null);
        t.setNimi("Simple Tutkinto-Ohjelma toteutus");
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
        t.setNimi("Simple Tutkinto-Ohjelma toteutus");
        t.setOid(komotoOid);
        t.setKoulutuksenAlkamisPvm(cal.getTime());
        t.setMaksullisuus(null);
        t.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        t.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));

        return t;

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
        hakukohde.setHakukohdeNimi(randomOid("hakukohde"));
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
        haku.setNimiFi("SimpleHaku");
        haku.setHakukausiVuosi(2013);
        haku.setKoulutuksenAlkamisVuosi(2014);
        haku.setHakukausiUri(randomUri("hakukausi"));
        haku.setKoulutuksenAlkamiskausiUri(randomUri("alkamiskausi"));
        haku.setKohdejoukkoUri(randomUri("haunkohdejoukko"));
        haku.setHakutapaUri(randomUri("hakutapa"));
        haku.setHakutyyppiUri(randomUri("hakutyyppi"));
        haku.setTila(KoodistoContract.TarjontaTilat.SUUNNITTELUSSA);
        return haku;
    }

    public Haku createPersistedHaku() {
        return hakuDAO.insert(createHaku());
    }

    public Hakukohde createPersistedHakukohdeWithKoulutus() {

        Hakukohde h = createPersistedHakukohde();//createHakukohde();

        KoulutusmoduuliToteutus t1 = createTutkintoOhjelmaToteutus();
        KoulutusmoduuliToteutus t2 = createTutkintoOhjelmaToteutus();
        KoulutusmoduuliToteutus t3 = createTutkintoOhjelmaToteutus();

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

        KoulutusmoduuliToteutus t1 = createTutkintoOhjelmaToteutus();

        t1.addHakukohde(h1);
        t1.addHakukohde(h2);
        t1.addHakukohde(h3);

        return (KoulutusmoduuliToteutus) koulutusmoduuliToteutusDAO.insert(t1);
    }

    /**
     * Creates structure like (not tree):
     *
     *    0
     *   / \
     *  1   2
     *   \ /
     *    3
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
        return "http://" + type + "/" + System.currentTimeMillis();
    }

    private String randomUri(String context) {
        return "http://" + context + "/" + System.currentTimeMillis();
    }

}

