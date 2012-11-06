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
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class HakukohdeDAOTest {

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    /**
     * Set of Koulutusmoduulitoteutus persisted into database.
     */
    private Set<KoulutusmoduuliToteutus> koulutusmoduuliToteutuses = new HashSet<KoulutusmoduuliToteutus>();

    @Autowired
    private TarjontaFixtures fixtures;

    @Before
    public void setUp() {

        fixtures.recreate();

        setUpKoulutusmoduuliToteutuses();

    }

    @Test(expected = ValidationException.class)
    public void testCreateWithoutName() {

        hakukohdeDAO.insert(new Hakukohde());

    }

    @Test
    public void testCreateMinimum() {

        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setHaku(fixtures.createPersistedHaku());

        hakukohdeDAO.insert(hakukohde);

    }

    /**
     * Test that references to KoulutusmoduuliToteutus objects are inserted properly.
     */
    @Test
    public void testInsertWithKoulutus() {

        int numToteutuses = koulutusmoduuliToteutuses.size();

        Hakukohde hakukohde = fixtures.simpleHakukohde;
        hakukohde.setHaku(fixtures.createPersistedHaku());

        for (KoulutusmoduuliToteutus t : koulutusmoduuliToteutuses) {
            hakukohde.addKoulutusmoduuliToteutus(t);
        }

        hakukohdeDAO.insert(hakukohde);
        Hakukohde loaded = hakukohdeDAO.read(hakukohde.getId());

        assertEquals(numToteutuses, loaded.getKoulutusmoduuliToteutuses().size());

    }


    @Test
    public void testUpdateWithKoulutus() {

        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setHaku(fixtures.createPersistedHaku());

        hakukohdeDAO.insert(hakukohde);

        assertEquals(0, hakukohde.getKoulutusmoduuliToteutuses().size());

        hakukohde.addKoulutusmoduuliToteutus(koulutusmoduuliToteutuses.iterator().next());

        hakukohdeDAO.update(hakukohde);

        hakukohde = hakukohdeDAO.read(hakukohde.getId());
        assertEquals(1, hakukohde.getKoulutusmoduuliToteutuses().size());

    }

    @Test
    public void testValintakoeCascadeInsert() {

        final Hakukohde hk = fixtures.hakukohdeWithValintakoe;
        hk.setHaku(fixtures.createPersistedHaku());

        hakukohdeDAO.insert(hk);

        Hakukohde loaded = hakukohdeDAO.read(hk.getId());
        assertEquals(1, loaded.getValintakoes().size());
        assertNotNull(loaded.getValintakoes().iterator().next().getId());

    }

    @Test
    public void testValintakoeCascadeDelete() {

        Hakukohde h = fixtures.hakukohdeWithValintakoe;
        h.setHaku(fixtures.createPersistedHaku());

        hakukohdeDAO.insert(h);

        Valintakoe koe = h.getValintakoes().iterator().next();
        assertNotNull(koe);

        h.removeValintakoe(koe);
        hakukohdeDAO.update(h);

        Hakukohde loaded = hakukohdeDAO.read(h.getId());
        //
        // todo: there is some problem with testing Hibernate's PersistentSet (remove/equals) hence this assertion is disabled
        // see: https://hibernate.onjira.com/browse/HHH-3799
        //
        // assertEquals(0, loaded.getValintakoes().size());

    }

    @Test
    public void testMonikielinenValintaperusteKuvaus() {

        Hakukohde h = fixtures.simpleHakukohde;
        h.setHaku(fixtures.createPersistedHaku());
        MonikielinenTeksti tekstis = new MonikielinenTeksti();
        tekstis.addTekstiKaannos("fi", "In Finnish");
        h.setValintaperusteKuvaus(tekstis);

        hakukohdeDAO.insert(h);

        flush();

        Hakukohde loaded = hakukohdeDAO.read(h.getId());
        assertEquals("In Finnish", loaded.getValintaperusteKuvaus().getTekstiForKieliKoodi("fi"));


        // update the same language

        tekstis.setTekstiKaannos("fi", "In Finnish updated");
        assertEquals("In Finnish updated", loaded.getValintaperusteKuvaus().getTekstiForKieliKoodi("fi"));


    }

    @Test
    public void testFindByKoulutusOid() {

        KoulutusmoduuliToteutus t = fixtures.createPersistedKoulutusmoduuliToteutusWithMultipleHakukohde();
        String koulutusOid = t.getOid();

        List<Hakukohde> hakukohdes = hakukohdeDAO.findByKoulutusOid(koulutusOid);
        assertEquals(3, hakukohdes.size());

    }

    @Test
    public void testFindOrphanHakukohteet() {
    	Hakukohde nonOrphan = fixtures.createPersistedHakukohdeWithKoulutus();
    	Hakukohde orphan = fixtures.createPersistedHakukohde();

    	List<Hakukohde> hakukohdes = this.hakukohdeDAO.findAll();

    	List<Hakukohde> orphanHakukohdes = this.hakukohdeDAO.findOrphanHakukohteet();

    	assertTrue(orphanHakukohdes.size() > 0);
    	assertTrue(hakukohdes.size() > orphanHakukohdes.size());
    }


    /**
     *
     */
    private void setUpKoulutusmoduuliToteutuses() {

        koulutusmoduuliToteutuses.clear();

        for (int i = 0; i < 5; i++) {

            // re-create new fixtures
            fixtures.recreate();

            Koulutusmoduuli moduuli = koulutusmoduuliDAO.insert(fixtures.simpleTutkintoOhjelma);
            KoulutusmoduuliToteutus toteutus = fixtures.simpleTutkintoOhjelmaToteutus;
            toteutus.setKoulutusmoduuli(moduuli);
            koulutusmoduuliToteutusDAO.insert(toteutus);
            koulutusmoduuliToteutuses.add(toteutus);

        }


    }

    private void flush() {

        ((HakukohdeDAOImpl) hakukohdeDAO).getEntityManager().flush();

    }

}

