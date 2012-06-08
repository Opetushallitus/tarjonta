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

import fi.vm.sade.tarjonta.HakueraTstHelper;
import fi.vm.sade.tarjonta.dao.impl.HakueraDAOImpl;
import fi.vm.sade.tarjonta.model.Hakuera;
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
    @Autowired
    private HakueraTstHelper helper;

    @Test
    public void findAll_dateSearchWorks() {
        long now = new Date().getTime();
        int dif = 10000;
        Hakuera meneillaan = helper.create(now-dif, now+dif);
        Hakuera tuleva = helper.create(now+dif, now+2*dif);
        Hakuera paattynyt = helper.create(now-2*dif, now-dif);
        List<Hakuera> l;

        // kaikki
        assertEquals(3, dao.findAll(helper.criteria(true, true, true, "fi")).size());

        // päättyneet ja meneillään -> alkuaika pienempi kuin nyt
        l = dao.findAll(helper.criteria(true, true, false, "fi"));
        assertEquals(2, l.size());
        assertTrue(l.contains(paattynyt));
        assertTrue(l.contains(meneillaan));

        // meneilläään ja tulevat -> loppuaika suurempi kuin nyt
        l = dao.findAll(helper.criteria(false, true, true, "fi"));
        assertEquals(2, l.size());
        assertTrue(l.contains(meneillaan));
        assertTrue(l.contains(tuleva));

        // päättyneet ja tulevat -> loppuaika pienempi kuin nyt TAI alkuaika suurempi kuin nyt
        l = dao.findAll(helper.criteria(true, false, true, "fi"));
        assertEquals(2, l.size());
        assertTrue(l.contains(paattynyt));
        assertTrue(l.contains(tuleva));

        // päättyneet -> loppuaika pienempi kuin nyt
        l = dao.findAll(helper.criteria(true, false, false, "fi"));
        assertEquals(1, l.size());
        assertTrue(l.contains(paattynyt));

        // meneillään -> alkuaika pienempi kuin nyt JA loppuaika suurempi kuin nyt
        l = dao.findAll(helper.criteria(false, true, false, "fi"));
        assertEquals(1, l.size());
        assertTrue(l.contains(meneillaan));

        // tulevat -> alkuaika suurempi kuin nyt
        l = dao.findAll(helper.criteria(false, false, true, "fi"));
        assertEquals(1, l.size());
        assertTrue(l.contains(tuleva));

        // ei mitään
        l = dao.findAll(helper.criteria(false, false, false, "fi"));
        assertEquals(0, l.size());

    }

    @Test
    public void findAll_sortingByABC() {
        long now = new Date().getTime();
        Hakuera h1 = helper.create(now, now, "bbb");
        Hakuera h2 = helper.create(now, now, "aaa");
        Hakuera h3 = helper.create(now, now, "ccc");

        // fi

        List<Hakuera> l = dao.findAll(helper.criteria(true, true, true, "fi"));
        assertEquals("aaa FI", l.get(0).getNimiFi());
        assertEquals("bbb FI", l.get(1).getNimiFi());
        assertEquals("ccc FI", l.get(2).getNimiFi());

        // sv

        h2.setNimiSv("ddd");
        dao.update(h2);
        l = dao.findAll(helper.criteria(true, true, true, "sv"));
        assertEquals("bbb SV", l.get(0).getNimiSv());
        assertEquals("ccc SV", l.get(1).getNimiSv());
        assertEquals("ddd", l.get(2).getNimiSv());

        // en

        h1.setNimiEn("xxx");
        dao.update(h1);
        l = dao.findAll(helper.criteria(true, true, true, "en"));
        assertEquals("aaa EN", l.get(0).getNimiEn());
        assertEquals("ccc EN", l.get(1).getNimiEn());
        assertEquals("xxx", l.get(2).getNimiEn());

    }

}

