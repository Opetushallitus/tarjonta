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
package fi.vm.sade.tarjonta.service.tasks;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fi.vm.sade.tarjonta.service.search.IndexerResource;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 *
 * @author mlyly
 */
@Service
@EnableScheduling
public class PathUpdaterTask {

    @Autowired
    private IndexerResource indexResource;

    private static final Logger LOG = LoggerFactory
            .getLogger(PathUpdaterTask.class);

    @PostConstruct
    public void afterCreate() {
        LOG.info("afterCreate()");
    }

    // @Scheduled(fixedRate = 5000L)
    @Scheduled(cron = "0 * * * * ?")
    public void updatePath() {
        // LOG.debug("updating tarjonta index.");
        // TODO update tarjonta index

        printCacheStats();
    }


    @Autowired(required = false)
    @Qualifier(value = "ehcacheTarjontaService")
    private CacheManager _cacheManager;

    private void printCacheStats() {
        LOG.warn("---------- printCacheStats(): " + this);

        if (_cacheManager == null) {
            LOG.info("  NO EHCACHE ... no stats!");
            return;
        }

        for (String cacheName : _cacheManager.getCacheNames()) {
            LOG.warn("  {}", _cacheManager.getCache(cacheName).getStatistics());
        }
    }

}
