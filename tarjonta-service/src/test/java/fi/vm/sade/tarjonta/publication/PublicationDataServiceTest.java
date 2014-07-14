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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = {
    "classpath:spring/test-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(readOnly = true)
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
public class PublicationDataServiceTest {

    @Autowired
    private PublicationDataService dataService;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Before
    public void setUpData() {

        TarjontaFixtures fixtures = new TarjontaFixtures();
        Haku haku;
        Hakukohde hakukohde;
        Koulutusmoduuli komo;
        KoulutusmoduuliToteutus komoto;

        // non published Haku
        haku = fixtures.createHaku();
        haku.setTila(TarjontaTila.VALMIS);
        hakuDAO.insert(haku);

        // published Haku
        haku = fixtures.createHaku();
        haku.setOid("1.1");
        haku.setTila(TarjontaTila.JULKAISTU);
        hakuDAO.insert(haku);

        // non published komo
        komo = fixtures.createTutkintoOhjelma();
        komo.setTila(TarjontaTila.VALMIS);
        koulutusmoduuliDAO.insert(komo);


        // published komoto with non-published komo
        komoto = fixtures.createTutkintoOhjelmaToteutus();
        komoto.setTila(TarjontaTila.JULKAISTU);


    }

    @Test
    public void testListHaku() {

        List<Haku> hakus = dataService.listHaku();

        // search should match only haku's in JULKAISTU state
        assertEquals(1, hakus.size());

        Haku haku = hakus.get(0);
        assertEquals("1.1", haku.getOid());
        // lazy loaded attributes are properly loaded
        assertEquals(3, haku.getNimi().getTekstiKaannos().size());

    }

}

