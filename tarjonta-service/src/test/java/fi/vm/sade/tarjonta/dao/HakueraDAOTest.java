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

import fi.vm.sade.tarjonta.dao.impl.HakueraDAOImpl;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Antti Salonen
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class HakueraDAOTest {
    
    private static final Logger log = LoggerFactory.getLogger(HakueraDAOTest.class);

    @Autowired
    private HakueraDAOImpl dao;

    @Test
    public void findAll_dateSearchWorks() {
        long now = new Date().getTime();
        int dif = 10000;
        Hakuera meneillaan = create(now-dif, now+dif);
        Hakuera tuleva = create(now+dif, now+2*dif);
        Hakuera paattynyt = create(now-2*dif, now-dif);
        List<Hakuera> l;

        // kaikki
        assertEquals(3, dao.findAll(criteria(true, true, true)).size());

        // päättyneet ja meneillään -> alkuaika pienempi kuin nyt
        l = dao.findAll(criteria(true, true, false));
        assertEquals(2, l.size());
        assertTrue(l.contains(paattynyt));
        assertTrue(l.contains(meneillaan));

        // meneilläään ja tulevat -> loppuaika suurempi kuin nyt
        l = dao.findAll(criteria(false, true, true));
        assertEquals(2, l.size());
        assertTrue(l.contains(meneillaan));
        assertTrue(l.contains(tuleva));

        // päättyneet ja tulevat -> loppuaika pienempi kuin nyt TAI alkuaika suurempi kuin nyt
        l = dao.findAll(criteria(true, false, true));
        assertEquals(2, l.size());
        assertTrue(l.contains(paattynyt));
        assertTrue(l.contains(tuleva));

        // päättyneet -> loppuaika pienempi kuin nyt
        l = dao.findAll(criteria(true, false, false));
        assertEquals(1, l.size());
        assertTrue(l.contains(paattynyt));

        // meneillään -> alkuaika pienempi kuin nyt JA loppuaika suurempi kuin nyt
        l = dao.findAll(criteria(false, true, false));
        assertEquals(1, l.size());
        assertTrue(l.contains(meneillaan));

        // tulevat -> alkuaika suurempi kuin nyt
        l = dao.findAll(criteria(false, false, true));
        assertEquals(1, l.size());
        assertTrue(l.contains(tuleva));

        // ei mitään
        l = dao.findAll(criteria(false, false, false));
        assertEquals(0, l.size());

    }

    private SearchCriteriaDTO criteria(boolean paattyneet, boolean meneillaan, boolean tuleva) {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setPaattyneet(paattyneet);
        criteria.setMeneillaan(meneillaan);
        criteria.setTulevat(tuleva);
        return criteria;
    }

    private Hakuera create(long alkuPvm, long loppuPvm) {
        Hakuera h = new Hakuera();
        h.setHaunAlkamisPvm(new Date(alkuPvm));
        h.setHaunLoppumisPvm(new Date(loppuPvm));
        return dao.insert(h);
    }

}

