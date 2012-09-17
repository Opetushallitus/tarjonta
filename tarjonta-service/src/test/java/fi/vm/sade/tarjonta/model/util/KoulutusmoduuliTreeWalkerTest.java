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
package fi.vm.sade.tarjonta.model.util;

import fi.vm.sade.tarjonta.KoulutusFixtures;
import fi.vm.sade.tarjonta.model.Koulutus;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker.NodeHandler;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliTreeWalkerTest {

    @Autowired
    private KoulutusFixtures fixtures;

    @Test
    public void testWalk() {

        final AtomicInteger counter = new AtomicInteger();

        KoulutusTreeWalker.NodeHandler handler = new NodeHandler() {

            @Override
            public boolean match(Koulutus moduuli) {
                counter.incrementAndGet();
                return true;
            }

        };


        new KoulutusTreeWalker(handler).walk(fixtures.simpleKoulutusmoduuliTree());

        // there are 4 nodes but one is shared from via routes
        assertEquals(5, counter.intValue());

    }

}

