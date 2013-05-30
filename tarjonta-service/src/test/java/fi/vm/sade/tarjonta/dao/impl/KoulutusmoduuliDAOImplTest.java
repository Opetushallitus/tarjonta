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

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import java.util.List;
import javax.persistence.EntityManager;
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
public class KoulutusmoduuliDAOImplTest {

    private static final String LUKIOLINJA_URI = "uri_lukiolinja";
    private static final String KOULUTUSOHJELMA_URI = "uri_koulutusohjelma";
    private static final String SEARCH_BY_URI_A = "uri_170";
    private static final String SEARCH_BY_URI_B = "uri_2";
    private static final String SEARCH_BY_URI_C = "uri_a";
    private static final String FI = "kieli_fi";
    private static final String EN = "kieli_en";
    private static final String SV = "kieli_sv";
    private static final String XX_LANG_TEST1 = "kieli_test1";
    private static final String XX_LANG_TEST2 = "kieli_test2";
    private static final String LANG_TEST1 = "  \"1!#¤%&/()}})\"";
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
        komo1.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        komo1.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value());
        komo1.setNimi(convertToMonikielinenTeksti(new String[][]{{FI, "ammatillinen peruskoulutus"}, {XX_LANG_TEST1, LANG_TEST1}}));
        persist(komo1);

        komo2 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo2.setOppilaitostyyppi(sep + SEARCH_BY_URI_C + sep);
        komo2.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI);
        komo2.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value());
        komo2.setNimi(convertToMonikielinenTeksti(new String[][]{{FI, "ammatillinen peruskoulutus"}, {SV, "Yrkesutbildningen"}, {XX_LANG_TEST1, LANG_TEST1}}));
        persist(komo2);

        komo3 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo3.setOppilaitostyyppi(sep + "uri_fuubar" + sep);
        komo3.setLukiolinja(LUKIOLINJA_URI);
        komo3.setNimi(convertToMonikielinenTeksti(new String[][]{{FI, "lukiokoulutus"}, {EN, "Upper Secondary School Education"}, {XX_LANG_TEST2, LANG_TEST1}}));
        komo3.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS.value());
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
    public void testSearchModuleBySearchwordAndLangFI() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();

        criteria.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        criteria.setNimiQuery("ammatillinen peruskoulutus");
        List<Koulutusmoduuli> result = instance.search(criteria);
        assertEquals(2, result.size());

        criteria.setKieliUri(FI);
        criteria.setNimiQuery("ammatillinen peruskoulutus");
        result = instance.search(criteria);
        assertEquals(2, result.size());

        criteria.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        result = instance.search(criteria);
        assertEquals(0, result.size());

        criteria.setNimiQuery("LUKIO");
        result = instance.search(criteria);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchModuleBySearchwordAndLangOther() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setNimiQuery(LANG_TEST1);
        criteria.setKieliUri(XX_LANG_TEST1);
        List<Koulutusmoduuli> result = instance.search(criteria);
        assertEquals(2, result.size());

        criteria.setKieliUri(XX_LANG_TEST2);
        result = instance.search(criteria);
        assertEquals(1, result.size());

        criteria.setKieliUri(SV);
        result = instance.search(criteria);
        assertEquals(0, result.size());

        criteria.setNimiQuery("Yrkesutbildningen");
        result = instance.search(criteria);
        assertEquals(1, result.size());
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

    protected MonikielinenTeksti convertToMonikielinenTeksti(String[][] langText) {
        MonikielinenTeksti tyyppi = new MonikielinenTeksti();

        for (int i = 0; i < langText.length; i++) {
            final String lang = langText[i][0];
            Preconditions.checkNotNull(lang);
            final String text = langText[i][1];
            Preconditions.checkNotNull(text);
            tyyppi.setTekstiKaannos(lang, text);
        }

        return tyyppi;
    }
}