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
package fi.vm.sade.tarjonta.service.impl;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.TarjontaDatabasePrinter;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import org.junit.Test;

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
@Transactional
public class TarjontaAdminServiceTest {

    @Autowired
    private TarjontaAdminService adminService;

    @Autowired
    private TarjontaFixtures fixtures;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Before
    public void setUp() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotCreateKoulutusWithoutKoulutusmoduuli() {

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();

        lisaaKoulutus.setKoulutusKoodi("321101");
        lisaaKoulutus.setKoulutusohjelmaKoodi("1603");

        adminService.lisaaKoulutus(lisaaKoulutus);

    }

    @Test
    public void testCreateKoulutusHappyPath() {

        Koulutusmoduuli moduuli = fixtures.createTutkintoOhjelma();
        moduuli.setKoulutusKoodi("321101");
        moduuli.setKoulutusohjelmaKoodi("1603");
        koulutusmoduuliDAO.insert(moduuli);

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();
        lisaaKoulutus.setKoulutusKoodi("321101");
        lisaaKoulutus.setKoulutusohjelmaKoodi("1603");
        lisaaKoulutus.setOpetusmuoto("opetusmuoto/lahiopetus");
        lisaaKoulutus.getOpetuskieli().add("opetuskieli/fi");
        lisaaKoulutus.setOid("1.2.3.4.5");

        adminService.lisaaKoulutus(lisaaKoulutus);

    }

}

