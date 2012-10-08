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
package fi.vm.sade.tarjonta.atdd;

import fi.vm.sade.tarjonta.model.*;
import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Before;
import static org.junit.Assert.*;

import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class OVT1534_KoulutusVersiointiTest {

    private TutkintoOhjelma tutkintoOhjelma;

    private TutkinnonOsa child1;

    private TutkinnonOsa child2;

    private TutkinnonOsa child3;

    private TutkinnonOsa child4;

    private TutkinnonOsa child5;

    @Before
    public void setUp() {
    }

    //@Test
    public void testKopioiKoulutusRakenne() {

        //TutkintoOhjelma kopio = (TutkintoOhjelma) tutkintoOhjelma.createCopy();
        TutkintoOhjelma kopio = null;

        // tarkistetaan ylimman tason koulutusmoduuli
        assertNotNull(kopio);
        assertNotSame(tutkintoOhjelma, kopio);
        assertEquals("0", kopio.getNimi());

        // tarkistetaan seuraavan tason alimoduulit, maara seka attribuutit
        assertEquals(2, kopio.getStructures().size());
        Iterator<LearningOpportunityObject> koulutukset = new TreeSet(kopio.getChildNodes()).iterator();

        LearningOpportunityObject child1Copy = koulutukset.next();
        LearningOpportunityObject child2Copy = koulutukset.next();

        assertEquals("1", child1Copy.getNimi());
        assertEquals("2", child2Copy.getNimi());

        // tarkistetaan että kopiot ovat omia instansseja
        assertNotSame(child1, child1Copy);
        assertNotSame(child2, child2Copy);

        // tarkistetaan että alkuperaiset alimoduulit ovat eri instansseja kuin kopioidut
        // tarkistetaan kolmannen tason moduulit (maara)
        assertEquals(2, child1Copy.getChildNodes().size());
        assertEquals(1, child2Copy.getChildNodes().size());

        // tarkistetaan kolmannen tason moduulit (attribuutit)
        koulutukset = new TreeSet(child1Copy.getChildNodes()).iterator();

        LearningOpportunityObject child3 = koulutukset.next();
        LearningOpportunityObject child4 = koulutukset.next();

        assertEquals("3", child3.getNimi());
        assertEquals("4", child4.getNimi());


        assertEquals(1, child2Copy.getChildNodes().size());
        koulutukset = new TreeSet(child2Copy.getChildNodes()).iterator();

        LearningOpportunityObject child5 = koulutukset.next();

        assertEquals("5", child5.getNimi());

    }

    //@Test
    public void testKopioituKoulutusrakenneEiSisallaTunnisteita() {

        final AtomicInteger count = new AtomicInteger();

        new KoulutusTreeWalker(new KoulutusTreeWalker.NodeHandler() {

            @Override
            public boolean match(LearningOpportunityObject koulutus) {
                assertNull(koulutus.getId());
                assertNull(koulutus.getOid());
                count.incrementAndGet();
                return true;
            }

        }).walk(tutkintoOhjelma);

        assertEquals(6, count.get());

    }

    /**
     * Tarkistaa että kopioitu Koulutusmoduuli rakenne ei sisalla alkuperaisen rakenteen toteutuksia.
     */
    //@Test
    public void testKopioidullaKoulutuksellaEiOleToteutuksia() {

        // luo koulutusmoduleille toteutukset
        initTutkintoOhjelmaToteutus();

        //TutkintoOhjelma kopio = (TutkintoOhjelma) tutkintoOhjelma.createCopy();
        TutkintoOhjelma kopio = null;

        new KoulutusTreeWalker(new KoulutusTreeWalker.NodeHandler() {

            @Override
            public boolean match(LearningOpportunityObject koulutus) {

                Koulutusmoduuli moduuli = (Koulutusmoduuli) koulutus;
                assertTrue("did not expect toteutus objects: "
                    + moduuli.getLearningOpportunityInstances(),
                    moduuli.getLearningOpportunityInstances().isEmpty());

                return true;
            }

        }).walk(kopio);


    }

    private void initTutkintoOhjelmaToteutus() {


        new KoulutusTreeWalker(new KoulutusTreeWalker.NodeHandler() {

            @Override
            public boolean match(LearningOpportunityObject koulutus) {

                KoulutusmoduuliToteutus toteutus = null;

                if (koulutus instanceof TutkintoOhjelma) {
                    toteutus = new TutkintoOhjelmaToteutus();
                } else if (koulutus instanceof TutkinnonOsa) {
                    toteutus = new TutkinnonOsaToteutus();
                }

                toteutus.setNimi(koulutus.getNimi() + " toteutus");
                ((Koulutusmoduuli) koulutus).addLearningOpportunityInstance(toteutus);

                return true;

            }

        }).walk(tutkintoOhjelma);

    }

}

