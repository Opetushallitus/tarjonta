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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import fi.vm.sade.tarjonta.model.*;
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

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.MonikielinenMetatdataDAOImpl;

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

    private static final String KOODI_URI_METADATA_RELATION = "uri:included_test_object";
    @Autowired
    private MonikielinenMetadataDAO monikielinenMetadataDAO;
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

    @Test(expected = PersistenceException.class)
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
     * Test that references to KoulutusmoduuliToteutus objects are inserted
     * properly.
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
    public void insertWithKoulutusmoduuliToteutusTarjoajat() {
        Hakukohde hakukohde = fixtures.simpleHakukohde;
        hakukohde.setHaku(fixtures.createPersistedHaku());

        KoulutusmoduuliToteutusTarjoajatiedot koulutusmoduuliToteutusTarjoajatiedot = new KoulutusmoduuliToteutusTarjoajatiedot();
        koulutusmoduuliToteutusTarjoajatiedot.setTarjoajaOids(new HashSet<String>(Arrays.asList("1.2.3")));
        hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot().put("4.5.6", koulutusmoduuliToteutusTarjoajatiedot);

        hakukohdeDAO.insert(hakukohde);
        Hakukohde loaded = hakukohdeDAO.read(hakukohde.getId());

        assertTrue(loaded.getKoulutusmoduuliToteutusTarjoajatiedot().size() == 1);
        assertEquals("1.2.3", loaded.getKoulutusmoduuliToteutusTarjoajatiedot().get("4.5.6").getTarjoajaOids().iterator().next());
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
    public void testHakukohdeSearchByNameTermAndYear() {

        Hakukohde hakukohde = fixtures.createHakukohde();

        final String hakukohdeName = hakukohde.getHakukohdeNimi();

        hakukohde.setHaku(fixtures.createPersistedHaku());

        hakukohdeDAO.insert(hakukohde);

        assertEquals(0, hakukohde.getKoulutusmoduuliToteutuses().size());

        KoulutusmoduuliToteutus komoto =  koulutusmoduuliToteutuses.iterator().next();

        final String term = "kausi_k";

        final Integer year = 2014;

        final String providerOid = komoto.getTarjoaja();

        komoto.setAlkamiskausiUri(term);

        komoto.setAlkamisVuosi(year);

        hakukohde.addKoulutusmoduuliToteutus(komoto);

        komoto.addHakukohde(hakukohde);

        hakukohdeDAO.update(hakukohde);


        System.out.println("-------------------------------> Ratkaiseva kysely  ------------------>");

        List<Hakukohde> hakukohdes = hakukohdeDAO.findByNameTermAndYear(hakukohdeName,term,year,providerOid);





        assertTrue(hakukohdes.size() > 0);

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

//    @Test
//    public void testMonikielinenValintaperusteKuvaus() {
//        Hakukohde h = fixtures.createHakukohde();
//        h.setValintaperustekuvausKoodiUri(KOODI_URI_METADATA_RELATION);
//        h.setHaku(fixtures.createPersistedHaku());
//
//        hakukohdeDAO.insert(h);
//        flush();
//
//        /*
//         * The object will be included to a query result.
//         */
//        MonikielinenMetadata metaIncluded = new fi.vm.sade.tarjonta.model.MonikielinenMetadata();
//        metaIncluded.setKategoria(MetaCategory.VALINTAPERUSTEKUVAUS.toString());
//        metaIncluded.setKieli("URI:FI");
//        metaIncluded.setArvo("Value");
//        assertNotNull("koodi uri was null?", h.getHakukohdeNimi());
//        metaIncluded.setAvain(KOODI_URI_METADATA_RELATION);
//        monikielinenMetadataDAO.insert(metaIncluded);
//
//        /*
//         * The object has no reference to Hakukohde object.
//         */
//        MonikielinenMetadata metaExcluded = new fi.vm.sade.tarjonta.model.MonikielinenMetadata();
//        metaExcluded.setKategoria(MetaCategory.VALINTAPERUSTEKUVAUS.toString());
//        metaExcluded.setKieli("URI:FI");
//        metaExcluded.setArvo("Value");
//        metaExcluded.setAvain("uri:excluded_test_object");
//
//        monikielinenMetadataDAO.insert(metaExcluded);
//        flush();
//        detach(metaExcluded);
//        detach(metaIncluded);
//        detach(h);
//
//        final Hakukohde result = hakukohdeDAO.read(h.getId());
//        final List<MonikielinenMetadata> metaResult = monikielinenMetadataDAO.findByAvain(metaIncluded.getAvain());
//
//        assertNotNull("MonikielinenMetadata entity was null", metaResult);
//        assertEquals("KuvausV1RDTO not inserted", 1, metaResult.size());
//        
//        assertEquals("query join key mismatch", KOODI_URI_METADATA_RELATION, metaResult.get(0).getAvain());
//        assertNotNull("Hakukohde entity was null", result);
//        assertEquals("Missing meta data", 1, result.getValintaperustekuvaus().size());
//        assertEquals("Missing category", MetaCategory.VALINTAPERUSTEKUVAUS.toString(), metaResult.get(0).getKategoria());
//        assertEquals("An invalid class", true, metaResult.get(0) instanceof MonikielinenMetadata);
//
//    }

    @Test
    public void testFindByKoulutusOid() {
        KoulutusmoduuliToteutus t = fixtures.createPersistedKoulutusmoduuliToteutusWithMultipleHakukohde();
        String koulutusOid = t.getOid();

        List<Hakukohde> hakukohdes = hakukohdeDAO.findByKoulutusOid(koulutusOid);
        assertEquals(3, hakukohdes.size());
    }


    /*  @Test
     public void testValintakoeInsert() {
     Hakukohde nonOrphan = fixtures.createPersistedHakukohdeWithKoulutus();
     Valintakoe valintakoe = fixtures.createValintakoe();

     nonOrphan.addValintakoe(valintakoe);

     hakukohdeDAO.insert(nonOrphan);
     }*/
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
        ((MonikielinenMetatdataDAOImpl) monikielinenMetadataDAO).getEntityManager().flush();
    }

    private void detach(Object o) {
        if (o instanceof MonikielinenMetadata) {
            ((MonikielinenMetatdataDAOImpl) monikielinenMetadataDAO).getEntityManager().detach(o);
        } else if (o instanceof Hakukohde) {
            ((HakukohdeDAOImpl) hakukohdeDAO).getEntityManager().detach(o);
        }
    }
}
