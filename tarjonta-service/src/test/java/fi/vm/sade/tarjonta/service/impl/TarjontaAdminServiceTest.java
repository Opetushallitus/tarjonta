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
import static org.junit.Assert.*;
import org.junit.Test;

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
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Yhteyshenkilo;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.YhteyshenkiloTyyppi;

import java.util.Date;
import java.util.List;

import java.util.Set;

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

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    private KoulutuksenKestoTyyppi kesto3Vuotta;

    /* Known koulutus that is inserted for each test. */
    private static final String SAMPLE_KOULUTUS_OID = "1.2.3.4.5";

    @Before
    public void setUp() {

        kesto3Vuotta = new KoulutuksenKestoTyyppi();
        kesto3Vuotta.setArvo("3");
        kesto3Vuotta.setYksikko("kesto/vuosi");

        insertSampleKoulutus();

    }

    @Test(expected = TarjontaBusinessException.class)
    public void testCannotCreateKoulutusWithoutKoulutusmoduuli() {

        LisaaKoulutusTyyppi lisaaKoulutus = createSampleKoulutus();

        lisaaKoulutus.setKoulutusKoodi(createKoodi("unknown-uri"));
        lisaaKoulutus.setKoulutusohjelmaKoodi(createKoodi("unknown-uri"));

        adminService.lisaaKoulutus(lisaaKoulutus);

    }

    @Test
    public void testCreateKoulutusHappyPath() throws Exception {

        // sample koulutus has been inserted before this test, check that data is correct
        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);
        assertNotNull(toteutus);

        Set<Yhteyshenkilo> yhteyshenkilos = toteutus.getYhteyshenkilos();
        assertEquals(1, yhteyshenkilos.size());

        Yhteyshenkilo actualHenkilo = yhteyshenkilos.iterator().next();
        YhteyshenkiloTyyppi expectedHenkilo = createYhteyshenkilo();

        assertMatch(expectedHenkilo, actualHenkilo);

    }

    @Test
    public void testUpdateKoulutusHappyPath() {

        PaivitaKoulutusTyyppi paivitaKoulutus = new PaivitaKoulutusTyyppi();
        paivitaKoulutus.setOid(SAMPLE_KOULUTUS_OID);

        KoulutuksenKestoTyyppi kesto = new KoulutuksenKestoTyyppi();
        kesto.setArvo("new-value");
        kesto.setYksikko("new-units");
        paivitaKoulutus.setKesto(kesto);

        paivitaKoulutus.setKoulutuksenAlkamisPaiva(new Date());
        paivitaKoulutus.setKoulutusKoodi(createKoodi("do-not-update-this"));
        paivitaKoulutus.setKoulutusohjelmaKoodi(createKoodi("do-not-update-this"));
        paivitaKoulutus.getOpetusmuoto().add(createKoodi("new-opetusmuoto"));

        adminService.paivitaKoulutus(paivitaKoulutus);

        KoulutusmoduuliToteutus toteutus = koulutusmoduuliToteutusDAO.findByOid(SAMPLE_KOULUTUS_OID);

        assertEquals("new-value", toteutus.getSuunniteltuKestoArvo());
        assertEquals("new-units", toteutus.getSuunniteltuKestoYksikko());

    }

    @Test
    public void testPoistaKoulutusHappyPath() throws Exception {

        adminService.initSample(new String());
        List<KoulutusmoduuliToteutus> komotos = this.koulutusmoduuliToteutusDAO.findAll();
        assertFalse(komotos.isEmpty());
        int komotosOriginalSize = komotos.size();
        String komotoOid = komotos.get(0).getOid();

        this.adminService.poistaKoulutus(komotoOid);
        komotos = this.koulutusmoduuliToteutusDAO.findAll();
        assertEquals(komotosOriginalSize - 1, komotos.size());
    }


    private void assertMatch(YhteyshenkiloTyyppi expected, Yhteyshenkilo actual) {

        assertEquals(expected.getEtunimet(), actual.getEtunimis());
        assertEquals(expected.getSukunimi(), actual.getSukunimi());
        assertEquals(expected.getHenkiloOid(), actual.getHenkioOid());
        assertEquals(expected.getPuhelin(), actual.getPuhelin());
        assertEquals(expected.getSahkoposti(), actual.getSahkoposti());
        assertEquals(expected.getTitteli(), actual.getTitteli());

    }

    private void insertSampleKoulutus() {

        Koulutusmoduuli moduuli = fixtures.createTutkintoOhjelma();
        moduuli.setKoulutusKoodi("321101");
        moduuli.setKoulutusohjelmaKoodi("1603");
        koulutusmoduuliDAO.insert(moduuli);

        adminService.lisaaKoulutus(createSampleKoulutus());

        flush();

    }

    private LisaaKoulutusTyyppi createSampleKoulutus() {

        LisaaKoulutusTyyppi lisaaKoulutus = new LisaaKoulutusTyyppi();
        lisaaKoulutus.setKoulutusKoodi(createKoodi("321101"));
        lisaaKoulutus.setKoulutusohjelmaKoodi(createKoodi("1603"));
        lisaaKoulutus.getOpetusmuoto().add(createKoodi("opetusmuoto/aikuisopetus"));
        lisaaKoulutus.getOpetuskieli().add(createKoodi("opetuskieli/fi"));
        lisaaKoulutus.getKoulutuslaji().add(createKoodi("koulutuslaji/lahiopetus"));
        lisaaKoulutus.setOid(SAMPLE_KOULUTUS_OID);
        lisaaKoulutus.setKoulutuksenAlkamisPaiva(new Date());
        lisaaKoulutus.setKesto(kesto3Vuotta);
        lisaaKoulutus.getYhteyshenkilo().add(createYhteyshenkilo());


        return lisaaKoulutus;
    }

    private YhteyshenkiloTyyppi createYhteyshenkilo() {

        YhteyshenkiloTyyppi h = new YhteyshenkiloTyyppi();
        h.setEtunimet("Kalle Matti"); // required
        h.setSukunimi("Kettu-Orava"); // required
        h.setHenkiloOid(null); // not recognized via HenkiloService
        h.setPuhelin("+358 123 123 123"); // optional
        h.setSahkoposti(null); // optional
        h.setTitteli(null); // optional
        h.getKielet().add("fi"); // min 1 required (for now)

        return h;

    }

    @Test
    public void testInitSample() {
        adminService.initSample(new String());
    }

    private static KoodistoKoodiTyyppi createKoodi(String uri) {
        KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        return koodi;
    }

    private void flush() {
        ((KoulutusmoduuliToteutusDAOImpl) koulutusmoduuliToteutusDAO).getEntityManager().flush();
    }

}

