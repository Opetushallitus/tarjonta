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
package fi.vm.sade.tarjonta.publication.enricher.koodisto.it;

import fi.vm.sade.tarjonta.publication.enricher.XMLStreamEnricher;
import fi.vm.sade.tarjonta.util.AssertXPath;
import fi.vm.sade.tarjonta.util.SystemUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests configuration loaded from Spring context and KoodistoLookupService wired to real webservice.
 * The tests are only run if -DskipKooodistoIT=false is set.
 *
 * @author Jukka Raanamo
 */
@ContextConfiguration(locations = "classpath:koodisto-service-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringIntegrationTest {

    @Autowired
    private XMLStreamEnricher xmlEnricher;

    private ByteArrayOutputStream out;

    @Before
    public void setUp() {

        Assume.assumeTrue(SystemUtils.isFalse("skipKoodistoIT", true));

        out = new ByteArrayOutputStream();
        xmlEnricher.setOutput(out);

    }

    @After
    public void tearDown() {
        if (out != null) {
            SystemUtils.printOutIf(new String(out.toByteArray()), "printXml");
        }
    }

    @Test
    public void testEnrichSingleCode() throws Exception {

        xmlEnricher.setInput(new FileInputStream("src/test/resources/test-enrich-with-koodisto.xml"));
        xmlEnricher.process();

        String basePath = "//LearningOpportunityDownloadData/LearningOpportunityInstance/Duration/Units";

        AssertXPath.assertEvals("bad fi value", "kuukautta", basePath + "/Label[@lang='fi']/text()", out);
        AssertXPath.assertEvals("bad sv value", "months", basePath + "/Label[@lang='sv']/text()", out);

    }

}

