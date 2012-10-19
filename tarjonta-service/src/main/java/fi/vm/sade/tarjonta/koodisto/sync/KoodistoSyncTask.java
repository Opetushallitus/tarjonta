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

import java.util.List;
import java.util.Set;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.koodisto.util.VersionedUri;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Task that loads koodis from Koodisto and invokes listeners with results.
 *
 * @author Jukka Raanamo
 */
public class KoodistoSyncTask {

    private Set<String> koodistoSyncSpecs;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private KoodistoService koodistoService;

    private List<KoodistoSyncTaskListener> listeners;

    private static final Logger log = LoggerFactory.getLogger(KoodistoSyncTask.class);

    /**
     * Performs the actual work. By now listeners, services and sync specs must
     * have been assigned.
     */
    public void execute() {

        for (String spec : koodistoSyncSpecs) {
            try {
                KoodistoSyncResult result = sync(spec);
                fireSyncLoaded(spec, result);
            } catch (Exception e) {
                fireSyncFailed(spec, e);
            }
        }

    }

    /**
     * Assigns a set of specifications that tell which koodisto's to sync.
     *
     * @param koodistoSyncSpec
     */
    public void setKoodistoSyncSpecs(Set<String> koodistoSyncSpec) {

        this.koodistoSyncSpecs = koodistoSyncSpec;

    }

    /**
     * Assigns a set of listeners that will be called during synchronization.
     * Any previously assigned listeners are lost.
     *
     * @param listeners
     */
    public void setListeners(List<KoodistoSyncTaskListener> listeners) {

        this.listeners = listeners;

    }

    /**
     * Adds another listener to receive sync events.
     *
     * @param listener
     */
    public void addListener(KoodistoSyncTaskListener listener) {

        if (listeners == null) {
            listeners = new ArrayList<KoodistoSyncTaskListener>();
        }

        listeners.add(listener);

    }

    /**
     * Removes listener if exists.
     *
     * @param listener
     */
    public void removeListener(KoodistoSyncTaskListener listener) {

        if (listeners != null) {
            listeners.remove(listener);
        }

    }

    /**
     * Assigns the koodi service used for synchronizing koodis.
     *
     * @param koodiService
     */
    public void setKoodiService(KoodiService koodiService) {

        this.koodiService = koodiService;

    }

    /**
     * Assigns the koodisto service user for looking up koodisto details.
     *
     * @param koodistoService
     */
    public void setKoodistoService(KoodistoService koodistoService) {
        this.koodistoService = koodistoService;
    }

    /**
     * Synchronize by a string parseable by KoodistoSyncSpec object.
     *
     * @param spec
     */
    public KoodistoSyncResult sync(String spec) {

        return sync(new VersionedUri(spec));

    }

    /**
     * Synchronize by sync spec object.
     *
     * @param spec
     */
    public KoodistoSyncResult sync(VersionedUri spec) {

        return sync(buildSearchCriteria(spec));

    }

    /**
     * Synchronize by Koodisto API criteria.
     *
     * @param criteria
     */
    private KoodistoSyncResult sync(SearchKoodisByKoodistoCriteriaType criteria) {

        return new KoodistoSyncResult(criteria, koodiService.searchKoodisByKoodisto(criteria));

    }

    /**
     * Converts sync spec into Koodisto search criteria.
     *
     * @param spec
     * @return
     */
    private SearchKoodisByKoodistoCriteriaType buildSearchCriteria(VersionedUri spec) {

        final String koodistoUri = spec.getUri();

        return KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(koodistoUri,
            (spec.getVersio() != null ? spec.getVersio() : lookupLatestKoodistoVersion(koodistoUri)));

    }

    /**
     * Invokes Koodisto Service to find out what is the current valid version of given koodisto.
     *
     * @param koodistoUri
     * @return
     */
    private int lookupLatestKoodistoVersion(String koodistoUri) {

        List<KoodistoType> koodistos = koodistoService.searchKoodistos(KoodistoServiceSearchCriteriaBuilder.latestAcceptedKoodistoByUri(koodistoUri));
        if (koodistos == null || koodistos.isEmpty()) {
            throw new IllegalArgumentException("cannot determine version, no koodisto matched uri: " + koodistoUri);
        } else if (koodistos.size() > 1) {
            throw new IllegalArgumentException("cannot determine version, multiple koodistos matched uri: " + koodistoUri);
        }

        return koodistos.get(0).getVersio();

    }

    private void fireSyncLoaded(String spec, KoodistoSyncResult result) {

        if (log.isInfoEnabled()) {
            log.info("synchronized koodisto from spec: " + spec
                + ", num koodis: " + result);
        }

        if (listeners != null && !listeners.isEmpty()) {
            for (KoodistoSyncTaskListener l : listeners) {
                try {
                    l.onSyncLoaded(spec, result);
                } catch (Exception e) {
                    log.error("listener failed on onSyncLoaded", e);
                }
            }
        } else {
            log.warn("no listeners assigned");
        }

    }

    private void fireSyncFailed(String spec, Exception syncException) {

        log.error("synchronizing failed, spec: " + spec, syncException);

        if (listeners != null && !listeners.isEmpty()) {
            for (KoodistoSyncTaskListener l : listeners) {
                try {
                    l.onSyncFailed(spec, syncException);
                } catch (Exception e) {
                    log.error("listener failed on onSyncFailed", e);
                }
            }
        } else {
            log.warn("no listeners assigned");
        }

    }

    public static class KoodistoSyncResult {

        private SearchKoodisByKoodistoCriteriaType criteria;

        private List<KoodiType> koodis;

        public KoodistoSyncResult(SearchKoodisByKoodistoCriteriaType criteria,
            List<KoodiType> koodis) {
            this.criteria = criteria;
            this.koodis = koodis;
        }

        public SearchKoodisByKoodistoCriteriaType getCriteria() {
            return criteria;
        }

        public List<KoodiType> getKoodis() {
            return koodis;
        }

    }


    /**
     * Listener that receives sync callbacks.
     */
    public interface KoodistoSyncTaskListener {

        /**
         * Invoked once a spec has been executed and results received.
         *
         * @param spec input spec that was converted into Koodisto search criteria
         * @param koodis koodis returned by Koodisto service
         */
        public void onSyncLoaded(String spec, KoodistoSyncResult result);

        /**
         * Invoked if syncronizing data with given spec raised an exception.
         *
         * @param spec input spec
         * @param e exception from the Koodisto service
         */
        public void onSyncFailed(String spec, Exception e);

    }


}

