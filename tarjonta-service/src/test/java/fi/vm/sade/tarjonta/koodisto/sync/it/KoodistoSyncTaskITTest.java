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
package fi.vm.sade.tarjonta.koodisto.sync.it;

import fi.vm.sade.tarjonta.koodisto.service.KoodiBusinessService;
import fi.vm.sade.tarjonta.koodisto.sync.KoodistoSyncTask;
import fi.vm.sade.tarjonta.koodisto.sync.SimpleSyncTaskListener;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import fi.vm.sade.tarjonta.koodisto.service.KoodistoTestSupport;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = {"classpath:spring/test-context.xml", "classpath:META-INF/spring/context/koodisto-sync-context.xml"})
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoSyncTaskITTest extends KoodistoTestSupport {

    @Autowired
    private KoodistoSyncTask syncTask;

    @Autowired
    private KoodiBusinessService koodiService;

    private static final String KNOWN_SMALL_KOODISTO_URI = "HAKUTAPA";

    @Ignore
    @Test
    public void testValidKoodistoReturnsResults() {

        // precondition
        assertEquals(0, koodiService.findKoodisByKoodistoUri(KNOWN_SMALL_KOODISTO_URI).size());

        SimpleSyncTaskListener listener = new SimpleSyncTaskListener();

        syncTask.setKoodistoSyncSpecs(set(KNOWN_SMALL_KOODISTO_URI));
        syncTask.addListener(listener);
        syncTask.execute();
        syncTask.removeListener(listener);

        assertFalse(listener.getKoodis().isEmpty());

        // listener is invoked and data is inserted
        assertFalse(koodiService.findKoodisByKoodistoUri(KNOWN_SMALL_KOODISTO_URI).isEmpty());

    }

}

