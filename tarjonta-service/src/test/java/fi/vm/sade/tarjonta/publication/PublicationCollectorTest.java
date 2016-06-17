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
package fi.vm.sade.tarjonta.publication;

import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.publication.PublicationCollector.EventHandler;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Jukka Raanamo
 */
public class PublicationCollectorTest {

    private EventHandlerMock handler;
    private PublicationCollector collector;
    protected final Logger log = LoggerFactory.getLogger("TEST");

    @Before
    public void setUp() {

        handler = new EventHandlerMock();

        PublicationDataService dataService = mock(PublicationDataService.class);

        collector = new PublicationCollector();
        collector.setDataService(dataService);
        collector.setHandler(handler);

    }

    @Test
    public void testStartAndCompletedEventsAreInvokedOnEmptyData() throws Exception {

        collector.start();

        assertEquals(1, handler.startEvents);
        assertEquals(1, handler.completedEvents);

    }

    @Test
    public void testOnlyOneCallPerUniqueHaku() throws Exception {

        // the same logic applies to all other callbacks

        List<Haku> list = new ArrayList<Haku>();
        list.add(createHaku("haku/123"));
        list.add(createHaku("haku/123"));

        PublicationDataService dataService = mock(PublicationDataService.class);
        when(dataService.listHaku()).thenReturn(list);
        collector.setDataService(dataService);

        collector.start();

        assertEquals(1, handler.hakuEvents);

    }

    @Test
    public void testMissingKoulutusmoduuliEndsWithFailure() throws Exception {

        List<KoulutusmoduuliToteutus> list = new ArrayList<KoulutusmoduuliToteutus>();
        list.add(new KoulutusmoduuliToteutus());

        PublicationDataService dataService = mock(PublicationDataService.class);
        when(dataService.listKoulutusmoduuliToteutus()).thenReturn(list);

        collector.setDataService(dataService);
        try {
            collector.start();
        } catch (Exception e) {
            // expected
        }

        assertEquals(1, handler.startEvents);
        assertEquals(0, handler.completedEvents);
        assertEquals(1, handler.failedEvents);
        assertEquals(0, handler.koulutusmoduuliEvents);
        assertEquals(0, handler.koulutusmoduuliToteutusEvents);

    }

    public class EventHandlerMock implements EventHandler {

        private int startEvents;
        private int failedEvents;
        private int completedEvents;
        private int warningEvents;
        private int hakuEvents;
        private int hakukohdeEvents;
        private int koulutusmoduuliEvents;
        private int koulutusmoduuliToteutusEvents;
        private int koulutustarjoajaEvents;

        @Override
        public void onCollectStart() {
            startEvents++;
        }

        @Override
        public void onCollectFailed(Exception e) {
            failedEvents++;
        }

        @Override
        public void onCollectWarning(String msg) {
            log.info("onCollectWarning: " + msg);
            warningEvents++;
        }

        @Override
        public void onCollectEnd() {
            completedEvents++;
        }

        @Override
        public void onCollect(Haku haku) {
            hakuEvents++;
        }

        @Override
        public void onCollect(Koulutusmoduuli moduuli) {
            koulutusmoduuliEvents++;
        }

        @Override
        public void onCollect(KoulutusmoduuliToteutus toteutus) {
            koulutusmoduuliToteutusEvents++;
        }

        @Override
        public void onCollect(OrganisaatioRDTO tarjoaja) throws Exception {
            koulutustarjoajaEvents++;
        }

        @Override
        public void onCollect(Koulutusmoduuli m, KoulutusmoduuliToteutus t)
                throws Exception {

            koulutusmoduuliEvents++;
        }

        @Override
        public void onCollect(Hakukohde hakukohde, List<MonikielinenMetadata> sora, List<MonikielinenMetadata> valintaperuste) throws Exception {
            hakukohdeEvents++;
        }
    }

    private Haku createHaku(String oid) {

        Haku h = new Haku();
        h.setOid(oid);

        return h;

    }
}
