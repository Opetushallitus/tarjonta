/*
* Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
*
* This program is free software: Licensed under the EUPL, Version 1.1 or - as
* soon as they will be approved by the European Commission - subsequent versions
* of the EUPL (the "Licence");
*
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* European Union Public Licence for more details.
*/
package fi.vm.sade.tarjonta.dao.impl;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

import java.util.List;
import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
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
*
* @author jani
*/
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
//@Ignore
public class KoulutusmoduuliDAOImplTest {

    private static final String LUKIOLINJA_URI = "uri_lukiolinja";
    private static final String KOULUTUSOHJELMA_URI = "uri_koulutusohjelma";
    private static final String SEARCH_BY_URI_A = "uri_170";
    private static final String SEARCH_BY_URI_B = "uri_2";
    private static final String SEARCH_BY_URI_C = "uri_a";
    @Autowired(required = true)
    private KoulutusmoduuliDAOImpl instance;
    private Koulutusmoduuli komo1, komo2, komo3;
    @Autowired(required = true)
    private TarjontaFixtures fixtures;
    private EntityManager em;

    @Before
    public void setUp() {
        //CLEAN ENTITYMANAGER:
        //This should be done in junit after method, but for some reason there is a
        //something wrong with the after annotation in Bamboo environment
        remove(komo1);
        remove(komo2);
        remove(komo3);
        if (em != null) {
            em.clear();
    }

        //RE-INITIALIZE ENITMANAGER:
        final String sep = EntityUtils.STR_ARRAY_SEPARATOR;

        em = instance.getEntityManager();
        komo1 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo1.setOppilaitostyyppi(sep + "uri_1" + sep + SEARCH_BY_URI_B + sep + SEARCH_BY_URI_A + sep);
        komo1.setKoulutusohjelmaUri(KOULUTUSOHJELMA_URI);
        komo1.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value());

        persist(komo1);

        komo2 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo2.setOppilaitostyyppi(sep + SEARCH_BY_URI_C + sep);
        komo2.setKoulutusohjelmaUri(KOULUTUSOHJELMA_URI);
        komo2.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value());
        persist(komo2);

        komo3 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo3.setOppilaitostyyppi(sep + "uri_fuubar" + sep);
        komo3.setLukiolinjaUri(LUKIOLINJA_URI);
        komo1.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS.value());
        persist(komo3);
    }

    /**
* Test of search method, of class KoulutusmoduuliDAOImpl.
*/
    @Test
    public void testSearchByOppilaitostyyppiByUri() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_A);
        List result = instance.search(criteria);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByOppilaitostyyppiByUris() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_A);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_B);

        List result = instance.search(criteria);
        assertEquals(1, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_A);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_B);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);

        result = instance.search(criteria);
        assertEquals(2, result.size());
    }

    @Test
    @Ignore // failaa bamboossa
    public void testSearchWithEmptyCriteria() {
        List result = instance.search(new KoulutusmoduuliDAO.SearchCriteria());
        assertEquals(3, result.size());
    }

    @Test
    public void testSearchByLukiolinja() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setLukiolinjaKoodiUri(LUKIOLINJA_URI);
        List result = instance.search(criteria);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByKoulutusohjelma() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        List result = instance.search(criteria);
        assertEquals(2, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);
        result = instance.search(criteria);
        assertEquals(1, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);
        criteria.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        result = instance.search(criteria);
        assertEquals(1, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);
        criteria.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        result = instance.search(criteria);
        assertEquals(0, result.size());
    }

    
    @Test
    public void testXSSFiltering(){
        //TODO test more fields...
        komo1 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo1.setOid("xss-1");
        komo1.setOppilaitostyyppi("zz");
        komo1.setKoulutusohjelmaUri(KOULUTUSOHJELMA_URI);
        komo1.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value());
        komo1.setNimi(new MonikielinenTeksti("fi", "ei saa muuttaa & merkkiä!"));
        komo1.getTekstit().put(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, new MonikielinenTeksti("fi", "jatko-opinto"));
        komo1.getTekstit().put(KomoTeksti.KOULUTUKSEN_RAKENNE, new MonikielinenTeksti("fi", "<table><a href='window.alert(\"hello\")'>foo</a></table>"));
        persist(komo1);

        komo1 = instance.findBy("oid", "xss-1").get(0);
        Assert.assertEquals("ei saa muuttaa & merkkiä!", komo1.getNimi().asMap().get("fi"));
        Assert.assertEquals("<table>foo</table>", komo1.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstiForKieliKoodi("fi"));
        Assert.assertEquals("jatko-opinto", komo1.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstiForKieliKoodi("fi"));
    }


    /*
*
* Private helpper methods
*
*/
    private void persist(Object o) {
        em.persist(o);
        em.flush();
        em.detach(o);

        //a quick check
        if (o instanceof Koulutusmoduuli) {
            Koulutusmoduuli v = (Koulutusmoduuli) o;
            Koulutusmoduuli find = em.find(Koulutusmoduuli.class, v.getId());
            assertNotNull(find);
            assertNotNull(find.getId());
        } else {
            fail("Found an unknown object type : " + o.toString());
        }
    }

    private void remove(Object o) {
        if (o == null) {
            return;
        }

        if (o instanceof Koulutusmoduuli) {
            em.remove(em.find(Koulutusmoduuli.class, ((Koulutusmoduuli) o).getId()));
        }
    }
}