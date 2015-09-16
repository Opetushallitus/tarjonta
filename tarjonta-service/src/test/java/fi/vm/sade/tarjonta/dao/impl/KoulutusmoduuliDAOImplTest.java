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

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import junit.framework.Assert;
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

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

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
public class KoulutusmoduuliDAOImplTest extends TestUtilityBase {

    private static final String KOULUTUS_URI = "uri_koulutus#1";
    private static final String LUKIOLINJA_URI = "uri_lukiolinja#5";
    private static final String KOULUTUSOHJELMA_URI1 = "uri_koulutusohjelma";
    private static final String OSAAMISALA_URI1 = "uri_osaamisala_1#1";
    private static final String OSAAMISALA_URI2 = "uri_osaamisala_2#2";
    private static final String OSAAMISALA_URI3 = "uri_osaamisala_3#100";
    private static final String VALUE = "123456";
    private static final String TWIN_VALUE_OSAAMISALA_URI = "uri_osaamisala_" + VALUE + "#5";
    private static final String TWIN_VALUE_KOULUTUSOHJELMA_URI = "uri_koulutusohjelma_" + VALUE + "#1";
    private static final String SEARCH_BY_URI_A = "uri_170";
    private static final String SEARCH_BY_URI_B = "uri_2";
    private static final String SEARCH_BY_URI_C = "uri_a";
    private Koulutusmoduuli komo1, komo2, komo3, komo4, komo5, komo6;
    private EntityManager em;

    @Autowired
    private KoulutusmoduuliDAOImpl instance;

    @Before
    public void setUp() {
        //CLEAN ENTITYMANAGER:
        //This should be done in junit after method, but for some reason there is a
        //something wrong with the after annotation in Bamboo environment
        remove(komo1);
        remove(komo2);
        remove(komo3);
        remove(komo4);
        remove(komo5);
        remove(komo6);

        if (em != null) {
            em.clear();
        }

        //RE-INITIALIZE ENITITYMANAGER:
        final String sep = EntityUtils.STR_ARRAY_SEPARATOR;

        em = instance.getEntityManager();
        komo1 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo1.setOppilaitostyyppi(sep + "uri_1" + sep + SEARCH_BY_URI_B + sep + SEARCH_BY_URI_A + sep);
        komo1.setKoulutusohjelmaUri(KOULUTUSOHJELMA_URI1);
        komo1.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        persist(komo1);

        komo2 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo2.setOppilaitostyyppi(sep + SEARCH_BY_URI_C + sep);
        komo2.setKoulutusohjelmaUri(KOULUTUSOHJELMA_URI1);
        komo2.setOsaamisalaUri(OSAAMISALA_URI1);
        komo2.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        persist(komo2);

        komo3 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo3.setKoulutusUri(KOULUTUS_URI);
        komo3.setOppilaitostyyppi(sep + "uri_fuubar" + sep);
        komo3.setLukiolinjaUri(LUKIOLINJA_URI);
        komo3.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
        persist(komo3);

        komo4 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, "OID_1", KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        komo4.setKoulutusUri(KOULUTUS_URI);
        komo4.setOppilaitostyyppi(sep + "uri_fuubar" + sep);
        komo4.setOsaamisalaUri(OSAAMISALA_URI2);
        komo4.setKoulutusohjelmaUri(null);
        komo4.setLukiolinjaUri(null);
        komo4.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        persist(komo4);

        komo5 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, "OID_2", KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        komo5.setKoulutusUri(KOULUTUS_URI);
        komo5.setOppilaitostyyppi(sep + "uri_fuubar" + sep);
        komo5.setOsaamisalaUri(OSAAMISALA_URI3);
        komo5.setKoulutusohjelmaUri(null);
        komo5.setLukiolinjaUri(null);
        komo5.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        persist(komo5);

        komo6 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, "OID_3", KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        komo6.setKoulutusUri(KOULUTUS_URI);
        komo6.setOppilaitostyyppi(sep + "uri_fuubar" + sep);
        komo6.setOsaamisalaUri(TWIN_VALUE_OSAAMISALA_URI);
        komo6.setKoulutusohjelmaUri(TWIN_VALUE_KOULUTUSOHJELMA_URI);
        komo6.setLukiolinjaUri(null);
        komo6.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        persist(komo6);
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
    public void testSearchByLukiolinja() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setLukiolinjaKoodiUri(LUKIOLINJA_URI);
        List result = instance.search(criteria);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchByKoulutusohjelma() {
        KoulutusmoduuliDAO.SearchCriteria criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI1);
        List result = instance.search(criteria);
        assertEquals(2, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI1);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);
        result = instance.search(criteria);
        assertEquals(1, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI1);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);
        criteria.setModuulityyppi(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
        result = instance.search(criteria);
        assertEquals(1, result.size());

        criteria = new KoulutusmoduuliDAO.SearchCriteria();
        criteria.setKoulutusohjelmaKoodi(KOULUTUSOHJELMA_URI1);
        criteria.getOppilaitostyyppis().add(SEARCH_BY_URI_C);
        criteria.setModuulityyppi(ModuulityyppiEnum.LUKIOKOULUTUS);

        result = instance.search(criteria);
        assertEquals(0, result.size());
    }

    @Test
    public void testXSSFiltering() {
        //TODO test more fields...
        komo1 = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo1.setOid("xss-1");
        komo1.setOppilaitostyyppi("zz");
        komo1.setKoulutusohjelmaUri(KOULUTUSOHJELMA_URI1);
        komo1.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);

        komo1.setNimi(new MonikielinenTeksti("fi", "ei saa muuttaa & merkkiä!"));
        komo1.getTekstit().put(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, new MonikielinenTeksti("fi", "jatko-opinto"));
        komo1.getTekstit().put(KomoTeksti.KOULUTUKSEN_RAKENNE, new MonikielinenTeksti("fi", "<table><a href='window.alert(\"hello\")'>foo</a></table>"));
        persist(komo1);

        komo1 = instance.findBy("oid", "xss-1").get(0);
        Assert.assertEquals("ei saa muuttaa & merkkiä!", komo1.getNimi().asMap().get("fi"));
        Assert.assertEquals("<table>foo</table>", komo1.getTekstit().get(KomoTeksti.KOULUTUKSEN_RAKENNE).getTekstiForKieliKoodi("fi"));
        Assert.assertEquals("jatko-opinto", komo1.getTekstit().get(KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET).getTekstiForKieliKoodi("fi"));
    }

    @Test
    public void searchModuleForImportProcess() {
        Koulutusmoduuli m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, "uri_koulutus", null, "uri_osaamisala_3", null);
        Assert.assertEquals(KOULUTUS_URI, m.getKoulutusUri());
        Assert.assertEquals(null, m.getKoulutusohjelmaUri());
        Assert.assertEquals(OSAAMISALA_URI3, m.getOsaamisalaUri());
        Assert.assertEquals(m.getLukiolinjaUri(), null);

        m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOULUTUS_URI, null, OSAAMISALA_URI2, null);
        Assert.assertEquals(KOULUTUS_URI, m.getKoulutusUri());
        Assert.assertEquals(null, m.getKoulutusohjelmaUri());
        Assert.assertEquals(OSAAMISALA_URI2, m.getOsaamisalaUri());
        Assert.assertEquals(null, m.getLukiolinjaUri());

        m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOULUTUS_URI, null, TWIN_VALUE_OSAAMISALA_URI, null);
        Assert.assertEquals("OID_3", m.getOid());
        Assert.assertEquals(KOULUTUS_URI, m.getKoulutusUri());
        Assert.assertEquals(TWIN_VALUE_OSAAMISALA_URI, m.getOsaamisalaUri());
        Assert.assertEquals(TWIN_VALUE_KOULUTUSOHJELMA_URI, m.getKoulutusohjelmaUri());
        Assert.assertEquals(null, m.getLukiolinjaUri());

        m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOULUTUS_URI, TWIN_VALUE_KOULUTUSOHJELMA_URI, null, null);
        Assert.assertEquals("OID_3", m.getOid());
        Assert.assertEquals(KOULUTUS_URI, m.getKoulutusUri());
        Assert.assertEquals(TWIN_VALUE_OSAAMISALA_URI, m.getOsaamisalaUri());
        Assert.assertEquals(TWIN_VALUE_KOULUTUSOHJELMA_URI, m.getKoulutusohjelmaUri());
        Assert.assertEquals(null, m.getLukiolinjaUri());

        m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOULUTUS_URI, TWIN_VALUE_KOULUTUSOHJELMA_URI, TWIN_VALUE_OSAAMISALA_URI, null);
        Assert.assertEquals("OID_3", m.getOid());
        Assert.assertEquals(KOULUTUS_URI, m.getKoulutusUri());
        Assert.assertEquals(TWIN_VALUE_OSAAMISALA_URI, m.getOsaamisalaUri());
        Assert.assertEquals(TWIN_VALUE_KOULUTUSOHJELMA_URI, m.getKoulutusohjelmaUri());
        Assert.assertEquals(null, m.getLukiolinjaUri());

        m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO, KOULUTUS_URI, null, null, LUKIOLINJA_URI);
        Assert.assertEquals(KOULUTUS_URI, m.getKoulutusUri());
        Assert.assertEquals(null, m.getOsaamisalaUri());
        Assert.assertEquals(LUKIOLINJA_URI, m.getLukiolinjaUri());

        //no results
        m = instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO, KOULUTUS_URI, null, null, "foobar");
        Assert.assertEquals(null, m);
    }

    @Test(expected = RuntimeException.class)
    public void searchModuleForImportProcessTooManyResults() {
        //unique result test
        instance.findModule(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, "uri_koulutus", null, null, null);
        fail("expect error");
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
