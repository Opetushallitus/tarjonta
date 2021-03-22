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
 * European Union Public Licence for more details.
 */

package fi.vm.sade.tarjonta.shared;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Cache parameters, refresh targets every minute.
 * 
 * Implements some parameter related logic checks.
 *
 * @author mlyly
 */
@Component
public class ParameterServices implements InitializingBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(ParameterServices.class);

    private LoadingCache<String, JSONObject> _cache;

    private final UrlConfiguration urlConfiguration;

    @Autowired
    public ParameterServices(UrlConfiguration urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final RemovalListener<String, JSONObject> onRemoval = notification ->
                LOG.debug("Cache - onRemoval() target: {} - cache size = {}", notification.getKey(), _cache.size()
                );
        
        final CacheLoader<String, JSONObject> cacheLoader = new CacheLoader<String, JSONObject>() {

            @Override
            public JSONObject load(String key) throws Exception {
                LOG.debug("Cache - load target: {}", key);
                return reloadParameter(key);
            }
            
        };
        
        _cache = CacheBuilder.newBuilder()
//                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .removalListener(onRemoval)
                .build(cacheLoader);        
    }
    
    /**
     * Reloads required parameter from ohjausparametrit-service and makes an internal cache for accessing them.
     */
    private JSONObject reloadParameter(String target) {
        LOG.info("reloadParameter({})", target);
        
        try {
            // Read ohjausparametrit and parse result with GSON
            URL url = new URL(urlConfiguration.url("ohjausparametrit-service.getParametrit", target));
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Caller-Id", HttpClientConfiguration.CALLER_ID);
            return new JSONObject(IOUtils.toString(conn.getInputStream()));
        } catch (FileNotFoundException ex) {
            LOG.debug("No parameters for target: " + target);
        } catch (Exception ex) {
            LOG.error("Failed to load parameter from: " + urlConfiguration.url("ohjausparametrit-service.getParametrit", target), ex);
        }
        return new JSONObject();
    }

    public JSONObject getParameters(String target) {
        try {
            // Get from cache - triggers reload if not available
            return _cache.get(target);
        } catch (ExecutionException ex) {
            LOG.error("Failed to load parameters for: " + target, ex);
            return null;
        }
    }

    public JSONObject getParameter(String target, String parameterName) {
        LOG.debug("getParameter({}, {})", target, parameterName);
        try {
          JSONObject tmp = getParameters(target);
          if (tmp != null && tmp.has(parameterName)) {
              return tmp.getJSONObject(parameterName);
          }
        } catch (JSONException ex) {
                LOG.error("Failed to get parameter: " + target + "/" + parameterName, ex);
        }
        return null;
    }

    public Date getParameterAsDate(String target, String parameterName, String fieldName) {
        JSONObject p = getParameter(target, parameterName);
        if (p != null && p.has(fieldName)) {
            Long ts;
            try {
                ts = p.getLong(fieldName);
            } catch (JSONException ex) {
                LOG.info("Failed to parse to long: " + target + "/" + parameterName + "/" + fieldName);
                ts = null;
            }
            return (ts != null) ? new Date(ts) : null;
        }
        return null;
    }
        
    /*
     * PARAMETER RELATED CHECKS -- (HJVO-21, HJVO-22, HJVO-54)
     */

    /**
     * True if parameters PH_HKLPT and PH_HKMT allow it.
     * 
     * More explicitly:
     * <pre>
     * (
     *   (PH_HKLPT == null || PH_HKLPT >= now()) 
     * AND 
     *   (PH_HKMT == null || PH_HKMT >= now())
     * )
     * </pre>
     * 
     * @param hakuOid the Haku where the target hakukohde is attached
     * @return true if allowed
     */
    public boolean parameterCanAddHakukohdeToHaku(String hakuOid) {
        Date ph_hklpt = getParameterAsDate(hakuOid, "PH_HKLPT", "date");
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT", "date");
        Date now = new Date();
        
        boolean result = (ph_hklpt == null || ph_hklpt.after(now)) && (ph_hkmt == null || ph_hkmt.after(now));
        LOG.debug("parameterCanAddHakukohdeToHaku({}) hklpt={}, hkmt={} --> {}", new Object[] {hakuOid, ph_hklpt, ph_hkmt, result});
        return result;
    }

    
    /**
     * True if parameters PH_HKLPT and PH_HKMT allow it.
     * 
     * More explicitly:
     * <pre>
     * (
     *   (PH_HKLPT == null || PH_HKLPT >= now()) 
     * AND 
     *   (PH_HKMT == null || PH_HKMT >= now())
     * )
     * </pre>
     * 
     * @param hakuOid the Haku where the target hakukohde is attached
     * @return true if allowed
     */
    public boolean parameterCanRemoveHakukohdeFromHaku(String hakuOid) {
        Date ph_hklpt = getParameterAsDate(hakuOid, "PH_HKLPT", "date");
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT", "date");
        Date now = new Date();
        
        boolean result = (ph_hklpt == null || ph_hklpt.after(now)) && (ph_hkmt == null || ph_hkmt.after(now));
        LOG.debug("parameterCanRemoveHakukohdeFromHaku({}) hklpt={}, hkmt={} --> {}", new Object[] {hakuOid, ph_hklpt, ph_hkmt, result});
        return result;
    }

    /**
     * True if parameters PH_HKLPT and PH_HKMT allow it.
     * 
     * NOTE: This may be false BUT "parameterCanEditHakukohdeLimited()" may be true
     * 
     * More explicitly:
     * <pre>
     * // Examples of return values: 
     * // ---------------- - (edit / edit limited)
     * // x - null - null  - true/true
     * // null - x - null  - true/true
     * // null - null - x  - true/true
     *
     * // x - 1.1. - null  - true/true
     * // 1.1. - x - null  - false/true
     * // 1.1. - null - x  - false/true
     *
     * // x - null - 1.2.  - true/true
     * // null - x - 1.2.  - true/true
     * // null - 1.2. - x  - false/false
     *
     * // x - 1.1. - 1.2.  - true/true
     * // 1.1. - x - 1.2.  - false/true
     * // 1.1. - 1.2. - x  - false/false
     * 
     * (
     *   (PH_HKLPT == null || PH_HKLPT >= now()) 
     * AND 
     *   (PH_HKMT == null || PH_HKMT >= now())
     * )
     * </pre>
     * 
     * @param hakuOid the Haku where the target hakukohde is attached
     * @return 
     */
    public boolean parameterCanEditHakukohde(String hakuOid) {
        Date ph_hklpt = getParameterAsDate(hakuOid, "PH_HKLPT", "date");
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT", "date");
        Date now = new Date();
        
        boolean result = (ph_hklpt == null || ph_hklpt.after(now)) && (ph_hkmt == null || ph_hkmt.after(now));
        LOG.info("parameterCanEditHakukohde({}) hklpt={}, hkmt={} --> {}", new Object[] {hakuOid, ph_hklpt, ph_hkmt, result});
        return result;
    }

    /**
     * True if parameter PH_HKMT allow it
     * 
     * Note: this is always true if "parameterCanEditHakukohde()" is true.
     * 
     * If parameterCanEditHakukohde() is false AND parameterCanEditHakukohdeLimited() is true then
     * only limited set of fields should be allowed to be modified.
     * 
     * More explicitly:
     * <pre>
     * // Examples of return values: 
     * // ---------------- - (edit / edit limited)
     * // x - null - null  - true/true
     * // null - x - null  - true/true
     * // null - null - x  - true/true
     *
     * // x - 1.1. - null  - true/true
     * // 1.1. - x - null  - false/true
     * // 1.1. - null - x  - false/true
     *
     * // x - null - 1.2.  - true/true
     * // null - x - 1.2.  - true/true
     * // null - 1.2. - x  - false/false
     *
     * // x - 1.1. - 1.2.  - true/true
     * // 1.1. - x - 1.2.  - false/true
     * // 1.1. - 1.2. - x  - false/false
     * 
     * (
     *   (PH_HKMT == null || PH_HKMT >= now())
     * )
     * </pre>
     * 
     * @param hakuOid the Haku where the target hakukohde is attached
     * @return 
     */
    public boolean parameterCanEditHakukohdeLimited(String hakuOid) {
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT", "date");
        Date now = new Date();
        
        boolean result = (ph_hkmt == null || ph_hkmt.after(now));
        LOG.info("parameterCanEditHakukohdeLimited({}) hkmt={} --> {}", new Object[] {hakuOid, ph_hkmt, result});
        return result;
    }
}
