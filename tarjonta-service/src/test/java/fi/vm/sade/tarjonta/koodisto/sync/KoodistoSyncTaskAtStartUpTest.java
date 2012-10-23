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
package fi.vm.sade.tarjonta.koodisto.sync;

import fi.vm.sade.koodisto.service.mock.MockDataHandler;

import org.junit.Test;
import static org.junit.Assert.*;

import fi.vm.sade.tarjonta.koodisto.service.KoodistoTestSupport;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = {"classpath:spring/test-context.xml", "classpath:spring/test-koodisto-sync-context.xml"})
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoSyncTaskAtStartUpTest extends KoodistoTestSupport {

    @Autowired
    private SimpleSyncTaskListener listener;

    @Test
    public void testTaskRunsAtStartUp() {

        // give it some time
        sleep(1000);

        // either success or failure should be called
        assertEquals("sync task was not called at just once startup", 1, listener.getCountOnFailedCalled());

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

}

