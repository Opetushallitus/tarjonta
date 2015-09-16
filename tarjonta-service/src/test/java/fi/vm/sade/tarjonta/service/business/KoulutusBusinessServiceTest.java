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
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
public class KoulutusBusinessServiceTest extends TestUtilityBase {

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

        Koulutusmoduuli k = koulutusBusinessService.create(tutkintoOhjelma);
        assertEquals(TarjontaTila.LUONNOS, k.getTila());

    }

    @Test
    public void testCreateKoulutusmoduuliWithToteutus() {

        KoulutusmoduuliToteutus t = koulutusBusinessService.create(tutkintoOhjemanToteutus, tutkintoOhjelma);

        // check that koulutusmoduuli is assigned
        assertEquals(tutkintoOhjelma, t.getKoulutusmoduuli());

    }

    @Test
    public void testFindByOid() {

        KoulutusmoduuliToteutus t = koulutusBusinessService.create(tutkintoOhjemanToteutus, tutkintoOhjelma);
        assertNotNull(t.getOid());

    }
}

