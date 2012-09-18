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

import fi.vm.sade.tarjonta.KoulutusDatabasePrinter;
import fi.vm.sade.tarjonta.KoulutusFixtures;
import fi.vm.sade.tarjonta.dao.KoulutusDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.model.LearningOpportunityObject;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import static org.junit.Assert.*;

import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import java.util.Iterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Acceptance tests for OVT-1534
 * 
 * @see https://liitu.hard.ware.fi/jira/browse/OVT-1534
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class OVT1534_KoulutusrakenneTest {

    @Autowired
    private KoulutusDAO koulutusDAO;

    @Autowired
    private KoulutusSisaltyvyysDAO sisaltyvyysDAO;

    @Autowired
    private KoulutusDatabasePrinter dbPrinter;

    @Autowired
    private KoulutusFixtures fixtures;

    @Test
    public void testKoulutusmoduuliSisaltaaKoulutusmouduleita() {

        Koulutusmoduuli moduuli = fixtures.simpleKoulutusmoduuliTree();
        assertEquals(2, moduuli.getChildren().size());

    }

    @Test
    public void testKoulutusmoduuliVoidaanJakaa() {

        Koulutusmoduuli moduuli = fixtures.simpleKoulutusmoduuliTree();
        
        Iterator<LearningOpportunityObject> i = moduuli.getChildNodes().iterator();
        LearningOpportunityObject child1 = i.next();
        LearningOpportunityObject child2 = i.next();
        
        LearningOpportunityObject child3 = child1.getChildNodes().iterator().next();
        LearningOpportunityObject child4 = child2.getChildNodes().iterator().next();
        
        assertEquals(child3, child4);

    }

    @Test
    public void testKoulutusmoduuliinVoidaanLiittaaUseitaToteutuksia() {

        TutkintoOhjelma moduuli = new TutkintoOhjelma();
        TutkintoOhjelmaToteutus toteutusA = new TutkintoOhjelmaToteutus(moduuli);
        TutkintoOhjelmaToteutus toteutusB = new TutkintoOhjelmaToteutus(moduuli);

        moduuli.addLearningOpportunityInstance(toteutusA);
        moduuli.addLearningOpportunityInstance(toteutusB);

        // tarkista että toteutukset on liitetty
        assertEquals(2, moduuli.getLearningOpportunityInstances().size());

        // tarkista että toteutukset viittaavat oikean moduuliin
        assertEquals(moduuli, toteutusA.getLearningOpportunitySpecification());
        assertEquals(moduuli, toteutusB.getLearningOpportunitySpecification());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testKoulutusmoduulinToteutuksenKoulutusmoduuliaEiVoiMuuttaa() {

        TutkintoOhjelma moduuliA = new TutkintoOhjelma();
        TutkintoOhjelma moduuliB = new TutkintoOhjelma();

        TutkintoOhjelmaToteutus toteutus = new TutkintoOhjelmaToteutus(moduuliA);

        // yrita vaihtaa modulia
        toteutus.setLearningOpportunitySpecification(moduuliB);

    }

    @Test
    public void testViittausToteutuksestaModuuliinOnKaksisuuntainen() {

        TutkintoOhjelma moduuli = new TutkintoOhjelma();
        TutkintoOhjelmaToteutus toteutus = new TutkintoOhjelmaToteutus();

        toteutus.setLearningOpportunitySpecification(moduuli);

        // tarkista etta koulutusmoduuli viittaa toteutukseen
        assertEquals(toteutus, moduuli.getLearningOpportunityInstances().iterator().next());

        // tarkista että toteutus viittaa koulutusmoduulin
        assertEquals(moduuli, toteutus.getLearningOpportunitySpecification());

    }

}

