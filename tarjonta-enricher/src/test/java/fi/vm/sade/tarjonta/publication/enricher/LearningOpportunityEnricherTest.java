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
package fi.vm.sade.tarjonta.publication.enricher;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import fi.vm.sade.tarjonta.publication.enricher.KoodistoLookupService.SimpleKoodiValue;
import fi.vm.sade.tarjonta.publication.enricher.factory.LearningOpportunityDataEnricherFactory;

/**
 *
 * @author Jukka Raanamo
 */
public class LearningOpportunityEnricherTest {

    private XMLStreamEnricher processor;

    private ByteArrayOutputStream out;

    @Before
    public void setUp() throws Exception {

        KoodistoLookupService koodistoService = mock(KoodistoLookupService.class);
        when(koodistoService.lookupKoodi("371101", 2010)).
            thenReturn(new SimpleKoodiValue("371101", "2010", "Nimi", "Name", "Namn"));

        LearningOpportunityDataEnricherFactory factory = new LearningOpportunityDataEnricherFactory();
        factory.setKoodistoService(koodistoService);

        processor = factory.bean();
        processor.setInput(new FileInputStream("src/test/resources/simple-enrich-in.xml"));
        processor.setOutput(out = new ByteArrayOutputStream());

    }

    @Test
    public void testProcess() throws Exception {

        processor.process();

        // todo: add e.g. xpath expression to validate that processing has taken place
        // and also we need to validate that XML is still valid
        //
        //System.out.println("output:\n" + out.toString());

    }

}

