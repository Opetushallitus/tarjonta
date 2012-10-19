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
package fi.vm.sade.tarjonta.koodisto.service.impl;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import static org.junit.Assert.*;
import org.springframework.transaction.annotation.Transactional;
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
import fi.vm.sade.tarjonta.koodisto.model.Koodi;
import fi.vm.sade.tarjonta.koodisto.service.KoodiBusinessService;
import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.ws.addressing.MetadataType;

/**
 * TODO: use DBUnit
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoodiBusinessServiceImplTest {

    private static final String DEMO_KOODISTO_URI = "koodisto";

    private static final int DEMO_KOODISTO_VERSION = 1;

    @Autowired
    private KoodiBusinessService businessService;

    private Koodi koodi1;

    @Before
    public void setUp() {

        // insert sample data
        koodi1 = new Koodi(DEMO_KOODISTO_URI, DEMO_KOODISTO_VERSION, "koodi1", 1, "1");
        koodi1.setKoodiNimiFi("Yksi");

        koodi1 = businessService.updateOrInsert(koodi1);
        assertNotNull(koodi1);


    }

    @Test
    public void testFindByUri() {

        Koodi koodi = businessService.findByKoodiUri("koodi1");

        assertNotNull(koodi);
        assertEquals(koodi1, koodi);

    }

    @Test
    public void testUpdateOrInsert() {

        // tests update, insert is tested in setup

        // precondition
        Koodi koodi = businessService.findByKoodiUri("koodi1");
        assertEquals("1", koodi.getKoodiArvo());
        assertEquals("Yksi", koodi.getKoodiNimiFi());
        assertNull(koodi.getKoodiNimiEn());

        // update values
        koodi.setKoodiArvo("2");
        koodi.setKoodiNimiFi("Kaksi");
        koodi.setKoodiNimiEn("Two");
        businessService.updateOrInsert(koodi);

        // validate changes
        koodi = businessService.findByKoodiUri("koodi1");
        assertEquals("2", koodi.getKoodiArvo());
        assertEquals("Kaksi", koodi.getKoodiNimiFi());
        assertEquals("Two", koodi.getKoodiNimiEn());
        assertEquals(null, koodi.getKoodiNimiSv());

    }

    @Test
    public void testBatchImport() {

        final String koodistoUri = "newUri";
        final int koodistoVersio = 1;
        final List<KoodiType> koodis = createRandomKoodis(20);

        businessService.batchImportKoodis(koodistoUri, koodistoVersio, koodis);

        // todo: validate, create finder by koodisto uri

    }

    private List<KoodiType> createRandomKoodis(int count) {

        List<KoodiType> result = new ArrayList<KoodiType>(count);

        for (int i = 0; i < count; i++) {

            KoodiType koodi = new KoodiType();
            koodi.setKoodiArvo(String.valueOf(i));
            koodi.setKoodiUri("koodiUri:" + i);
            koodi.setVersio(1);
            KoodiMetadataType meta = new KoodiMetadataType();
            meta.setKieli(KieliType.FI);
            meta.setNimi("Random Koodi " + i);
            koodi.getMetadata().add(meta);
            result.add(koodi);

        }

        return result;

    }

}

