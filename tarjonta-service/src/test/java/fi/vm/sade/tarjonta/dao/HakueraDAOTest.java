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
import fi.vm.sade.tarjonta.dao.impl.HakuDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Antti Salonen
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Ignore
public class HakueraDAOTest {
    
    private static final Logger log = LoggerFactory.getLogger(HakueraDAOTest.class);

    @Autowired
    private HakuDAOImpl dao;
    @Autowired
    private HakueraTstHelper helper;

    @Test
    public void findAll_dateSearchWorks() {
        long now = new Date().getTime();
        int dif = 10000;
        Haku meneillaan = helper.create(now-dif, now+dif);
        Haku tuleva = helper.create(now+dif, now+2*dif);
        Haku paattynyt = helper.create(now-2*dif, now-dif);
        List<Haku> l;

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

    //@Test
    // sorting disabled since translations were moved to separate entities, waiting for DSL to replace
    // criteria api (if any).
    public void findAll_sortingByABC() {
        long now = new Date().getTime();
        Haku h1 = helper.create(now, now, "bbb", "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);
       
        Haku h2 = helper.create(now, now, "aaa", "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);

        Haku h3 = helper.create(now, now, "ccc", "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);
     

        // fi

        List<Haku> l = dao.findAll(helper.criteria(true, true, true, "fi"));
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
    
    @Test
    public void testSimpleSaveAndRead() {
        long now = new Date().getTime();
        int dif = 10000;
        String oid = "1.2.34566.3";
        Haku hakuera =  helper.create(now, now+dif, oid, "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);

        assertNotNull(hakuera.getId());

        Haku hakuera2 = read(hakuera.getId());
        assertNotNull(hakuera2);
        assertEquals(oid, hakuera2.getOid());
    }
    
    @Test
    public void testSimpleUpdateAndRead() {
        long now = new Date().getTime();
        int dif = 10000;
        String oid = "1.2.34566.4";
        String hakutyyppi = "Ammattikorkeakoulut";
        Haku hakuera =  helper.create(now, now+dif, oid, "Varsinainen haku", "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);

        assertNotNull(hakuera.getId());

        Haku hakuera2 = read(hakuera.getId());
        hakuera2.setHakutyyppiUri(hakutyyppi);
        dao.update(hakuera2);
        Haku hakuera3 = read(hakuera.getId());
        assertEquals(hakutyyppi, hakuera3.getHakutyyppiUri());        
    }
    
    @Test
    public void testFindByOid() {
        long now = new Date().getTime();
        int dif = 10000;
        String oid = "1.2.34566.5";
        String hakutyyppi = "Varsinainen haku";
        
        Haku hakuera = helper.create(now, now+dif, oid, hakutyyppi, "Syksy", "Syksy 2013", "Korkeakoulutus", "Yhteishaku",2013,2014);
        
        assertNotNull(hakuera.getId());
        
        Haku hakuera2 = dao.findByOid(oid);
        
        assertEquals(hakutyyppi, hakuera2.getHakutyyppiUri());
        
    }

    @Test
    public void testValidation_notNull() {
        Haku h = helper.createValidHaku();
        h.setHakutapaUri(null);
        try {
            dao.update(h);
            fail("expected an exception");
        } catch (ConstraintViolationException e) {
            assertConstraintViolationException(e, "hakutapaUri", "{javax.validation.constraints.NotNull.message}");
        }
    }

    @Test
    public void testValidation_nimi() {
        Haku h = helper.createValidHaku();
        h.setNimiFi("x"); // too short
        try {
            dao.update(h);
            // todo: length not validated since translation is in it's own entity
            //fail("expected an exception");
        } catch (ConstraintViolationException e) {
            assertConstraintViolationException(e, "nimiFi", "{javax.validation.constraints.Size.message}");
        }
    }

    @Test
    public void testValidation_uniqueOid() {
        Haku h = helper.createValidHaku();
        Haku h2 = helper.createValidHaku();
        h2.setOid(h.getOid());
        try {
            dao.update(h);
            fail("expected an exception");
        } catch (PersistenceException e) {
            assertTrue(e.getMessage().contains("unique constraint or index violation"));
        }
    }
    
    
    @Test
    public void testNimis() {
        
        Haku h = helper.createValidHaku();
        h.setNimiFi("Suomi");
        
    }

    private void assertConstraintViolationException(ConstraintViolationException e, String propertyPath, String messageTemplate) {
        ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();
        assertEquals(propertyPath, violation.getPropertyPath().toString());
        assertEquals(messageTemplate, violation.getMessageTemplate());
    }

    private Haku read(Long id) {
        return (Haku) dao.read(id);
    }

}