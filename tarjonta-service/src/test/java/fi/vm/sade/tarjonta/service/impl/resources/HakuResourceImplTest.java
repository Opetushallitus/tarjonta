/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.impl.resources;

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author mlyly
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
// @ActiveProfiles("embedded-solr")
@Transactional()
public class HakuResourceImplTest extends TestUtilityBase {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResourceImplTest.class);

    @org.junit.Before
    @org.junit.After
    public void tearDown(){
        fixtures.deleteAll();
    }

    @Test
    public void testSearchHaku() throws InterruptedException {
        LOG.info("testSearchHaku()...");

        // Create test data
        Haku haku = fixtures.createHaku();
        hakuDao.insert(haku);

        // Fetch it
        List<OidRDTO> tmp = hakuResource.search(null, 10, 0, null, null);

        // Make assertions
        assertTrue("Shoud have one Haku", tmp.size() == 1);
        assertTrue("Shoud have one Haku with known oid", haku.getOid().equals(tmp.get(0).getOid()));

        // Save created Haku OIDs here for referrals
        List<String> tmpOids = new ArrayList<String>();
        tmpOids.add(haku.getOid());
        
        Thread.sleep(250L);

        Date beforeAnyHakusCreated = new Date();
        Thread.sleep(250L); // make sure dates wont overlap

        // Create test hakus
        for (int i = 0; i < 100; i++) {
            haku = fixtures.createHaku();
            hakuDao.insert(haku);
            tmpOids.add(haku.getOid());
        }

        Thread.sleep(250L); // make sure dates wont overlap
        Date afterHakusCreated = new Date();

        // Test paging and finding by OID
        int pageSize = 21;
        int starIndex = 0;
        do {
            tmp = hakuResource.search(null, pageSize, starIndex, null, null);

            for (OidRDTO oidRDTO : tmp) {
                // Mark OID used
                assertTrue("Should have pre-logged OID in list to be removed...", tmpOids.remove(oidRDTO.getOid()));

                HakuDTO haku2 = hakuResource.getByOID(oidRDTO.getOid());
                assertTrue("Find by OID should find Haku", haku2 != null);
                assertTrue("OID should be same", oidRDTO.getOid().equals(haku2.getOid()));
            }

            starIndex += pageSize;
            pageSize++;
        } while (tmp.size() > 0);

        assertTrue("All created test Hakus should have been looped through", tmpOids.isEmpty());

        // Should have 1 Haku (that oid check one)
        tmp = hakuResource.search(null, 1000, 0, beforeAnyHakusCreated, null);
        assertTrue("Shoud have one Haku that was created before the 100 ones... but was " + tmp.size(), tmp.size() == 1);

        // Should have 100 Haku (that oid check one)
        tmp = hakuResource.search(null, 1000, 0, null, beforeAnyHakusCreated);
        assertTrue("Shoud have 100 Haku's created in loop...", tmp.size() == 100);

        LOG.info("testSearchHaku()... done.");
    }



}
