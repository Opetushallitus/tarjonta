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
import fi.vm.sade.tarjonta.dao.KuvausDAO;
import fi.vm.sade.tarjonta.model.ValintaperusteSoraKuvaus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KuvausSearchV1RDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KuvausDaoImplTest {

    @Autowired
    private KuvausDAO kuvausDAO;

    @Autowired
    private TarjontaFixtures fixtures;

    @Before
    public void setUp() {
        fixtures.createPersistedValintaperustekuvaukset();
    }

    @Test
    public void thatTermSearchMatches() {
        KuvausSearchV1RDTO searhParams = new KuvausSearchV1RDTO();
        searhParams.setHakusana("Suomi");

        List<ValintaperusteSoraKuvaus> result = kuvausDAO.findBySearchSpec(searhParams, ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS);
        assertTrue(result.size() == 1);

        searhParams.setHakusana("English");
        result = kuvausDAO.findBySearchSpec(searhParams, ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS);
        assertTrue(result.size() == 1);

        searhParams.setHakusana("title");
        result = kuvausDAO.findBySearchSpec(searhParams, ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS);
        assertTrue(result.isEmpty());

        searhParams.setHakusana(null);
        result = kuvausDAO.findBySearchSpec(searhParams, ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS);
        assertTrue(result.size() == 2);
    }

    @Test
    public void thatAvainSearchMatches() {
        KuvausSearchV1RDTO searhParams = new KuvausSearchV1RDTO();
        searhParams.setAvain("valintaperusteryhma_01");

        List<ValintaperusteSoraKuvaus> result = kuvausDAO.findBySearchSpec(searhParams, ValintaperusteSoraKuvaus.Tyyppi.VALINTAPERUSTEKUVAUS);
        assertTrue(result.size() == 1);
    }
}