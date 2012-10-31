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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

/**
 * Gathers learning opportunity material (tarjonta) that is ready for publication. Invokes
 * handler to do something on the collected data, e.g. write to stream. Note: this class is not thread safe.
 *
 * @author Jukka Raanamo
 */
public class PublicationCollector {

    private EventHandler handler;

    private PublicationDataService dataService;

    /**
     * Map used to avoid triggering events on re-occurring items.
     */
    private Map<String, String> notifiedMap = new HashMap<String, String>();

    private static final Logger log = LoggerFactory.getLogger(PublicationCollector.class);

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    public void setDataService(PublicationDataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Starts the data collecting process. Invokes handler with data ready to be published.
     * @throws fi.vm.sade.tarjonta.publication.PublicationCollector.ConfigurationException if
     * collector has is not properly configured.
     * @throws Exception  if data processing fails. Handler's onCollectFailed will be als called
     * with the same exception.
     */
    public void start() throws ConfigurationException, Exception {

        validateConfig();

        try {

            fireCollectStarted();
            processData();
            fireCollectCompleted();

        } catch (Exception e) {

            log.error("error while processing data", e);

            fireCollectFailed(e);
            throw e;

        }

    }

    protected void fireCollectStarted() throws Exception {

        handler.onCollectStart();

    }

    protected void fireCollectCompleted() throws Exception {

        handler.onCollectEnd();

    }

    protected void fireCollectFailed(Exception e) {

        handler.onCollectFailed(e);

    }

    protected void fireCollect(KoulutusmoduuliToteutus t) throws Exception {

        if (isNotifiedBefore(t.getOid())) {
            handler.onCollect(t);
        }

    }

    protected void fireCollect(Koulutusmoduuli m) throws Exception {

        if (!isNotifiedBefore(m.getOid())) {
            handler.onCollect(m);
        }

    }

    protected void fireCollect(Hakukohde h) throws Exception {

        if (!isNotifiedBefore(h.getOid())) {
            handler.onCollect(h);
        }

    }

    protected void fireCollect(Haku h) throws Exception {

        if (!isNotifiedBefore(h.getOid())) {
            handler.onCollect(h);
        }

    }

    private void processData() throws CollectorException, Exception {

        List<KoulutusmoduuliToteutus> koulutusList = dataService.listKoulutusmoduuliToteutus();

        if (koulutusList.isEmpty()) {
            handler.onCollectWarning("zero koulutusmoduuliToteutus found");
        }

        for (KoulutusmoduuliToteutus t : koulutusList) {

            Koulutusmoduuli m = t.getKoulutusmoduuli();

            // todo: this should not be possible by the db
            validateThat("koulutusmoduuliToteutus.koulutusmoduuli is null", m != null);

            fireCollect(m);
            fireCollect(t);

        }

        List<Hakukohde> hakukohdeList = dataService.listHakukohde();
        for (Hakukohde hakukohde : hakukohdeList) {
            fireCollect(hakukohde);
        }

        List<Haku> hakuList = dataService.listHaku();
        for (Haku h : hakuList) {
            fireCollect(h);
        }

    }

    private void validateThat(String msg, boolean condition) {

        if (!condition) {
            throw new CollectorException(msg);
        }

    }

    private void validateConfig() {

        if (handler == null) {
            throw new ConfigurationException("handler must be non null");
        }
        if (dataService == null) {
            throw new ConfigurationException("dataService must be non null");
        }

    }

    /**
     * Returns true if isNotifiedBefore has been called with the same key before.
     *
     * @param key
     * @return
     */
    private boolean isNotifiedBefore(String key) {

        if (!notifiedMap.containsKey(key)) {
            notifiedMap.put(key, key);
            return false;
        }

        return true;

    }

    public interface EventHandler {

        public void onCollectStart() throws Exception;

        public void onCollectEnd() throws Exception;

        public void onCollectFailed(Exception e);

        public void onCollectWarning(String msg);

        public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception;

        public void onCollect(Koulutusmoduuli moduuli) throws Exception;

        public void onCollect(Hakukohde hakukohde) throws Exception;

        public void onCollect(Haku haku) throws Exception;

    }


    /**
     * Convenience class that implements all EventHandler's methods as no-op.
     */
    public static class EventHandlerSuppport implements EventHandler {

        @Override
        public void onCollect(Haku haku) throws Exception {
        }

        @Override
        public void onCollect(Hakukohde hakukohde) throws Exception {
        }

        @Override
        public void onCollect(Koulutusmoduuli moduuli) throws Exception {
        }

        @Override
        public void onCollect(KoulutusmoduuliToteutus toteutus) throws Exception {
        }

        @Override
        public void onCollectEnd() throws Exception {
        }

        @Override
        public void onCollectFailed(Exception e) {
        }

        @Override
        public void onCollectStart() throws Exception {
        }

        @Override
        public void onCollectWarning(String msg) {
        }

    }


    /**
     * Thrown when collector has not been properly configured.
     */
    public static class ConfigurationException extends IllegalStateException {

        public ConfigurationException(String string) {
            super(string);
        }

    }


    /**
     * Thrown when error occurs during data collection.
     */
    public static class CollectorException extends RuntimeException {

        public CollectorException(String string) {
            super(string);
        }

    }


}

