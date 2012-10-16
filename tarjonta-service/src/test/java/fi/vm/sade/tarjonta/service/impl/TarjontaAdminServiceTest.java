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

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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

    private KoulutuksenKestoTyyppi kesto3Vuotta;

    @Before
    public void setUp() {

        kesto3Vuotta = new KoulutuksenKestoTyyppi();
        kesto3Vuotta.setArvo("3");
        kesto3Vuotta.setYksikko("kesto/vuosi");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotCreateKoulutusWithoutKoulutusmoduuli() {

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();

        lisaaKoulutus.setKoulutusKoodi("321101");
        lisaaKoulutus.setKoulutusohjelmaKoodi("1603");

        adminService.lisaaKoulutus(lisaaKoulutus);

    }

    @Test
    public void testCreateKoulutusHappyPath() throws Exception {

        Koulutusmoduuli moduuli = fixtures.createTutkintoOhjelma();
        moduuli.setKoulutusKoodi("321101");
        moduuli.setKoulutusohjelmaKoodi("1603");
        koulutusmoduuliDAO.insert(moduuli);

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();
        lisaaKoulutus.setKoulutusKoodi("321101");
        lisaaKoulutus.setKoulutusohjelmaKoodi("1603");
        lisaaKoulutus.setOpetusmuoto("opetusmuoto/aikuisopetus");
        lisaaKoulutus.getOpetuskieli().add("opetuskieli/fi");
        lisaaKoulutus.getKoulutuslaji().add("koulutuslaji/lahiopetus");
        lisaaKoulutus.setOid("1.2.3.4.5");
        lisaaKoulutus.setKoulutuksenAlkamisPaiva(toXmlDateTime(new Date()));
        lisaaKoulutus.setKesto(kesto3Vuotta);

        adminService.lisaaKoulutus(lisaaKoulutus);

    }

    private XMLGregorianCalendar toXmlDateTime(Date date) {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(date.getTime());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {
            throw new RuntimeException("converting date failed: " + date, e);
        }
    }

}

