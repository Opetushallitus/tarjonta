/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.tarjonta.service.impl.resources.v1.util;


import com.google.common.base.Ticker;
import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * This is a LoadingCache wrapper which refreshes the entries asynchronously.
 * The idea is to have the heaviest entries marked as "keeper" and
 * have them all the time in cache. After invalidateAll, the "keep" entries
 * will be reloaded in background so that users (almost) never need to wait for
 * heavy entries to load.
 *
 * Cache.refreshAfterWrite(long, TimeUnit) is not used because it will reload
 * the entry only when it is next time queried by the user, and this what we try
 * to avoid with this class.
 */
@Service
public class AutoRefreshableCache<T> {

    private LoadingCache<String, T> cache;

    private static final Logger LOG = LoggerFactory.getLogger(AutoRefreshableCache.class);

    private ConcurrentMap<String, Callable<? extends T>> loadersForKeys;
    private ConcurrentMap<String, Callable<? extends T>> loadersForKeeperKeys;

    public AutoRefreshableCache() {
        loadersForKeys = new ConcurrentHashMap<>();
        loadersForKeeperKeys = new ConcurrentHashMap<>();
        initializeCache(null);
    }

    /**
     * Can be also used from unit test to supply a custom Ticker
     * @param ticker
     */
    void initializeCache(Ticker ticker) {
        CacheBuilder builder = CacheBuilder.newBuilder();
        if (ticker != null) {
            builder.ticker(ticker);
        }
        this.cache = builder
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, T>() {
                    @Override
                    public T load(String key) throws Exception {
                        Callable<? extends T> valueLoader = loadersForKeys.get(key);
                        return valueLoader.call();
                    }
                });
    }

    /**
     * Gets the value of the key from the cache, using the valueLoader. Note that value will be
     * taken from cache if entry corresponding to cacheKey already exists. The refresh of value in
     * cache will happen asynchronously, when the refresh period for that entry expires.
     * @param cacheKey
     * @param valueLoader
     * @return value of the entry identified by key, either from cache or freshly retrieved using loader
     */
    public T get(String cacheKey, Callable<? extends T> valueLoader) {
        loadersForKeys.put(cacheKey, valueLoader);
        loadersForKeeperKeys.replace(cacheKey, valueLoader);
        try {
            return cache.get(cacheKey);
        } catch (ExecutionException e) {
            String message = "Failed to put value for key " + cacheKey + " into cache";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Mark that given key is a "keeper". "Keepers" are the entries that will be automatically
     * reloaded after invalidateAll().
     * @param key
     */
    public void markAsKeeper(String key) {
        Callable<? extends T> loader = loadersForKeys.get(key);
        if (loader != null) {
            loadersForKeeperKeys.put(key, loader);
        } else {
            String message = "Cannot mark a non-existent key as keeper (key=" + key + ")";
            LOG.error(message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Does {@link #get(String, Callable)} and, if isKeeper is true, also {@link #markAsKeeper(String)}
     * @param cacheKey
     * @param valueLoader
     * @param isKeeper
     * @return
     */
    public T get(String cacheKey, Callable<? extends T> valueLoader, boolean isKeeper) {
        T result = get(cacheKey, valueLoader);
        if (isKeeper) {
            markAsKeeper(cacheKey);
        }
        return result;
    }

    public T getIfPresent(T key) {
        return cache.getIfPresent(key);
    }

    /**
     * Invalidates all entries, but the entries that are marked as "keep" will be
     * asynchronously reloaded.
     */
    public void invalidateAll() {
        cache.invalidateAll();
        loadersForKeys.clear();
        loadersForKeeperKeys.forEach((key, loader) -> get(key, loader));
    }

    LoadingCache<String, T> getCache() {
        return cache;
    }
}
