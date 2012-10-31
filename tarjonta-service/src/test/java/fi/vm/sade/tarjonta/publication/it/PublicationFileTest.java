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
package fi.vm.sade.tarjonta.publication.it;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static org.mockito.Mockito.*;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

import fi.vm.sade.tarjonta.publication.LearningOpportunityDataWriter;
import fi.vm.sade.tarjonta.publication.PublicationCollector;
import fi.vm.sade.tarjonta.publication.PublicationDataService;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
public class PublicationFileTest {

    private PublicationCollector collector;

    private File outputFile;

    private OutputStream outputStream;

    private TarjontaFixtures fixtures = new TarjontaFixtures();

    private Random random = new Random(System.currentTimeMillis());

    private static final Logger log = LoggerFactory.getLogger("TEST");

    @Before
    public void setUp() throws Exception {

        collector = new PublicationCollector();
        collector.setDataService(null);

        outputFile = File.createTempFile("learning_publication", ".xml");
        outputStream = new FileOutputStream(outputFile);

        log.info("writing test data to: " + outputFile);

        LearningOpportunityDataWriter jaxbWriter = new LearningOpportunityDataWriter();
        jaxbWriter.setOutput(outputStream);

        collector.setHandler(jaxbWriter);

    }

    @After
    public void tearDown() {

        close(outputStream);

    }

    @Test
    public void testHowBigIsExportFileWithLargeDataSet() throws Exception {

        int numKoulutus = 2000;
        int numHaku = 20;
        int numHakukohde = 1900;

        collector.setDataService(setUpDataService(numKoulutus, numHaku, numHakukohde));
        collector.start();

        outputStream.flush();

        long bytes = outputFile.length();

        System.out.print("publication file size with " + numKoulutus + " koulutus"
            + ", " + numHaku + " haku"
            + ", " + numHakukohde + " hakukohde"
            + ", file size: " + bytes + " bytes (" + megs(bytes) + " MB)");

    }

    private double megs(long bytes) {
        return (bytes / (double) (1024 * 1024));
    }

    private PublicationDataService setUpDataService(int numKoulutus, int numHaku, int numHakukohde) {

        List<KoulutusmoduuliToteutus> toteutusList = new ArrayList<KoulutusmoduuliToteutus>(numKoulutus);
        for (int i = 0; i < numKoulutus; i++) {
            Koulutusmoduuli m = fixtures.createTutkintoOhjelma();
            KoulutusmoduuliToteutus t = fixtures.createTutkintoOhjelmaToteutus();
            t.setKoulutusmoduuli(m);
            toteutusList.add(t);
        }

        List<Haku> hakuList = new ArrayList<Haku>(numHaku);
        for (int i = 0; i < numHaku; i++) {
            Haku h = fixtures.createHaku();
            hakuList.add(h);
        }

        List<Hakukohde> hakukohdeList = new ArrayList<Hakukohde>(numHakukohde);
        for (int i = 0; i < numHakukohde; i++) {
            Hakukohde h = fixtures.createHakukohde();
            h.setHaku(hakuList.get(random.nextInt(numHaku)));
            hakukohdeList.add(h);
        }

        PublicationDataService service = mock(PublicationDataService.class);

        when(service.listKoulutusmoduuliToteutus()).thenReturn(toteutusList);
        when(service.listHaku()).thenReturn(hakuList);
        when(service.listHakukohde()).thenReturn(hakukohdeList);

        return service;


    }

    private void close(Closeable c) {
        try {
            c.close();
        } catch (Exception ignore) {
        }
    }

}

