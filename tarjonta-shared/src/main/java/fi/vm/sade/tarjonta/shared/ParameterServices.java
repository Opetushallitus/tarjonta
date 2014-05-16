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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cache all parameters, refresh every 10 minutes.
 * 
 * Implements some parameter related logic checks.
 *
 * @author mlyly
 */
@Component
public class ParameterServices implements InitializingBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(ParameterServices.class);
    
    @Value("${cas.service.ohjausparametrit-service}/api/rest/parametri/ALL")
    private String _ohjausparemetritServiceUrl;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.debug("afterPropertiesSet() --  _ohjausparemetritServiceUrl = {}", _ohjausparemetritServiceUrl);
        reloadParametersIfNeeded();
    }

    /**
     * Simple class for processing / storing parameters.
     */
    public class Parameter {
        private String name; // target
        private String path; // parameter name
        private String value;
        private String type;
        
        public Date getValueAsDate() {
            try {
                return new Date(getValueAsLong());
            } catch (NumberFormatException ex) {
                LOG.warn("Parameter: {}/{} with value {} ({}) cannot be parsed as long", new Object[] {name, path, value, type});
                return null;
            }
        }

        public long getValueAsLong() {
            return Long.parseLong(value);
        }

        public boolean getValueAsBoolean() {
            return Boolean.parseBoolean(value);
        }
        
    }

    /*
     * Store parameters here.
     * {name : { path : Parameter }}
     * NOTE: "name" == "target" (Haku, Hakukohde, etc.), path = "parameter name" (PH_TJT, etc).
     */
    private Map<String, Map<String, Parameter>> _parameters = new HashMap<String, Map<String, Parameter>>();
    private long _nextUpdateDue = 0L;
    private final long _nextUpdatePeriod = 1L * 60L * 1000L;
    
    /**
     * Calls reload params if needed
     */
    private void reloadParametersIfNeeded() {
        if (isParameterReloadNeeded()) {
            reloadParameters();
        }
    }

    /**
     * @return true if reload time has passed
     */
    private boolean isParameterReloadNeeded() {
        return (_nextUpdateDue <= System.currentTimeMillis());
    }

    /**
     * Reloads all parameters from ohjausparametrit-service and makes an internal cache for accessing them.
     * Schedules next parameter reload.
     */
    synchronized private void reloadParameters() {
        if (!isParameterReloadNeeded()) {
            return;
        }
        
        LOG.info("reloadParameters()...");

        try {
            Map<String, Map<String, Parameter>> tmp = new HashMap<String, Map<String, Parameter>>();

            // Read ohjausparametrit and parse result with GSON
            URL url = new URL(_ohjausparemetritServiceUrl);
            URLConnection conn = url.openConnection();
    
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            List<Parameter> result = new ArrayList<Parameter>();
            result = new Gson().fromJson(br, new TypeToken<List<Parameter>>() {
            }.getType());

            // Save for faster lookup
            for (Parameter p : result) {
                LOG.debug("  processing: {}/{} == {}", new Object[] {p.name, p.path, p.value});

                Map<String, Parameter> targetParams = tmp.get(p.name);
                if (targetParams == null) {
                    targetParams = new HashMap<String, Parameter>();
                    tmp.put(p.name, targetParams);
                }
                
                targetParams.put(p.path, p);
            }
            
            // Only update parameters if processed successfully
            _parameters = tmp;            
        } catch (Exception ex) {
            LOG.error("Failed to load parameter from: " + _ohjausparemetritServiceUrl, ex);
        }
        
        // Next update scheduling
        _nextUpdateDue = System.currentTimeMillis() + _nextUpdatePeriod;

        LOG.info("reloadParameters()... done.");
    }

    public Map<String, Parameter> getParameters(String target) {
        reloadParametersIfNeeded();
        return _parameters.get(target);
    }

    public Parameter getParameter(String target, String parameterName) {
        Map<String, Parameter> tmp = getParameters(target);        
        return (tmp != null) ? tmp.get(parameterName) : null;
    }

    public Date getParameterAsDate(String target, String parameterName) {
        Parameter p = getParameter(target, parameterName);
        return (p != null) ? p.getValueAsDate() : null;
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
        Date ph_hklpt = getParameterAsDate(hakuOid, "PH_HKLPT");
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT");
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
        Date ph_hklpt = getParameterAsDate(hakuOid, "PH_HKLPT");
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT");
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
        Date ph_hklpt = getParameterAsDate(hakuOid, "PH_HKLPT");
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT");
        Date now = new Date();
        
        boolean result = (ph_hklpt == null || ph_hklpt.after(now)) && (ph_hkmt == null || ph_hkmt.after(now));
        LOG.debug("parameterCanEditHakukohde({}) hklpt={}, hkmt={} --> {}", new Object[] {hakuOid, ph_hklpt, ph_hkmt, result});
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
     * // null - 1.2. - x  - true/false
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
        Date ph_hkmt = getParameterAsDate(hakuOid, "PH_HKMT");
        Date now = new Date();
        
        boolean result = (ph_hkmt == null || ph_hkmt.after(now));
        LOG.debug("parameterCanEditHakukohdeLimited({}) hkmt={} --> {}", new Object[] {hakuOid, ph_hkmt, result});
        return result;
    }
}
