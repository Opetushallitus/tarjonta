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
package fi.vm.sade.tarjonta.service.business;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.*;
import javax.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 *
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class KoulutusBusinessServiceTest {

    @Autowired
    private KoulutusBusinessService service;

    private Koulutusmoduuli tutkintoOhjelma;

    private KoulutusmoduuliToteutus tutkintoOhjemanToteutus;

    private TarjontaFixtures fixtures = new TarjontaFixtures();

    @Before
    public void setUp() {

        fixtures.recreate();

        tutkintoOhjelma = fixtures.simpleTutkintoOhjelma;
        tutkintoOhjemanToteutus = fixtures.simpleTutkintoOhjelmaToteutus;

    }

    @Test
    public void testNewKoulutusmoduuliIsInSuunnitteluState() {

        Koulutusmoduuli k = service.create(tutkintoOhjelma);
        assertEquals(TarjontaTila.LUONNOS, k.getTila());

    }

    /**
     * Tama testi ei nyt feilaa koska @NotNull constraint:ia ei voi maaritella yhteiselle Koulutusmoduulille - harkitse oman
     * validator:n tekemista.
     */
    @Ignore
    @Test(expected = ValidationException.class)
    public void testTutkintoOhjelmaMustHaveKoulutusKoodi() {

        Koulutusmoduuli m = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINNON_OSA);
        m.setOid("12345");
        service.create(m);

    }

    @Test
    public void testCreateKoulutusmoduuliWithToteutus() {

        KoulutusmoduuliToteutus t = service.create(tutkintoOhjemanToteutus, tutkintoOhjelma);

        // check that koulutusmoduuli is assigned
        assertEquals(tutkintoOhjelma, t.getKoulutusmoduuli());

    }

    @Test
    public void testFindByOid() {

        KoulutusmoduuliToteutus t = service.create(tutkintoOhjemanToteutus, tutkintoOhjelma);
        assertNotNull(t.getOid());

    }

    @Test
    public void testCopyStructure() {
//        Koulutusmoduuli original = fixtures.simpleKoulutusTree;
//        Koulutusmoduuli copy = service.createCopy(original);
    }

}

