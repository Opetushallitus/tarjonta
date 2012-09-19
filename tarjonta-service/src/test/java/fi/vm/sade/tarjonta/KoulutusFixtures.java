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

import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sets up common test fixtures that can be used through out different DAO/service tests.
 *
 */
@Component
public class KoulutusFixtures {

    public static final String OID_TIETOJENKASITTELYN_KOULUTUS = "dummy";

    public TutkintoOhjelma simpleTutkintoOhjelma;

    public TutkintoOhjelmaToteutus simpleTutkintoOhjelmaToteutus;

    public TutkinnonOsa simpleTutkinnonOsa;

    public Hakukohde simpleHakukohde;

    public Hakukohde hakukohdeWithValintakoe;

    public Haku simpleHaku;

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    public void recreate() {

        simpleTutkintoOhjelma = createTutkintoOhjelma();
        simpleTutkintoOhjelmaToteutus = createTutkintoOhjelmaToteutus();
        simpleHakukohde = createHakukohde();
        simpleTutkinnonOsa = createTutkinnonOsa();
        simpleHaku = createHaku();

        hakukohdeWithValintakoe = createHakukohde();
        hakukohdeWithValintakoe.addValintakoe(new Valintakoe());

    }

    public TutkintoOhjelma createTutkintoOhjelma() {

        TutkintoOhjelma t = new TutkintoOhjelma();
        t.setOid(randomOid("koulutusmoduuli"));
        t.setTutkintoOhjelmanNimi("Simple Tutkinto-Ohjelma");
        t.setKoulutusKoodi("500001");

        return t;

    }

    public TutkintoOhjelmaToteutus createTutkintoOhjelmaToteutus() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        TutkintoOhjelmaToteutus t = new TutkintoOhjelmaToteutus();
        t.setNimi("Simple Tutkinto-Ohjelma toteutus");
        t.setOid(randomOid("koulutusmoduulitotetutus"));
        t.setKoulutuksenAlkamisPvm(cal.getTime());
        t.setMaksullisuus(null);
        t.addOpetuskieli(new KoodistoUri("http://kielet/fi"));
        t.addOpetusmuoto(new KoodistoUri("http://opetusmuodot/lahiopetus"));

        return t;

    }

    public TutkinnonOsa createTutkinnonOsa() {

        TutkinnonOsa t = new TutkinnonOsa();
        t.setOid(randomOid("tutkinnonosa"));
        return t;

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
        haku.setHaunAlkamisPvm(new Date());
        haku.setHaunLoppumisPvm(new Date());
        haku.setHakukausiUri(randomUri("hakukausi"));
        haku.setKoulutuksenAlkamiskausiUri(randomUri("alkamiskausi"));
        haku.setKohdejoukkoUri(randomUri("haunkohdejoukko"));
        haku.setHakutapaUri(randomUri("hakutapa"));
        haku.setHakutyyppiUri(randomUri("hakutyyppi"));
        return haku;
    }

    public Haku createPersistedHaku() {
        return hakuDAO.insert(createHaku());
    }

    public Hakukohde createPersistedHakukohdeWithKoulutus() {

        Hakukohde h = createHakukohde();

        KoulutusmoduuliToteutus t1 = createTutkintoOhjelmaToteutus();
        KoulutusmoduuliToteutus t2 = createTutkintoOhjelmaToteutus();
        KoulutusmoduuliToteutus t3 = createTutkintoOhjelmaToteutus();

        koulutusDAO.insert(t1);
        koulutusDAO.insert(t2);
        koulutusDAO.insert(t3);

        h.addKoulutusmoduuliToteutus(t1);
        h.addKoulutusmoduuliToteutus(t2);
        h.addKoulutusmoduuliToteutus(t3);

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

        return (KoulutusmoduuliToteutus) koulutusDAO.insert(t1);
    }

    public Koulutusmoduuli createPersistedKoulutusmoduuliTree() {

        Koulutusmoduuli root = createTutkintoOhjelma();
        Koulutusmoduuli child1 = createTutkinnonOsa();
        Koulutusmoduuli child2 = createTutkinnonOsa();
        Koulutusmoduuli child3 = createTutkinnonOsa();

        koulutusDAO.insert(root);
        koulutusDAO.insert(child1);
        koulutusDAO.insert(child2);
        koulutusDAO.insert(child3);

        sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(root, child1, true));
        sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(root, child2, true));
        sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(child1, child3, false));
        sisaltyvyysDAO.insert(new KoulutusSisaltyvyys(child2, child3, true));

        flush();
        clear();

        return (Koulutusmoduuli) koulutusDAO.read(root.getId());

    }

    /**
     * Deletes all entities used in testing.
     */
    public void removeAll() {
        
        for (Haku o : hakuDAO.findAll()) {
            hakuDAO.remove(o);
        }
        
        for (Hakukohde o : hakukohdeDAO.findAll()) {
            hakukohdeDAO.remove(o);
        }
        
        for (KoulutusSisaltyvyys o : sisaltyvyysDAO.findAll()) {
            sisaltyvyysDAO.remove(o);
        }

        for (LearningOpportunityObject o : koulutusDAO.findAll()) {
            koulutusDAO.remove(o);
        }
        
        flush();
        clear();
        
    }

    private void flush() {
        ((KoulutusDAOImpl) koulutusDAO).getEntityManager().flush();
    }

    private void clear() {
        ((KoulutusDAOImpl) koulutusDAO).getEntityManager().clear();
    }

    private String randomOid(String type) {
        return "http://" + type + "/" + System.currentTimeMillis();
    }

    private String randomUri(String context) {
        return "http://" + context + "/" + System.currentTimeMillis();
    }

}

