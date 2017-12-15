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

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.util.KoulutusTreeWalker.NodeHandler;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


@Transactional
public class KoulutusmoduuliTreeWalkerTest extends TestUtilityBase {

    @Test
    public void testWalk() {

        final AtomicInteger counter = new AtomicInteger();

        KoulutusTreeWalker.NodeHandler handler = moduuli -> {
            counter.incrementAndGet();
            return true;
        };


        new KoulutusTreeWalker(handler).walk(fixtures.createPersistedKoulutusmoduuliStructure());

        // there are 4 nodes but one is shared from via routes
        assertEquals(5, counter.intValue());

    }

}

