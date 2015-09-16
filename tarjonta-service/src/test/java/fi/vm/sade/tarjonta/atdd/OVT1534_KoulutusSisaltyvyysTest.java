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

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Acceptance tests for OVT-1534
 *
 * @see https://liitu.hard.ware.fi/jira/browse/OVT-1534
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class OVT1534_KoulutusSisaltyvyysTest extends TestUtilityBase {
    @Test
    public void testKoulutusmoduuliSisaltaaKoulutusmouduleita() {

        Koulutusmoduuli moduuli = fixtures.createPersistedKoulutusmoduuliStructure();
        assertEquals(2, moduuli.getSisaltyvyysList().size());

    }

    @Test
    public void testKoulutusmoduuliVoidaanJakaa() {

        Koulutusmoduuli moduuli = fixtures.createPersistedKoulutusmoduuliStructure();

        Iterator<Koulutusmoduuli> i = moduuli.getAlamoduuliList().iterator();
        Koulutusmoduuli child1 = i.next();
        Koulutusmoduuli child2 = i.next();

        Koulutusmoduuli child3 = child1.getAlamoduuliList().iterator().next();
        Koulutusmoduuli child4 = child2.getAlamoduuliList().iterator().next();

        assertEquals(child3, child4);

    }

    @Test
    public void testKoulutusmoduuliinVoidaanLiittaaUseitaToteutuksia() {

        Koulutusmoduuli moduuli = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        KoulutusmoduuliToteutus toteutusA = new KoulutusmoduuliToteutus(moduuli);
        KoulutusmoduuliToteutus toteutusB = new KoulutusmoduuliToteutus(moduuli);

        moduuli.addKoulutusmoduuliToteutus(toteutusA);
        moduuli.addKoulutusmoduuliToteutus(toteutusB);

        // tarkista että toteutukset on liitetty
        assertEquals(2, moduuli.getKoulutusmoduuliToteutusList().size());

        // tarkista että toteutukset viittaavat oikean moduuliin
        assertEquals(moduuli, toteutusA.getKoulutusmoduuli());
        assertEquals(moduuli, toteutusB.getKoulutusmoduuli());

    }

    @Test(expected = IllegalStateException.class)
    public void testKoulutusmoduulinToteutuksenKoulutusmoduuliaEiVoiMuuttaa() {

        Koulutusmoduuli moduuliA = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        Koulutusmoduuli moduuliB = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

        KoulutusmoduuliToteutus toteutus = new KoulutusmoduuliToteutus(moduuliA);

        // yrita vaihtaa modulia
        toteutus.setKoulutusmoduuli(moduuliB);

    }

}

