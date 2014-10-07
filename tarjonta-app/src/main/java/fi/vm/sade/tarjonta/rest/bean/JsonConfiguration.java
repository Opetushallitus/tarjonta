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
package fi.vm.sade.tarjonta.rest.bean;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.rest.dto.JsonConfigObject;
import fi.vm.sade.tarjonta.rest.helper.PropertyPlaceholder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/")
public class JsonConfiguration {

    @Value("${importAllKeys.startsWith}")
    private String keysStartWith;
    @Value("${importAllKeys.contains}")
    private String keysContains;
    private static String configurationJson;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String help() {
        return "<p>"
                + "Output configuration JSON object : <environment><a href=\"json/env-configuration.json\">/kk/ext/json/env-configuration.json</a>.<br/>"
                + "Output configuration JSON object in JavaScript variable : <environment>/<a href=\"js/env-configuration.js\">/kk/ext/js/env-configuration.js</a>."
                + "</p>";
    }

    @RequestMapping(value = "/json/env-configuration.json", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getEnvJsonConfig() {
        if (configurationJson == null) {
            final Properties properties = PropertyPlaceholder.getProperties();
            configurationJson = createJsonConfiguration(properties);
        }

        return configurationJson;
    }

    @RequestMapping(value = "/js/env-configuration.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String getEnvConfigurationJsFile() {
        if (configurationJson == null) {
            final Properties properties = PropertyPlaceholder.getProperties();
            configurationJson = createJsonConfiguration(properties);
        }

        return "window.CONFIG = " + configurationJson + ";";
    }

    private String createJsonConfiguration(Properties properties) {
        Set<String> visibleKeys = searchAllRequiredPropertyKeys(properties);

        List<JsonConfigObject> jsons = Lists.<JsonConfigObject>newLinkedList();
        for (final String key : visibleKeys) {
            jsons.add(new JsonConfigObject(key, PropertyPlaceholder.getProperty(key)));
        }

        Collections.sort(jsons, new Comparator<JsonConfigObject>() {
            @Override
            public int compare(JsonConfigObject a, JsonConfigObject b) {
                int ret = a.getKey().compareTo(b.getKey());
                return ret != 0 ? ret : a.getKey().compareTo(b.getKey());
            }
        });

        StringBuilder outputJson = new StringBuilder();
        outputJson.append("{ \"env\" : {");
        outputJson.append(StringUtils.join(jsons.toArray(), ","));
        outputJson.append("}}");
        return outputJson.toString();
    }

    private Set<String> searchAllRequiredPropertyKeys(Properties propertyes) {
        Preconditions.checkNotNull(propertyes, "System properties object cannot be null.");
        final String[] arrKeysStartWith = keysStartWith.split(",");
        final String[] arrKeysContains = keysContains.split(",");

        Set<String> keysNeeded = Sets.<String>newHashSet();

        if ((arrKeysStartWith == null && arrKeysStartWith == null) || (arrKeysStartWith.length == 0 && arrKeysContains.length == 0)) {
            return keysNeeded;
        }

        for (Object keyObj : propertyes.keySet()) {
            final String realKey = (String) keyObj;

            //start with
            for (String k : arrKeysStartWith) {
                final String filter = k.trim();
                if (filter != null && realKey.startsWith(filter)) {
                    keysNeeded.add(realKey);
                }
            }

            //contains
            for (String k : arrKeysContains) {
                final String filter = k.trim();
                if (filter != null && realKey.contains(filter)) {
                    keysNeeded.add(realKey);
                }
            }
        }

        return keysNeeded;
    }
}